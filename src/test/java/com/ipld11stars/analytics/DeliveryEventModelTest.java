package com.ipld11stars.analytics;

import com.ipld11stars.analytics.model.DeliveryEvent;
import com.ipld11stars.analytics.model.DismissalType;
import com.ipld11stars.analytics.model.ExtrasType;
import com.ipld11stars.analytics.model.PlayerRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryEventModelTest {

    @Test
    void testDeliveryEventProperties() {
        DeliveryEvent event = new DeliveryEvent();
        event.setDeliveryId("2024-M14-I1-O1-B1");
        event.setMatchId("2024-M14");
        event.setInnings(1);
        event.setOverNumber(1);
        event.setBallNumber(1);
        event.setLegalDelivery(true);
        event.setStrikerId(101L);
        event.setBowlerId(201L);
        event.setRunsBatter(4);
        event.setTotalRuns(4);
        event.setPowerplay(true);

        assertThat(event.getDeliveryId()).isEqualTo("2024-M14-I1-O1-B1");
        assertThat(event.isLegalDelivery()).isTrue();
        assertThat(event.isPowerplay()).isTrue();
        assertThat(event.getTotalRuns()).isEqualTo(4);
    }

    @Test
    void testExtrasClassification() {
        assertThat(ExtrasType.WIDE.isIllegalDelivery()).isTrue();
        assertThat(ExtrasType.NO_BALL.isIllegalDelivery()).isTrue();
        assertThat(ExtrasType.BYE.isIllegalDelivery()).isFalse();
        assertThat(ExtrasType.NONE.isIllegalDelivery()).isFalse();
    }

    @Test
    void testDismissalClassification() {
        assertThat(DismissalType.BOWLED.isCreditedToBowler()).isTrue();
        assertThat(DismissalType.BOWLED.isBowledLbwBonus()).isTrue();
        assertThat(DismissalType.LBW.isBowledLbwBonus()).isTrue();
        assertThat(DismissalType.RUN_OUT.isCreditedToBowler()).isFalse();
        assertThat(DismissalType.CAUGHT.isBowledLbwBonus()).isFalse();
    }

    @Test
    void testPlayerRoleBounds() {
        assertThat(PlayerRole.WICKET_KEEPER.getMinRequired()).isEqualTo(1);
        assertThat(PlayerRole.WICKET_KEEPER.getMaxAllowed()).isEqualTo(4);

        assertThat(PlayerRole.BATSMAN.getMinRequired()).isEqualTo(3);
        assertThat(PlayerRole.BATSMAN.getMaxAllowed()).isEqualTo(6);

        assertThat(PlayerRole.ALL_ROUNDER.getMinRequired()).isEqualTo(1);
        assertThat(PlayerRole.ALL_ROUNDER.getMaxAllowed()).isEqualTo(4);

        assertThat(PlayerRole.BOWLER.getMinRequired()).isEqualTo(3);
        assertThat(PlayerRole.BOWLER.getMaxAllowed()).isEqualTo(6);
    }
}
