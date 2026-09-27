package com.ipld11stars.analytics.ranking;

import com.ipld11stars.analytics.model.LeaderboardEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Baseline live leaderboard ranking implementation.
 * Agents are tasked with optimizing this to an O(log N) Fenwick Tree / Skip List.
 */
@Service
public class DefaultLiveLeaderboardEngine implements LiveLeaderboardEngine {

    private final Map<String, LeaderboardEntry> entryMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void registerEntry(LeaderboardEntry entry) {
        if (entry != null && entry.getEntryId() != null) {
            entryMap.put(entry.getEntryId(), entry);
        }
    }

    @Override
    public synchronized void updateScore(String entryId, double newTotalPoints) {
        LeaderboardEntry entry = entryMap.get(entryId);
        if (entry != null) {
            entry.setTotalPoints(newTotalPoints);
        }
    }

    @Override
    public synchronized List<LeaderboardEntry> getTopK(int limit) {
        List<LeaderboardEntry> sorted = new ArrayList<>(entryMap.values());
        // Sort descending by points, then ascending by submittedTimestamp
        sorted.sort((e1, e2) -> {
            int cmp = Double.compare(e2.getTotalPoints(), e1.getTotalPoints());
            if (cmp != 0) return cmp;
            return Long.compare(e1.getSubmittedTimestampMs(), e2.getSubmittedTimestampMs());
        });

        // Assign standard competition ranks ("1224" ranking)
        int currentRank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && sorted.get(i).getTotalPoints() < sorted.get(i - 1).getTotalPoints()) {
                currentRank = i + 1;
            }
            sorted.get(i).setRank(currentRank);
        }

        return sorted.stream().limit(Math.max(1, limit)).collect(Collectors.toList());
    }

    @Override
    public synchronized int getRank(String entryId) {
        List<LeaderboardEntry> ranked = getTopK(entryMap.size());
        for (LeaderboardEntry e : ranked) {
            if (e.getEntryId().equals(entryId)) {
                return e.getRank();
            }
        }
        return -1;
    }

    @Override
    public int getEntryCount() {
        return entryMap.size();
    }
}
