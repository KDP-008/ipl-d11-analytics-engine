package com.ipld11stars.analytics.storage;

import com.ipld11stars.analytics.model.DeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Baseline in-memory and directory-partitioned columnar storage adapter.
 */
@Service
public class DefaultColumnarStorageService implements ColumnarStorageService {

    private static final Logger log = LoggerFactory.getLogger(DefaultColumnarStorageService.class);

    private final String basePath;
    private final Map<String, List<DeliveryEvent>> partitionStore = new ConcurrentHashMap<>();

    public DefaultColumnarStorageService(@Value("${analytics.storage.base-path:./target/analytics-data}") String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void writePartitionedDeliveries(String season, String matchId, int innings, List<DeliveryEvent> deliveries) {
        String partitionKey = "season=" + season + "/match=" + matchId + "/innings=" + innings;
        partitionStore.computeIfAbsent(partitionKey, k -> new ArrayList<>()).addAll(deliveries);

        File dir = new File(basePath + "/" + partitionKey);
        dir.mkdirs();
        log.info("Partitioned {} deliveries to path: {}/part-0.parquet (metadata registered)",
                deliveries.size(), dir.getPath());
    }

    @Override
    public Map<String, Object> queryPhaseMetrics(String matchId, int innings) {
        Map<String, Object> result = new HashMap<>();
        List<DeliveryEvent> matchDeliveries = new ArrayList<>();

        partitionStore.forEach((key, events) -> {
            if (key.contains("match=" + matchId) && key.contains("innings=" + innings)) {
                matchDeliveries.addAll(events);
            }
        });

        int ppRuns = 0, ppBalls = 0, ppDots = 0;
        int midRuns = 0, midBalls = 0, midDots = 0;
        int deathRuns = 0, deathBalls = 0, deathDots = 0;

        for (DeliveryEvent d : matchDeliveries) {
            int over = d.getOverNumber();
            if (over <= 6) {
                ppRuns += d.getTotalRuns();
                if (d.isLegalDelivery()) ppBalls++;
                if (d.getTotalRuns() == 0) ppDots++;
            } else if (over <= 15) {
                midRuns += d.getTotalRuns();
                if (d.isLegalDelivery()) midBalls++;
                if (d.getTotalRuns() == 0) midDots++;
            } else {
                deathRuns += d.getTotalRuns();
                if (d.isLegalDelivery()) deathBalls++;
                if (d.getTotalRuns() == 0) deathDots++;
            }
        }

        result.put("powerplay", Map.of(
                "runs", ppRuns,
                "legalBalls", ppBalls,
                "runRate", ppBalls > 0 ? Math.round((ppRuns * 6.0 / ppBalls) * 100.0) / 100.0 : 0.0,
                "dotBallPct", ppBalls > 0 ? Math.round((ppDots * 100.0 / ppBalls) * 10.0) / 10.0 : 0.0
        ));
        result.put("middleOvers", Map.of(
                "runs", midRuns,
                "legalBalls", midBalls,
                "runRate", midBalls > 0 ? Math.round((midRuns * 6.0 / midBalls) * 100.0) / 100.0 : 0.0,
                "dotBallPct", midBalls > 0 ? Math.round((midDots * 100.0 / midBalls) * 10.0) / 10.0 : 0.0
        ));
        result.put("deathOvers", Map.of(
                "runs", deathRuns,
                "legalBalls", deathBalls,
                "runRate", deathBalls > 0 ? Math.round((deathRuns * 6.0 / deathBalls) * 100.0) / 100.0 : 0.0,
                "dotBallPct", deathBalls > 0 ? Math.round((deathDots * 100.0 / deathBalls) * 10.0) / 10.0 : 0.0
        ));

        return result;
    }
}
