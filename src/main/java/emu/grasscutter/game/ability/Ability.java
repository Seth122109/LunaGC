package emu.grasscutter.game.ability;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.data.binout.OpenConfigEntry.AbilityVarSetter;
import emu.grasscutter.data.excels.ProudSkillData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.AbilityStringOuterClass.AbilityString;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import java.util.*;
import lombok.Getter;

public class Ability {
    @Getter private AbilityData data;
    @Getter private GameEntity owner;
    @Getter private Player playerOwner;

    @Getter private AbilityManager manager;

    @Getter private Map<String, AbilityModifierController> modifiers = new HashMap<>();
    @Getter private Object2FloatMap<String> abilitySpecials = new Object2FloatOpenHashMap<>();

    @Getter
    private static Map<String, Object2FloatMap<String>> abilitySpecialsModified = new HashMap<>();

    @Getter private int hash;
    @Getter private Set<Integer> avatarSkillStartIds;

    public Ability(AbilityData data, GameEntity owner, Player playerOwner) {
        this.data = data;
        this.owner = owner;
        this.manager = owner.getWorld().getHost().getAbilityManager();

        if (this.data.abilitySpecials != null) {
            for (var entry : this.data.abilitySpecials.entrySet())
                abilitySpecials.put(entry.getKey(), entry.getValue().floatValue());
        }

        this.playerOwner = playerOwner;

        Avatar casterAvatar = resolveCasterAvatar(playerOwner, owner);
        if (casterAvatar != null) {
            applyConstellationSpecials(casterAvatar);
            applySkillSpecials(casterAvatar);
            applyEquipAffixSpecials(this.data, casterAvatar, this.abilitySpecials);
        }

        hash = Utils.abilityHash(data.abilityName);

        data.initialize();

        avatarSkillStartIds = new HashSet<>();
        if (data.onAbilityStart != null) {
            avatarSkillStartIds.addAll(
                    Arrays.stream(data.onAbilityStart)
                            .filter(action -> action.type == AbilityModifierAction.Type.AvatarSkillStart)
                            .map(action -> action.skillID)
                            .toList());
        }
        avatarSkillStartIds.addAll(
                data.modifiers.values().stream()
                        .map(
                                m ->
                                        (List<AbilityModifierAction>)
                                                (m.onAdded == null ? Collections.emptyList() : Arrays.asList(m.onAdded)))
                        .flatMap(List::stream)
                        .filter(action -> action.type == AbilityModifierAction.Type.AvatarSkillStart)
                        .map(action -> action.skillID)
                        .toList());

        if (data.onAdded != null) {
            processOnAddedAbilityModifiers();
        }
    }

    private Avatar resolveCasterAvatar(Player player, GameEntity owner) {
        EntityAvatar entity = resolveCasterEntity(player, owner);
        return entity != null ? entity.getAvatar() : null;
    }

    public EntityAvatar getCasterEntity() {
        return resolveCasterEntity(this.playerOwner, this.owner);
    }

    private EntityAvatar resolveCasterEntity(Player player, GameEntity owner) {
        if (player == null || data.abilityName == null) {
            return owner instanceof EntityAvatar avatarEntity ? avatarEntity : null;
        }
        String abilityName = data.abilityName;
        for (var member : player.getTeamManager().getActiveTeam()) {
            var av = member.getAvatar();
            var avData = av.getAvatarData();
            if (avData == null) continue;
            String charName = avData.getName();
            if (charName == null || charName.isEmpty()) continue;
            if (abilityName.startsWith(charName + "_")
                || abilityName.contains("_" + charName + "_")
                || abilityName.endsWith("_" + charName)) {
                return member;
            }
        }
        return owner instanceof EntityAvatar avatarEntity ? avatarEntity : null;
    }

    private void applyConstellationSpecials(Avatar avatar) {
        for (int talentId : avatar.getTalentIdList()) {
            var talentData = GameData.getAvatarTalentDataMap().get(talentId);
            if (talentData == null || talentData.getOpenConfig() == null) continue;
            var entry = GameData.getOpenConfigEntries().get(talentData.getOpenConfig());
            if (entry == null || entry.getAbilityVarSetters() == null) continue;
            float[] params = talentData.getParamList();
            if (params == null) continue;
            for (AbilityVarSetter setter : entry.getAbilityVarSetters()) {
                if (!data.abilityName.equals(setter.getAbilityName())) continue;
                int idx = setter.getParamIndex();
                if (idx >= params.length) continue;
                abilitySpecials.put(setter.getVarName(), params[idx]);
            }
        }
    }

