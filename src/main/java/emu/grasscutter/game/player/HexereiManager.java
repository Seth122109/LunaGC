package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.avatar.Avatar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** Owns the explicit, per-player GC 7.0 Hexerei activation fallback. */
@Entity(useDiscriminator = false)
public final class HexereiManager extends BasePlayerDataManager {
    public static final int SCHEMA_VERSION = 1;
    public static final int CONSOLE_ACTOR_UID = 0;
    public static final String AUTHORITATIVE_COMPLETION = "UNAVAILABLE_RESOURCE";
    private static final String HEXEREI_TAG = "AVATAR_TAG_HEXENZIRKEL";

    private static final Map<Integer, RosterEntry> ROSTER = buildRoster();

    private Map<Integer, ActivationRecord> activationRecords;

    public HexereiManager() {
        this.activationRecords = new HashMap<>();
    }

    public HexereiManager(Player player) {
        this();
        this.setPlayer(player);
    }

    HexereiManager(Map<Integer, ActivationRecord> activationRecords) {
        this.activationRecords = new HashMap<>(activationRecords);
    }

    private static Map<Integer, RosterEntry> buildRoster() {
        Map<Integer, RosterEntry> roster = new LinkedHashMap<>();
        add(roster, new RosterEntry("Durin", 10000123, 12301, 10006, 1000606, 12351, 1235101, "Durin_Hexenzirkel_1"));
        add(roster, new RosterEntry("Venti", 10000022, 2201, 10007, 1000707, 2251, 225101, "Venti_Hexenzirkel_1"));
        add(roster, new RosterEntry("Klee", 10000029, 2901, 10010, 1001008, 2951, 295101, "Klee_Hexenzirkel_1"));
        add(roster, new RosterEntry("Albedo", 10000038, 3801, 10005, 1000502, 3851, 385101, "Albedo_Hexenzirkel_1"));
        add(roster, new RosterEntry("Mona", 10000041, 4101, 10004, 1000406, 4151, 415101, "Mona_Hexenzirkel_1"));
        add(roster, new RosterEntry("Fischl", 10000031, 3101, 10003, 1000312, 3151, 315101, "Fischl_Hexenzirkel_1"));
        add(roster, new RosterEntry("Sucrose", 10000043, 4301, 10009, 1000911, 4351, 435101, "Sucrose_Hexenzirkel_1"));
        add(roster, new RosterEntry("Razor", 10000020, 2001, 10008, 1000806, 2051, 205101, "Razor_Hexenzirkel_1"));
        add(roster, new RosterEntry("Varka", 10000128, 12801, 70074, 7007409, 12851, 1285101, "Varka_Hexenzirkel_1"));
        add(roster, new RosterEntry("Nicole", 10000131, 13101, 76159, 7615907, 13151, 1315101, "Nicole_PermanentSkill_3"));
        add(roster, new RosterEntry("Lohen", 10000129, 12901, 70096, 7009606, 12951, 1295101, "Lohen_Hexenzirkel_1"));
        add(roster, new RosterEntry("Prune", 10000132, 13201, 70092, 7009203, 13251, 1325101, "Prune_PermanentSkill_3"));
        return Collections.unmodifiableMap(roster);
    }

    private static void add(Map<Integer, RosterEntry> roster, RosterEntry entry) {
        if (roster.put(entry.avatarId(), entry) != null) {
            throw new IllegalStateException("Duplicate Hexerei avatar ID " + entry.avatarId());
        }
    }

    public static Map<Integer, RosterEntry> getRoster() {
        return ROSTER;
    }

    public static RosterEntry getRosterEntry(int avatarId) {
        return ROSTER.get(avatarId);
    }

    public void attachPlayer(Player player) {
        this.player = player;
        if (this.activationRecords == null) {
            this.activationRecords = new HashMap<>();
        }
    }

    public Map<Integer, ActivationRecord> getActivationRecords() {
        return Collections.unmodifiableMap(this.records());
    }

    public ActivationRecord getRecord(int avatarId) {
        return this.records().get(avatarId);
    }

    public boolean hasValidManualRecord(int avatarId) {
        return validateRecord(avatarId, this.getRecord(avatarId));
    }

    public boolean isEffectivelyActive(Avatar avatar) {
        if (avatar == null) return false;
        return this.isEffectivelyActive(
                avatar.getAvatarId(),
                avatar.getSkillDepotId(),
                hasStaticEligibility(avatar));
    }

    boolean isEffectivelyActive(int avatarId, int skillDepotId, boolean staticallyEligible) {
        RosterEntry entry = ROSTER.get(avatarId);
        return entry != null
                && staticallyEligible
                && entry.skillDepotId() == skillDepotId
                && this.hasValidManualRecord(avatarId);
    }

    public int countEffectiveActivations(Stream<Avatar> avatars) {
        if (avatars == null) return 0;
        return (int) avatars.filter(this::isEffectivelyActive).count();
    }

