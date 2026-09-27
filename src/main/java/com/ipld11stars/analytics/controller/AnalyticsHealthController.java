package com.ipld11stars.analytics.controller;

import com.ipld11stars.analytics.engine.MatchReplaySimulator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Health probe and runtime telemetry diagnostics endpoint.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsHealthController {

    private final MatchReplaySimulator simulator;

    public AnalyticsHealthController(MatchReplaySimulator simulator) {
        this.simulator = simulator;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        int deliveryCount = simulator.getDeliveryCount();
        Runtime runtime = Runtime.getRuntime();

        Map<String, Object> body = Map.of(
                "status", "UP",
                "service", "ipl-d11-analytics-engine",
                "timestamp", Instant.now().toString(),
                "telemetryFixtureLoaded", deliveryCount > 0,
                "loadedDeliveries", deliveryCount,
                "jvmVersion", System.getProperty("java.version"),
                "memory", Map.of(
                        "totalMb", runtime.totalMemory() / (1024 * 1024),
                        "freeMb", runtime.freeMemory() / (1024 * 1024),
                        "maxMb", runtime.maxMemory() / (1024 * 1024)
                )
        );

        return ResponseEntity.ok(body);
    }
}
