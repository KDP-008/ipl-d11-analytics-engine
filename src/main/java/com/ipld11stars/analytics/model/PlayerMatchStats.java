package com.ipld11stars.analytics.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Cumulative and phase-wise player statistics and fantasy points accumulator.
 */
public class PlayerMatchStats {

    private Long playerId;
    private String playerName;
    private String team;
    private PlayerRole role;
    private double credit;

    // Batting
    private int runsScored;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean isDismissed;

    // Bowling
    private int wickets;
    private int bowledLbwCount;
    private int legalBallsBowled;
    private int runsConceded;
    private int maidens;

    // Fielding
    private int catches;
    private int stumpings;
    private int runOuts;

    // Fantasy Points Breakdown
    private Map<String, Integer> pointsBreakdown = new HashMap<>();
    private int totalFantasyPoints;

    public PlayerMatchStats() {
    }

    public PlayerMatchStats(Long playerId, String playerName, String team, PlayerRole role, double credit) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.team = team;
        this.role = role;
        this.credit = credit;
    }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public PlayerRole getRole() { return role; }
    public void setRole(PlayerRole role) { this.role = role; }

    public double getCredit() { return credit; }
    public void setCredit(double credit) { this.credit = credit; }

    public int getRunsScored() { return runsScored; }
    public void setRunsScored(int runsScored) { this.runsScored = runsScored; }

    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }

    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }

    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }

    public boolean isDismissed() { return isDismissed; }
    public void setDismissed(boolean dismissed) { isDismissed = dismissed; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public int getBowledLbwCount() { return bowledLbwCount; }
    public void setBowledLbwCount(int bowledLbwCount) { this.bowledLbwCount = bowledLbwCount; }

    public int getLegalBallsBowled() { return legalBallsBowled; }
    public void setLegalBallsBowled(int legalBallsBowled) { this.legalBallsBowled = legalBallsBowled; }

    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }

    public int getMaidens() { return maidens; }
    public void setMaidens(int maidens) { this.maidens = maidens; }

    public int getCatches() { return catches; }
    public void setCatches(int catches) { this.catches = catches; }

    public int getStumpings() { return stumpings; }
    public void setStumpings(int stumpings) { this.stumpings = stumpings; }

    public int getRunOuts() { return runOuts; }
    public void setRunOuts(int runOuts) { this.runOuts = runOuts; }

    public Map<String, Integer> getPointsBreakdown() { return pointsBreakdown; }
    public void setPointsBreakdown(Map<String, Integer> pointsBreakdown) { this.pointsBreakdown = pointsBreakdown; }

    public int getTotalFantasyPoints() { return totalFantasyPoints; }
    public void setTotalFantasyPoints(int totalFantasyPoints) { this.totalFantasyPoints = totalFantasyPoints; }

    public double getStrikeRate() {
        if (ballsFaced == 0) return 0.0;
        return (runsScored * 100.0) / ballsFaced;
    }

    public double getEconomyRate() {
        if (legalBallsBowled == 0) return 0.0;
        double overs = legalBallsBowled / 6.0;
        return runsConceded / overs;
    }
}
