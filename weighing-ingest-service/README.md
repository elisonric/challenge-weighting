# Weighing Ingest Service

Serviço de ingestão de dados de pesagem das balanças, responsável por receber e publicar eventos no Kafka.

## Descrição

O **Weighing Ingest Service** é o ponto de entrada do sistema de pesagens, responsável por:

- Receber dados de peso das balanças via API REST
- Validar autenticação por API Key
- Publicar eventos no Kafka para processamento assíncrono
- Fornecer alta disponibilidade e baixa latência

Este serviço é **stateless** e **reativo**, permitindo escalar horizontalmente para suportar alto volume de requisições.

## Tecnologias

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring WebFlux** (reativo)
- **Spring Kafka**
- **Lombok**
- **Validation API**

## Arquitetura

```
┌─────────────────────────────────────┐
│   Weighing Ingest Service           │
│                                     │
│  ┌──────────────────────────────┐  │
│  │   REST API (Reactive)        │  │
│  │   POST /weighings            │  │
│  │   (API Key Auth)             │  │
│  └───────────┬──────────────────┘  │
│              │                      │
│              ▼                      │
│  ┌──────────────────────────────┐  │
│  │   Validation Layer           │  │
│  │   (DTO Validation)           │  │
│  └───────────┬──────────────────┘  │
│              │                      │
│              ▼                      │
│  ┌──────────────────────────────┐  │
│  │   Kafka Producer             │  │
│  │   (Async Publishing)         │  │
│  └───────────┬──────────────────┘  │
│              │                      │
└──────────────┼──────────────────────┘
               │
               ▼
       ┌────────────────┐
       │ Kafka Topic    │
       │weighing-events │
       └────────────────┘
```

## Como Executar

### Pré-requisitos
- Java 21
- Maven 3.8+
- Kafka rodando na porta 9092

### 1. Iniciar o Kafka

```bash
# Usando Docker Compose
docker-compose up -d kafka

# Ou usar Kafka local
bin/kafka-server-start.sh config/server.properties
```

### 2. Executar o Serviço

```bash
cd weighing-ingest-service
mvn clean install
mvn spring-boot:run
```

### 3. Verificar se está rodando

```bash
curl -H "X-API-KEY: 238903D28ED15A17D227B12DDF1E295CA30466BA34D4FC435946B3046922B3F6" \
     http://localhost:8080/api/actuator/health
```

## Monitoramento

### Actuator Endpoints

O serviço expõe endpoints do Spring Boot Actuator:

- **Health**: http://localhost:8080/api/actuator/health
- **Info**: http://localhost:8080/api/actuator/info
- **Metrics**: http://localhost:8080/api/actuator/metrics


### Otimizações

1. **Batch Processing**: Producer envia mensagens em lotes
2. **Compression**: Mensagens são comprimidas (gzip)
3. **Non-blocking I/O**: Uso de WebFlux para I/O assíncrono

## Licença

Este projeto foi desenvolvido como parte de um desafio técnico.
