package com.ipld11stars.analytics.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 11-player fantasy team selection with Captain, Vice-Captain, and 100-credit budget validator.
 */
public class FantasyRoster {

    public static final double MAX_CREDIT_BUDGET = 100.0;
    public static final int REQUIRED_ROSTER_SIZE = 11;
    public static final int MAX_PLAYERS_PER_TEAM = 7;

    private List<Long> playerIds = new ArrayList<>();
    private Long captainId;
    private Long viceCaptainId;
    private double totalCredits;
    private double totalFantasyPoints;

    public FantasyRoster() {
    }

    public FantasyRoster(List<Long> playerIds, Long captainId, Long viceCaptainId) {
        this.playerIds = playerIds != null ? playerIds : new ArrayList<>();
        this.captainId = captainId;
        this.viceCaptainId = viceCaptainId;
    }

    public List<Long> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<Long> playerIds) { this.playerIds = playerIds; }

    public Long getCaptainId() { return captainId; }
    public void setCaptainId(Long captainId) { this.captainId = captainId; }

    public Long getViceCaptainId() { return viceCaptainId; }
    public void setViceCaptainId(Long viceCaptainId) { this.viceCaptainId = viceCaptainId; }

    public double getTotalCredits() { return totalCredits; }
    public void setTotalCredits(double totalCredits) { this.totalCredits = totalCredits; }

    public double getTotalFantasyPoints() { return totalFantasyPoints; }
    public void setTotalFantasyPoints(double totalFantasyPoints) { this.totalFantasyPoints = totalFantasyPoints; }

    /**
     * Validates whether this roster satisfies all Dream11 rules.
     */
    public boolean isValid(Map<Long, PlayerMatchStats> playerRegistry) {
        if (playerIds == null || playerIds.size() != REQUIRED_ROSTER_SIZE) {
            return false;
        }

        // Distinct players
        long uniqueCount = playerIds.stream().distinct().count();
        if (uniqueCount != REQUIRED_ROSTER_SIZE) {
            return false;
        }

        // Captain & VC must be in roster and distinct
        if (captainId == null || viceCaptainId == null || captainId.equals(viceCaptainId)) {
            return false;
        }
        if (!playerIds.contains(captainId) || !playerIds.contains(viceCaptainId)) {
            return false;
        }

        // Check budget and role counts
        double creditsSum = 0.0;
        Map<PlayerRole, Integer> roleCounts = new EnumMap<>(PlayerRole.class);
        Map<String, Integer> teamCounts = new HashMap<>();

        for (Long pid : playerIds) {
            PlayerMatchStats player = playerRegistry.get(pid);
            if (player == null) {
                return false;
            }
            creditsSum += player.getCredit();
            roleCounts.merge(player.getRole(), 1, Integer::sum);
            teamCounts.merge(player.getTeam(), 1, Integer::sum);
        }

        if (creditsSum > MAX_CREDIT_BUDGET) {
            return false;
        }

        // Check franchise team limit
        for (int count : teamCounts.values()) {
            if (count > MAX_PLAYERS_PER_TEAM) {
                return false;
            }
        }

        // Check role bounds
        for (PlayerRole role : PlayerRole.values()) {
            int count = roleCounts.getOrDefault(role, 0);
            if (count < role.getMinRequired() || count > role.getMaxAllowed()) {
                return false;
            }
        }

        return true;
    }
}
