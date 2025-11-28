package br.com.challenge.weighing_ingest_service.controller;

import br.com.challenge.weighing_ingest_service.dto.WeighingIngestInputDto;
import br.com.challenge.weighing_ingest_service.service.WeighingIngestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weighings")
public class WeighingIngestController {

    private final WeighingIngestService weighingIngestService;

    public WeighingIngestController(WeighingIngestService weighingIngestService) {
        this.weighingIngestService = weighingIngestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> ingest(@RequestBody @Valid WeighingIngestInputDto weighingIngestInputDto) {
        return weighingIngestService.process(weighingIngestInputDto);
    }

}
