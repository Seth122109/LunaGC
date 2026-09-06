package emu.grasscutter.game.ability;

import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.data.binout.AbilityModifier;
import java.util.Arrays;

/** Classifies resource modifiers that represent successful shield creation. */
final class AngelosShieldTrigger {
    private AngelosShieldTrigger() {}

    static boolean isShieldCreationModifier(AbilityModifier modifierData) {
        return modifierData != null
                && modifierData.modifierMixins != null
                && Arrays.stream(modifierData.modifierMixins)
                        .anyMatch(
                                mixin ->
                                        mixin != null
                                                && (mixin.type
                                                                == AbilityMixinData.Type.ShieldBarMixin
                                                        || mixin.type
                                                                == AbilityMixinData.Type.GlobalMainShieldMixin));
    }
}
