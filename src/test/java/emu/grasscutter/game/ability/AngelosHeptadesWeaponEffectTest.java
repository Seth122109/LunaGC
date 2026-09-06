package emu.grasscutter.game.ability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.excels.EquipAffixData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.avatar.AvatarSkillData;
import emu.grasscutter.data.excels.avatar.AvatarSkillDepotData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AngelosHeptadesWeaponEffectTest {
    private static final int WEAPON_ID = 14523;
    private static final int WEAPON_AFFIX_ID = 114523;
    private static final int TEST_BURST_ID = 9876501;

    @Test
    void realModifierPredicateRecognizesNicolesGlobalMainShield() {
        var modifier =
                JsonUtils.decode(
                        """
                        { "modifierMixins": [{ "$type": "GlobalMainShieldMixin" }] }
                        """,
                        AbilityModifier.class);

        assertTrue(AngelosShieldTrigger.isShieldCreationModifier(modifier));
    }

    @Test
    void joinsTheEquippedWeaponRefinementToTheMatchingAffix() {
        try (var fixture = installResourceFixture()) {
            var r1 = AngelosHeptadesWeaponEffect.resolve(equippedWeapon(0)).orElseThrow();
            var r5 = AngelosHeptadesWeaponEffect.resolve(equippedWeapon(4)).orElseThrow();

            assertEquals(0.12f, r1.attackPercent(), 0.00001f);
            assertEquals(0.10f, r1.damagePerAttackStep(), 0.00001f);
            assertEquals(0.24f, r5.attackPercent(), 0.00001f);
            assertEquals(0.22f, r5.damagePerAttackStep(), 0.00001f);
            assertEquals(0.58f, r5.maxDamageBonus(), 0.00001f);
        }
    }

    @Test
    void rejectsAWeaponThatIsNotAngelosHeptades() {
        try (var fixture = installResourceFixture()) {
            var itemData =
                    JsonUtils.decode(
                            """
                            { "id": 14524, "itemType": "ITEM_WEAPON", "skillAffix": [114523] }
                            """,
                            ItemData.class);
            var wrongWeapon = new GameItem(itemData);
            wrongWeapon.setRefinement(4);

            assertFalse(AngelosHeptadesWeaponEffect.resolve(wrongWeapon).isPresent());
        }
    }

    @Test
    void requiresTheShieldCreatorRatherThanTheShieldRecipientToHoldTheWeapon() {
        try (var fixture = installResourceFixture()) {
            var creatorWithoutWeapon = new TestAvatar();
            creatorWithoutWeapon.setFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK, 3_000f);
            var shieldRecipient = wearer(3_000f, 4);
            var effect = new AngelosHeptadesWeaponEffect();

            assertFalse(
                    effect.onShieldCreated(
                            creatorWithoutWeapon,
                            shieldRecipient,
                            List.of(creatorWithoutWeapon, shieldRecipient),
                            0,
                            avatar -> false,
                            1_000L,
                            (task, delay) -> {},
                            noOpPublisher()));
            assertEquals(
                    0f,
                    shieldRecipient.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
        }
    }

    @Test
    void usesWholeAttackStepsAndCapsAtTheR5Maximum() {
        try (var fixture = installResourceFixture()) {
            var resolved = AngelosHeptadesWeaponEffect.resolve(equippedWeapon(4)).orElseThrow();

            assertEquals(0f, resolved.pathfindersLightDamageBonus(999f), 0.00001f);
            assertEquals(0.22f, resolved.pathfindersLightDamageBonus(1_000f), 0.00001f);
            assertEquals(0.22f, resolved.pathfindersLightDamageBonus(1_999f), 0.00001f);
            assertEquals(0.44f, resolved.pathfindersLightDamageBonus(2_000f), 0.00001f);
            assertEquals(0.58f, resolved.pathfindersLightDamageBonus(3_000f), 0.00001f);
            assertEquals(0.58f, resolved.pathfindersLightDamageBonus(10_000f), 0.00001f);
        }
    }

    @Test
    void transfersTheSnapshottedEffectOnSwitchAndRefreshesWithoutStacking() {
        try (var fixture = installResourceFixture()) {
            var creator = wearer(3_000f, 4);
            var active = new Avatar();
            var nextActive = new Avatar();
            var offFieldHexerei = new Avatar();
            var regularTeammate = new Avatar();
            var expiry = new ArrayList<Runnable>();
            var publisher = new RecordingPublisher();
            var effect = new AngelosHeptadesWeaponEffect();

            assertTrue(
                    effect.onShieldCreated(
                            creator,
                            active,
                            List.of(
                                    creator,
                                    active,
                                    nextActive,
                                    offFieldHexerei,
                                    regularTeammate),
                            2,
                            avatar -> avatar == offFieldHexerei,
                            1_000L,
                            (task, delay) -> expiry.add(task),
                            publisher));
            assertEquals(
                    0.58f,
                    active.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0.29f,
                    offFieldHexerei.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0f,
                    nextActive.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);

            creator.setFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK, 1_000f);
            assertTrue(
                    effect.refreshCurrentActive(
                            nextActive,
                            List.of(
                                    creator,
                                    active,
                                    nextActive,
                                    offFieldHexerei,
                                    regularTeammate),
                            2,
                            avatar -> avatar == offFieldHexerei,
                            publisher));
            assertEquals(
                    0f,
                    active.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0.58f,
                    nextActive.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0.29f,
                    offFieldHexerei.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);

            assertTrue(
                    effect.onShieldCreated(
                            creator,
                            nextActive,
                            List.of(
                                    creator,
                                    active,
                                    nextActive,
                                    offFieldHexerei,
                                    regularTeammate),
                            2,
                            avatar -> avatar == offFieldHexerei,
                            1_001L,
                            (task, delay) -> expiry.add(task),
                            publisher));
            assertEquals(
                    0.22f,
                    nextActive.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            expiry.get(0).run();
            assertEquals(
                    0.22f,
                    nextActive.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            expiry.get(1).run();
            assertEquals(
                    0f,
                    nextActive.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertEquals(
                    0f,
                    offFieldHexerei.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertTrue(
                    publisher.damageUpdates >= 9,
                    "additions, switch removals, refreshes, and expiry removals are published");
        }
    }

    @Test
    void replacesThePlayerScopedEffectWhenAnotherValidWielderTriggersIt() {
        try (var fixture = installResourceFixture()) {
            var firstCreator = wearer(3_000f, 4);
            var secondCreator = wearer(1_000f, 4);
            var active = new Avatar();
            var expiry = new ArrayList<Runnable>();
            var effect = new AngelosHeptadesWeaponEffect();

            assertTrue(
                    effect.onShieldCreated(
                            firstCreator,
                            active,
                            List.of(firstCreator, active),
                            0,
                            avatar -> false,
                            1_000L,
                            (task, delay) -> expiry.add(task),
                            noOpPublisher()));
            assertEquals(
                    0.58f,
                    active.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
            assertTrue(
                    effect.onShieldCreated(
                            secondCreator,
                            active,
                            List.of(secondCreator, active),
                            0,
                            avatar -> false,
                            1_001L,
                            (task, delay) -> expiry.add(task),
                            noOpPublisher()));
            assertEquals(
                    0.22f,
                    active.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT),
                    0.00001f);
        }
    }

    @Test
    void energyCooldownIsIndependentFromPathfindersRefreshAndClampsAtMaximum() {
        try (var fixture = installResourceFixture()) {
            var creator = wearer(1_000f, 4);
            creator.setTestSkillDepot(fireSkillDepot());
            creator.setFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY, 50f);
            creator.setFightProperty(FightProperty.FIGHT_PROP_MAX_FIRE_ENERGY, 60f);
            var active = new TestAvatar();
            active.setFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY, 7f);
            var expiry = new ArrayList<Runnable>();
            var publisher = new RecordingPublisher();
            var effect = new AngelosHeptadesWeaponEffect();

            assertTrue(
                    effect.onShieldCreated(
                            creator,
                            active,
                            List.of(creator, active),
                            0,
                            avatar -> false,
                            1_000L,
                            (task, delay) -> expiry.add(task),
                            publisher));
            assertEquals(
                    60f,
                    creator.getFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY),
                    0.00001f);
            assertEquals(
                    7f,
                    active.getFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY),
                    0.00001f);
            assertEquals(1, publisher.energyUpdates);

            creator.setFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY, 30f);
            assertTrue(
                    effect.onShieldCreated(
                            creator,
                            active,
                            List.of(creator, active),
                            0,
                            avatar -> false,
                            2_000L,
                            (task, delay) -> expiry.add(task),
                            publisher));
            assertEquals(
                    30f,
                    creator.getFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY),
                    0.00001f);
            assertEquals(0.22f, active.getFightProperty(FightProperty.FIGHT_PROP_ADD_HURT), 0.00001f);
            assertEquals(2, expiry.size(), "Pathfinder's Light still refreshes during energy cooldown");
            assertEquals(1, publisher.energyUpdates);

            assertTrue(
                    effect.onShieldCreated(
                            creator,
                            active,
                            List.of(creator, active),
                            0,
                            avatar -> false,
                            15_000L,
                            (task, delay) -> expiry.add(task),
                            publisher));
            assertEquals(
                    48f,
                    creator.getFightProperty(FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY),
                    0.00001f);
            assertEquals(2, publisher.energyUpdates);
            assertEquals(
                    FightProperty.FIGHT_PROP_CUR_FIRE_ENERGY,
                    publisher.lastEnergyProperty);
        } finally {
            GameData.getAvatarSkillDataMap().remove(TEST_BURST_ID);
        }
    }

    private static TestAvatar wearer(float attack, int refinement) {
        var avatar = new TestAvatar();
        avatar.getEquips().put(6, equippedWeapon(refinement));
        avatar.setFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK, attack);
        return avatar;
    }

    private static AngelosHeptadesWeaponEffect.Publisher noOpPublisher() {
        return new AngelosHeptadesWeaponEffect.Publisher() {
            @Override
            public void publishDamage(Avatar avatar) {}

            @Override
            public void publishEnergy(Avatar avatar, FightProperty property) {}
        };
    }

    private static AvatarSkillDepotData fireSkillDepot() {
        var burst =
                JsonUtils.decode(
                        """
                        { "id": %d, "costElemVal": 60, "costElemType": "Fire" }
                        """
                                .formatted(TEST_BURST_ID),
                        AvatarSkillData.class);
        GameData.getAvatarSkillDataMap().put(TEST_BURST_ID, burst);
        var depot =
                JsonUtils.decode(
                        """
                        { "id": 9876500, "attackModeSkill": 0, "energySkill": %d,
                          "skills": [], "subSkills": [], "extraAbilities": [], "talents": [0],
                          "inherentProudSkillOpens": [], "DAEIJGCFNLL": [] }
                        """
                                .formatted(TEST_BURST_ID),
                        AvatarSkillDepotData.class);
        depot.onLoad();
        return depot;
    }

    private static final class TestAvatar extends Avatar {
        private void setTestSkillDepot(AvatarSkillDepotData depot) {
            setSkillDepot(depot);
        }

        @Override
        public void setCurrentEnergy(FightProperty property, float currentEnergy) {
            setFightProperty(property, currentEnergy);
        }

        @Override
        public void save() {}
    }

    private static final class RecordingPublisher
            implements AngelosHeptadesWeaponEffect.Publisher {
        private int energyUpdates;
        private int damageUpdates;
        private FightProperty lastEnergyProperty;

        @Override
        public void publishDamage(Avatar avatar) {
            damageUpdates++;
        }

        @Override
        public void publishEnergy(Avatar avatar, FightProperty property) {
            energyUpdates++;
            lastEnergyProperty = property;
        }
    }

    private static GameItem equippedWeapon(int refinement) {
        var weapon = new GameItem(GameData.getItemDataMap().get(WEAPON_ID));
        weapon.setRefinement(refinement);
        return weapon;
    }

    private static ResourceFixture installResourceFixture() {
        var itemData =
                JsonUtils.decode(
                        """
                        { "id": 14523, "itemType": "ITEM_WEAPON", "skillAffix": [114523] }
                        """,
                        ItemData.class);
        var r1 = affix(1145230, 0.12f, 0.10f, 0.26f);
        var r5 = affix(1145234, 0.24f, 0.22f, 0.58f);
        return new ResourceFixture(
                GameData.getItemDataMap().put(WEAPON_ID, itemData),
                GameData.getEquipAffixDataMap().put(WEAPON_AFFIX_ID * 10, r1),
                GameData.getEquipAffixDataMap().put(WEAPON_AFFIX_ID * 10 + 4, r5));
    }

    private static EquipAffixData affix(
            int id, float attackPercent, float damagePerStep, float maxDamage) {
        return JsonUtils.decode(
                """
                { "affixId": %d, "openConfig": "Weapon_Catalyst_Nicole", "addProps": [],
                  "paramList": [%f, 1000.0, %f, %f, 20.0, 18.0, 14.0, 0.5] }
                """
                        .formatted(id, attackPercent, damagePerStep, maxDamage),
                EquipAffixData.class);
    }

    private record ResourceFixture(
            ItemData previousItem, EquipAffixData previousR1, EquipAffixData previousR5)
            implements AutoCloseable {
        @Override
        public void close() {
            restoreItem(previousItem);
            restoreAffix(WEAPON_AFFIX_ID * 10, previousR1);
            restoreAffix(WEAPON_AFFIX_ID * 10 + 4, previousR5);
        }

        private static void restoreItem(ItemData previous) {
            if (previous == null) GameData.getItemDataMap().remove(WEAPON_ID);
            else GameData.getItemDataMap().put(WEAPON_ID, previous);
        }

        private static void restoreAffix(int id, EquipAffixData previous) {
            if (previous == null) GameData.getEquipAffixDataMap().remove(id);
            else GameData.getEquipAffixDataMap().put(id, previous);
        }
    }
}