    public ActivationResult activateOwnedAvatar(Avatar avatar, int actorUid, long activatedAtEpochSecond) {
        boolean owned = avatar != null
                && this.player != null
                && this.player.getAvatars() != null
                && this.player.getAvatars().getAvatarById(avatar.getAvatarId()) == avatar;
        return this.activate(
                avatar == null ? 0 : avatar.getAvatarId(),
                avatar == null ? 0 : avatar.getSkillDepotId(),
                owned,
                hasStaticEligibility(avatar),
                actorUid,
                activatedAtEpochSecond);
    }

    public ActivationResult activateOwnedAvatar(
            int avatarId, int actorUid, long activatedAtEpochSecond) {
        Avatar avatar = this.player == null || this.player.getAvatars() == null
                ? null
                : this.player.getAvatars().getAvatarById(avatarId);
        if (avatar == null) {
            return this.activate(avatarId, 0, false, false, actorUid, activatedAtEpochSecond);
        }
        return this.activateOwnedAvatar(avatar, actorUid, activatedAtEpochSecond);
    }

    ActivationResult activate(
            int avatarId,
            int skillDepotId,
            boolean owned,
            boolean staticallyEligible,
            int actorUid,
            long activatedAtEpochSecond) {
        RosterEntry entry = ROSTER.get(avatarId);
        if (entry == null) return ActivationResult.NOT_MAPPED;
        if (!owned) return ActivationResult.NOT_OWNED;
        if (!staticallyEligible) return ActivationResult.INELIGIBLE;
        if (entry.skillDepotId() != skillDepotId) return ActivationResult.DEPOT_MISMATCH;

        ActivationRecord current = this.getRecord(avatarId);
        if (current != null) {
            return validateRecord(avatarId, current)
                    ? ActivationResult.ALREADY_ACTIVE
                    : ActivationResult.INVALID_RECORD_PRESENT;
        }

        this.records().put(
                avatarId,
                new ActivationRecord(
                        SCHEMA_VERSION,
                        avatarId,
                        entry.endQuestId(),
                        ActivationProvenance.MANUAL_COMMAND,
                        actorUid,
                        activatedAtEpochSecond));
        return ActivationResult.ACTIVATED;
    }

    public boolean reset(int avatarId) {
        return this.records().remove(avatarId) != null;
    }

    public Set<Integer> resetAll() {
        Set<Integer> removed = new HashSet<>(this.records().keySet());
        this.records().clear();
        return Collections.unmodifiableSet(removed);
    }

    public StatusSnapshot inspect(Avatar avatar) {
        if (avatar == null) return null;
        boolean owned = this.player != null
                && this.player.getAvatars() != null
                && this.player.getAvatars().getAvatarById(avatar.getAvatarId()) == avatar;
        return this.inspect(
                avatar.getAvatarId(),
                owned,
                hasStaticEligibility(avatar),
                avatar.getSkillDepotId(),
                avatar.getProudSkillList());
    }

    public StatusSnapshot inspectUnowned(int avatarId) {
        RosterEntry entry = ROSTER.get(avatarId);
        if (entry == null) return null;
        return this.inspect(
                avatarId,
                false,
                hasStaticEligibility(avatarId),
                entry.skillDepotId(),
                Collections.emptySet());
    }

    StatusSnapshot inspect(
            int avatarId,
            boolean owned,
            boolean staticallyEligible,
            int skillDepotId,
            Set<Integer> rawProudSkills) {
        RosterEntry entry = ROSTER.get(avatarId);
        if (entry == null) return null;

        ActivationRecord record = this.getRecord(avatarId);
        boolean validRecord = validateRecord(avatarId, record);
        boolean effective = owned
                && staticallyEligible
                && entry.skillDepotId() == skillDepotId
                && validRecord;
        boolean rawProud = rawProudSkills != null && rawProudSkills.contains(entry.proudSkillId());
        boolean effectiveProud = effectiveProudSkillView(avatarId, rawProudSkills, effective)
                .contains(entry.proudSkillId());

        Consistency consistency;
        if (!owned) {
            consistency = Consistency.NOT_OWNED;
        } else if (!staticallyEligible) {
            consistency = Consistency.ELIGIBILITY_MISMATCH;
        } else if (entry.skillDepotId() != skillDepotId) {
            consistency = Consistency.DEPOT_MISMATCH;
        } else if (record != null && !validRecord) {
            consistency = Consistency.INVALID_RECORD;
        } else if (!validRecord) {
            consistency = rawProud
                    ? Consistency.INACTIVE_RAW_FILTERED
                    : Consistency.INACTIVE_NO_RAW;
        } else {
            consistency = rawProud
                    ? Consistency.ACTIVE_CONSISTENT
                    : Consistency.ACTIVE_MISSING_RAW;
        }

        return new StatusSnapshot(
                entry,
                owned,
                staticallyEligible,
                skillDepotId,
                record,
                validRecord,
                effective,
                rawProud,
                effectiveProud,
                consistency);
    }

