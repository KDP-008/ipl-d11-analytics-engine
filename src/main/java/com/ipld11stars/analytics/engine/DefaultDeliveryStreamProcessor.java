package com.ipld11stars.analytics.engine;

import com.ipld11stars.analytics.model.DeliveryEvent;
import com.ipld11stars.analytics.model.ExtrasType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Baseline state machine tracking cricket delivery state.
 */
@Service
public class DefaultDeliveryStreamProcessor implements DeliveryStreamProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultDeliveryStreamProcessor.class);

    private String currentMatchId;
    private int currentInnings = 1;
    private int currentOver = 1;
    private int legalBallsInOver = 0;
    private int totalInningsRuns = 0;
    private int totalInningsWickets = 0;
    private int totalLegalBallsInnings = 0;
    private boolean freeHitPending = false;

    private Long strikerId;
    private Long nonStrikerId;
    private Long previousOverBowlerId;
    private final Map<Long, Integer> bowlerOversMap = new HashMap<>();

    @Override
    public synchronized void reset(String matchId) {
        this.currentMatchId = matchId;
        this.currentInnings = 1;
        this.currentOver = 1;
        this.legalBallsInOver = 0;
        this.totalInningsRuns = 0;
        this.totalInningsWickets = 0;
        this.totalLegalBallsInnings = 0;
        this.freeHitPending = false;
        this.strikerId = null;
        this.nonStrikerId = null;
        this.previousOverBowlerId = null;
        this.bowlerOversMap.clear();
        log.info("Reset delivery stream state machine for match: {}", matchId);
    }

    @Override
    public synchronized int processDelivery(DeliveryEvent delivery) {
        if (delivery == null) return legalBallsInOver;

        // Innings change check
        if (delivery.getInnings() != currentInnings) {
            currentInnings = delivery.getInnings();
            currentOver = 1;
            legalBallsInOver = 0;
            totalInningsRuns = 0;
            totalInningsWickets = 0;
            totalLegalBallsInnings = 0;
            freeHitPending = false;
            previousOverBowlerId = null;
            bowlerOversMap.clear();
        }

        totalInningsRuns += delivery.getTotalRuns();

        if (delivery.isLegalDelivery()) {
            legalBallsInOver++;
            totalLegalBallsInnings++;
        }

        if (delivery.isWicket()) {
            totalInningsWickets++;
        }

        // Strike rotation on odd runs
        if (delivery.getRunsBatter() == 1 || delivery.getRunsBatter() == 3) {
            rotateStrike();
        }

        // Free hit logic
        if (delivery.getExtrasType() == ExtrasType.NO_BALL) {
            freeHitPending = true;
        } else if (delivery.isLegalDelivery()) {
            freeHitPending = false;
        }

        // Over completion
        if (legalBallsInOver >= 6) {
            previousOverBowlerId = delivery.getBowlerId();
            bowlerOversMap.merge(delivery.getBowlerId(), 1, Integer::sum);
            legalBallsInOver = 0;
            currentOver++;
            rotateStrike(); // Strike rotates at end of over
        }

        return legalBallsInOver;
    }

    private void rotateStrike() {
        Long temp = strikerId;
        strikerId = nonStrikerId;
        nonStrikerId = temp;
    }

    @Override
    public boolean isFreeHitPending() {
        return freeHitPending;
    }

    @Override
    public int getInningsLegalBalls() {
        return totalLegalBallsInnings;
    }

    @Override
    public String getCurrentScore() {
        return totalInningsRuns + "/" + totalInningsWickets + " (" + (totalLegalBallsInnings / 6) + "." + (totalLegalBallsInnings % 6) + " ov)";
    }
}
