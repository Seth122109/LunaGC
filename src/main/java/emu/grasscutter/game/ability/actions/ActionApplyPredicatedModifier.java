package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.PredicateEvaluator;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketAvatarFightPropUpdateNotify;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/** Server fallback for the BJJ-guarded MajoKai CRIT modifier. */
@AbilityAction(AbilityModifierAction.Type.POIDCNKIFGD)
public final class ActionApplyPredicatedModifier extends AbilityActionHandler {
    private static final String ABILITY_NAME = "Relic_6.3_MajoKai";
    private static final String MODIFIER_NAME = "UNIQUE_Relic_MajoKaiBuff2";
    private static final String TRIGGER_MODIFIER_NAME = "Relic_MajoKaiBuff_TriggerCD";
    private static final String BJJ_PREDICATE = "BJJDEAIEIGP";
    private static final String MODIFIER_KEY =
            ABILITY_NAME + ":" + MODIFIER_NAME + ":Actor_CriticalDelta";

    @Override
    public boolean execute(
            Ability ability,
            AbilityModifierAction action,
            ByteString abilityData,
            GameEntity target) {
        if (ability == null || action == null) return false;

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> predicates =
                (List<Map<String, Object>>) (List<?>) action.predicates;
        boolean predicatesPass =
                PredicateEvaluator.all(predicates, ability, ability.getOwner(), target, action);
        var fallback = resolveFallback(
                ability.getData(), action, ability.getAbilitySpecials(), predicatesPass);
        if (fallback.isEmpty()) return true;

        EntityAvatar avatarEntity = target instanceof EntityAvatar entity
                ? entity
                : ability.getCasterEntity();
        if (avatarEntity == null || avatarEntity.getScene() == null) return false;

        Avatar avatar = avatarEntity.getAvatar();
        if (avatar == null || avatar.getPlayer() == null || avatar.getPlayer().getSession() == null) {
            return false;
        }

        var plan = fallback.get();
        applyFallback(
                avatar,
                plan,
                (expiry, ticks) -> avatarEntity
                        .getScene()
                        .getScheduler()
                        .scheduleDelayedTask(expiry, ticks),
                property -> publish(avatar, property));
        return true;
    }

    static void applyFallback(
            Avatar avatar,
            ResolvedFightPropertyModifier plan,
            ExpiryScheduler scheduler,
            Consumer<FightProperty> publisher) {
        long generation = avatar.upsertTransientFightPropertyModifier(
                plan.key(), plan.property(), plan.value());
        publisher.accept(plan.property());

        int durationTicks = Math.max(1, (int) Math.ceil(plan.durationSeconds()));
        scheduler.schedule(
                () -> {
                    if (avatar.removeTransientFightPropertyModifier(plan.key(), generation)) {
                        publisher.accept(plan.property());
                    }
                },
                durationTicks);
    }

    static Optional<ResolvedFightPropertyModifier> resolveFallback(
            AbilityData abilityData,
            AbilityModifierAction action,
            Object2FloatMap<String> specials,
            boolean predicatesPass) {
        if (!predicatesPass
                || abilityData == null
                || !ABILITY_NAME.equals(abilityData.abilityName)
                || abilityData.modifiers == null
                || action == null
                || action.type != AbilityModifierAction.Type.POIDCNKIFGD
                || !MODIFIER_NAME.equals(action.modifierName)
                || !"Self".equals(action.target)
                || !hasPredicate(action.predicates, BJJ_PREDICATE)
                || specials == null) {
            return Optional.empty();
        }

        var modifier = abilityData.modifiers.get(MODIFIER_NAME);
        if (modifier == null || modifier.properties == null) return Optional.empty();

        float value = modifier.properties.Actor_CriticalDelta.get(specials, 0f);
        float duration = modifier.duration.get(specials, 0f);
        if (!Float.isFinite(value)
                || value <= 0f
                || !Float.isFinite(duration)
                || duration <= 0f) {
            return Optional.empty();
        }

        return Optional.of(
                new ResolvedFightPropertyModifier(
                        MODIFIER_KEY,
                        FightProperty.FIGHT_PROP_CRITICAL,
                        value,
                        duration));
    }

    static Optional<ResolvedFightPropertyModifier> resolveLandedAttack(
            Avatar avatar,
            boolean effectivelyActive,
            boolean targetIsAvatar,
            float damage) {
        if (avatar == null
                || !effectivelyActive
                || targetIsAvatar
                || !Float.isFinite(damage)
                || damage <= 0f) {
            return Optional.empty();
        }

        AbilityData abilityData = GameData.getAbilityData(ABILITY_NAME);
        if (abilityData == null || abilityData.modifiers == null) return Optional.empty();

        var triggerModifier = abilityData.modifiers.get(TRIGGER_MODIFIER_NAME);
        if (triggerModifier == null || triggerModifier.onAdded == null) return Optional.empty();

        var specials = new Object2FloatOpenHashMap<String>();
        Ability.applyEquipAffixSpecials(abilityData, avatar, specials);
        for (AbilityModifierAction action : triggerModifier.onAdded) {
            var plan = resolveFallback(abilityData, action, specials, true);
            if (plan.isPresent()) return plan;
        }
        return Optional.empty();
    }

    static boolean applyLandedAttack(
            Avatar avatar,
            boolean effectivelyActive,
            boolean targetIsAvatar,
            float damage,
            ExpiryScheduler scheduler,
            Consumer<FightProperty> publisher) {
        var plan = resolveLandedAttack(avatar, effectivelyActive, targetIsAvatar, damage);
        if (plan.isEmpty()) return false;

        applyFallback(avatar, plan.get(), scheduler, publisher);
        return true;
    }

    public static boolean onLandedAttack(
            EntityAvatar attacker, GameEntity target, float damage) {
        if (attacker == null || target == null || attacker.getScene() == null) return false;

        Avatar avatar = attacker.getAvatar();
        if (avatar == null || avatar.getPlayer() == null) return false;

        var hexereiManager = avatar.getPlayer().getHexereiManager();
        boolean effectivelyActive =
                hexereiManager != null && hexereiManager.isEffectivelyActive(avatar);
        return applyLandedAttack(
                avatar,
                effectivelyActive,
                target instanceof EntityAvatar,
                damage,
                (expiry, ticks) -> attacker
                        .getScene()
                        .getScheduler()
                        .scheduleDelayedTask(expiry, ticks),
                property -> publish(avatar, property));
    }

    public static boolean isServerOrchestration(AbilityModifierAction.Type type) {
        return type == AbilityModifierAction.Type.POIDCNKIFGD;
    }

    private static boolean hasPredicate(List<Object> predicates, String type) {
        if (predicates == null || predicates.isEmpty()) return false;
        for (Object predicate : predicates) {
            if (predicate instanceof Map<?, ?> map && type.equals(map.get("$type"))) return true;
        }
        return false;
    }

    private static void publish(Avatar avatar, FightProperty property) {
        var player = avatar.getPlayer();
        if (player != null && player.getSession() != null) {
            player.sendPacket(new PacketAvatarFightPropUpdateNotify(avatar, property));
        }
    }

    record ResolvedFightPropertyModifier(
            String key, FightProperty property, float value, float durationSeconds) {}

    @FunctionalInterface
    interface ExpiryScheduler {
        void schedule(Runnable expiry, int delayTicks);
    }
}
