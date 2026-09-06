package emu.grasscutter.game.ability;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.avatar.AvatarSkillDepotData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.props.ElementType;
import emu.grasscutter.game.props.FightProperty;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Server fallback for Celestial Gift's Light's Guidance and Mortal Hymn effects. */
public final class CelestialGiftRelicEffect {
    private static final String ABILITY_NAME = "Relic_6.6_Nicole";
    private static final String MODIFIER_KEY_PREFIX = ABILITY_NAME + ":team:";

    private final Map<Avatar, Map<FightProperty, Long>> activeModifiers =
            new IdentityHashMap<>();
    private long activeGeneration;
    private ResolvedEffect activeEffect;

    public CelestialGiftRelicEffect() {}

    public boolean onElementalSkill(
            Avatar wearer,
            Avatar currentActive,
            Collection<Avatar> team,
            int skillId,
            boolean effectivelyActive,
            int effectiveHexereiCount,
            ExpiryScheduler scheduler,
            FightPropertyPublisher publisher) {
        if (wearer == null || currentActive == null) return false;
        return onElementalSkill(
                wearer,
                wearer.getSkillDepot(),
                currentActive.getSkillDepot(),
                team,
                skillId,
                effectivelyActive,
                effectiveHexereiCount,
                scheduler,
                publisher);
    }

    public boolean onElementalSkill(
            Avatar wearer,
            AvatarSkillDepotData wearerDepot,
            AvatarSkillDepotData activeDepot,
            Collection<Avatar> team,
            int skillId,
            boolean effectivelyActive,
            int effectiveHexereiCount,
            ExpiryScheduler scheduler,
            FightPropertyPublisher publisher) {
        var resolved = resolveActivation(
                wearer,
                wearerDepot,
                activeDepot,
                skillId,
                effectivelyActive,
                effectiveHexereiCount);
        if (resolved.isEmpty()) return false;

        activate(resolved.get(), team, scheduler, publisher);
        return true;
    }

    public synchronized boolean refreshTeamAndCurrentActiveElement(
            Avatar currentActive,
            Collection<Avatar> team,
            FightPropertyPublisher publisher) {
        if (this.activeEffect == null
                || currentActive == null
                || currentActive.getSkillDepot() == null
                || team == null
                || publisher == null) {
            return false;
        }
        if (this.activeEffect.mortalHymn()) {
            return refreshCurrentActiveElement(currentActive.getSkillDepot(), team, publisher);
        }

        applyBonuses(this.activeEffect.bonuses(), team, publisher);
        return true;
    }

    public synchronized void activate(
            ResolvedEffect effect,
            Collection<Avatar> team,
            ExpiryScheduler scheduler,
            FightPropertyPublisher publisher) {
        if (effect == null || team == null || scheduler == null || publisher == null) return;

        long effectGeneration = ++this.activeGeneration;
        this.activeEffect = effect;
        applyBonuses(effect.bonuses(), team, publisher);

        int delayTicks = Math.max(1, (int) Math.ceil(effect.durationSeconds()));
        scheduler.schedule(() -> expire(effectGeneration, publisher), delayTicks);
    }

    public synchronized boolean refreshCurrentActiveElement(
            AvatarSkillDepotData activeDepot,
            Collection<Avatar> team,
            FightPropertyPublisher publisher) {
        if (this.activeEffect == null
                || !this.activeEffect.mortalHymn()
                || activeDepot == null
                || team == null
                || publisher == null) {
            return false;
        }

        FightProperty activeProperty = elementalDamageProperty(activeDepot.getElementType());
        if (activeProperty == null) return false;

        var bonuses = new LinkedHashMap<FightProperty, Float>();
        bonuses.put(this.activeEffect.wearerProperty(), this.activeEffect.value());
        bonuses.put(activeProperty, this.activeEffect.value());
        this.activeEffect = new ResolvedEffect(
                this.activeEffect.durationSeconds(),
                this.activeEffect.wearerProperty(),
                this.activeEffect.value(),
                true,
                Map.copyOf(bonuses));
        applyBonuses(this.activeEffect.bonuses(), team, publisher);
        return true;
    }

    private void applyBonuses(
            Map<FightProperty, Float> bonuses,
            Collection<Avatar> team,
            FightPropertyPublisher publisher) {
        var nextTeam = java.util.Collections.newSetFromMap(new IdentityHashMap<Avatar, Boolean>());
        nextTeam.addAll(team);

        for (var entry : new IdentityHashMap<>(this.activeModifiers).entrySet()) {
            if (!nextTeam.contains(entry.getKey())) {
                removeModifiers(entry.getKey(), entry.getValue(), publisher);
                this.activeModifiers.remove(entry.getKey());
            }
        }

        for (Avatar avatar : nextTeam) {
            if (avatar == null) continue;
            var previous = this.activeModifiers.getOrDefault(avatar, Map.of());
            var changed = new java.util.LinkedHashSet<FightProperty>(previous.keySet());
            changed.addAll(bonuses.keySet());
            for (var entry : previous.entrySet()) {
                if (!bonuses.containsKey(entry.getKey())) {
                    avatar.removeTransientFightPropertyModifier(
                            modifierKey(entry.getKey()), entry.getValue());
                }
            }

            var generations = new LinkedHashMap<FightProperty, Long>();
            for (var bonus : bonuses.entrySet()) {
                long generation = avatar.upsertTransientFightPropertyModifier(
                        modifierKey(bonus.getKey()), bonus.getKey(), bonus.getValue());
                generations.put(bonus.getKey(), generation);
            }
            this.activeModifiers.put(avatar, Map.copyOf(generations));
            publisher.publish(avatar, propertySnapshot(avatar, changed));
        }
    }

