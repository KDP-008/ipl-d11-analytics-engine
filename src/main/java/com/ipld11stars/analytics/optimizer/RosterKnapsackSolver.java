package com.ipld11stars.analytics.optimizer;

import com.ipld11stars.analytics.model.FantasyRoster;
import com.ipld11stars.analytics.model.PlayerMatchStats;

import java.util.List;
import java.util.Map;

/**
 * Combinatorial 100-credit roster knapsack / Mixed Integer Linear Programming (MILP) solver.
 *
 * Objective:
 *   Maximize: Sum(P_i * x_i) + P_c + 0.5 * P_vc
 * Constraints:
 *   - Sum(x_i) == 11
 *   - Sum(C_i * x_i) <= 100.0
 *   - Team counts <= 7 per IPL franchise
 *   - Role counts: WK 1-4, BAT 3-6, AR 1-4, BOWL 3-6
 *   - Exactly 1 Captain and 1 Vice-Captain (c != vc)
 * Target runtime: < 50ms for a 30-player squad.
 */
public interface RosterKnapsackSolver {

    /**
     * Finds the optimal 11-player roster maximizing fantasy points.
     *
     * @param availablePlayers Candidate players from match squad
     * @param playerProjections Map of player ID to expected/actual fantasy points
     * @param maxBudget Maximum credit budget (default 100.0)
     * @return Provably optimal FantasyRoster
     */
    FantasyRoster solveOptimalRoster(List<PlayerMatchStats> availablePlayers,
                                     Map<Long, Double> playerProjections,
                                     double maxBudget);
}
