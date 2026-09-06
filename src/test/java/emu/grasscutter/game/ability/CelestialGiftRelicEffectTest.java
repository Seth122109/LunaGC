package emu.grasscutter.game.ability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.ResourceLoader.OpenConfigData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.data.binout.OpenConfigEntry;
import emu.grasscutter.data.excels.EquipAffixData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.avatar.AvatarSkillData;
import emu.grasscutter.data.excels.avatar.AvatarSkillDepotData;
import emu.grasscutter.data.excels.reliquary.ReliquarySetData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.utils.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CelestialGiftRelicEffectTest {
    private static final int SET_ID = 15045;
    private static final int FOUR_PIECE_AFFIX_ID = 2150451;
    private static final String ABILITY_NAME = "Relic_6.6_Nicole";
    private static final int WEARER_ATTACK_SKILL = 13100;
    private static final int WEARER_ELEMENTAL_SKILL = 13101;
    private static final int WEARER_BURST = 13102;
    private static final int ACTIVE_ATTACK_SKILL = 22000;
    private static final int ACTIVE_ELEMENTAL_SKILL = 22001;
    private static final int ACTIVE_BURST = 22002;
    private static final int NEXT_ACTIVE_ATTACK_SKILL = 33000;
    private static final int NEXT_ACTIVE_ELEMENTAL_SKILL = 33001;
    private static final int NEXT_ACTIVE_BURST = 33002;

    @Test
    void resolvesResourceDrivenLightsGuidanceForWearerElement() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Water");

            var resolved =
                    CelestialGiftRelicEffect.resolveActivation(
                            wearer,
                            wearerDepot,
                            activeDepot,
                            WEARER_ELEMENTAL_SKILL,
                            true,
                            1);

            assertTrue(resolved.isPresent());
            assertEquals(20f, resolved.get().durationSeconds(), 0.00001f);
            assertEquals(
                    Map.of(FightProperty.FIGHT_PROP_FIRE_ADD_HURT, 0.20f),
                    resolved.get().bonuses());
        }
    }

    @Test
    void resolvesMortalHymnForWearerAndCurrentActiveElements() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Water");

            var resolved =
                    CelestialGiftRelicEffect.resolveActivation(
                            wearer,
                            wearerDepot,
                            activeDepot,
                            WEARER_ELEMENTAL_SKILL,
                            true,
                            2);

            assertTrue(resolved.isPresent());
            assertEquals(20f, resolved.get().durationSeconds(), 0.00001f);
            assertEquals(
                    Map.of(
                            FightProperty.FIGHT_PROP_FIRE_ADD_HURT,
                            0.40f,
                            FightProperty.FIGHT_PROP_WATER_ADD_HURT,
                            0.40f),
                    resolved.get().bonuses());
        }
    }

    @Test
    void sameElementMortalHymnDoesNotDoubleStack() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Fire");

            var resolved =
                    CelestialGiftRelicEffect.resolveActivation(
                                    wearer,
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_ELEMENTAL_SKILL,
                                    true,
                                    2)
                            .orElseThrow();

            assertEquals(
                    Map.of(FightProperty.FIGHT_PROP_FIRE_ADD_HURT, 0.40f),
                    resolved.bonuses());
        }
    }

    @Test
    void appliesResolvedEffectTeamwideAndExpiresEveryRecipient() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            Avatar teammate = new Avatar();
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Water");
            var scheduled = new ArrayList<Runnable>();
            var delays = new ArrayList<Integer>();
            var published = new ArrayList<Avatar>();
            var effect = new CelestialGiftRelicEffect();

            assertTrue(effect.onElementalSkill(
                    wearer,
                    wearerDepot,
                    activeDepot,
                    List.of(wearer, teammate),
                    WEARER_ELEMENTAL_SKILL,
                    true,
                    1,
                    (expiry, delay) -> {
                        scheduled.add(expiry);
                        delays.add(delay);
                    },
                    (avatar, properties) -> published.add(avatar)));

            assertEquals(
                    0.20f,
                    wearer.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0.20f,
                    teammate.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
            assertEquals(List.of(20), delays);
            assertEquals(2, published.size());
            assertEquals(1, Collections.frequency(published, wearer));
            assertEquals(1, Collections.frequency(published, teammate));

            scheduled.get(0).run();

            assertEquals(
                    0f,
                    wearer.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0f,
                    teammate.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
            assertEquals(4, published.size());
            assertEquals(2, Collections.frequency(published, wearer));
            assertEquals(2, Collections.frequency(published, teammate));
        }
    }

    @Test
    void mortalHymnFollowsCurrentActiveElementWithoutExtendingDuration() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            Avatar teammate = new Avatar();
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Water");
            AvatarSkillDepotData nextActiveDepot =
                    skillDepot(
                            33001,
                            NEXT_ACTIVE_ATTACK_SKILL,
                            NEXT_ACTIVE_ELEMENTAL_SKILL,
                            NEXT_ACTIVE_BURST,
                            "Ice");
            var plan =
                    CelestialGiftRelicEffect.resolveActivation(
                                    wearer,
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_ELEMENTAL_SKILL,
                                    true,
                                    2)
                            .orElseThrow();
            var scheduled = new ArrayList<Runnable>();
            var effect = new CelestialGiftRelicEffect();

            effect.activate(
                    plan,
                    List.of(wearer, teammate),
                    (expiry, delay) -> scheduled.add(expiry),
                    (avatar, properties) -> {});

            assertTrue(
                    effect.refreshCurrentActiveElement(
                            nextActiveDepot,
                            List.of(wearer, teammate),
                            (avatar, properties) -> {}));
            for (Avatar avatar : List.of(wearer, teammate)) {
                assertEquals(
                        0.40f,
                        avatar.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                        0.00001f);
                assertEquals(
                        0f,
                        avatar.getFightProperty(FightProperty.FIGHT_PROP_WATER_ADD_HURT),
                        0.00001f);
                assertEquals(
                        0.40f,
                        avatar.getFightProperty(FightProperty.FIGHT_PROP_ICE_ADD_HURT),
                        0.00001f);
            }
            assertEquals(1, scheduled.size(), "switch refresh must not extend the 20-second window");
        }
    }

    @Test
    void retriggerRefreshesWithoutStackingAndRejectsStaleExpiry() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Water");
            var plan =
                    CelestialGiftRelicEffect.resolveActivation(
                                    wearer,
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_ELEMENTAL_SKILL,
                                    true,
                                    1)
                            .orElseThrow();
            var scheduled = new ArrayList<Runnable>();
            var effect = new CelestialGiftRelicEffect();

            effect.activate(
                    plan,
                    List.of(wearer),
                    (expiry, delay) -> scheduled.add(expiry),
                    (avatar, properties) -> {});
            effect.activate(
                    plan,
                    List.of(wearer),
                    (expiry, delay) -> scheduled.add(expiry),
                    (avatar, properties) -> {});

            assertEquals(
                    0.20f,
                    wearer.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
            scheduled.get(0).run();
            assertEquals(
                    0.20f,
                    wearer.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
            scheduled.get(1).run();
            assertEquals(
                    0f,
                    wearer.getFightProperty(FightProperty.FIGHT_PROP_FIRE_ADD_HURT),
                    0.00001f);
        }
    }

    @Test
    void rejectsIncompleteSetInactiveWearerNormalAttackAndBurst() {
        try (var fixture = installResourceFixture()) {
            Avatar wearer = avatarWithRelicCount(4);
            AvatarSkillDepotData wearerDepot =
                    skillDepot(
                            13101,
                            WEARER_ATTACK_SKILL,
                            WEARER_ELEMENTAL_SKILL,
                            WEARER_BURST,
                            "Fire");
            AvatarSkillDepotData activeDepot =
                    skillDepot(
                            22001,
                            ACTIVE_ATTACK_SKILL,
                            ACTIVE_ELEMENTAL_SKILL,
                            ACTIVE_BURST,
                            "Water");

            assertTrue(
                    CelestialGiftRelicEffect.resolveActivation(
                                    avatarWithRelicCount(3),
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_ELEMENTAL_SKILL,
                                    true,
                                    2)
                            .isEmpty());
            assertTrue(
                    CelestialGiftRelicEffect.resolveActivation(
                                    wearer,
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_ELEMENTAL_SKILL,
                                    false,
                                    2)
                            .isEmpty());
            assertTrue(
                    CelestialGiftRelicEffect.resolveActivation(
                                    wearer,
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_ATTACK_SKILL,
                                    true,
                                    2)
                            .isEmpty());
            assertTrue(
                    CelestialGiftRelicEffect.resolveActivation(
                                    wearer,
                                    wearerDepot,
                                    activeDepot,
                                    WEARER_BURST,
                                    true,
                                    2)
                            .isEmpty());
        }
    }

    private static ResourceFixture installResourceFixture() {
        var setData =
                JsonUtils.decode(
                        """
                        { "setId": 15045, "setNeedNum": [2, 4], "equipAffixId": 215045 }
                        """,
                        ReliquarySetData.class);
        var affixData =
                JsonUtils.decode(
                        """
                        {
                          "affixId": 2150451,
                          "openConfig": "Relic_6.6_Nicole",
                          "addProps": [],
                          "paramList": [0.20, 20.0, 0.40]
                        }
                        """,
                        EquipAffixData.class);
        var openConfigData =
                JsonUtils.decode(
                        """
                        [
                          { "$type": "ModifyAbility", "abilityName": "Relic_6.6_Nicole", "paramSpecial": "AddHurtDelta", "paramDelta": "%1" },
                          { "$type": "ModifyAbility", "abilityName": "Relic_6.6_Nicole", "paramSpecial": "Dura", "paramDelta": "%2" },
                          { "$type": "ModifyAbility", "abilityName": "Relic_6.6_Nicole", "paramSpecial": "Hexenzirkel_Ratio", "paramDelta": "%3" }
                        ]
                        """,
                        OpenConfigData[].class);
        var abilityData = new AbilityData();
        abilityData.abilityName = ABILITY_NAME;

        var previousSet = GameData.getReliquarySetDataMap().put(SET_ID, setData);
        var previousAffix = GameData.getEquipAffixDataMap().put(FOUR_PIECE_AFFIX_ID, affixData);
        var previousOpenConfig =
                GameData.getOpenConfigEntries()
                        .put(ABILITY_NAME, new OpenConfigEntry(ABILITY_NAME, openConfigData));
        var previousAbility = GameData.getAbilityDataMap().put(ABILITY_NAME, abilityData);
        return new ResourceFixture(
                previousSet, previousAffix, previousOpenConfig, previousAbility);
    }

    private static AvatarSkillDepotData skillDepot(
            int depotId, int attackSkill, int elementalSkill, int burstSkill, String element) {
        var burstData =
                JsonUtils.decode(
                        """
                        {
                          "id": %d,
                          "costElemVal": 60,
                          "costElemType": "%s"
                        }
                        """
                                .formatted(burstSkill, element),
                        AvatarSkillData.class);
        var previousBurst = GameData.getAvatarSkillDataMap().put(burstSkill, burstData);
        assertEquals(null, previousBurst, "fixture skill IDs must remain isolated");

        var depot =
                JsonUtils.decode(
                        """
                        {
                          "id": %d,
                          "attackModeSkill": %d,
                          "energySkill": %d,
                          "skills": [%d, %d],
                          "subSkills": [],
                          "extraAbilities": [],
                          "talents": [0],
                          "inherentProudSkillOpens": [],
                          "DAEIJGCFNLL": []
                        }
                        """
                                .formatted(
                                        depotId,
                                        attackSkill,
                                        burstSkill,
                                        attackSkill,
                                        elementalSkill),
                        AvatarSkillDepotData.class);
        depot.onLoad();
        return depot;
    }

    private static Avatar avatarWithRelicCount(int relicCount) {
        Avatar avatar = new Avatar();
        ItemData itemData =
                JsonUtils.decode(
                        """
                        { "id": 90000002, "itemType": "ITEM_RELIQUARY", "setId": 15045 }
                        """,
                        ItemData.class);
        for (int slot = 1; slot <= relicCount; slot++) {
            GameItem item = new GameItem();
            item.setItemData(itemData);
            avatar.getEquips().put(slot, item);
        }
        return avatar;
    }

    private record ResourceFixture(
            ReliquarySetData previousSet,
            EquipAffixData previousAffix,
            OpenConfigEntry previousOpenConfig,
            AbilityData previousAbility)
            implements AutoCloseable {
        @Override
        public void close() {
            restore(GameData.getReliquarySetDataMap(), SET_ID, previousSet);
            restore(GameData.getEquipAffixDataMap(), FOUR_PIECE_AFFIX_ID, previousAffix);
            restore(GameData.getOpenConfigEntries(), ABILITY_NAME, previousOpenConfig);
            restore(GameData.getAbilityDataMap(), ABILITY_NAME, previousAbility);
            GameData.getAvatarSkillDataMap().remove(WEARER_BURST);
            GameData.getAvatarSkillDataMap().remove(ACTIVE_BURST);
            GameData.getAvatarSkillDataMap().remove(NEXT_ACTIVE_BURST);
        }
    }

    private static <T> void restore(Map<String, T> map, String key, T previous) {
        if (previous == null) map.remove(key);
        else map.put(key, previous);
    }

    private static <T> void restore(
            it.unimi.dsi.fastutil.ints.Int2ObjectMap<T> map, int key, T previous) {
        if (previous == null) map.remove(key);
        else map.put(key, previous);
    }
}
