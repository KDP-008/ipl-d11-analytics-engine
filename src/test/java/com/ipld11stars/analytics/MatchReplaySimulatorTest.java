package com.ipld11stars.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipld11stars.analytics.engine.MatchReplaySimulator;
import com.ipld11stars.analytics.model.DeliveryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MatchReplaySimulatorTest {

    @Test
    void testLoadAllDeliveriesFromNDJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("telemetry/match_284_csk_vs_mi.ndjson");
        MatchReplaySimulator simulator = new MatchReplaySimulator(mapper, resource);

        List<DeliveryEvent> deliveries = simulator.loadAllDeliveries();

        assertThat(deliveries).isNotEmpty();
        assertThat(deliveries.size()).isEqualTo(246);

        // Verify first delivery
        DeliveryEvent first = deliveries.get(0);
        assertThat(first.getMatchId()).isEqualTo("2024-M14");
        assertThat(first.getInnings()).isEqualTo(1);
        assertThat(first.getOverNumber()).isEqualTo(1);
        assertThat(first.getBallNumber()).isEqualTo(1);
        assertThat(first.isPowerplay()).isTrue();

        // Verify last delivery
        DeliveryEvent last = deliveries.get(deliveries.size() - 1);
        assertThat(last.getInnings()).isEqualTo(2);
        assertThat(last.getOverNumber()).isEqualTo(20);
        assertThat(last.isPowerplay()).isFalse();
    }
}
