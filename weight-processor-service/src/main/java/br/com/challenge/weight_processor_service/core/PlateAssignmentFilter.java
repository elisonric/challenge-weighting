package br.com.challenge.weight_processor_service.core;

import org.springframework.stereotype.Component;

@Component
public class PlateAssignmentFilter {

    private final KeyAssignmentRegistry registry;
    private final WorkerInfo workerInfo;

    public PlateAssignmentFilter(KeyAssignmentRegistry registry, WorkerInfo workerInfo) {
        this.registry = registry;
        this.workerInfo = workerInfo;
    }

    public boolean shouldProcess(String plate) {
        return registry.tryAssign(plate, workerInfo.getWorkerId());
    }

    public void release(String plate) {
        registry.releasePlate(plate);
    }
}
