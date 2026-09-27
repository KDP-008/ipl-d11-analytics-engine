package com.ipld11stars.analytics.engine;

import com.ipld11stars.analytics.model.DeliveryEvent;
import com.ipld11stars.analytics.model.DismissalType;
import com.ipld11stars.analytics.model.ExtrasType;
import com.ipld11stars.analytics.model.PlayerMatchStats;
import com.ipld11stars.analytics.model.PlayerRole;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Baseline incremental fantasy point calculator.
 */
@Service
public class DefaultFantasyPointEngine implements FantasyPointEngine {

    private final Map<Long, PlayerMatchStats> playerStatsMap = new ConcurrentHashMap<>();
    private final Map<String, OverTracker> overTrackers = new HashMap<>();

    private static class OverTracker {
        int legalBalls = 0;
        int bowlerRuns = 0;
    }

    @Override
    public synchronized void reset() {
        playerStatsMap.clear();
        overTrackers.clear();
    }

    @Override
    public synchronized void ingestDelivery(DeliveryEvent delivery) {
        if (delivery == null) return;

        // Ensure striker stats exist
        PlayerMatchStats striker = getOrCreate(delivery.getStrikerId(), delivery.getStrikerName());
        // Ensure bowler stats exist
        PlayerMatchStats bowler = getOrCreate(delivery.getBowlerId(), delivery.getBowlerName());

        // 1. Batting update
        if (delivery.isLegalDelivery()) {
            striker.setBallsFaced(striker.getBallsFaced() + 1);
        }
        striker.setRunsScored(striker.getRunsScored() + delivery.getRunsBatter());
        if (delivery.getRunsBatter() == 4) {
            striker.setFours(striker.getFours() + 1);
        } else if (delivery.getRunsBatter() == 6) {
            striker.setSixes(striker.getSixes() + 1);
        }

        // 2. Bowling update
        if (delivery.isLegalDelivery()) {
            bowler.setLegalBallsBowled(bowler.getLegalBallsBowled() + 1);
        }
        int conceded = delivery.getRunsBatter() +
                (delivery.getExtrasType().isBowlerCharged() ? delivery.getExtrasRuns() : 0);
        bowler.setRunsConceded(bowler.getRunsConceded() + conceded);

        // Track maiden over
        String overKey = delivery.getInnings() + "-" + delivery.getOverNumber() + "-" + delivery.getBowlerId();
        OverTracker ot = overTrackers.computeIfAbsent(overKey, k -> new OverTracker());
        if (delivery.isLegalDelivery()) {
            ot.legalBalls++;
        }
        ot.bowlerRuns += conceded;
        if (ot.legalBalls == 6 && ot.bowlerRuns == 0) {
            bowler.setMaidens(bowler.getMaidens() + 1);
        }

        // 3. Wicket update
        if (delivery.isWicket()) {
            PlayerMatchStats dismissed = getOrCreate(delivery.getDismissedPlayerId(), null);
            dismissed.setDismissed(true);

            DismissalType dt = delivery.getDismissalType();
            if (dt != null && dt.isCreditedToBowler()) {
                bowler.setWickets(bowler.getWickets() + 1);
                if (dt.isBowledLbwBonus()) {
                    bowler.setBowledLbwCount(bowler.getBowledLbwCount() + 1);
                }
            }

            if (dt == DismissalType.CAUGHT && delivery.getFielderId() != null) {
                PlayerMatchStats fielder = getOrCreate(delivery.getFielderId(), null);
                fielder.setCatches(fielder.getCatches() + 1);
            } else if (dt == DismissalType.STUMPED && delivery.getFielderId() != null) {
                PlayerMatchStats fielder = getOrCreate(delivery.getFielderId(), null);
                fielder.setStumpings(fielder.getStumpings() + 1);
            } else if (dt == DismissalType.RUN_OUT && delivery.getFielderId() != null) {
                PlayerMatchStats fielder = getOrCreate(delivery.getFielderId(), null);
                fielder.setRunOuts(fielder.getRunOuts() + 1);
            }
        }

        // Recompute running totals
        recomputePoints(striker);
        recomputePoints(bowler);
    }

