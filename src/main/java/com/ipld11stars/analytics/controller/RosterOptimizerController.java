package com.ipld11stars.analytics.controller;

import com.ipld11stars.analytics.engine.FantasyPointEngine;
import com.ipld11stars.analytics.model.FantasyRoster;
import com.ipld11stars.analytics.model.PlayerMatchStats;
import com.ipld11stars.analytics.optimizer.RosterKnapsackSolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST endpoint exposing combinatorial knapsack roster optimization.
 */
@RestController
@RequestMapping("/api/analytics/optimize")
public class RosterOptimizerController {

    private final RosterKnapsackSolver solver;
    private final FantasyPointEngine fantasyEngine;

    public RosterOptimizerController(RosterKnapsackSolver solver, FantasyPointEngine fantasyEngine) {
        this.solver = solver;
        this.fantasyEngine = fantasyEngine;
    }

    @GetMapping("/roster")
    public ResponseEntity<FantasyRoster> optimizeRoster(
            @RequestParam(defaultValue = "100.0") double maxBudget) {
        List<PlayerMatchStats> players = new ArrayList<>(fantasyEngine.getAllPlayerStats().values());
        if (players.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<Long, Double> projections = new HashMap<>();
        for (PlayerMatchStats p : players) {
            projections.put(p.getPlayerId(), (double) p.getTotalFantasyPoints());
        }

        FantasyRoster roster = solver.solveOptimalRoster(players, projections, maxBudget);
        return ResponseEntity.ok(roster);
    }
}
