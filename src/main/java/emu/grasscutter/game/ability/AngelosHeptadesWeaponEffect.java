package emu.grasscutter.game.ability;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.FightProperty;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/** Resource-driven server fallback for Angelos' Heptades (weapon 14523). */
public final class AngelosHeptadesWeaponEffect {
    private static final int WEAPON_ID = 14523;
    private static final int AFFIX_ID = 114523;
    private static final String MODIFIER_KEY = "Weapon_Catalyst_Nicole:PathfindersLight";

    private final Map<Avatar, Long> activeModifiers = new IdentityHashMap<>();
    private final Map<Avatar, Long> cooldownEnds = new IdentityHashMap<>();
    private Avatar activeWearer;
    private ResolvedEffect activeEffect;
    private float activeDamageBonus;
    private long activeGeneration;

    public AngelosHeptadesWeaponEffect() {}

    public static Optional<ResolvedEffect> resolve(GameItem weapon) {
        if (weapon == null
                || weapon.getItemData() == null
                || weapon.getItemId() != WEAPON_ID
                || weapon.getAffixes() == null
                || !weapon.getAffixes().contains(AFFIX_ID)) {
            return Optional.empty();
        }

        int refinement = weapon.getRefinement();
        if (refinement < 0 || refinement > 4) return Optional.empty();
        var affix = GameData.getEquipAffixDataMap().get(AFFIX_ID * 10 + refinement);
        if (affix == null
                || !"Weapon_Catalyst_Nicole".equals(affix.getOpenConfig())
                || affix.getParamList() == null
                || affix.getParamList().length < 8) {
            return Optional.empty();
        }

        float[] params = affix.getParamList();
        var resolved = new ResolvedEffect(
                params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
        return resolved.isValid() ? Optional.of(resolved) : Optional.empty();
    }

    public synchronized boolean onShieldCreated(
            Avatar wearer,
            Avatar currentActive,
            Collection<Avatar> team,
            int effectiveHexereiCount,
            HexereiActivation hexereiActivation,
            long nowMillis,
            ExpiryScheduler scheduler,
            Publisher publisher) {
        if (wearer == null || currentActive == null || team == null || scheduler == null || publisher == null) {
            return false;
        }
        var resolved = resolve(wearer.getWeapon());
        if (resolved.isEmpty()) return false;

        var effect = resolved.get();
        // The value is snapshotted when the shield is created; switching recipients does not
        // recalculate it from a later attack value.
        float damageBonus =
                effect.pathfindersLightDamageBonus(
                        wearer.getFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK));
        applyPathfindersLight(
                wearer,
                currentActive,
                team,
                effect,
                damageBonus,
                effectiveHexereiCount,
                hexereiActivation,
                scheduler,
                publisher);
        restoreEnergyIfReady(wearer, effect, nowMillis, publisher);
        return true;
    }

    public synchronized boolean refreshCurrentActive(
            Avatar currentActive,
            Collection<Avatar> team,
            int effectiveHexereiCount,
            HexereiActivation hexereiActivation,
            Publisher publisher) {
        if (currentActive == null || team == null || publisher == null || this.activeModifiers.isEmpty()) return false;
        if (this.activeWearer == null || this.activeEffect == null) return false;
        applyRecipients(
                currentActive,
                team,
                this.activeEffect,
                this.activeDamageBonus,
                effectiveHexereiCount,
                hexereiActivation,
                publisher);
        return true;
    }

    private void applyPathfindersLight(
            Avatar wearer,
            Avatar currentActive,
            Collection<Avatar> team,
            ResolvedEffect effect,
            float damageBonus,
            int effectiveHexereiCount,
            HexereiActivation hexereiActivation,
            ExpiryScheduler scheduler,
            Publisher publisher) {
        long generation = ++this.activeGeneration;
        this.activeWearer = wearer;
        this.activeEffect = effect;
        this.activeDamageBonus = damageBonus;
        applyRecipients(
                currentActive,
                team,
                effect,
                damageBonus,
                effectiveHexereiCount,
                hexereiActivation,
                publisher);
        scheduler.schedule(() -> expire(generation, publisher), Math.max(1, (int) Math.ceil(effect.pathfindersLightDurationSeconds())));
    }