    private PlayerMatchStats getOrCreate(Long playerId, String name) {
        if (playerId == null) return new PlayerMatchStats();
        return playerStatsMap.computeIfAbsent(playerId, id -> {
            PlayerMatchStats stats = new PlayerMatchStats();
            stats.setPlayerId(id);
            stats.setPlayerName(name != null ? name : "Player #" + id);
            stats.setRole(PlayerRole.BATSMAN); // Default fallback
            stats.setCredit(8.5);
            return stats;
        });
    }

    private void recomputePoints(PlayerMatchStats s) {
        int pts = 0;
        Map<String, Integer> b = new HashMap<>();

        // Batting
        int bat = s.getRunsScored() + s.getFours() + (s.getSixes() * 2);
        if (s.getRunsScored() >= 100) {
            bat += 16;
            b.put("milestoneRuns", 16);
        } else if (s.getRunsScored() >= 50) {
            bat += 8;
            b.put("milestoneRuns", 8);
        } else if (s.getRunsScored() >= 30) {
            bat += 4;
            b.put("milestoneRuns", 4);
        }

        if (s.isDismissed() && s.getRunsScored() == 0 && s.getBallsFaced() >= 1 && s.getRole() != PlayerRole.BOWLER) {
            bat -= 2;
            b.put("duckPenalty", -2);
        }
        b.put("battingTotal", bat);
        pts += bat;

        // Bowling
        int bowl = (s.getWickets() * 25) + (s.getBowledLbwCount() * 8) + (s.getMaidens() * 12);
        if (s.getWickets() >= 5) {
            bowl += 16;
            b.put("wicketMilestone", 16);
        } else if (s.getWickets() >= 4) {
            bowl += 8;
            b.put("wicketMilestone", 8);
        } else if (s.getWickets() >= 3) {
            bowl += 4;
            b.put("wicketMilestone", 4);
        }
        b.put("bowlingTotal", bowl);
        pts += bowl;

        // Fielding
        int field = (s.getCatches() * 8) + (s.getStumpings() * 12) + (s.getRunOuts() * 12);
        if (s.getCatches() >= 3) {
            field += 4;
        }
        b.put("fieldingTotal", field);
        pts += field;

        s.setPointsBreakdown(b);
        s.setTotalFantasyPoints(pts);
    }

    @Override
    public synchronized void finalizeMatchScoring() {
        for (PlayerMatchStats s : playerStatsMap.values()) {
            recomputePoints(s);
            int pts = s.getTotalFantasyPoints();

            // Strike rate adjustment (>= 10 balls)
            if (s.getBallsFaced() >= 10) {
                double sr = s.getStrikeRate();
                int srBonus = 0;
                if (sr > 170) srBonus = 6;
                else if (sr >= 150) srBonus = 4;
                else if (sr >= 130) srBonus = 2;
                else if (sr < 60) srBonus = -4;
                else if (sr <= 70) srBonus = -2;
                pts += srBonus;
                if (srBonus != 0) s.getPointsBreakdown().put("strikeRateBonus", srBonus);
            }

            // Economy rate adjustment (>= 12 legal balls / 2 overs)
            if (s.getLegalBallsBowled() >= 12) {
                double econ = s.getEconomyRate();
                int econBonus = 0;
                if (econ < 5.0) econBonus = 6;
                else if (econ < 6.0) econBonus = 4;
                else if (econ <= 7.0) econBonus = 2;
                else if (econ > 11.0) econBonus = -4;
                else if (econ >= 10.0) econBonus = -2;
                pts += econBonus;
                if (econBonus != 0) s.getPointsBreakdown().put("economyBonus", econBonus);
            }

            s.setTotalFantasyPoints(pts);
        }
    }

    @Override
    public Map<Long, PlayerMatchStats> getAllPlayerStats() {
        return Collections.unmodifiableMap(playerStatsMap);
    }

    @Override
    public PlayerMatchStats getPlayerStats(Long playerId) {
        return playerStatsMap.get(playerId);
    }
}
