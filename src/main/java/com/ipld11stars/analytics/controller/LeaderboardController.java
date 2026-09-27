package com.ipld11stars.analytics.controller;

import com.ipld11stars.analytics.model.LeaderboardEntry;
import com.ipld11stars.analytics.ranking.LiveLeaderboardEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST endpoint for live contest leaderboard operations.
 */
@RestController
@RequestMapping("/api/analytics/leaderboard")
public class LeaderboardController {

    private final LiveLeaderboardEngine leaderboardEngine;

    public LeaderboardController(LiveLeaderboardEngine leaderboardEngine) {
        this.leaderboardEngine = leaderboardEngine;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardEngine.getTopK(limit));
    }

    @GetMapping("/entry/{entryId}/rank")
    public ResponseEntity<Map<String, Object>> getEntryRank(@PathVariable String entryId) {
        int rank = leaderboardEngine.getRank(entryId);
        if (rank < 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("entryId", entryId, "rank", rank));
    }

    @PostMapping("/entry")
    public ResponseEntity<Void> registerEntry(@RequestBody LeaderboardEntry entry) {
        if (entry.getSubmittedTimestampMs() == 0) {
            entry.setSubmittedTimestampMs(System.currentTimeMillis());
        }
        leaderboardEngine.registerEntry(entry);
        return ResponseEntity.ok().build();
    }
}
