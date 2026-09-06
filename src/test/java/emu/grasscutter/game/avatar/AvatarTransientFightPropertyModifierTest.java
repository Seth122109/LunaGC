package emu.grasscutter.game.avatar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import emu.grasscutter.game.props.FightProperty;
import org.junit.jupiter.api.Test;

class AvatarTransientFightPropertyModifierTest {
    private static final String MAJOKAI_CRIT = "Relic_6.3_MajoKai:Actor_CriticalDelta";

    @Test
    void refreshesWithoutStackingAndRejectsStaleExpiry() {
        Avatar avatar = new Avatar();
        avatar.setFightProperty(FightProperty.FIGHT_PROP_CRITICAL, 0.793f);

        long first = avatar.upsertTransientFightPropertyModifier(
                MAJOKAI_CRIT, FightProperty.FIGHT_PROP_CRITICAL, 0.20f);
        long refreshed = avatar.upsertTransientFightPropertyModifier(
                MAJOKAI_CRIT, FightProperty.FIGHT_PROP_CRITICAL, 0.20f);

        assertEquals(0.993f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertFalse(avatar.removeTransientFightPropertyModifier(MAJOKAI_CRIT, first));
        assertEquals(0.993f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertTrue(avatar.removeTransientFightPropertyModifier(MAJOKAI_CRIT, refreshed));
        assertEquals(0.793f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
    }

    @Test
    void reappliesAcrossStatRecalculationAndRemovesOnlyItsDelta() {
        Avatar avatar = new Avatar();
        avatar.setFightProperty(FightProperty.FIGHT_PROP_CRITICAL, 0.793f);
        long generation = avatar.upsertTransientFightPropertyModifier(
                MAJOKAI_CRIT, FightProperty.FIGHT_PROP_CRITICAL, 0.20f);

        avatar.setFightProperty(FightProperty.FIGHT_PROP_CRITICAL, 0.80f);
        avatar.reapplyTransientFightPropertyModifiers();

        assertEquals(1.00f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertTrue(avatar.removeTransientFightPropertyModifier(MAJOKAI_CRIT, generation));
        assertEquals(0.80f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
    }
}
