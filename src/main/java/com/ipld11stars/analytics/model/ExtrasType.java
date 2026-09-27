package com.ipld11stars.analytics.model;

/**
 * Cricket delivery extras classification.
 */
public enum ExtrasType {
    NONE,
    WIDE,
    NO_BALL,
    BYE,
    LEG_BYE,
    PENALTY;

    public boolean isIllegalDelivery() {
        return this == WIDE || this == NO_BALL;
    }

    public boolean isBowlerCharged() {
        return this == WIDE || this == NO_BALL;
    }
}
