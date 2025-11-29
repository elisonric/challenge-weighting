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
- **Docker & Docker Compose**
- **Kafka UI** (interface de gerenciamento do Kafka)

### Documentação
- **Swagger/OpenAPI 3.0**
- **Postman Collection**

## Requisitos

- Docker & Docker Compose
- Java 21 (para desenvolvimento local)
- Maven 3.8+

## Como Executar

### 1. Subir a infraestrutura (PostgreSQL, Kafka e Kafka UI)

```bash
docker-compose up -d postgres kafka kafka-ui
```

Aguarde os serviços ficarem saudáveis (verificar logs):
```bash
docker-compose logs -f postgres kafka
```

### 2. Executar os serviços

**Opção A: Via Docker Compose (Todos os serviços)**
```bash
docker-compose up -d
```

**Opção B: Desenvolvimento local**

Terminal 1 - Weighing Management Service:
```bash
cd weighing-management-service
mvn spring-boot:run
```

Terminal 2 - Weighing Ingest Service:
```bash
cd weighing-ingest-service
mvn spring-boot:run
```

Terminal 3 - Weight Processor Service:
```bash
cd weight-processor-service
mvn spring-boot:run
```

### 3. Acessar as interfaces

- **Weighing Management API**: http://localhost:8082/api
- **Weighing Management Swagger**: http://localhost:8082/api/swagger-ui.html
- **Weighing Ingest API**: http://localhost:8081/weighing
- **Weight Processor**: Serviço backend (sem interface web)
- **Kafka UI**: http://localhost:8090
- **PostgreSQL**: localhost:5432 (user: postgres, password: postgres, db: weighing_db)

## Tópicos Kafka

| Tópico | Produtor | Consumidor | Descrição |
|--------|----------|------------|-----------|
| `weighing-events` | Weighing Ingest | Weight Processor | Eventos de pesagem brutos das balanças |
| `stables-weight` | Weight Processor | Weighing Management | Eventos de peso estável processados |

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

## Postman Collection

Uma collection completa do Postman está disponível em:
```
weighing-management-service/Weighing_Management_API.postman_collection.json
```

**Como usar:**
1. Importe a collection no Postman
2. Configure a variável `baseUrl` (padrão: http://localhost:8082/api)
3. Execute o endpoint de Login para obter o token JWT
4. O token será salvo automaticamente para os demais requests

## Estrutura do Projeto

```
Challenge/
├── weighing-ingest-service/          # Serviço de ingestão
│   ├── src/
│   ├── Dockerfile
│   └── README.md
├── weight-processor-service/          # Serviço de processamento
│   ├── src/
│   ├── Dockerfile
│   └── README.md
├── weighing-management-service/       # Serviço de gerenciamento
│   ├── src/
│   ├── Dockerfile
│   ├── Weighing_Management_API.postman_collection.json
│   └── README.md
├── docker-compose.yml                 # Orquestração dos serviços
└── README.md                         # Este arquivo
```

## Banco de Dados

O sistema utiliza PostgreSQL com as seguintes entidades principais:

- **Branch**: Filiais
- **Truck**: Caminhões
- **GrainType**: Tipos de grãos
- **Scale**: Balanças
- **TransportTransaction**: Transações de transporte
- **Weighing**: Pesagens processadas
- **User**: Usuários do sistema

O schema é gerenciado automaticamente pelo Flyway com migrations versionadas.

## Segurança

### Weighing Ingest Service
- Autenticação via API Key (header `X-API-KEY`)
- Configurável via variável de ambiente `API_KEY`

### Weighing Management Service
- Autenticação JWT
- Roles: ADMIN e USER
- Endpoints protegidos por permissões
- Token expira em 24 horas

## Escalabilidade

O sistema foi projetado para escalar horizontalmente:

1. **Weighing Ingest Service**: Stateless, pode ter múltiplas instâncias
2. **Weight Processor Service**: Usa sistema de atribuição de placas para distribuir carga entre workers
3. **Weighing Management Service**: Pode escalar com balanceamento de carga

## Monitoramento

- **Kafka UI**: Monitoramento de tópicos, mensagens e consumers
- **Logs**: Todos os serviços geram logs estruturados
- **Health Checks**: Endpoints de health check em todos os serviços

## Desenvolvimento

Para mais detalhes sobre cada serviço, consulte os READMEs individuais:

- [Weighing Ingest Service README](weighing-ingest-service/README.md)
- [Weight Processor Service README](weight-processor-service/README.md)
- [Weighing Management Service README](weighing-management-service/README.md)

## Troubleshooting

### Kafka não conecta
```bash
# Verificar se o Kafka está rodando
docker-compose logs kafka

# Recriar o container do Kafka
docker-compose down kafka
docker-compose up -d kafka
```

### PostgreSQL não conecta
```bash
# Verificar logs do PostgreSQL
docker-compose logs postgres

# Verificar se a porta 5432 está livre
lsof -i :5432
```

### Serviço não consome mensagens do Kafka
```bash
# Verificar consumer groups no Kafka UI
# Acessar: http://localhost:8090

# Resetar offset do consumer (CUIDADO em produção)
docker exec -it challenge-kafka kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group <group-id> \
  --reset-offsets --to-earliest --execute --topic <topic-name>
```

## Licença

Este projeto foi desenvolvido como parte de um desafio técnico.
