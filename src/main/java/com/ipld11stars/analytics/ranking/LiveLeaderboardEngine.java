package com.ipld11stars.analytics.ranking;

import com.ipld11stars.analytics.model.LeaderboardEntry;

import java.util.List;

/**
 * High-concurrency live leaderboard ranking engine.
 * Requires O(log N) updates and O(log N) rank lookups.
 * Enforces standard competition ranking ("1224" ranking) with tie-breaks based on submission timestamp.
 */
public interface LiveLeaderboardEngine {

    /**
     * Registers a new entry into the contest leaderboard.
     */
    void registerEntry(LeaderboardEntry entry);

    /**
     * Updates an entry's total points and recalculates its rank.
     */
    void updateScore(String entryId, double newTotalPoints);

    /**
     * Retrieves the top-K leaderboard slice.
     */
    List<LeaderboardEntry> getTopK(int limit);

    /**
     * Retrieves the current rank of an entry.
     */
    int getRank(String entryId);

    /**
     * Total number of tracked entries.
     */
    int getEntryCount();
}
