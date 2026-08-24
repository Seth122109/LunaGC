package emu.grasscutter.game.ability;

/** Keeps Furina's mutually exclusive Elemental Skill summons within their in-game limits. */
public final class FurinaGadgetPolicy {
    public static final long SKILL_DURATION_MILLIS = 30_000L;
    public static final int ORDER_CONTROLLER = 41089010;
    public static final int GENTILHOMME_USHER = 41089011;
    public static final int SURINTENDANTE_CHEVALMARIN = 41089012;
    public static final int MADEMOISELLE_CRABALETTA = 41089013;
    public static final int SINGER_OF_MANY_WATERS = 41089014;

    private FurinaGadgetPolicy() {}

    public static boolean isManagedGadget(int gadgetId) {
        return gadgetId >= ORDER_CONTROLLER && gadgetId <= SINGER_OF_MANY_WATERS;
    }

    public static boolean shouldEchoToOwner(int invokingGadgetId, int createdGadgetId) {
        return !isManagedGadget(invokingGadgetId) && !isManagedGadget(createdGadgetId);
    }

    public static boolean shouldCreateOnServer(int gadgetId) {
        return !isManagedGadget(gadgetId);
    }

    public static long expiryTimeMillis(
            int gadgetId, long createdAtMillis, long ownerExpiryTimeMillis) {
        if (!isManagedGadget(gadgetId)) {
            return Long.MAX_VALUE;
        }
        if (gadgetId != ORDER_CONTROLLER && ownerExpiryTimeMillis != Long.MAX_VALUE) {
            return ownerExpiryTimeMillis;
        }
        return createdAtMillis + SKILL_DURATION_MILLIS;
    }

    public static boolean isExpired(long nowMillis, long expiryTimeMillis) {
        return nowMillis >= expiryTimeMillis;
    }

    public static boolean shouldReplace(int existingGadgetId, int incomingGadgetId) {
        if (!isManagedGadget(incomingGadgetId)) {
            return false;
        }

        if (incomingGadgetId == ORDER_CONTROLLER) {
            return isManagedGadget(existingGadgetId);
        }

        if (incomingGadgetId == SINGER_OF_MANY_WATERS) {
            return existingGadgetId >= GENTILHOMME_USHER
                    && existingGadgetId <= SINGER_OF_MANY_WATERS;
        }

        return existingGadgetId == incomingGadgetId
                || existingGadgetId == SINGER_OF_MANY_WATERS;
    }
}
