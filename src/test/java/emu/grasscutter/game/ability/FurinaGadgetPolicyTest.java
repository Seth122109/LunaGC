package emu.grasscutter.game.ability;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FurinaGadgetPolicyTest {
    private static final long CAST_TIME = 1_000L;

    @Test
    void recognizesOnlyFurinaElementalSkillGadgets() {
        assertFalse(FurinaGadgetPolicy.isManagedGadget(41089009));
        for (int gadgetId = 41089010; gadgetId <= 41089014; gadgetId++) {
            assertTrue(FurinaGadgetPolicy.isManagedGadget(gadgetId));
        }
        assertFalse(FurinaGadgetPolicy.isManagedGadget(41089015));
    }

    @Test
    void recastingOusiaKeepsOneOfEachSalonMember() {
        assertTrue(FurinaGadgetPolicy.shouldReplace(41089011, 41089011));
        assertFalse(FurinaGadgetPolicy.shouldReplace(41089012, 41089011));
        assertFalse(FurinaGadgetPolicy.shouldReplace(41089013, 41089011));
        assertTrue(FurinaGadgetPolicy.shouldReplace(41089014, 41089011));
    }

    @Test
    void switchingToPneumaReplacesEverySkillSummonWithOneSinger() {
        for (int existing = 41089011; existing <= 41089014; existing++) {
            assertTrue(FurinaGadgetPolicy.shouldReplace(existing, 41089014));
        }
        assertFalse(FurinaGadgetPolicy.shouldReplace(41089010, 41089014));
    }

    @Test
    void recastingCreatesOnlyOneOrderController() {
        assertTrue(FurinaGadgetPolicy.shouldReplace(41089010, 41089010));
        assertTrue(FurinaGadgetPolicy.shouldReplace(41089011, 41089010));
        assertTrue(FurinaGadgetPolicy.shouldReplace(41089014, 41089010));
    }

    @Test
    void controllerAndSummonsExpireThirtySecondsAfterTheCast() {
        long controllerExpiry =
                FurinaGadgetPolicy.expiryTimeMillis(41089010, CAST_TIME, Long.MAX_VALUE);
        long switchedMemberExpiry =
                FurinaGadgetPolicy.expiryTimeMillis(41089011, CAST_TIME + 20_000, controllerExpiry);

        assertEquals(CAST_TIME + 30_000, controllerExpiry);
        assertEquals(controllerExpiry, switchedMemberExpiry);
        assertFalse(FurinaGadgetPolicy.isExpired(controllerExpiry - 1, controllerExpiry));
        assertTrue(FurinaGadgetPolicy.isExpired(controllerExpiry, controllerExpiry));
    }

    @Test
    void furinaGadgetInvokesAreNeverEchoedToTheirOwningClient() {
        assertFalse(FurinaGadgetPolicy.shouldEchoToOwner(41089010, 0));
        assertFalse(FurinaGadgetPolicy.shouldEchoToOwner(41089011, 0));
        assertFalse(FurinaGadgetPolicy.shouldEchoToOwner(0, 41089010));
        assertTrue(FurinaGadgetPolicy.shouldEchoToOwner(12345, 67890));
    }

    @Test
    void furinaSkillGadgetsAreNeverCreatedByTheServerActionExecutor() {
        for (int gadgetId = 41089010; gadgetId <= 41089014; gadgetId++) {
            assertFalse(FurinaGadgetPolicy.shouldCreateOnServer(gadgetId));
        }
        assertTrue(FurinaGadgetPolicy.shouldCreateOnServer(12345));
    }
}