    private void applyRecipients(
            Avatar currentActive,
            Collection<Avatar> team,
            ResolvedEffect effect,
            float damageBonus,
            int effectiveHexereiCount,
            HexereiActivation hexereiActivation,
            Publisher publisher) {
        var recipients = java.util.Collections.newSetFromMap(new IdentityHashMap<Avatar, Boolean>());
        recipients.add(currentActive);
        if (effectiveHexereiCount >= 2 && hexereiActivation != null) {
            for (var member : team) {
                if (member != null && member != currentActive && hexereiActivation.isEffectivelyActive(member)) recipients.add(member);
            }
        }
        for (var previous : new IdentityHashMap<>(activeModifiers).entrySet()) {
            if (!recipients.contains(previous.getKey())) {
                if (previous.getKey().removeTransientFightPropertyModifier(MODIFIER_KEY, previous.getValue())) {
                    publisher.publishDamage(previous.getKey());
                }
                activeModifiers.remove(previous.getKey());
            }
        }
        for (var recipient : recipients) {
            float recipientBonus =
                    recipient == currentActive
                            ? damageBonus
                            : damageBonus * effect.offFieldHexereiRatio();
            long modifierGeneration = recipient.upsertTransientFightPropertyModifier(
                    MODIFIER_KEY, FightProperty.FIGHT_PROP_ADD_HURT, recipientBonus);
            activeModifiers.put(recipient, modifierGeneration);
            publisher.publishDamage(recipient);
        }
    }

    private boolean restoreEnergyIfReady(Avatar wearer, ResolvedEffect effect, long nowMillis, Publisher publisher) {
        long cooldownEnd = cooldownEnds.getOrDefault(wearer, 0L);
        if (nowMillis < cooldownEnd || wearer.getSkillDepot() == null) return false;
        var energy = wearer.getSkillDepot().getElementType().getCurEnergyProp();
        var maxEnergy = wearer.getSkillDepot().getElementType().getMaxEnergyProp();
        float before = wearer.getFightProperty(energy);
        float after = Math.min(before + effect.energyRestore(), wearer.getFightProperty(maxEnergy));
        wearer.setCurrentEnergy(energy, after);
        cooldownEnds.put(wearer, nowMillis + Math.round(effect.cooldownSeconds() * 1000f));
        publisher.publishEnergy(wearer, energy);
        return true;
    }

    private synchronized void expire(long expectedGeneration, Publisher publisher) {
        if (expectedGeneration != activeGeneration) return;
        for (var entry : new IdentityHashMap<>(activeModifiers).entrySet()) {
            if (entry.getKey().removeTransientFightPropertyModifier(MODIFIER_KEY, entry.getValue())) {
                publisher.publishDamage(entry.getKey());
            }
        }
        activeModifiers.clear();
        activeWearer = null;
        activeEffect = null;
        activeDamageBonus = 0f;
    }

    public record ResolvedEffect(
            float attackPercent,
            float attackStep,
            float damagePerAttackStep,
            float maxDamageBonus,
            float pathfindersLightDurationSeconds,
            float energyRestore,
            float cooldownSeconds,
            float offFieldHexereiRatio) {
        public float pathfindersLightDamageBonus(float attack) {
            if (!Float.isFinite(attack) || attack <= 0f) return 0f;
            return Math.min(maxDamageBonus, (float) Math.floor(attack / attackStep) * damagePerAttackStep);
        }

        private boolean isValid() {
            return Float.isFinite(attackPercent)
                    && attackPercent > 0f
                    && Float.isFinite(attackStep)
                    && attackStep > 0f
                    && Float.isFinite(damagePerAttackStep)
                    && damagePerAttackStep > 0f
                    && Float.isFinite(maxDamageBonus)
                    && maxDamageBonus > 0f
                    && Float.isFinite(pathfindersLightDurationSeconds)
                    && pathfindersLightDurationSeconds > 0f
                    && Float.isFinite(energyRestore)
                    && energyRestore > 0f
                    && Float.isFinite(cooldownSeconds)
                    && cooldownSeconds > 0f
                    && Float.isFinite(offFieldHexereiRatio)
                    && offFieldHexereiRatio > 0f;
        }
    }

    @FunctionalInterface
    public interface HexereiActivation { boolean isEffectivelyActive(Avatar avatar); }

    @FunctionalInterface
    public interface ExpiryScheduler { void schedule(Runnable expiry, int delayTicks); }

    public interface Publisher {
        void publishDamage(Avatar avatar);
        void publishEnergy(Avatar avatar, FightProperty energyProperty);
    }
}
