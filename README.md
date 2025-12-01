# Sistema de Gestão de Pesagens de Grãos

Sistema distribuído de microserviços para gerenciamento e processamento de pesagens de caminhões transportadores de grãos, com arquitetura orientada a eventos usando Apache Kafka.

## Arquitetura do Sistema

O sistema é composto por 3 microserviços principais:

```
┌─────────────────────┐
│  Weighing Ingest    │ → Porta 8081
│     Service         │
└──────────┬──────────┘
           │ Kafka Topic: weighing-events
           ↓
┌─────────────────────┐
│ Weight Processor    │ → Porta 8083
│     Service         │
└──────────┬──────────┘
           │ Kafka Topic: stables-weight
           ↓
┌─────────────────────┐
│Weighing Management  │ → Porta 8082
│     Service         │
└─────────────────────┘
```

### Serviços

#### 1. **Weighing Ingest Service** (Porta 8081)
Serviço responsável pela **ingestão de dados** de pesagem das balanças.

- Recebe dados de peso das balanças via API REST
- Valida autorização por API Key
- Publica eventos no Kafka (tópico: `weighing-events`)
- Arquitetura stateless para alta disponibilidade

**Funcionalidades:**
- Endpoint POST `/weighing` para receber dados das balanças
- Autenticação via API Key no header `X-API-KEY`
- Validação de dados de entrada
- Produção assíncrona de eventos para o Kafka

#### 2. **Weight Processor Service** (Porta 8083)
Serviço responsável pelo **processamento e filtragem** de pesos estáveis.

- Consome eventos do tópico `weighing-events`
- Implementa lógica de detecção de peso estável
- Filtro distribuído por placa de caminhão para processamento paralelo
- Publica eventos de peso estável no tópico `stables-weight`

**Funcionalidades:**
- Processamento de múltiplas pesagens por placa
- Algoritmo de detecção de peso estável (variação máxima de 0.5%)
- Sistema de atribuição de placas por worker (scale horizontal)
- Processamento assíncrono com múltiplas instâncias

#### 3. **Weighing Management Service** (Porta 8082)
Serviço responsável pelo **gerenciamento** de dados de negócio e relatórios.

- API REST completa para gerenciamento de entidades
- Consome eventos de peso estável do Kafka
- Processa pesagens e calcula custos
- Geração de relatórios analíticos

**Funcionalidades:**
- CRUD completo de: Filiais, Caminhões, Tipos de Grãos, Balanças, Transações
- Processamento idempotente de pesagens
- Sistema de autenticação JWT
- Relatórios de custos e lucros com filtros e agrupamentos
- Documentação Swagger/OpenAPI

## Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3.3.5** (todos os serviços)
- **Spring Data JPA** (Weighing Management)
- **Spring WebFlux** (Weighing Ingest - reativo)
- **Spring Kafka** (todos os serviços)
- **Spring Security + JWT** (Weighing Management e Weighing Ingest)
- **PostgreSQL 16** (Weighing Management)
- **Apache Kafka 3.7.0**
- **Flyway** (migração de banco de dados)

### Infraestrutura
- **Docker**
- **Kafka**

### Documentação
- **Swagger/OpenAPI 3.0**
- **Postman Collection**

## Requisitos

- Docker
- Java 21 (para desenvolvimento local)
- Maven 3.8+

## Como Executar

### Subir a infraestrutura (PostgreSQL, Kafka e Kafka UI)

```bash
docker-compose up -d
```

### Acessar as interfaces

- **Weighing Management API**: http://localhost:8081/api
- **Weighing Management Swagger**: http://localhost:8081/api/swagger-ui.html
- **Weighing Ingest API**: http://localhost:8080/weighing
- **Weight Processor**: Serviço backend (sem interface web)
- **Kafka UI**: http://localhost:8090
- **PostgreSQL**: localhost:5432

## Fluxo de Dados

1. **Balança → Weighing Ingest Service**
   - POST /weighing com dados de peso e placa
   - Header `X-API-KEY` para autenticação

2. **Weighing Ingest → Kafka (weighing-events)**
   - Publicação de evento com dados da pesagem

3. **Kafka → Weight Processor Service**
   - Consumo de eventos agrupados por placa
   - Processamento paralelo por worker

4. **Weight Processor → Kafka (stables-weight)**
   - Publicação de evento quando peso estabiliza
   - Peso considerado estável com variação < 0.5%

5. **Kafka → Weighing Management Service**
   - Consumo de eventos de peso estável
   - Processamento idempotente
   - Cálculo de custos e armazenamento

## Diagramas C4

1. **Diagrama de contexto**

![Context](./challenge-weighting%20C4-Context.png)

2. **Diagrama de container**

![Context](./challenge-weighting%20C4-Container.png)

## Licença

Este projeto foi desenvolvido como parte de um desafio técnico.
