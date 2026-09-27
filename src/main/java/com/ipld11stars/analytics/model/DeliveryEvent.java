package com.ipld11stars.analytics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Immutable representation of a single cricket delivery event in a match stream.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryEvent {

    private String deliveryId;
    private String matchId;
    private int innings;
    private int overNumber;
    private int ballNumber;

    @com.fasterxml.jackson.annotation.JsonProperty("isLegalDelivery")
    private boolean isLegalDelivery;

    private Long strikerId;
    private String strikerName;
    private Long nonStrikerId;
    private Long bowlerId;
    private String bowlerName;

    private int runsBatter;
    private ExtrasType extrasType = ExtrasType.NONE;
    private int extrasRuns;
    private int totalRuns;

    @com.fasterxml.jackson.annotation.JsonProperty("isWicket")
    private boolean isWicket;
    private DismissalType dismissalType;
    private Long dismissedPlayerId;
    private Long fielderId;

    private String shotZone;

    @com.fasterxml.jackson.annotation.JsonProperty("isPowerplay")
    private boolean isPowerplay;
    private String timestampUtc;

    public DeliveryEvent() {
    }

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public int getInnings() { return innings; }
    public void setInnings(int innings) { this.innings = innings; }

    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }

    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }

    @com.fasterxml.jackson.annotation.JsonProperty("isLegalDelivery")
    public boolean isLegalDelivery() { return isLegalDelivery; }
    @com.fasterxml.jackson.annotation.JsonProperty("isLegalDelivery")
    public void setLegalDelivery(boolean legalDelivery) { isLegalDelivery = legalDelivery; }

    public Long getStrikerId() { return strikerId; }
    public void setStrikerId(Long strikerId) { this.strikerId = strikerId; }

    public String getStrikerName() { return strikerName; }
    public void setStrikerName(String strikerName) { this.strikerName = strikerName; }

    public Long getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(Long nonStrikerId) { this.nonStrikerId = nonStrikerId; }

    public Long getBowlerId() { return bowlerId; }
    public void setBowlerId(Long bowlerId) { this.bowlerId = bowlerId; }

    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }

    public int getRunsBatter() { return runsBatter; }
    public void setRunsBatter(int runsBatter) { this.runsBatter = runsBatter; }

    public ExtrasType getExtrasType() { return extrasType; }
    public void setExtrasType(ExtrasType extrasType) { this.extrasType = extrasType; }

    public int getExtrasRuns() { return extrasRuns; }
    public void setExtrasRuns(int extrasRuns) { this.extrasRuns = extrasRuns; }

    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }

    @com.fasterxml.jackson.annotation.JsonProperty("isWicket")
    public boolean isWicket() { return isWicket; }
    @com.fasterxml.jackson.annotation.JsonProperty("isWicket")
    public void setWicket(boolean wicket) { isWicket = wicket; }

    public DismissalType getDismissalType() { return dismissalType; }
    public void setDismissalType(DismissalType dismissalType) { this.dismissalType = dismissalType; }

    public Long getDismissedPlayerId() { return dismissedPlayerId; }
    public void setDismissedPlayerId(Long dismissedPlayerId) { this.dismissedPlayerId = dismissedPlayerId; }

    public Long getFielderId() { return fielderId; }
    public void setFielderId(Long fielderId) { this.fielderId = fielderId; }

    public String getShotZone() { return shotZone; }
    public void setShotZone(String shotZone) { this.shotZone = shotZone; }

    @com.fasterxml.jackson.annotation.JsonProperty("isPowerplay")
    public boolean isPowerplay() { return isPowerplay; }
    @com.fasterxml.jackson.annotation.JsonProperty("isPowerplay")
    public void setPowerplay(boolean powerplay) { isPowerplay = powerplay; }

    public String getTimestampUtc() { return timestampUtc; }
    public void setTimestampUtc(String timestampUtc) { this.timestampUtc = timestampUtc; }

    @Override
    public String toString() {
        return "DeliveryEvent{" +
                "id='" + deliveryId + '\'' +
                ", inn=" + innings +
                ", ov=" + overNumber + "." + ballNumber +
                ", striker=" + strikerName +
                ", bowler=" + bowlerName +
                ", runs=" + totalRuns +
                (isWicket ? ", WICKET=" + dismissalType : "") +
                '}';
    }
}