    private void applySkillSpecials(Avatar avatar) {
        var depot = GameData.getAvatarSkillDepotDataMap().get(avatar.getSkillDepotId());
        if (depot == null) return;
        var skillLevelMap = avatar.getSkillLevelMap();
        depot.getSkillsAndEnergySkill().forEach(skillId -> {
            var skillData = GameData.getAvatarSkillDataMap().get(skillId);
            if (skillData == null || skillData.getProudSkillGroupId() == 0) return;
            int level = skillLevelMap.getOrDefault(skillId, 1);
            ProudSkillData proudSkill = GameData.getProudSkillDataMap().get(skillData.getProudSkillGroupId() * 100 + level);
            if (proudSkill == null || proudSkill.getOpenConfig() == null) return;
            var entry = GameData.getOpenConfigEntries().get(proudSkill.getOpenConfig());
            if (entry == null || entry.getAbilityVarSetters() == null) return;
            float[] params = proudSkill.getParamList();
            if (params == null) return;
            for (AbilityVarSetter setter : entry.getAbilityVarSetters()) {
                if (!data.abilityName.equals(setter.getAbilityName())) continue;
                int idx = setter.getParamIndex();
                if (idx >= params.length) continue;
                abilitySpecials.put(setter.getVarName(), params[idx]);
            }
        });
    }

    public static void applyEquipAffixSpecials(
            AbilityData abilityData,
            Avatar avatar,
            Object2FloatMap<String> specials) {
        if (abilityData == null
                || abilityData.abilityName == null
                || avatar == null
                || specials == null) {
            return;
        }

        var equippedSetCounts = new Int2IntOpenHashMap();
        for (int slotId = 1; slotId <= 5; slotId++) {
            var item = avatar.getEquips().get(slotId);
            if (item == null || item.getItemData() == null) continue;
            int setId = item.getItemData().getSetId();
            if (setId > 0) equippedSetCounts.addTo(setId, 1);
        }

        equippedSetCounts.forEach(
                (setId, equippedCount) -> {
                    var setData = GameData.getReliquarySetDataMap().get((int) setId);
                    if (setData == null || setData.getSetNeedNum() == null) return;

                    int[] setNeedNum = setData.getSetNeedNum();
                    for (int setIndex = 0; setIndex < setNeedNum.length; setIndex++) {
                        if (equippedCount < setNeedNum[setIndex]) break;

                        int affixId = (setData.getEquipAffixId() * 10) + setIndex;
                        var affix = GameData.getEquipAffixDataMap().get(affixId);
                        if (affix == null
                                || affix.getOpenConfig() == null
                                || affix.getParamList() == null) {
                            continue;
                        }

                        var entry = GameData.getOpenConfigEntries().get(affix.getOpenConfig());
                        if (entry == null || entry.getAbilityVarSetters() == null) continue;

                        float[] params = affix.getParamList();
                        for (AbilityVarSetter setter : entry.getAbilityVarSetters()) {
                            if (!abilityData.abilityName.equals(setter.getAbilityName())) continue;
                            int paramIndex = setter.getParamIndex();
                            if (paramIndex < 0 || paramIndex >= params.length) continue;
                            specials.put(setter.getVarName(), params[paramIndex]);
                        }
                    }
                });
    }

    public void processOnAddedAbilityModifiers() {
        for (AbilityModifierAction modifierAction : data.onAdded) {
            if (modifierAction.type == null) continue;

            if (modifierAction.type == AbilityModifierAction.Type.ApplyModifier) {
                if (modifierAction.modifierName == null) continue;
                else if (!data.modifiers.containsKey(modifierAction.modifierName)) continue;

                var modifierData = data.modifiers.get(modifierAction.modifierName);
                owner.onAddAbilityModifier(modifierData);
            }
        }
    }

    public static String getAbilityName(AbilityString abString) {
        if (abString.hasStr()) return abString.getStr();
        if (abString.hasHash()) return GameData.getAbilityHashes().get(abString.getHash());

        return null;
    }

    @Override
    public String toString() {
        return "Ability Name: %s; Entity Owner: %s; Player Owner: %s"
                .formatted(data.abilityName, owner, playerOwner);
    }
}
