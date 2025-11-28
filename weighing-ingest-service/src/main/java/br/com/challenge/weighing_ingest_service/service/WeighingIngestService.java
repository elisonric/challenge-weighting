package br.com.challenge.weighing_ingest_service.service;

import br.com.challenge.weighing_ingest_service.dto.WeighingIngestInputDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import tools.jackson.databind.ObjectMapper;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class WeighingIngestService {

    @Value("${kafka.topic.weighing-events}")
    private String topic;

    private final KafkaSender<String, String> kafksender;

    public WeighingIngestService(KafkaSender<String, String> kafksender) {
        this.kafksender = kafksender;
    }

    public Mono<Void> process(WeighingIngestInputDto weighingIngestInputDto) {
        try {
            String payload = new ObjectMapper().writeValueAsString(weighingIngestInputDto);
            SenderRecord<String, String, String> record =
                    SenderRecord.create(topic, null, null, weighingIngestInputDto.getPlate(), payload, weighingIngestInputDto.getPlate());

            return kafksender.send(Mono.just(record))
                    .then();
        } catch (Exception e) {
            log.error("Erro ao serializar WeighingIngestInputDto: {}", weighingIngestInputDto, e);
            return Mono.empty();
        }
    }
}
