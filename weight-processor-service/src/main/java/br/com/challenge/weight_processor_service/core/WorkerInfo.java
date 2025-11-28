package br.com.challenge.weight_processor_service.core;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkerInfo {
    private final String workerId = UUID.randomUUID().toString();

    public String getWorkerId() {
        return workerId;
    }
}
