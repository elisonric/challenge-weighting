package br.com.challenge.weight_processor_service.service;

import br.com.challenge.weight_processor_service.core.WorkerInfo;
import br.com.challenge.weight_processor_service.dto.WeighingEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeighingProcessorService {

    private static final int MIN_SAMPLES = 30;
    private static final int WINDOW_SIZE = 30;
    private static final BigDecimal MAX_VARIATION = new BigDecimal("0.003");
    private final Map<String, List<BigDecimal>> plateWeights = new ConcurrentHashMap<>();
    private final Map<String, List<BigDecimal>> plateStableWeights = new ConcurrentHashMap<>();

    private final Map<String, Long> lastEventTime = new ConcurrentHashMap<>();
    private static final long INACTIVITY_TIMEOUT_MS = 5000;
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private final WorkerInfo workerInfo;

    public void process(WeighingEventDto weighingEventDto) {
        String plate = weighingEventDto.getPlate();
        BigDecimal weight = weighingEventDto.getWeight();

        lastEventTime.put(plate, System.currentTimeMillis());

        try {
            log.info("Worker {} process plate {} eventId {}",
                    workerInfo.getWorkerId(), weighingEventDto.getPlate(), weighingEventDto.getId());

            plateWeights.computeIfAbsent(plate, p -> new ArrayList<>()).add(weight);

            List<BigDecimal> list = plateWeights.get(plate);

            if (list.size() < MIN_SAMPLES) {
                log.info("Worker {} Register plate {} eventId {}",
                        workerInfo.getWorkerId(), weighingEventDto.getPlate(), weighingEventDto.getId());
                return;
            }

            List<BigDecimal> window =
                    list.subList(Math.max(0, list.size() - WINDOW_SIZE), list.size());

            BigDecimal max = window.stream().max(BigDecimal::compareTo).orElse(weight);
            BigDecimal min = window.stream().min(BigDecimal::compareTo).orElse(weight);

            BigDecimal sum = window.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(window.size()), MC);

            BigDecimal diff = max.subtract(min);
            BigDecimal variation = diff.divide(avg, MC);

            if (variation.compareTo(MAX_VARIATION) <= 0) {
                plateStableWeights.computeIfAbsent(plate, p -> new ArrayList<>())
                        .add(avg);

                log.info("\n=== STABLE WEIGHT DETECTED ===");
                log.info("Plate: " + plate);
                log.info("Final weight: " + avg);
                log.info("AVG: " + variation + "\n");

                plateWeights.remove(plate);
            }
            log.info("Worker {} Register plate {} eventId {}",
                    workerInfo.getWorkerId(), weighingEventDto.getPlate(), weighingEventDto.getId());
        } catch (Exception ex) {
            log.error("Error on process plate {} , worker {}", weighingEventDto.getPlate(), workerInfo.getWorkerId(), ex);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void checkInactivePlates() {
        long now = System.currentTimeMillis();
        for (String plate : new ArrayList<>(lastEventTime.keySet())) {
            long last = lastEventTime.get(plate);
            if (now - last >= INACTIVITY_TIMEOUT_MS) {

                log.info("=== AUTOMATIC TERMINATION DUE TO INACTIVITY ===");
                log.info("Plate: {}", plate);

                List<BigDecimal> stableList = plateStableWeights.getOrDefault(plate, Collections.emptyList());
                if (!stableList.isEmpty()) {
                    BigDecimal finalWeight = stableList.stream().max(BigDecimal::compareTo).get();
                    log.info("Final weight detected: {}", finalWeight);

                    //TODO Create a logic to insert the weight
                } else {
                    log.warn("No stable weight found for the plate. {}", plate);
                }

                plateWeights.remove(plate);
                plateStableWeights.remove(plate);
                lastEventTime.remove(plate);
            }
        }
    }
}
