package com.ipld11stars.analytics.controller;

import com.ipld11stars.analytics.engine.DeliveryStreamProcessor;
import com.ipld11stars.analytics.engine.FantasyPointEngine;
import com.ipld11stars.analytics.engine.MatchReplaySimulator;
import com.ipld11stars.analytics.model.DeliveryEvent;
import com.ipld11stars.analytics.storage.ColumnarStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller to trigger offline telemetry replay simulation and inspect scoring.
 */
@RestController
@RequestMapping("/api/analytics/replay")
public class TelemetryReplayController {

    private final MatchReplaySimulator simulator;
    private final DeliveryStreamProcessor streamProcessor;
    private final FantasyPointEngine fantasyEngine;
    private final ColumnarStorageService storageService;

    public TelemetryReplayController(MatchReplaySimulator simulator,
                                     DeliveryStreamProcessor streamProcessor,
                                     FantasyPointEngine fantasyEngine,
                                     ColumnarStorageService storageService) {
        this.simulator = simulator;
        this.streamProcessor = streamProcessor;
        this.fantasyEngine = fantasyEngine;
        this.storageService = storageService;
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runReplaySimulation() {
        List<DeliveryEvent> deliveries = simulator.loadAllDeliveries();

        streamProcessor.reset("2024-M14");
        fantasyEngine.reset();

        deliveries.forEach(d -> {
            streamProcessor.processDelivery(d);
            fantasyEngine.ingestDelivery(d);
        });

        fantasyEngine.finalizeMatchScoring();

        // Export to partitioned columnar storage
        storageService.writePartitionedDeliveries("2024", "M14", 1,
                deliveries.stream().filter(d -> d.getInnings() == 1).toList());
        storageService.writePartitionedDeliveries("2024", "M14", 2,
                deliveries.stream().filter(d -> d.getInnings() == 2).toList());

        Map<String, Object> summary = Map.of(
                "matchId", "2024-M14",
                "totalDeliveriesReplayed", deliveries.size(),
                "finalScore", streamProcessor.getCurrentScore(),
                "playersEvaluated", fantasyEngine.getAllPlayerStats().size(),
                "phaseMetricsInnings1", storageService.queryPhaseMetrics("M14", 1),
                "phaseMetricsInnings2", storageService.queryPhaseMetrics("M14", 2)
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/scores")
    public ResponseEntity<Map<Long, Object>> getLiveScores() {
        return ResponseEntity.ok(Map.copyOf(fantasyEngine.getAllPlayerStats()));
    }
}
