package emu.grasscutter.data.binout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.utils.JsonUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.junit.jupiter.api.Test;

class MajoKaiRelicResourceTest {
    @Test
    void parsesRawPredicatedModifierActionUsedByMajoKai() {
        AbilityModifierAction action = JsonUtils.decode(
                """
                {
                  "$type": "POIDCNKIFGD",
                  "modifierName": "UNIQUE_Relic_MajoKaiBuff2",
                  "predicates": [
                    { "$type": "BJJDEAIEIGP", "target": "Target" }
                  ],
                  "target": "Self"
                }
                """,
                AbilityModifierAction.class);

        assertNotNull(action);
        assertEquals(
                AbilityModifierAction.Type.POIDCNKIFGD,
                action.type,
                "The raw action must not disappear before server orchestration");
    }

    @Test
    void parsesDynamicCriticalDeltaUsedByMajoKai() {
        AbilityModifier modifier = JsonUtils.decode(
                """
                {
                  "duration": "Dura",
                  "properties": { "Actor_CriticalDelta": "Rate2" }
                }
                """,
                AbilityModifier.class);
        var specials = new Object2FloatOpenHashMap<String>();
        specials.put("Dura", 6f);
        specials.put("Rate2", 0.20f);

        assertNotNull(modifier);
        assertNotNull(modifier.properties);
        assertEquals(6f, modifier.duration.get(specials, 0f));
        assertEquals(0.20f, modifier.properties.Actor_CriticalDelta.get(specials, 0f));
    }
}
