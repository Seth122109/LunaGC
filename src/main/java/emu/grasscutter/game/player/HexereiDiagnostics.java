package emu.grasscutter.game.player;

import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.GameEntity;
import java.util.Set;
import java.util.stream.Collectors;

/** Temporary, narrowly scoped probes for the GC 7.0 Venti Hexerei diagnostic candidate. */
public final class HexereiDiagnostics {
    public static final String TAG = "[HEX-V70-VENTI-DIAG-4F2C]";
    public static final int VENTI_AVATAR_ID = 10000022;
    public static final String TEAM_SGV = "SGV_HexenzirkelLevel";
    public static final String VENTI_HEX_ABILITY = "Avatar_Venti_HexenzirkelSkill";
    public static final String VENTI_IS_HEX_VALUE = "_ABILITY_Venti_Is_Hexenzirkel";
    public static final String VENTI_BURST_ENCHANTED_VALUE =
            "_ABILITY_Venti_ElementalBurst_Enchanted";

    private static final String VENTI_NORMAL_ATTACK_PREFIX = "Avatar_Venti_ShootArrow_0";
    private static final Set<String> NORMAL_ATTACK_PREDICATE_VALUES = Set.of(
            VENTI_IS_HEX_VALUE,
            VENTI_BURST_ENCHANTED_VALUE,
            TEAM_SGV);
    private static final Set<String> VENTI_RUNTIME_VALUES = Set.of(
            VENTI_IS_HEX_VALUE,
            VENTI_BURST_ENCHANTED_VALUE);

    private HexereiDiagnostics() {}

    public static String abilityName(Ability ability) {
        return ability == null || ability.getData() == null
                ? null
                : ability.getData().abilityName;
    }

    public static boolean isVentiNormalAttackName(String abilityName) {
        return abilityName != null && abilityName.startsWith(VENTI_NORMAL_ATTACK_PREFIX);
    }

    public static boolean shouldTraceNormalAttackPredicate(Ability ability, String key) {
        return isVentiNormalAttackName(abilityName(ability))
                && NORMAL_ATTACK_PREDICATE_VALUES.contains(key);
    }

    public static boolean isPrimaryNormalAttackBranch(AbilityModifierAction action) {
        return action != null
                && action.failActions != null
                && action.failActions.length > 0
                && action.successActions != null
                && action.successActions.length > 0;
    }

    public static boolean shouldTraceVentiRuntimeValue(GameEntity entity, String key) {
        return isVentiEntity(entity) && VENTI_RUNTIME_VALUES.contains(key);
    }

    public static boolean isVentiEntity(GameEntity entity) {
        return entity instanceof EntityAvatar avatarEntity
                && avatarEntity.getAvatar() != null
                && avatarEntity.getAvatar().getAvatarId() == VENTI_AVATAR_ID;
    }

    public static boolean hasEffectivelyActiveVenti(Player player) {
        if (player == null || player.getHexereiManager() == null || player.getTeamManager() == null) {
            return false;
        }
        return player.getTeamManager().getActiveTeam().stream()
                .map(EntityAvatar::getAvatar)
                .anyMatch(avatar -> avatar != null
                        && avatar.getAvatarId() == VENTI_AVATAR_ID
                        && player.getHexereiManager().isEffectivelyActive(avatar));
    }

    public static String activeAvatarIds(Player player) {
        if (player == null || player.getTeamManager() == null) return "";
        return player.getTeamManager().getActiveTeam().stream()
                .map(EntityAvatar::getAvatar)
                .filter(avatar -> avatar != null)
                .map(avatar -> Integer.toString(avatar.getAvatarId()))
                .collect(Collectors.joining(","));
    }
}
