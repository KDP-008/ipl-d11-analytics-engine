package com.ipld11stars.analytics.model;

/**
 * Cricket dismissal modes.
 */
public enum DismissalType {
    BOWLED(true, true),
    CAUGHT(true, false),
    LBW(true, true),
    RUN_OUT(false, false),
    STUMPED(true, false),
    HIT_WICKET(true, false),
    RETIRED_HURT(false, false);

    private final boolean creditedToBowler;
    private final boolean bowledLbwBonus;

    DismissalType(boolean creditedToBowler, boolean bowledLbwBonus) {
        this.creditedToBowler = creditedToBowler;
        this.bowledLbwBonus = bowledLbwBonus;
    }

    public boolean isCreditedToBowler() {
        return creditedToBowler;
    }

    public boolean isBowledLbwBonus() {
        return bowledLbwBonus;
    }
}