    private synchronized void expire(
            long expectedEffectGeneration, FightPropertyPublisher publisher) {
        if (expectedEffectGeneration != this.activeGeneration) return;

        for (var entry : new IdentityHashMap<>(this.activeModifiers).entrySet()) {
            removeModifiers(entry.getKey(), entry.getValue(), publisher);
        }
        this.activeModifiers.clear();
        this.activeEffect = null;
    }

    private static void removeModifiers(
            Avatar avatar,
            Map<FightProperty, Long> modifiers,
            FightPropertyPublisher publisher) {
        var removed = new java.util.LinkedHashSet<FightProperty>();
        modifiers.forEach(
                (property, generation) -> {
                    if (avatar.removeTransientFightPropertyModifier(
                            modifierKey(property), generation)) {
                        removed.add(property);
                    }
                });
        if (!removed.isEmpty()) {
            publisher.publish(avatar, propertySnapshot(avatar, removed));
        }
    }

    private static Map<Integer, Float> propertySnapshot(
            Avatar avatar, Collection<FightProperty> properties) {
        var values = new LinkedHashMap<Integer, Float>();
        properties.forEach(
                property -> values.put(property.getId(), avatar.getFightProperty(property)));
        return Map.copyOf(values);
    }

    private static String modifierKey(FightProperty property) {
        return MODIFIER_KEY_PREFIX + property.name();
    }

    public static Optional<ResolvedEffect> resolveActivation(
            Avatar wearer,
            AvatarSkillDepotData wearerDepot,
            AvatarSkillDepotData activeDepot,
            int skillId,
            boolean effectivelyActive,
            int effectiveHexereiCount) {
        if (wearer == null
                || wearerDepot == null
                || !effectivelyActive
                || wearerDepot.getSkills() == null
                || !wearerDepot.getSkills().contains(skillId)
                || skillId == wearerDepot.getAttackModeSkill()
                || skillId == wearerDepot.getEnergySkill()) {
            return Optional.empty();
        }

        var abilityData = GameData.getAbilityData(ABILITY_NAME);
        if (abilityData == null) return Optional.empty();

        var specials = new Object2FloatOpenHashMap<String>();
        Ability.applyEquipAffixSpecials(abilityData, wearer, specials);
        float value = specials.getFloat("AddHurtDelta");
        float duration = specials.getFloat("Dura");
        FightProperty property = elementalDamageProperty(wearerDepot.getElementType());
        if (property == null
                || !Float.isFinite(value)
                || value <= 0f
                || !Float.isFinite(duration)
                || duration <= 0f) {
            return Optional.empty();
        }

        if (effectiveHexereiCount < 2) {
            return Optional.of(
                    new ResolvedEffect(
                            duration, property, value, false, Map.of(property, value)));
        }

        float hexenzirkelValue = specials.getFloat("Hexenzirkel_Ratio");
        FightProperty activeProperty = activeDepot == null
                ? null
                : elementalDamageProperty(activeDepot.getElementType());
        if (activeProperty == null
                || !Float.isFinite(hexenzirkelValue)
                || hexenzirkelValue <= 0f) {
            return Optional.empty();
        }

        var bonuses = new LinkedHashMap<FightProperty, Float>();
        bonuses.put(property, hexenzirkelValue);
        bonuses.put(activeProperty, hexenzirkelValue);
        return Optional.of(
                new ResolvedEffect(
                        duration,
                        property,
                        hexenzirkelValue,
                        true,
                        Map.copyOf(bonuses)));
    }

    private static FightProperty elementalDamageProperty(ElementType element) {
        if (element == null) return null;
        return switch (element) {
            case Fire -> FightProperty.FIGHT_PROP_FIRE_ADD_HURT;
            case Electric -> FightProperty.FIGHT_PROP_ELEC_ADD_HURT;
            case Water -> FightProperty.FIGHT_PROP_WATER_ADD_HURT;
            case Grass -> FightProperty.FIGHT_PROP_GRASS_ADD_HURT;
            case Wind -> FightProperty.FIGHT_PROP_WIND_ADD_HURT;
            case Rock -> FightProperty.FIGHT_PROP_ROCK_ADD_HURT;
            case Ice -> FightProperty.FIGHT_PROP_ICE_ADD_HURT;
            default -> null;
        };
    }

    public record ResolvedEffect(
            float durationSeconds,
            FightProperty wearerProperty,
            float value,
            boolean mortalHymn,
            Map<FightProperty, Float> bonuses) {}

    @FunctionalInterface
    public interface ExpiryScheduler {
        void schedule(Runnable expiry, int delayTicks);
    }

    @FunctionalInterface
    public interface FightPropertyPublisher {
        void publish(Avatar avatar, Map<Integer, Float> properties);
    }
}
