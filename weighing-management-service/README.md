# Weighing Management Service

Serviço de gerenciamento de dados de negócio e geração de relatórios do sistema de pesagens de grãos.

## Descrição

O **Weighing Management Service** é o serviço central de gerenciamento do sistema, responsável por:

- Gerenciar entidades de negócio (Filiais, Caminhões, Tipos de Grãos, Balanças, Transações)
- Processar pesagens estáveis vindas do Kafka
- Calcular custos de carregamento
- Gerar relatórios analíticos de custos e lucros
- Fornecer API REST completa com autenticação JWT

## Tecnologias

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **Spring Kafka**
- **Spring Security + JWT**
- **PostgreSQL 16**
- **Flyway** (migração de banco de dados)
- **Swagger/OpenAPI 3.0**
- **Lombok**

## Arquitetura

```
┌─────────────────────────────────────────┐
│     Weighing Management Service         │
│                                         │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │  REST API    │  │  Kafka Consumer │ │
│  │  (JWT Auth)  │  │  stables-weight │ │
│  └──────┬───────┘  └────────┬────────┘ │
│         │                   │          │
│         └───────┬───────────┘          │
│                 │                      │
│         ┌───────▼────────┐            │
│         │  Service Layer │            │
│         └───────┬────────┘            │
│                 │                      │
│         ┌───────▼────────┐            │
│         │  Data Layer    │            │
│         │  (PostgreSQL)  │            │
│         └────────────────┘            │
└─────────────────────────────────────────┘
```

## Migrações do Banco de Dados

O Flyway gerencia as migrations automaticamente em:
```
src/main/resources/db/migration/
```

## Segurança

### Autenticação JWT

Todos os endpoints (exceto `/auth/login`) requerem token JWT no header:
```
Authorization: Bearer <token>
```

### Senha Padrão do Admin

- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

## Documentação Swagger

Acesse a documentação interativa em:
```
http://localhost:8081/api/swagger-ui.html
```

Ou o JSON da especificação:
```
http://localhost:8081/api/v3/api-docs
```

## Postman Collection

Uma collection completa está disponível em:
```
Weighing_Management_API.postman_collection.json
```

**Como usar:**
1. Importe no Postman
2. Execute o endpoint de Login
3. O token será salvo automaticamente na variável `{{token}}`
4. Todos os requests já estão configurados para usar o token

## Como Executar

### Pré-requisitos
- Java 21
- Maven 3.8+
- PostgreSQL rodando na porta 5432
- Kafka rodando na porta 9092

### 1. Configurar o Banco de Dados

```bash
# Usando Docker
docker-compose up -d postgres

# Ou criar manualmente
createdb weighing_db
```

### 2. Executar o Serviço

```bash
cd weighing-management-service
mvn clean install
mvn spring-boot:run
```

### 3. Verificar se está rodando

```bash
curl http://localhost:8081/api/actuator/health
```

## Licença

Este projeto foi desenvolvido como parte de um desafio técnico.
