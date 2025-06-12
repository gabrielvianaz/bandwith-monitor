# Bandwidth Monitor para Mikrotik 🌐📊

Projeto desenvolvido para a disciplina **Redes II**, com o objetivo de criar uma interface web que exiba em **tempo real** o gráfico de utilização de **bandwidth** de uma interface de rede de um **roteador Mikrotik**.

---

## 📌 Descrição

A aplicação é composta por três serviços principais:

- **Front-end (Vue.js)**: Interface gráfica que exibe os dados de tráfego em tempo real.
- **Back-end (Spring Boot + Kotlin)**: Responsável por coletar os dados de bandwidth do Mikrotik via protocolo SNMP, utilizando a biblioteca **Snmp4j**.
  - Também expõe um **WebSocket** que envia notificações periódicas ao front-end com os valores atuais de **TX (transmissão)** e **RX (recepção)** da interface monitorada.
- **CHR (Cloud Hosted Router)**: Container com o Mikrotik RouterOS provisionado via Docker.

---

## 🧱 Estrutura do Projeto

```
bandwith-monitor/
├── back/               # Código da API (Spring Boot + Kotlin)
├── front/              # Interface web (Vue.js)
├── chr/                # Dockerfile para provisionar o Mikrotik CHR
├── docker-compose.yml  # Orquestração dos serviços
└── README.md           # Documentação do projeto
```

---

## ⚙️ Comunicação entre os serviços

- O **back-end** consulta o CHR utilizando **SNMP (porta UDP 161)** para obter dados da interface de rede.
- Para isso, utiliza a biblioteca **Snmp4j**, amplamente adotada para comunicação SNMP em aplicações Java.
- Os dados de **TX/RX** são enviados em tempo real ao **front-end** por meio de um **WebSocket** exposto pela API.

---

## 🚀 Como executar

### Pré-requisitos

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### Passo a passo

1. Clone o repositório:
```bash
git clone https://github.com/gabrielvianaz/bandwith-monitor.git
cd bandwith-monitor
```

2. Suba os containers:
```bash
docker compose up --build
```

3. Acesse a interface web em:  
   👉 `http://localhost`

4. A API estará disponível em:  
   👉 `http://localhost:8080`

5. O roteador Mikrotik CHR estará exposto nas seguintes portas:

| Porta | Protocolo | Descrição                     |
|-------|-----------|-------------------------------|
| 22    | TCP       | SSH                           |
| 161   | UDP       | SNMP                          |
| 8291  | TCP       | Winbox                        |