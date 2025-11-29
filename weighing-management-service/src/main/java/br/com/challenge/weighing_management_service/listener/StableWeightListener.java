package br.com.challenge.weighing_management_service.listener;

import br.com.challenge.weighing_management_service.dto.WeighingIngestDto;
import br.com.challenge.weighing_management_service.service.WeighingIngestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class StableWeightListener {

    private final WeighingIngestService weighingIngestService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StableWeightListener(WeighingIngestService weighingIngestService) {
        this.weighingIngestService = weighingIngestService;
    }

    @KafkaListener(
            topics = "${kafka.topic.stable-weight}",
            groupId = "weight-workers",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            WeighingIngestDto event = objectMapper.readValue(record.value(), WeighingIngestDto.class);

            weighingIngestService.processWeighing(event);

            ack.acknowledge();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
