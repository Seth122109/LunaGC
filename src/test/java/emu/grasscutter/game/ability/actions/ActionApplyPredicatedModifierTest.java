package emu.grasscutter.game.ability.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.ResourceLoader.OpenConfigData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.data.binout.OpenConfigEntry;
import emu.grasscutter.data.excels.EquipAffixData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.reliquary.ReliquarySetData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.utils.JsonUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ActionApplyPredicatedModifierTest {
    @Test
    void eligibleLandedAttackResolvesResourceDerivedCriticalModifier() {
        Avatar avatar = avatarWithMajoKaiPieces(4);

        try (var ignored = installMajoKaiResources()) {
            var plan = ActionApplyPredicatedModifier.resolveLandedAttack(
                            avatar, true, false, 1.0f)
                    .orElseThrow();

            assertEquals(FightProperty.FIGHT_PROP_CRITICAL, plan.property());
            assertEquals(0.20f, plan.value());
            assertEquals(6.0f, plan.durationSeconds());
        }
    }

    @Test
    void eligibleLandedAttackAppliesCriticalAndSchedulesResourceDuration() {
        Avatar avatar = avatarWithMajoKaiPieces(4);
        avatar.setFightProperty(FightProperty.FIGHT_PROP_CRITICAL, 0.793f);
        avatar.setFightProperty(FightProperty.FIGHT_PROP_ATTACK_PERCENT, 1.25f);
        var expiries = new ArrayList<Runnable>();
        var scheduledTicks = new AtomicInteger();
        var published = new AtomicInteger();

        try (var ignored = installMajoKaiResources()) {
            assertTrue(ActionApplyPredicatedModifier.applyLandedAttack(
                    avatar,
                    true,
                    false,
                    1.0f,
                    (expiry, ticks) -> {
                        expiries.add(expiry);
                        scheduledTicks.set(ticks);
                    },
                    property -> {
                        assertEquals(FightProperty.FIGHT_PROP_CRITICAL, property);
                        published.incrementAndGet();
                    }));
        }

        assertEquals(0.993f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertEquals(
                1.25f,
                avatar.getFightProperty(FightProperty.FIGHT_PROP_ATTACK_PERCENT),
                0.00001f,
                "The server fallback must not duplicate the client-owned ATK branch");
        assertEquals(1, expiries.size());
        assertEquals(6, scheduledTicks.get());
        assertEquals(1, published.get());
    }

    @Test
    void ineligibleLandedAttacksDoNotMutateScheduleOrPublish() {
        var scheduled = new AtomicInteger();
        var published = new AtomicInteger();

        try (var ignored = installMajoKaiResources()) {
            Avatar inactive = avatarWithMajoKaiPieces(4);
            Avatar avatarTarget = avatarWithMajoKaiPieces(4);
            Avatar zeroDamage = avatarWithMajoKaiPieces(4);
            Avatar threePiece = avatarWithMajoKaiPieces(3);
            for (Avatar avatar : List.of(inactive, avatarTarget, zeroDamage, threePiece)) {
                avatar.setFightProperty(FightProperty.FIGHT_PROP_CRITICAL, 0.793f);
            }

            assertFalse(applyLandedAttack(inactive, false, false, 1.0f, scheduled, published));
            assertFalse(applyLandedAttack(avatarTarget, true, true, 1.0f, scheduled, published));
            assertFalse(applyLandedAttack(zeroDamage, true, false, 0.0f, scheduled, published));
            assertFalse(applyLandedAttack(threePiece, true, false, 1.0f, scheduled, published));

            for (Avatar avatar : List.of(inactive, avatarTarget, zeroDamage, threePiece)) {
                assertEquals(
                        0.793f,
                        avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL),
                        0.00001f);
            }
        }

        assertEquals(0, scheduled.get());
        assertEquals(0, published.get());
    }

    private static ResourceFixture installMajoKaiResources() {
        AbilityData abilityData = JsonUtils.decode(
                """
                {
                  "abilityName": "Relic_6.3_MajoKai",
                  "modifiers": {
                    "Relic_MajoKaiBuff_TriggerCD": {
                      "onAdded": [
                        {
                          "$type": "POIDCNKIFGD",
                          "modifierName": "UNIQUE_Relic_MajoKaiBuff1",
                          "predicates": [],
                          "target": "Self"
                        },
                        {
                          "$type": "POIDCNKIFGD",
                          "modifierName": "UNIQUE_Relic_MajoKaiBuff2",
                          "predicates": [{ "$type": "BJJDEAIEIGP", "target": "Target" }],
                          "target": "Self"
                        }
                      ]
                    },
                    "UNIQUE_Relic_MajoKaiBuff1": {
                      "duration": "Dura",
                      "properties": { "Actor_AttackRatio": "Rate1" }
                    },
                    "UNIQUE_Relic_MajoKaiBuff2": {
                      "duration": "Dura",
                      "properties": { "Actor_CriticalDelta": "Rate2" }
                    }
                  }
                }
                """,
                AbilityData.class);
        var setData = JsonUtils.decode(
                "{ \"setId\": 15044, \"setNeedNum\": [2, 4], \"equipAffixId\": 215044 }",
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

        var previousAbility = GameData.getAbilityDataMap().put("Relic_6.3_MajoKai", abilityData);
        var previousSet = GameData.getReliquarySetDataMap().put(15044, setData);
        var previousAffix = GameData.getEquipAffixDataMap().put(2150441, affixData);
        var previousOpenConfig =
                GameData.getOpenConfigEntries().put("Relic_6.3_MajoKai", openConfig);

        return new ResourceFixture(
                previousAbility, previousSet, previousAffix, previousOpenConfig);
    }

    @Test
    void resolvesOnlyTheBjjGuardedMajoKaiCriticalModifier() {
        AbilityData data = JsonUtils.decode(
                """
                {
                  "abilityName": "Relic_6.3_MajoKai",
                  "modifiers": {
                    "UNIQUE_Relic_MajoKaiBuff1": {
                      "duration": "Dura",
                      "properties": { "Actor_AttackRatio": "Rate1" }
                    },
                    "UNIQUE_Relic_MajoKaiBuff2": {
                      "duration": "Dura",
                      "properties": { "Actor_CriticalDelta": "Rate2" }
                    }
                  }
                }
                """,
                AbilityData.class);
        AbilityModifierAction attackAction = action(
                """
                {
                  "$type": "POIDCNKIFGD",
                  "modifierName": "UNIQUE_Relic_MajoKaiBuff1",
                  "predicates": [],
                  "target": "Self"
                }
                """);
        AbilityModifierAction criticalAction = action(
                """
                {
                  "$type": "POIDCNKIFGD",
                  "modifierName": "UNIQUE_Relic_MajoKaiBuff2",
                  "predicates": [{ "$type": "BJJDEAIEIGP", "target": "Target" }],
                  "target": "Self"
                }
                """);
        var specials = new Object2FloatOpenHashMap<String>();
        specials.put("Dura", 6.0f);
        specials.put("Rate1", 0.25f);
        specials.put("Rate2", 0.20f);

        assertTrue(ActionApplyPredicatedModifier.resolveFallback(
                        data, attackAction, specials, true).isEmpty(),
                "The server must not duplicate the client-working unconditional ATK branch");

        var plan = ActionApplyPredicatedModifier.resolveFallback(
                        data, criticalAction, specials, true)
                .orElseThrow();
        assertEquals(FightProperty.FIGHT_PROP_CRITICAL, plan.property());
        assertEquals(0.20f, plan.value());
        assertEquals(6.0f, plan.durationSeconds());
        assertEquals(
                "Relic_6.3_MajoKai:UNIQUE_Relic_MajoKaiBuff2:Actor_CriticalDelta",
                plan.key());
    }

    @Test
    void rejectsFailedPredicateAndNearMatchAbilities() {
        AbilityData data = JsonUtils.decode(
                """
                {
                  "abilityName": "Relic_6.3_MajoKai",
                  "modifiers": {
                    "UNIQUE_Relic_MajoKaiBuff2": {
                      "duration": "Dura",
                      "properties": { "Actor_CriticalDelta": "Rate2" }
                    }
                  }
                }
                """,
                AbilityData.class);
        AbilityModifierAction criticalAction = action(
                """
                {
                  "$type": "POIDCNKIFGD",
                  "modifierName": "UNIQUE_Relic_MajoKaiBuff2",
                  "predicates": [{ "$type": "BJJDEAIEIGP", "target": "Target" }],
                  "target": "Self"
                }
                """);
        var specials = new Object2FloatOpenHashMap<String>();
        specials.put("Dura", 6.0f);
        specials.put("Rate2", 0.20f);

        assertFalse(ActionApplyPredicatedModifier.resolveFallback(
                        data, criticalAction, specials, false).isPresent());

        data.abilityName = "Relic_6.3_NotMajoKai";
        assertFalse(ActionApplyPredicatedModifier.resolveFallback(
                        data, criticalAction, specials, true).isPresent());
    }

    @Test
    void registersTheRawActionAsServerOrchestration() {
        var annotation = ActionApplyPredicatedModifier.class.getAnnotation(AbilityAction.class);
        assertEquals(AbilityModifierAction.Type.POIDCNKIFGD, annotation.value());
        assertTrue(ActionApplyPredicatedModifier.isServerOrchestration(
                AbilityModifierAction.Type.POIDCNKIFGD));
        assertFalse(ActionApplyPredicatedModifier.isServerOrchestration(
                AbilityModifierAction.Type.ApplyModifier));
    }

    @Test
    void refreshSchedulesGenerationCheckedExpiryAndPublishesOnlyRealChanges() {
        Avatar avatar = new Avatar();
        avatar.setFightProperty(FightProperty.FIGHT_PROP_CRITICAL, 0.793f);
        var plan = new ActionApplyPredicatedModifier.ResolvedFightPropertyModifier(
                "Relic_6.3_MajoKai:UNIQUE_Relic_MajoKaiBuff2:Actor_CriticalDelta",
                FightProperty.FIGHT_PROP_CRITICAL,
                0.20f,
                6.0f);
        var expiries = new ArrayList<Runnable>();
        var published = new AtomicInteger();

        ActionApplyPredicatedModifier.applyFallback(
                avatar,
                plan,
                (expiry, ticks) -> {
                    assertEquals(6, ticks);
                    expiries.add(expiry);
                },
                property -> {
                    assertEquals(FightProperty.FIGHT_PROP_CRITICAL, property);
                    published.incrementAndGet();
                });
        ActionApplyPredicatedModifier.applyFallback(
                avatar,
                plan,
                (expiry, ticks) -> expiries.add(expiry),
                property -> published.incrementAndGet());

        assertEquals(0.993f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertEquals(2, published.get(), "Each apply/refresh publishes its current value");

        expiries.get(0).run();
        assertEquals(0.993f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertEquals(2, published.get(), "A stale expiry must not publish or remove the refresh");

        expiries.get(1).run();
        assertEquals(0.793f, avatar.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL), 0.00001f);
        assertEquals(3, published.get(), "The current expiry publishes the restored value");
    }

    private static AbilityModifierAction action(String json) {
        return JsonUtils.decode(json, AbilityModifierAction.class);
    }

    private static boolean applyLandedAttack(
            Avatar avatar,
            boolean effectivelyActive,
            boolean targetIsAvatar,
            float damage,
            AtomicInteger scheduled,
            AtomicInteger published) {
        return ActionApplyPredicatedModifier.applyLandedAttack(
                avatar,
                effectivelyActive,
                targetIsAvatar,
                damage,
                (expiry, ticks) -> scheduled.incrementAndGet(),
                property -> published.incrementAndGet());
    }

    private static Avatar avatarWithMajoKaiPieces(int relicCount) {
        Avatar avatar = new Avatar();
        ItemData itemData = JsonUtils.decode(
                "{ \"id\": 90000001, \"itemType\": \"ITEM_RELIQUARY\", \"setId\": 15044 }",
                ItemData.class);
        for (int slot = 1; slot <= relicCount; slot++) {
            GameItem item = new GameItem();
            item.setItemData(itemData);
            avatar.getEquips().put(slot, item);
        }
        return avatar;
    }

    private static <K, V> void restore(Map<K, V> map, K key, V previous) {
        if (previous == null) {
            map.remove(key);
        } else {
            map.put(key, previous);
        }
    }

    private static <V> void restore(
            it.unimi.dsi.fastutil.ints.Int2ObjectMap<V> map, int key, V previous) {
        if (previous == null) {
            map.remove(key);
        } else {
            map.put(key, previous);
        }
    }

    private record ResourceFixture(
            AbilityData previousAbility,
            ReliquarySetData previousSet,
            EquipAffixData previousAffix,
            OpenConfigEntry previousOpenConfig)
            implements AutoCloseable {
        @Override
        public void close() {
            restore(GameData.getAbilityDataMap(), "Relic_6.3_MajoKai", previousAbility);
            restore(GameData.getReliquarySetDataMap(), 15044, previousSet);
            restore(GameData.getEquipAffixDataMap(), 2150441, previousAffix);
            restore(
                    GameData.getOpenConfigEntries(),
                    "Relic_6.3_MajoKai",
                    previousOpenConfig);
        }
    }
}
