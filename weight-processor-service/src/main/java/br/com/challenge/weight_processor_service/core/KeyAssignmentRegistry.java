package br.com.challenge.weight_processor_service.core;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KeyAssignmentRegistry {
    private final Map<String, String> assigned = new ConcurrentHashMap<>();

    public synchronized boolean tryAssign(String plate, String workerId) {
        return assigned.computeIfAbsent(plate, p -> workerId).equals(workerId);
    }

    public synchronized void releasePlate(String plate) {
        assigned.remove(plate);
    }
}
