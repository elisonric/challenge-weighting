package br.com.challenge.weight_processor_service.listener;

import br.com.challenge.weight_processor_service.core.KeyAssignmentRegistry;
import br.com.challenge.weight_processor_service.core.PlateAssignmentFilter;
import br.com.challenge.weight_processor_service.core.WorkerInfo;
import br.com.challenge.weight_processor_service.dto.WeighingEventDto;
import br.com.challenge.weight_processor_service.service.WeighingProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class WeighingEventListener {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PlateAssignmentFilter plateAssignmentFilter;
    private final WeighingProcessorService weighingProcessorService;
    private final KeyAssignmentRegistry registry;
    private final WorkerInfo workerInfo;

    public WeighingEventListener(
            PlateAssignmentFilter plateAssignmentFilter,
            WeighingProcessorService weighingProcessorService,
            KeyAssignmentRegistry registry,
            WorkerInfo workerInfo
    ) {
        this.plateAssignmentFilter = plateAssignmentFilter;
        this.weighingProcessorService = weighingProcessorService;
        this.registry = registry;
        this.workerInfo = workerInfo;
    }

    @KafkaListener(
            topics = "${kafka.topic.weighing-events}",
            groupId = "weight-workers",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record,  Acknowledgment ack) {
        try {
            WeighingEventDto event = objectMapper.readValue(record.value(), WeighingEventDto.class);

            if (!plateAssignmentFilter.shouldProcess(event.getPlate())) {
                return;
            }

            weighingProcessorService.process(event);

            ack.acknowledge();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
