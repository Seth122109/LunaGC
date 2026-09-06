package emu.grasscutter.game.ability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.ResourceLoader.OpenConfigData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.data.binout.OpenConfigEntry;
import emu.grasscutter.data.excels.EquipAffixData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.reliquary.ReliquarySetData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.utils.JsonUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.junit.jupiter.api.Test;

class AbilityEquipAffixSpecialsTest {
    private static final int SET_ID = 15044;
    private static final int FOUR_PIECE_AFFIX_ID = 2150441;

    @Test
    void resolvesMajoKaiFourPieceParametersFromEquippedAffixOpenConfig() {
        var setData = JsonUtils.decode(
                """
                { "setId": 15044, "setNeedNum": [2, 4], "equipAffixId": 215044 }
                """,
                ReliquarySetData.class);
        var affixData = JsonUtils.decode(
                """
                {
                  "affixId": 2150441,
                  "openConfig": "Relic_6.3_MajoKai",
                  "addProps": [],
                  "paramList": [6.0, 0.25, 0.20]
                }
                """,
                EquipAffixData.class);
        var openConfigData = JsonUtils.decode(
                """
                [
                  { "$type": "ModifyAbility", "abilityName": "Relic_6.3_MajoKai", "paramSpecial": "Dura", "paramDelta": "%1" },
                  { "$type": "ModifyAbility", "abilityName": "Relic_6.3_MajoKai", "paramSpecial": "Rate1", "paramDelta": "%2" },
                  { "$type": "ModifyAbility", "abilityName": "Relic_6.3_MajoKai", "paramSpecial": "Rate2", "paramDelta": "%3" }
                ]
                """,
                OpenConfigData[].class);
        var openConfig = new OpenConfigEntry("Relic_6.3_MajoKai", openConfigData);

        var previousSet = GameData.getReliquarySetDataMap().put(SET_ID, setData);
        var previousAffix = GameData.getEquipAffixDataMap().put(FOUR_PIECE_AFFIX_ID, affixData);
        var previousOpenConfig =
                GameData.getOpenConfigEntries().put("Relic_6.3_MajoKai", openConfig);

        try {
            Avatar avatar = avatarWithRelicCount(4);
            AbilityData abilityData = new AbilityData();
            abilityData.abilityName = "Relic_6.3_MajoKai";
            var specials = new Object2FloatOpenHashMap<String>();

            Ability.applyEquipAffixSpecials(abilityData, avatar, specials);

            assertEquals(6.0f, specials.getFloat("Dura"));
            assertEquals(0.25f, specials.getFloat("Rate1"));
            assertEquals(0.20f, specials.getFloat("Rate2"));
        } finally {
            restore(GameData.getReliquarySetDataMap(), SET_ID, previousSet);
            restore(GameData.getEquipAffixDataMap(), FOUR_PIECE_AFFIX_ID, previousAffix);
            if (previousOpenConfig == null) {
                GameData.getOpenConfigEntries().remove("Relic_6.3_MajoKai");
            } else {
                GameData.getOpenConfigEntries().put("Relic_6.3_MajoKai", previousOpenConfig);
            }
        }
    }

    @Test
    void doesNotResolveFourPieceParametersWithOnlyThreePieces() {
        var setData = JsonUtils.decode(
                """
                { "setId": 15044, "setNeedNum": [2, 4], "equipAffixId": 215044 }
                """,
                ReliquarySetData.class);
        var affixData = JsonUtils.decode(
                """
                {
                  "affixId": 2150441,
                  "openConfig": "Relic_6.3_MajoKai",
                  "addProps": [],
                  "paramList": [6.0, 0.25, 0.20]
                }
                """,
                EquipAffixData.class);
        var openConfigData = JsonUtils.decode(
                """
                [
                  { "$type": "ModifyAbility", "abilityName": "Relic_6.3_MajoKai", "paramSpecial": "Rate2", "paramDelta": "%3" }
                ]
                """,
                OpenConfigData[].class);
        var openConfig = new OpenConfigEntry("Relic_6.3_MajoKai", openConfigData);

        var previousSet = GameData.getReliquarySetDataMap().put(SET_ID, setData);
        var previousAffix = GameData.getEquipAffixDataMap().put(FOUR_PIECE_AFFIX_ID, affixData);
        var previousOpenConfig =
                GameData.getOpenConfigEntries().put("Relic_6.3_MajoKai", openConfig);

        try {
            Avatar avatar = avatarWithRelicCount(3);
            AbilityData abilityData = new AbilityData();
            abilityData.abilityName = "Relic_6.3_MajoKai";
            var specials = new Object2FloatOpenHashMap<String>();

            Ability.applyEquipAffixSpecials(abilityData, avatar, specials);

            assertEquals(0.0f, specials.getFloat("Rate2"));
        } finally {
            restore(GameData.getReliquarySetDataMap(), SET_ID, previousSet);
            restore(GameData.getEquipAffixDataMap(), FOUR_PIECE_AFFIX_ID, previousAffix);
            if (previousOpenConfig == null) {
                GameData.getOpenConfigEntries().remove("Relic_6.3_MajoKai");
            } else {
                GameData.getOpenConfigEntries().put("Relic_6.3_MajoKai", previousOpenConfig);
            }
        }
    }

    private static Avatar avatarWithRelicCount(int relicCount) {
        Avatar avatar = new Avatar();
        ItemData itemData = JsonUtils.decode(
                """
                { "id": 90000001, "itemType": "ITEM_RELIQUARY", "setId": 15044 }
                """,
                ItemData.class);
        for (int slot = 1; slot <= relicCount; slot++) {
            GameItem item = new GameItem();
            item.setItemData(itemData);
            avatar.getEquips().put(slot, item);
        }
        return avatar;
    }

    private static <T> void restore(
            it.unimi.dsi.fastutil.ints.Int2ObjectMap<T> map, int key, T previous) {
        if (previous == null) {
            map.remove(key);
        } else {
            map.put(key, previous);
        }
    }
}
