package cloud.gviana.bandwithmonitor.event

import cloud.gviana.bandwithmonitor.service.SnmpService
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TrafficEventListener(
    private val snmpService: SnmpService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    @EventListener(TrafficEvent::class)
    fun publishRxAndTx(event: TrafficEvent) {
        val rx = snmpService.sendCommandAndGetFirstVariable(event.address, RX_OID + event.interfaceName)
        val tx = snmpService.sendCommandAndGetFirstVariable(event.address, TX_OID + event.interfaceName)
        val payload = mapOf(
            "rx" to rx.variable.toLong(),
            "tx" to tx.variable.toLong(),
        )

        println("${Instant.now()}: publishing")

        simpMessagingTemplate.convertAndSendToUser(event.sessionId, "/queue/traffic", payload)
    }

    companion object {
        const val RX_OID = ".1.3.6.1.2.1.31.1.1.1.6."
        const val TX_OID = ".1.3.6.1.2.1.31.1.1.1.10."
    }

}
