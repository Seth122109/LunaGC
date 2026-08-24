package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.HexereiDiagnostics;
import emu.grasscutter.game.player.HexereiManager;
import emu.grasscutter.game.player.HexereiManager.ActivationRecord;
import emu.grasscutter.game.player.HexereiManager.ActivationResult;
import emu.grasscutter.game.player.HexereiManager.RosterEntry;
import emu.grasscutter.game.player.HexereiManager.StatusSnapshot;
import emu.grasscutter.game.player.HexereiManager.TeamSnapshot;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketAvatarSkillDepotChangeNotify;
import emu.grasscutter.server.packet.send.PacketProudSkillChangeNotify;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Command(
        label = "hexerei",
        usage = {"team", "status [all|<avatarId>]", "activate <avatarId>", "reset <avatarId|all>"},
        permission = "player.hexerei",
        permissionTargeted = "player.hexerei.others",
        targetRequirement = Command.TargetRequirement.ONLINE)
public final class HexereiCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.isEmpty()) {
            this.sendUsageMessage(sender);
            return;
        }

        switch (args.get(0).toLowerCase()) {
            case "team" -> this.team(sender, targetPlayer, args);
            case "status" -> this.status(sender, targetPlayer, args);
            case "activate" -> this.activate(sender, targetPlayer, args);
            case "reset" -> this.reset(sender, targetPlayer, args);
            default -> this.sendUsageMessage(sender);
        }
    }

    private void team(Player sender, Player target, List<String> args) {
        if (args.size() != 1) {
            this.sendUsageMessage(sender);
            return;
        }

        var activeTeam = target.getTeamManager().getActiveTeam();
        var teamEntity = target.getTeamManager().getEntity();
        Float serverCachedSgvValue = teamEntity == null
                ? null
                : teamEntity.getGlobalAbilityValues().get(HexereiDiagnostics.TEAM_SGV);
        TeamSnapshot snapshot = target.getHexereiManager().inspectTeam(
                activeTeam.stream().map(entity -> entity.getAvatar()),
                serverCachedSgvValue);
        String activeAvatarIds = snapshot.members().stream()
                .map(member -> Integer.toString(member.avatarId()))
                .reduce((left, right) -> left + "," + right)
                .orElse("NONE");

        CommandHandler.sendMessage(
                sender,
                "hexerei team"
                        + " active_avatar_ids=" + activeAvatarIds
                        + " calculated_team_count=" + snapshot.calculatedTeamCount()
                        + " sgv_name=" + HexereiDiagnostics.TEAM_SGV
                        + " sgv_send_value=" + snapshot.sgvValueToSend()
                        + " team_entity_id=" + value(teamEntity == null ? null : teamEntity.getId())
                        + " server_cached_present=" + (snapshot.serverCachedSgvValue() != null)
                        + " server_cached_value=" + value(snapshot.serverCachedSgvValue()));

        for (int slot = 0; slot < snapshot.members().size(); slot++) {
            var member = snapshot.members().get(slot);
            CommandHandler.sendMessage(
                    sender,
                    "hexerei team_member"
                            + " slot=" + (slot + 1)
                            + " avatar_id=" + member.avatarId()
                            + " name=" + member.name()
                            + " mapped=" + member.mapped()
                            + " eligible=" + member.staticallyEligible()
                            + " effective_active=" + member.effectiveActivation()
                            + " expected_depot_id=" + member.expectedSkillDepotId()
                            + " actual_depot_id=" + member.actualSkillDepotId()
                            + " consistency=" + member.consistency());
        }

        activeTeam.stream()
                .filter(HexereiDiagnostics::isVentiEntity)
                .findFirst()
                .ifPresent(venti -> {
                    boolean hexAbilityInstanced = venti.getInstancedAbilities().stream()
                            .filter(ability -> ability != null && ability.getData() != null)
                            .anyMatch(ability -> HexereiDiagnostics.VENTI_HEX_ABILITY.equals(
                                    ability.getData().abilityName));
                    boolean hexExtraEmbryoPresent = venti.getAvatar().getExtraAbilityEmbryos() != null
                            && venti.getAvatar().getExtraAbilityEmbryos()
                                    .contains(HexereiDiagnostics.VENTI_HEX_ABILITY);
                    var values = venti.getGlobalAbilityValues();
                    CommandHandler.sendMessage(
                            sender,
                            "hexerei venti_boundary"
                                    + " entity_id=" + venti.getId()
                                    + " hex_extra_embryo_present=" + hexExtraEmbryoPresent
                                    + " hex_ability_instanced=" + hexAbilityInstanced
                                    + " is_hex_value="
                                    + value(values.get(HexereiDiagnostics.VENTI_IS_HEX_VALUE))
                                    + " burst_enchanted_value="
                                    + value(values.get(HexereiDiagnostics.VENTI_BURST_ENCHANTED_VALUE)));
                });
    }

    private void status(Player sender, Player target, List<String> args) {
        if (args.size() > 2) {
            this.sendUsageMessage(sender);
            return;
        }

        String selector = args.size() == 1 ? "all" : args.get(1);
        if ("all".equalsIgnoreCase(selector)) {
            for (RosterEntry entry : HexereiManager.getRoster().values()) {
                this.sendStatus(sender, target, entry);
            }
            return;
        }

        Integer avatarId = parseAvatarId(selector);
        RosterEntry entry = avatarId == null ? null : HexereiManager.getRosterEntry(avatarId);
        if (entry == null) {
            CommandHandler.sendMessage(sender, "hexerei error=avatar_not_mapped avatar_id=" + selector);
            return;
        }
        this.sendStatus(sender, target, entry);
    }

    private void sendStatus(Player sender, Player target, RosterEntry entry) {
        HexereiManager manager = target.getHexereiManager();
        Avatar avatar = target.getAvatars().getAvatarById(entry.avatarId());
        StatusSnapshot status = avatar == null
                ? manager.inspectUnowned(entry.avatarId())
                : manager.inspect(avatar);
        ActivationRecord record = status.record();
        String manualState = record == null ? "NONE" : status.validRecord() ? "VALID" : "INVALID";

        CommandHandler.sendMessage(
                sender,
                "hexerei"
                        + " avatar_id=" + entry.avatarId()
                        + " name=" + entry.name()
                        + " owned=" + status.owned()
                        + " eligible=" + status.staticallyEligible()
                        + " expected_depot_id=" + entry.skillDepotId()
                        + " actual_depot_id=" + status.actualSkillDepotId()
                        + " end_quest_id=" + entry.endQuestId()
                        + " authoritative_completion=" + HexereiManager.AUTHORITATIVE_COMPLETION
                        + " manual_state=" + manualState
                        + " effective_active=" + status.effectiveActivation()
                        + " record_schema=" + value(record == null ? null : record.getSchemaVersion())
                        + " record_provenance=" + value(record == null ? null : record.getProvenance())
                        + " actor_uid=" + value(record == null ? null : record.getActorUid())
                        + " activated_at_epoch_second="
                        + value(record == null ? null : record.getActivatedAtEpochSecond())
                        + " raw_proud_id=" + entry.proudSkillId()
                        + " raw_proud_present=" + status.rawProudPresent()
                        + " effective_proud_present=" + status.effectiveProudPresent()
                        + " consistency=" + status.consistency());
    }

    private void activate(Player sender, Player target, List<String> args) {
        if (args.size() != 2 || "all".equalsIgnoreCase(args.get(1))) {
            this.sendUsageMessage(sender);
            return;
        }
        Integer avatarId = parseAvatarId(args.get(1));
        if (avatarId == null) {
            CommandHandler.sendMessage(sender, "hexerei error=invalid_avatar_id avatar_id=" + args.get(1));
            return;
        }

        Avatar avatar = target.getAvatars().getAvatarById(avatarId);
        int actorUid = sender == null ? HexereiManager.CONSOLE_ACTOR_UID : sender.getUid();
        ActivationResult result = target.getHexereiManager().activateOwnedAvatar(
                avatarId, actorUid, Instant.now().getEpochSecond());
        if (result == ActivationResult.ACTIVATED) {
            target.save();
            refreshAvatar(target, avatar);
        }
        CommandHandler.sendMessage(
                sender, "hexerei action=activate avatar_id=" + avatarId + " result=" + result);
    }

    private void reset(Player sender, Player target, List<String> args) {
        if (args.size() != 2) {
            this.sendUsageMessage(sender);
            return;
        }

        if ("all".equalsIgnoreCase(args.get(1))) {
            Set<Integer> removed = target.getHexereiManager().resetAll();
            if (!removed.isEmpty()) {
                target.save();
                boolean activeTeamAffected = false;
                for (int avatarId : removed) {
                    Avatar avatar = target.getAvatars().getAvatarById(avatarId);
                    if (avatar == null || HexereiManager.getRosterEntry(avatarId) == null) continue;
                    refreshAvatarState(target, avatar);
                    activeTeamAffected |= isInActiveTeam(target, avatar);
                }
                if (activeTeamAffected) target.getTeamManager().sendHexereiTeamUpdate();
            }
            CommandHandler.sendMessage(
                    sender, "hexerei action=reset selector=all removed=" + removed.size());
            return;
        }

        Integer avatarId = parseAvatarId(args.get(1));
        if (avatarId == null) {
            CommandHandler.sendMessage(sender, "hexerei error=invalid_avatar_id avatar_id=" + args.get(1));
            return;
        }

        boolean removed = target.getHexereiManager().reset(avatarId);
        if (removed) {
            target.save();
            Avatar avatar = target.getAvatars().getAvatarById(avatarId);
            if (avatar != null && HexereiManager.getRosterEntry(avatarId) != null) {
                refreshAvatar(target, avatar);
            }
        }
        CommandHandler.sendMessage(
                sender,
                "hexerei action=reset avatar_id=" + avatarId + " result="
                        + (removed ? "REMOVED" : "ABSENT"));
    }

    private static void refreshAvatar(Player target, Avatar avatar) {
        refreshAvatarState(target, avatar);
        if (isInActiveTeam(target, avatar)) {
            target.getTeamManager().sendHexereiTeamUpdate();
        }
    }

    private static void refreshAvatarState(Player target, Avatar avatar) {
        avatar.recalcStats(true);
        target.sendPacket(new PacketProudSkillChangeNotify(avatar));
        target.sendPacket(new PacketAvatarSkillDepotChangeNotify(avatar));
    }

    private static boolean isInActiveTeam(Player target, Avatar avatar) {
        return target.getTeamManager().getActiveTeam().stream()
                .anyMatch(entity -> entity.getAvatar() == avatar);
    }

    private static Integer parseAvatarId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String value(Object value) {
        return value == null ? "NONE" : value.toString();
    }
}
