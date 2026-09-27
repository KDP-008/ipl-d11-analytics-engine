package com.ipld11stars.analytics;

import com.ipld11stars.analytics.model.FantasyRoster;
import com.ipld11stars.analytics.model.PlayerMatchStats;
import com.ipld11stars.analytics.model.PlayerRole;
import com.ipld11stars.analytics.optimizer.DefaultRosterKnapsackSolver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RosterKnapsackSolverBaselineTest {

    @Test
    void testSolveRosterValidatesConstraints() {
        DefaultRosterKnapsackSolver solver = new DefaultRosterKnapsackSolver();

        List<PlayerMatchStats> candidates = new ArrayList<>();
        Map<Long, Double> projections = new HashMap<>();

        // Create 15 players (7 CSK, 8 MI) satisfying role bounds
        // WK (2)
        candidates.add(createPlayer(101L, "MS Dhoni", "CSK", PlayerRole.WICKET_KEEPER, 8.5, 45.0, projections));
        candidates.add(createPlayer(201L, "Ishan Kishan", "MI", PlayerRole.WICKET_KEEPER, 8.5, 60.0, projections));

        // BAT (5)
        candidates.add(createPlayer(102L, "Ruturaj Gaikwad", "CSK", PlayerRole.BATSMAN, 9.5, 80.0, projections));
        candidates.add(createPlayer(103L, "Ajinkya Rahane", "CSK", PlayerRole.BATSMAN, 8.0, 30.0, projections));
        candidates.add(createPlayer(202L, "Rohit Sharma", "MI", PlayerRole.BATSMAN, 10.0, 75.0, projections));
        candidates.add(createPlayer(203L, "Suryakumar Yadav", "MI", PlayerRole.BATSMAN, 10.0, 85.0, projections));
        candidates.add(createPlayer(204L, "Tilak Varma", "MI", PlayerRole.BATSMAN, 8.5, 40.0, projections));

        // AR (3)
        candidates.add(createPlayer(104L, "Ravindra Jadeja", "CSK", PlayerRole.ALL_ROUNDER, 9.0, 65.0, projections));
        candidates.add(createPlayer(105L, "Shivam Dube", "CSK", PlayerRole.ALL_ROUNDER, 9.0, 50.0, projections));
        candidates.add(createPlayer(205L, "Hardik Pandya", "MI", PlayerRole.ALL_ROUNDER, 9.5, 55.0, projections));

        // BOWL (5)
        candidates.add(createPlayer(106L, "Shardul Thakur", "CSK", PlayerRole.BOWLER, 8.0, 35.0, projections));
        candidates.add(createPlayer(107L, "Mustafizur Rahman", "CSK", PlayerRole.BOWLER, 8.5, 45.0, projections));
        candidates.add(createPlayer(206L, "Jasprit Bumrah", "MI", PlayerRole.BOWLER, 9.5, 70.0, projections));
        candidates.add(createPlayer(207L, "Gerald Coetzee", "MI", PlayerRole.BOWLER, 8.5, 40.0, projections));
        candidates.add(createPlayer(208L, "Shreyas Gopal", "MI", PlayerRole.BOWLER, 7.5, 25.0, projections));

        FantasyRoster roster = solver.solveOptimalRoster(candidates, projections, 100.0);

        assertThat(roster).isNotNull();
        assertThat(roster.getPlayerIds()).hasSize(11);
        assertThat(roster.getTotalCredits()).isLessThanOrEqualTo(100.0);
        assertThat(roster.getCaptainId()).isNotNull();
        assertThat(roster.getViceCaptainId()).isNotNull();
        assertThat(roster.getCaptainId()).isNotEqualTo(roster.getViceCaptainId());
        assertThat(roster.getPlayerIds()).contains(roster.getCaptainId());
        assertThat(roster.getPlayerIds()).contains(roster.getViceCaptainId());
    }

    private PlayerMatchStats createPlayer(Long id, String name, String team, PlayerRole role,
                                         double credit, double points, Map<Long, Double> projections) {
        PlayerMatchStats p = new PlayerMatchStats(id, name, team, role, credit);
        p.setTotalFantasyPoints((int) points);
        projections.put(id, points);
        return p;
    }
}
