package br.com.challenge.weight_processor_service.producer;

import br.com.challenge.weight_processor_service.dto.WeighingOutputEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeighingProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kafka.topic.stable-weight}")
    private String topicStableWeight;

    public void sendStableWeight(WeighingOutputEventDto weighingOutputEventDto) {
        try {
            String json = objectMapper.writeValueAsString(weighingOutputEventDto);

            kafkaTemplate.send(topicStableWeight, weighingOutputEventDto.getPlate(), json);

            log.info("📤 Enviado para Kafka ({}): {}", topicStableWeight, json);
        } catch (Exception ex) {
            log.error("Erro ao enviar peso estável para Kafka", ex);
        }
    }
}
