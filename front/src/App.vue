<script lang="ts" setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { Client, type StompSubscription } from '@stomp/stompjs'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'

use([LineChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

const client = new Client({
  brokerURL: 'ws://localhost:8080/ws',
  onConnect: () => console.log('Connected to WebSocket'),
})

onMounted(() => client.activate())
onBeforeUnmount(() => client.deactivate())

const address = ref('')
const interfaceName = ref('')
const interfaces = ref([])
const lastTraffic = ref<{
  tx: number
  rx: number
  date: Date
} | null>(null)
const option = ref({
  title: { text: 'Traffic' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['TX', 'RX'] },
  xAxis: { type: 'category', data: [] as string[] },
  yAxis: { type: 'value' },
  series: [
    { name: 'TX', type: 'line', data: [] as number[] },
    { name: 'RX', type: 'line', data: [] as number[] },
  ],
})
const subscription = ref<StompSubscription | null>(null)

const currentRate = computed(() => {
  if (!option.value.series[1].data.length) return null

  return {
    tx: option.value.series[0].data.slice(-1)[0],
    rx: option.value.series[1].data.slice(-1)[0],
    lastUpdate: option.value.xAxis.data.slice(-1)[0],
  }
})

function getTraffic(event: Event) {
  event.preventDefault()

  if (subscription.value) {
    subscription.value.unsubscribe()
    subscription.value = null
  }
  lastTraffic.value = null

  subscription.value = client.subscribe(
    '/user/queue/traffic',
    (message) => {
      const { tx, rx } = JSON.parse(message.body)
      const now = new Date()

      if (lastTraffic.value) {
        const { tx: prevTx, rx: prevRx, date: prevDate } = lastTraffic.value
        const intervalInSeconds = (now.getTime() - prevDate.getTime()) / 1000
        const sentBytes = tx - prevTx
        const receivedBytes = rx - prevRx
        const txRate = toKbps(sentBytes, intervalInSeconds)
        const rxRate = toKbps(receivedBytes, intervalInSeconds)

        option.value.xAxis.data.push(now.toLocaleString())
        option.value.series[0].data.push(txRate)
        option.value.series[1].data.push(rxRate)

        if (option.value.xAxis.data.length > 100) {
          option.value.xAxis.data.shift()
          option.value.series[0].data.shift()
          option.value.series[1].data.shift()
        }
      }

      lastTraffic.value = { tx, rx, date: now }
    },
    {
      address: address.value,
      interface: interfaceName.value,
    },
  )
}

function stop(event: Event) {
  event.preventDefault()

  if (subscription.value) {
    subscription.value.unsubscribe()
    subscription.value = null
  }

  lastTraffic.value = null
  option.value.xAxis.data = []
  option.value.series[0].data = []
  option.value.series[1].data = []
}

function toKbps(bytes: number, intervalInSeconds: number): number {
  return roundToOneDecimalPlace(((bytes / intervalInSeconds) * 8) / 1000)
}

function roundToOneDecimalPlace(value: number): number {
  return Math.round(value * 10) / 10
}
</script>

<template>
  <main>
    <form>
      <div class="inputs">
        <div>
          <label for="address">Address:</label>
          <input id="address" v-model="address" :disabled="!!subscription" type="text" />
        </div>
        <div>
          <label for="interface">Interface:</label>
          <input id="interface" v-model="interfaceName" :disabled="!!subscription" type="text" />
        </div>
      </div>
      <div class="buttons">
        <button
          :disabled="!address || !interfaceName || !!subscription"
          type="submit"
          @click="getTraffic"
        >
          Get Traffic
        </button>

        <button :disabled="!subscription" type="submit" @click="stop">Stop</button>
      </div>
    </form>
    <VChart v-if="subscription" :option="option" autoresize style="height: 400px" />
    <div v-if="currentRate">
      <p>TX Rate: {{ currentRate.tx }} Kbps</p>
      <p>RX Rate: {{ currentRate.rx }} Kbps</p>
      <p>Last Update: {{ currentRate.lastUpdate }}</p>
    </div>
  </main>
</template>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;

  .inputs {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;
    flex-wrap: wrap;

    div {
      display: grid;
      grid-template-columns: 1fr 7fr;
      align-items: center;
    }
  }

  .buttons {
    display: flex;
    gap: 16px;
    justify-content: center;
  }
}

main {
  max-width: 600px;
  margin: 40px auto;
  padding: 32px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  font-family: 'Segoe UI', Arial, sans-serif;
}

label {
  font-weight: 500;
  margin-right: 8px;
}

input[type='text'],
select {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d0d7de;
  border-radius: 6px;
  font-size: 1rem;
  min-width: 180px;
  background: #f8fafc;
  transition: border 0.2s;
}

input[type='text']:focus,
select:focus {
  border-color: #42b983;
  outline: none;
}

button {
  padding: 8px 18px;
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

button:disabled {
  background: #b2dfdb;
  cursor: not-allowed;
}

div[v-if='currentRate'] {
  margin-top: 24px;
  padding: 16px;
  background: #f6fffa;
  border: 1px solid #b2dfdb;
  border-radius: 8px;
  text-align: center;
}

p {
  margin: 0.5em 0;
  font-size: 1.1em;
  color: #333;
}
</style>
