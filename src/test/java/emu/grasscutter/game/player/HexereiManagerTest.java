package emu.grasscutter.game.player;

import static org.junit.jupiter.api.Assertions.*;

import emu.grasscutter.game.player.HexereiManager.ActivationProvenance;
import emu.grasscutter.game.player.HexereiManager.ActivationRecord;
import emu.grasscutter.game.player.HexereiManager.ActivationResult;
import emu.grasscutter.game.player.HexereiManager.Consistency;
import emu.grasscutter.game.player.HexereiManager.RosterEntry;
import emu.grasscutter.game.player.HexereiManager.TeamMemberSnapshot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HexereiManagerTest {
    private static final RosterEntry DURIN = HexereiManager.getRosterEntry(10000123);
    private static final RosterEntry VENTI = HexereiManager.getRosterEntry(10000022);

    @Test
    void rosterHasExactlyTwelveUniqueVerifiedMappings() {
        var roster = HexereiManager.getRoster();
        assertEquals(
                Map.ofEntries(
                        mapped("Durin", 10000123, 12301, 10006, 1000606, 12351, 1235101, "Durin_Hexenzirkel_1"),
                        mapped("Venti", 10000022, 2201, 10007, 1000707, 2251, 225101, "Venti_Hexenzirkel_1"),
                        mapped("Klee", 10000029, 2901, 10010, 1001008, 2951, 295101, "Klee_Hexenzirkel_1"),
                        mapped("Albedo", 10000038, 3801, 10005, 1000502, 3851, 385101, "Albedo_Hexenzirkel_1"),
                        mapped("Mona", 10000041, 4101, 10004, 1000406, 4151, 415101, "Mona_Hexenzirkel_1"),
                        mapped("Fischl", 10000031, 3101, 10003, 1000312, 3151, 315101, "Fischl_Hexenzirkel_1"),
                        mapped("Sucrose", 10000043, 4301, 10009, 1000911, 4351, 435101, "Sucrose_Hexenzirkel_1"),
                        mapped("Razor", 10000020, 2001, 10008, 1000806, 2051, 205101, "Razor_Hexenzirkel_1"),
                        mapped("Varka", 10000128, 12801, 70074, 7007409, 12851, 1285101, "Varka_Hexenzirkel_1"),
                        mapped("Nicole", 10000131, 13101, 76159, 7615907, 13151, 1315101, "Nicole_PermanentSkill_3"),
                        mapped("Lohen", 10000129, 12901, 70096, 7009606, 12951, 1295101, "Lohen_Hexenzirkel_1"),
                        mapped("Prune", 10000132, 13201, 70092, 7009203, 13251, 1325101, "Prune_PermanentSkill_3")),
                roster);
        assertEquals(12, uniqueCount(roster.values().stream(), RosterEntry::avatarId));
        assertEquals(12, uniqueCount(roster.values().stream(), RosterEntry::skillDepotId));
        assertEquals(12, uniqueCount(roster.values().stream(), RosterEntry::endQuestId));
        assertEquals(12, uniqueCount(roster.values().stream(), RosterEntry::proudSkillGroupId));
        assertEquals(12, uniqueCount(roster.values().stream(), RosterEntry::proudSkillId));
        assertEquals(12, uniqueCount(roster.values().stream(), RosterEntry::openConfig));
        roster.values().forEach(
                entry -> assertEquals(
                        entry.proudSkillGroupId() * 100 + 1, entry.proudSkillId()));
    }

    @Test
    void eligibilityAndRawProudPresenceNeverImplyActivationOrCompletion() {
        HexereiManager manager = new HexereiManager();
        Set<Integer> raw = Set.of(DURIN.proudSkillId());

        assertFalse(manager.isEffectivelyActive(
                DURIN.avatarId(), DURIN.skillDepotId(), true));
        var status = manager.inspect(
                DURIN.avatarId(), true, true, DURIN.skillDepotId(), raw);
        assertEquals("UNAVAILABLE_RESOURCE", HexereiManager.AUTHORITATIVE_COMPLETION);
        assertFalse(status.validRecord());
        assertFalse(status.effectiveActivation());
        assertTrue(status.rawProudPresent());
        assertFalse(status.effectiveProudPresent());
        assertEquals(Consistency.INACTIVE_RAW_FILTERED, status.consistency());
    }

    @Test
    void validManualRecordActivates() {
        HexereiManager manager = new HexereiManager();
        assertEquals(
                ActivationResult.ACTIVATED,
                manager.activate(
                        DURIN.avatarId(), DURIN.skillDepotId(), true, true, 12345, 987654321L));

        assertTrue(manager.hasValidManualRecord(DURIN.avatarId()));
        assertTrue(manager.isEffectivelyActive(
                DURIN.avatarId(), DURIN.skillDepotId(), true));
        ActivationRecord record = manager.getRecord(DURIN.avatarId());
        assertEquals(HexereiManager.SCHEMA_VERSION, record.getSchemaVersion());
        assertEquals(DURIN.endQuestId(), record.getEndQuestId());
        assertEquals(ActivationProvenance.MANUAL_COMMAND.name(), record.getProvenance());
        assertEquals(12345, record.getActorUid());
        assertEquals(987654321L, record.getActivatedAtEpochSecond());
    }

    @ParameterizedTest
    @MethodSource("invalidRecords")
    void mismatchedRecordsFailClosedAndRemainUntilReset(ActivationRecord invalidRecord) {
        HexereiManager manager = new HexereiManager(Map.of(DURIN.avatarId(), invalidRecord));

        assertFalse(manager.hasValidManualRecord(DURIN.avatarId()));
        assertFalse(manager.isEffectivelyActive(
                DURIN.avatarId(), DURIN.skillDepotId(), true));
        assertEquals(
                ActivationResult.INVALID_RECORD_PRESENT,
                manager.activate(
                        DURIN.avatarId(), DURIN.skillDepotId(), true, true, 222, 333L));
        assertSame(invalidRecord, manager.getRecord(DURIN.avatarId()));

        assertTrue(manager.reset(DURIN.avatarId()));
        assertNull(manager.getRecord(DURIN.avatarId()));
    }

    static Stream<ActivationRecord> invalidRecords() {
        return Stream.of(
                new ActivationRecord(
                        2,
                        DURIN.avatarId(),
                        DURIN.endQuestId(),
                        ActivationProvenance.MANUAL_COMMAND,
                        1,
                        2L),
                new ActivationRecord(
                        HexereiManager.SCHEMA_VERSION,
                        VENTI.avatarId(),
                        DURIN.endQuestId(),
                        ActivationProvenance.MANUAL_COMMAND,
                        1,
                        2L),
                new ActivationRecord(
                        HexereiManager.SCHEMA_VERSION,
                        DURIN.avatarId(),
                        VENTI.endQuestId(),
                        ActivationProvenance.MANUAL_COMMAND,
                        1,
                        2L),
                new ActivationRecord(
                        HexereiManager.SCHEMA_VERSION,
                        DURIN.avatarId(),
                        DURIN.endQuestId(),
                        "OTHER_PROVENANCE",
                        1,
                        2L));
    }

    @Test
    void effectiveViewFiltersOnlyTheMappedHexProudSkill() {
        int unrelatedSpecialProud = VENTI.proudSkillId();
        int unrelatedProud = 999999;
        Set<Integer> raw = Set.of(
                DURIN.proudSkillId(), unrelatedSpecialProud, unrelatedProud);

        Set<Integer> effective = HexereiManager.effectiveProudSkillView(
                DURIN.avatarId(), raw, false);
        assertFalse(effective.contains(DURIN.proudSkillId()));
        assertTrue(effective.contains(unrelatedSpecialProud));
        assertTrue(effective.contains(unrelatedProud));
        assertEquals(3, raw.size());
    }

    @Test
    void repeatedActivationPreservesFirstAuditRecord() {
        HexereiManager manager = new HexereiManager();
        assertEquals(
                ActivationResult.ACTIVATED,
                manager.activate(
                        DURIN.avatarId(), DURIN.skillDepotId(), true, true, 111, 222L));
        ActivationRecord first = manager.getRecord(DURIN.avatarId());

        assertEquals(
                ActivationResult.ALREADY_ACTIVE,
                manager.activate(
                        DURIN.avatarId(), DURIN.skillDepotId(), true, true, 333, 444L));
        assertSame(first, manager.getRecord(DURIN.avatarId()));
        assertEquals(111, first.getActorUid());
        assertEquals(222L, first.getActivatedAtEpochSecond());
    }

    @Test
    void targetedResetPreservesOtherRecords() {
        HexereiManager manager = activatedFor(DURIN, VENTI);

        assertTrue(manager.reset(DURIN.avatarId()));
        assertFalse(manager.hasValidManualRecord(DURIN.avatarId()));
        assertTrue(manager.hasValidManualRecord(VENTI.avatarId()));
    }

    @Test
    void resetAllClearsOnlyThisNamespace() {
        HexereiManager manager = activatedFor(DURIN, VENTI);
        Set<Integer> unrelatedPlayerState = new HashSet<>(Set.of(17, 18));

        Set<Integer> removed = manager.resetAll();
        assertEquals(Set.of(DURIN.avatarId(), VENTI.avatarId()), removed);
        assertTrue(manager.getActivationRecords().isEmpty());
        assertEquals(Set.of(17, 18), unrelatedPlayerState);
    }

    @Test
    void statusReportsEveryConsistencyState() {
        HexereiManager empty = new HexereiManager();
        Set<Integer> raw = Set.of(DURIN.proudSkillId());

        assertEquals(
                Consistency.NOT_OWNED,
                empty.inspect(DURIN.avatarId(), false, true, DURIN.skillDepotId(), raw).consistency());
        assertEquals(
                Consistency.ELIGIBILITY_MISMATCH,
                empty.inspect(DURIN.avatarId(), true, false, DURIN.skillDepotId(), raw).consistency());
        assertEquals(
                Consistency.DEPOT_MISMATCH,
                empty.inspect(DURIN.avatarId(), true, true, -1, raw).consistency());
        assertEquals(
                Consistency.INACTIVE_RAW_FILTERED,
                empty.inspect(DURIN.avatarId(), true, true, DURIN.skillDepotId(), raw).consistency());
        assertEquals(
                Consistency.INACTIVE_NO_RAW,
                empty.inspect(DURIN.avatarId(), true, true, DURIN.skillDepotId(), Set.of()).consistency());

        ActivationRecord bad = new ActivationRecord(
                9,
                DURIN.avatarId(),
                DURIN.endQuestId(),
                ActivationProvenance.MANUAL_COMMAND,
                1,
                2L);
        HexereiManager invalid = new HexereiManager(Map.of(DURIN.avatarId(), bad));
        assertEquals(
                Consistency.INVALID_RECORD,
                invalid.inspect(DURIN.avatarId(), true, true, DURIN.skillDepotId(), raw).consistency());

        HexereiManager active = activatedFor(DURIN);
        assertEquals(
                Consistency.ACTIVE_CONSISTENT,
                active.inspect(DURIN.avatarId(), true, true, DURIN.skillDepotId(), raw).consistency());
        assertEquals(
                Consistency.ACTIVE_MISSING_RAW,
                active.inspect(DURIN.avatarId(), true, true, DURIN.skillDepotId(), Set.of()).consistency());
    }

    @Test
    void activationRequiresMappedOwnedEligibleDepot() {
        HexereiManager manager = new HexereiManager();
        assertEquals(
                ActivationResult.NOT_MAPPED,
                manager.activate(123, 456, true, true, 1, 2L));
        assertEquals(
                ActivationResult.NOT_OWNED,
                manager.activate(
                        DURIN.avatarId(), DURIN.skillDepotId(), false, true, 1, 2L));
        assertEquals(
                ActivationResult.INELIGIBLE,
                manager.activate(
                        DURIN.avatarId(), DURIN.skillDepotId(), true, false, 1, 2L));
        assertEquals(
                ActivationResult.DEPOT_MISMATCH,
                manager.activate(DURIN.avatarId(), -1, true, true, 1, 2L));
        assertTrue(manager.getActivationRecords().isEmpty());
    }

    @Test
    void teamSnapshotCountsOnlyEffectivelyActiveMembersAndPreservesSendValue() {
        var members = List.of(
                teamMember(DURIN, true, "ACTIVE_CONSISTENT"),
                teamMember(VENTI, true, "ACTIVE_CONSISTENT"),
                teamMember(
                        HexereiManager.getRosterEntry(10000029),
                        false,
                        "INACTIVE_RAW_FILTERED"),
                new TeamMemberSnapshot(
                        10000046,
                        "UNMAPPED",
                        false,
                        false,
                        0,
                        4601,
                        false,
                        "NOT_MAPPED"));

        var snapshot = HexereiManager.summarizeTeamMembers(members, 0f);

        assertEquals(members, snapshot.members());
        assertEquals(2, snapshot.calculatedTeamCount());
        assertEquals(2f, snapshot.sgvValueToSend());
        assertEquals(0f, snapshot.serverCachedSgvValue());
    }

    @Test
    void cachesExactEffectiveTeamCountForServerPredicates() {
        Map<String, Float> values = new HashMap<>();

        HexereiManager.cacheTeamSgv(values, 2);

        assertEquals(2f, values.get(HexereiManager.TEAM_SGV));
    }

    private static HexereiManager activatedFor(RosterEntry... entries) {
        HexereiManager manager = new HexereiManager();
        for (RosterEntry entry : entries) {
            assertEquals(
                    ActivationResult.ACTIVATED,
                    manager.activate(
                            entry.avatarId(), entry.skillDepotId(), true, true, 1, 2L));
        }
        return manager;
    }

    private static TeamMemberSnapshot teamMember(
            RosterEntry entry, boolean effectiveActivation, String consistency) {
        return new TeamMemberSnapshot(
                entry.avatarId(),
                entry.name(),
                true,
                true,
                entry.skillDepotId(),
                entry.skillDepotId(),
                effectiveActivation,
                consistency);
    }

    private static <T, U> long uniqueCount(
            Stream<T> stream, Function<T, U> mapper) {
        return stream.map(mapper).collect(Collectors.toSet()).size();
    }

    private static Map.Entry<Integer, RosterEntry> mapped(
            String name,
            int avatarId,
            int skillDepotId,
            int mainQuestId,
            int endQuestId,
            int proudSkillGroupId,
            int proudSkillId,
            String openConfig) {
        return Map.entry(
                avatarId,
                new RosterEntry(
                        name,
                        avatarId,
                        skillDepotId,
                        mainQuestId,
                        endQuestId,
                        proudSkillGroupId,
                        proudSkillId,
                        openConfig));
    }
}
