package com.ipld11stars.analytics.model;

/**
 * Entry on a contest leaderboard.
 */
public class LeaderboardEntry {

    private String entryId;
    private String userId;
    private String teamName;
    private FantasyRoster roster;
    private double totalPoints;
    private int rank;
    private long submittedTimestampMs;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String entryId, String userId, String teamName, FantasyRoster roster, long submittedTimestampMs) {
        this.entryId = entryId;
        this.userId = userId;
        this.teamName = teamName;
        this.roster = roster;
        this.submittedTimestampMs = submittedTimestampMs;
    }

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public FantasyRoster getRoster() { return roster; }
    public void setRoster(FantasyRoster roster) { this.roster = roster; }

    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public long getSubmittedTimestampMs() { return submittedTimestampMs; }
    public void setSubmittedTimestampMs(long submittedTimestampMs) { this.submittedTimestampMs = submittedTimestampMs; }
}
