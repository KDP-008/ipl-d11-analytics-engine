package com.ipld11stars.analytics.storage;

import com.ipld11stars.analytics.model.DeliveryEvent;

import java.util.List;
import java.util.Map;

/**
 * Analytical columnar storage interface for telemetry archiving and OLAP phase analytics.
 */
public interface ColumnarStorageService {

    /**
     * Appends a batch of delivery events to partitioned storage (season/match/innings).
     */
    void writePartitionedDeliveries(String season, String matchId, int innings, List<DeliveryEvent> deliveries);

    /**
     * Computes phase metrics for a match innings:
     * - Powerplay (overs 1 to 6)
     * - Middle (overs 7 to 15)
     * - Death (overs 16 to 20)
     */
    Map<String, Object> queryPhaseMetrics(String matchId, int innings);
}
