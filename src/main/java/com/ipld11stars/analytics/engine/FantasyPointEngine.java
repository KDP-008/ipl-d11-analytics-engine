package com.ipld11stars.analytics.engine;

import com.ipld11stars.analytics.model.DeliveryEvent;
import com.ipld11stars.analytics.model.PlayerMatchStats;

import java.util.Map;

/**
 * Real-time incremental Fantasy Point System (FPS) engine.
 * Implements Dream11 T20 points matrix:
 * Batting:
 *  - +1 pt per run
 *  - +1 pt boundary four bonus
 *  - +2 pts six bonus
 *  - Milestones: +4 pts for 30 runs, +8 pts for 50 runs, +16 pts for 100 runs
 *  - Duck penalty: -2 pts (BAT/WK/AR dismissed for 0 after >= 1 ball)
 *  - Strike rate: evaluated for >= 10 balls (>170: +6, 150-170: +4, 130-149: +2, 60-70: -2, <60: -4)
 * Bowling:
 *  - +25 pts per wicket (excl. run-outs)
 *  - +8 pts Bowled / LBW bonus
 *  - Milestones: +4 pts for 3 wickets, +8 pts for 4 wickets, +16 pts for 5 wickets
 *  - Maiden over: +12 pts
 *  - Economy rate: evaluated for >= 2 overs (<5.0: +6, 5-5.99: +4, 6-7: +2, 10-11: -2, >11: -4)
 * Fielding:
 *  - +8 pts per catch (+4 bonus for 3 catches in a match)
 *  - +12 pts per stumping
 *  - +12 pts per direct hit run-out (+6 pts split)
 */
public interface FantasyPointEngine {

    /**
     * Resets scoring state for a new match.
     */
    void reset();

    /**
     * Incrementally updates player stats and points on a new delivery event.
     */
    void ingestDelivery(DeliveryEvent delivery);

    /**
     * Returns current accumulated statistics and points for all players.
     */
    Map<Long, PlayerMatchStats> getAllPlayerStats();

    /**
     * Returns statistics for a specific player.
     */
    PlayerMatchStats getPlayerStats(Long playerId);

    /**
     * Finalizes match-level adjustments (strike rate and economy bonuses) once match concludes.
     */
    void finalizeMatchScoring();
}