    public static Set<Integer> effectiveProudSkillView(
            int avatarId, Set<Integer> rawProudSkills, boolean effectiveActivation) {
        if (rawProudSkills == null || rawProudSkills.isEmpty()) {
            return Collections.emptySet();
        }
        RosterEntry entry = ROSTER.get(avatarId);
        if (entry == null || effectiveActivation || !rawProudSkills.contains(entry.proudSkillId())) {
            return Collections.unmodifiableSet(rawProudSkills);
        }
        Set<Integer> filtered = new HashSet<>(rawProudSkills);
        filtered.remove(entry.proudSkillId());
        return Collections.unmodifiableSet(filtered);
    }

    public static boolean hasStaticEligibility(Avatar avatar) {
        if (avatar == null || !ROSTER.containsKey(avatar.getAvatarId()) || avatar.getAvatarData() == null) {
            return false;
        }
        var tags = avatar.getAvatarData().getTags();
        return tags != null && tags.contains(HEXEREI_TAG);
    }

    public static boolean hasStaticEligibility(int avatarId) {
        if (!ROSTER.containsKey(avatarId)) return false;
        var avatarData = GameData.getAvatarDataMap().get(avatarId);
        if (avatarData == null) return false;
        var tags = avatarData.getTags();
        return tags != null && tags.contains(HEXEREI_TAG);
    }

    private static boolean validateRecord(int mapAvatarId, ActivationRecord record) {
        if (record == null) return false;
        RosterEntry entry = ROSTER.get(mapAvatarId);
        return entry != null
                && record.getSchemaVersion() == SCHEMA_VERSION
                && record.getAvatarId() == mapAvatarId
                && record.getAvatarId() == entry.avatarId()
                && record.getEndQuestId() == entry.endQuestId()
                && ActivationProvenance.MANUAL_COMMAND.name().equals(record.getProvenance());
    }

    private Map<Integer, ActivationRecord> records() {
        if (this.activationRecords == null) {
            this.activationRecords = new HashMap<>();
        }
        return this.activationRecords;
    }

    public enum ActivationProvenance {
        MANUAL_COMMAND
    }

    public enum ActivationResult {
        ACTIVATED,
        ALREADY_ACTIVE,
        INVALID_RECORD_PRESENT,
        NOT_MAPPED,
        NOT_OWNED,
        INELIGIBLE,
        DEPOT_MISMATCH
    }

    public enum Consistency {
        NOT_OWNED,
        ELIGIBILITY_MISMATCH,
        DEPOT_MISMATCH,
        INVALID_RECORD,
        INACTIVE_RAW_FILTERED,
        INACTIVE_NO_RAW,
        ACTIVE_CONSISTENT,
        ACTIVE_MISSING_RAW
    }

    public record RosterEntry(
            String name,
            int avatarId,
            int skillDepotId,
            int mainQuestId,
            int endQuestId,
            int proudSkillGroupId,
            int proudSkillId,
            String openConfig) {}

    public record StatusSnapshot(
            RosterEntry entry,
            boolean owned,
            boolean staticallyEligible,
            int actualSkillDepotId,
            ActivationRecord record,
            boolean validRecord,
            boolean effectiveActivation,
            boolean rawProudPresent,
            boolean effectiveProudPresent,
            Consistency consistency) {}

    @Entity(useDiscriminator = false)
    public static final class ActivationRecord {
        private int schemaVersion;
        private int avatarId;
        private int endQuestId;
        private String provenance;
        private int actorUid;
        private long activatedAtEpochSecond;

        @Deprecated
        public ActivationRecord() {}

        public ActivationRecord(
                int schemaVersion,
                int avatarId,
                int endQuestId,
                ActivationProvenance provenance,
                int actorUid,
                long activatedAtEpochSecond) {
            this(
                    schemaVersion,
                    avatarId,
                    endQuestId,
                    provenance == null ? null : provenance.name(),
                    actorUid,
                    activatedAtEpochSecond);
        }

        ActivationRecord(
                int schemaVersion,
                int avatarId,
                int endQuestId,
                String provenance,
                int actorUid,
                long activatedAtEpochSecond) {
            this.schemaVersion = schemaVersion;
            this.avatarId = avatarId;
            this.endQuestId = endQuestId;
            this.provenance = provenance;
            this.actorUid = actorUid;
            this.activatedAtEpochSecond = activatedAtEpochSecond;
        }

        public int getSchemaVersion() {
            return schemaVersion;
        }

        public int getAvatarId() {
            return avatarId;
        }

        public int getEndQuestId() {
            return endQuestId;
        }

        public String getProvenance() {
            return provenance;
        }

        public int getActorUid() {
            return actorUid;
        }

        public long getActivatedAtEpochSecond() {
            return activatedAtEpochSecond;
        }
    }
}
