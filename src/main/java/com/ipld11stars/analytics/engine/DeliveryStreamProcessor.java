package com.ipld11stars.analytics.engine;

import com.ipld11stars.analytics.model.DeliveryEvent;

import java.util.List;

/**
 * State machine and stream processor for cricket match delivery telemetry.
 * Enforces cricket invariants:
 * - Legal deliveries vs Extras (Wide / No-Ball do not advance over ball count)
 * - Strike rotation on odd runs (1, 3) and at over boundaries
 * - Bowler over limits (max 4 overs in T20, no consecutive overs for the same bowler)
 * - Free-hit rule following front-foot no-balls (dismissals restricted to run-outs)
 */
public interface DeliveryStreamProcessor {

    /**
     * Resets the match state machine for a fresh match stream.
     */
    void reset(String matchId);

    /**
     * Ingests a single delivery event into the state machine.
     *
     * @param delivery Delivery event telemetry
     * @return current legal balls bowled in the over (0 to 6)
     */
    int processDelivery(DeliveryEvent delivery);

    /**
     * Batch processes an ordered list of delivery events.
     */
    default void processAll(List<DeliveryEvent> deliveries) {
        if (deliveries != null) {
            deliveries.forEach(this::processDelivery);
        }
    }

    /**
     * Returns whether the next delivery is under Free Hit status.
     */
    boolean isFreeHitPending();

    /**
     * Returns total legal deliveries bowled in the current innings.
     */
    int getInningsLegalBalls();

    /**
     * Returns current score in the format "runs/wickets".
     */
    String getCurrentScore();
}
