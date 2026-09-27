package com.ipld11stars.analytics.model;

/**
 * Cricket playing roles with Dream11 standard roster min/max cardinality constraints.
 */
public enum PlayerRole {
    WICKET_KEEPER(1, 4),
    BATSMAN(3, 6),
    ALL_ROUNDER(1, 4),
    BOWLER(3, 6);

    private final int minRequired;
    private final int maxAllowed;

    PlayerRole(int minRequired, int maxAllowed) {
        this.minRequired = minRequired;
        this.maxAllowed = maxAllowed;
    }

    public int getMinRequired() {
        return minRequired;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}
