# Weight Processor Service

Serviço de processamento e análise de pesos, responsável por detectar pesos estáveis e filtrar eventos duplicados.

## Descrição

O **Weight Processor Service** é o motor de processamento do sistema, responsável por:

- Consumir eventos de pesagem do Kafka (tópico `weighing-events`)
- Analisar múltiplas amostras de peso por placa de caminhão
- Detectar quando o peso está estável (variação < 0.5%)
- Publicar eventos de peso estável no Kafka (tópico `stables-weight`)
- Distribuir processamento entre múltiplos workers (scale horizontal)

Este serviço implementa um algoritmo sofisticado de detecção de peso estável e permite escalabilidade horizontal com distribuição de placas entre workers.

## Tecnologias

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Kafka**
- **Lombok**
- **Jackson** (serialização JSON)
- **Spring Boot Actuator** (monitoramento)
- **Scheduler** (processamento assíncrono)

## Arquitetura

```
┌────────────────────────────────────────────┐
│      Weight Processor Service              │
│                                            │
│  ┌──────────────────────────────────────┐ │
│  │      Kafka Consumer                  │ │
│  │      (weighing-events)               │ │
│  └───────────────┬──────────────────────┘ │
│                  │                         │
│                  ▼                         │
│  ┌──────────────────────────────────────┐ │
│  │  Plate Assignment Filter             │ │
│  │  (Worker-based Distribution)         │ │
│  └───────────────┬──────────────────────┘ │
│                  │                         │
│                  ▼                         │
│  ┌──────────────────────────────────────┐ │
│  │  Weighing Processor Service          │ │
│  │  - Collect samples (min 30)          │ │
│  │  - Sliding window analysis (30)      │ │
│  │  - Stability check (< 0.5%)          │ │
│  └───────────────┬──────────────────────┘ │
│                  │                         │
│                  ▼                         │
│  ┌──────────────────────────────────────┐ │
│  │  Inactivity Monitor                  │ │
│  │  (Check every 1s, timeout 5s)        │ │
│  └───────────────┬──────────────────────┘ │
│                  │                         │
│                  ▼                         │
│  ┌──────────────────────────────────────┐ │
│  │      Kafka Producer                  │ │
│  │      (stables-weight)                │ │
│  └──────────────────────────────────────┘ │
└────────────────────────────────────────────┘
               │
               ▼
       ┌────────────────┐
       │ Kafka Topic    │
       │ stables-weight │
       └────────────────┘
```

## Algoritmo de Detecção de Peso Estável

### Parâmetros

```java
MIN_SAMPLES = 30           // Amostras mínimas antes de análise
WINDOW_SIZE = 30           // Tamanho da janela deslizante
MAX_VARIATION = 0.003      // Variação máxima permitida (0.3%)
INACTIVITY_TIMEOUT = 5000  // Timeout de inatividade (5s)
```

### Fluxo de Processamento

1. **Coleta de Amostras**
   - Recebe evento de peso da balança
   - Armazena peso em lista por placa
   - Aguarda mínimo de 30 amostras

2. **Análise de Janela Deslizante**
   - Pega últimas 30 amostras
   - Calcula média, máximo e mínimo
   - Calcula variação: `(max - min) / avg`

3. **Detecção de Estabilidade**
   - Se variação ≤ 0.3%: **Peso Estável Detectado**
   - Armazena peso médio como peso estável
   - Limpa buffer de amostras

4. **Monitoramento de Inatividade**
   - Job executado a cada 1 segundo
   - Verifica placas inativas por > 5 segundos
   - Publica peso estável mais alto detectado
   - Limpa dados da placa

### Exemplo de Cálculo

```
Amostras: [25000, 25001, 24999, 25000, 25002, ...]
Janela (últimas 30): [24999, 25000, 25000, 25001, 25002, ...]

Média = 25000.5 kg
Máximo = 25002 kg
Mínimo = 24999 kg

Variação = (25002 - 24999) / 25000.5
         = 3 / 25000.5
         = 0.00012 (0.012%)

0.012% < 0.3% → PESO ESTÁVEL! ✓
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
```

### 2. Executar o Serviço

```bash
cd weight-processor-service
mvn clean install
mvn spring-boot:run
```

### 3. Verificar se está rodando

```bash
curl http://localhost:8082/actuator/health
```

## Monitoramento

### Actuator Endpoints

- **Health**: http://localhost:8082/actuator/health
- **Info**: http://localhost:8082/actuator/info
- **Metrics**: http://localhost:8082/actuator/metrics

### Latência

- **Detecção de Peso Estável**: 30-60 segundos (depende da variação)
- **Timeout de Inatividade**: 5 segundos após último evento
- **Publicação no Kafka**: < 10ms

### Uso de Memória

- **Por Placa Ativa**: ~1-2 KB (30 amostras BigDecimal)
- **100 Placas**: ~200 KB
- **1000 Placas**: ~2 MB

## Otimizações Futuras

1. **Persistência de Estado**: Salvar estado em banco para recuperação após restart
2. **Métricas Customizadas**: Expor métricas de placas ativas, tempo médio de estabilização
3. **Alertas**: Notificar quando placa leva muito tempo para estabilizar
4. **Configuração Dinâmica**: Permitir ajuste de parâmetros sem restart
5. **Machine Learning**: Usar ML para prever tempo de estabilização

## Licença

Este projeto foi desenvolvido como parte de um desafio técnico.
