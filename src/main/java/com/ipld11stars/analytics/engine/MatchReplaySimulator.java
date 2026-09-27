package com.ipld11stars.analytics.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipld11stars.analytics.model.DeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deterministic offline match replay simulator reading Newline-Delimited JSON (NDJSON) feeds.
 */
@Component
public class MatchReplaySimulator {

    private static final Logger log = LoggerFactory.getLogger(MatchReplaySimulator.class);

    private final ObjectMapper objectMapper;
    private final Resource defaultFixture;
    private final List<DeliveryEvent> cachedDeliveries = new ArrayList<>();

    public MatchReplaySimulator(ObjectMapper objectMapper,
                                @Value("${analytics.telemetry.default-fixture:classpath:telemetry/match_284_csk_vs_mi.ndjson}")
                                Resource defaultFixture) {
        this.objectMapper = objectMapper;
        this.defaultFixture = defaultFixture;
    }

    /**
     * Loads and parses all delivery events from the NDJSON fixture resource.
     */
    public synchronized List<DeliveryEvent> loadAllDeliveries() {
        if (!cachedDeliveries.isEmpty()) {
            return Collections.unmodifiableList(cachedDeliveries);
        }

        log.info("Loading NDJSON match telemetry from fixture: {}", defaultFixture);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(defaultFixture.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    DeliveryEvent event = objectMapper.readValue(line, DeliveryEvent.class);
                    cachedDeliveries.add(event);
                    count++;
                }
            }
            log.info("Successfully loaded {} match delivery events from fixture", count);
        } catch (Exception e) {
            log.error("Failed to load NDJSON fixture: {}", e.getMessage(), e);
            throw new IllegalStateException("Could not load match telemetry fixture", e);
        }

        return Collections.unmodifiableList(cachedDeliveries);
    }

    /**
     * Total number of loaded delivery events.
     */
    public int getDeliveryCount() {
        return loadAllDeliveries().size();
    }
}
