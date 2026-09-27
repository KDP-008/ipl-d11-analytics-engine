package com.ipld11stars.analytics.optimizer;

import com.ipld11stars.analytics.model.FantasyRoster;
import com.ipld11stars.analytics.model.PlayerMatchStats;
import com.ipld11stars.analytics.model.PlayerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Baseline roster optimizer implementation.
 * Provides a greedy-with-backtracking heuristic baseline for agents to benchmark and replace
 * with a high-performance Branch-and-Bound / MILP solver.
 */
@Service
public class DefaultRosterKnapsackSolver implements RosterKnapsackSolver {

    private static final Logger log = LoggerFactory.getLogger(DefaultRosterKnapsackSolver.class);

    @Override
    public FantasyRoster solveOptimalRoster(List<PlayerMatchStats> availablePlayers,
                                             Map<Long, Double> playerProjections,
                                             double maxBudget) {
        long startTime = System.currentTimeMillis();
        log.info("Starting roster knapsack optimization across {} candidate players (budget={})",
                availablePlayers.size(), maxBudget);

        if (availablePlayers == null || availablePlayers.size() < 11) {
            throw new IllegalArgumentException("Cannot form 11-player roster with fewer than 11 candidate players");
        }

        Map<Long, PlayerMatchStats> playerRegistry = availablePlayers.stream()
                .collect(Collectors.toMap(PlayerMatchStats::getPlayerId, p -> p));

        // Sort players by value-per-credit ratio (greedy upper-bound ordering)
        List<PlayerMatchStats> sorted = new ArrayList<>(availablePlayers);
        sorted.sort((p1, p2) -> {
            double pts1 = playerProjections.getOrDefault(p1.getPlayerId(), (double) p1.getTotalFantasyPoints());
            double pts2 = playerProjections.getOrDefault(p2.getPlayerId(), (double) p2.getTotalFantasyPoints());
            double ratio1 = pts1 / Math.max(p1.getCredit(), 1.0);
            double ratio2 = pts2 / Math.max(p2.getCredit(), 1.0);
            return Double.compare(ratio2, ratio1);
        });

        // Search for best valid combination using recursive branch & bound
        BestRosterTracker tracker = new BestRosterTracker();
        searchCombinations(sorted, playerProjections, 0, new ArrayList<>(), 0.0, maxBudget, tracker);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Roster optimization completed in {}ms, best score={}", duration, tracker.bestPoints);

        if (tracker.bestRoster == null) {
            throw new IllegalStateException("No valid roster found satisfying all constraints");
        }

        return tracker.bestRoster;
    }

    private static class BestRosterTracker {
        double bestPoints = -1.0;
        FantasyRoster bestRoster = null;
    }

    private void searchCombinations(List<PlayerMatchStats> players,
                                    Map<Long, Double> projections,
                                    int index,
                                    List<PlayerMatchStats> current,
                                    double currentCredits,
                                    double maxBudget,
                                    BestRosterTracker tracker) {
        // Prune if budget exceeded
        if (currentCredits > maxBudget) return;

        // Prune if remaining players cannot form 11
        int remainingNeeded = 11 - current.size();
        if (players.size() - index < remainingNeeded) return;

        // Found 11 players
        if (current.size() == 11) {
            evaluateRoster(current, projections, currentCredits, tracker);
            return;
        }

        // Branch 1: include player
        PlayerMatchStats p = players.get(index);
        current.add(p);
        searchCombinations(players, projections, index + 1, current, currentCredits + p.getCredit(), maxBudget, tracker);
        current.remove(current.size() - 1);

        // Branch 2: exclude player
        searchCombinations(players, projections, index + 1, current, currentCredits, maxBudget, tracker);
    }

    private void evaluateRoster(List<PlayerMatchStats> roster,
                                Map<Long, Double> projections,
                                double totalCredits,
                                BestRosterTracker tracker) {
        // Check role and team counts
        Map<PlayerRole, Integer> roleCounts = new HashMap<>();
        Map<String, Integer> teamCounts = new HashMap<>();

        for (PlayerMatchStats p : roster) {
            roleCounts.merge(p.getRole(), 1, Integer::sum);
            teamCounts.merge(p.getTeam(), 1, Integer::sum);
        }

        for (int count : teamCounts.values()) {
            if (count > 7) return; // Exceeds max 7 per franchise
        }

        for (PlayerRole role : PlayerRole.values()) {
            int count = roleCounts.getOrDefault(role, 0);
            if (count < role.getMinRequired() || count > role.getMaxAllowed()) return;
        }

        // Select Captain and Vice-Captain (top 2 projected scorers)
        List<PlayerMatchStats> sortedByPoints = new ArrayList<>(roster);
        sortedByPoints.sort((p1, p2) -> {
            double pts1 = projections.getOrDefault(p1.getPlayerId(), (double) p1.getTotalFantasyPoints());
            double pts2 = projections.getOrDefault(p2.getPlayerId(), (double) p2.getTotalFantasyPoints());
            return Double.compare(pts2, pts1);
        });

        PlayerMatchStats c = sortedByPoints.get(0);
        PlayerMatchStats vc = sortedByPoints.get(1);

        double basePoints = roster.stream()
                .mapToDouble(p -> projections.getOrDefault(p.getPlayerId(), (double) p.getTotalFantasyPoints()))
                .sum();
        double cPts = projections.getOrDefault(c.getPlayerId(), (double) c.getTotalFantasyPoints());
        double vcPts = projections.getOrDefault(vc.getPlayerId(), (double) vc.getTotalFantasyPoints());

        double totalScore = basePoints + cPts + (0.5 * vcPts);

        if (totalScore > tracker.bestPoints) {
            tracker.bestPoints = totalScore;
            FantasyRoster r = new FantasyRoster();
            r.setPlayerIds(roster.stream().map(PlayerMatchStats::getPlayerId).collect(Collectors.toList()));
            r.setCaptainId(c.getPlayerId());
            r.setViceCaptainId(vc.getPlayerId());
            r.setTotalCredits(Math.round(totalCredits * 10.0) / 10.0);
            r.setTotalFantasyPoints(totalScore);
            tracker.bestRoster = r;
        }
    }
}
