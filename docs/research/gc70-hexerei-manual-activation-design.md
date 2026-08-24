# GC 7.0 Hexerei manual activation design

Status date: 2026-08-24
Mode: source-only implementation design; authoritative quest recovery is `NO`
Session intent: long-running GC 7.0 repair program, bounded implementation slice

## Goal and success criteria

Add the smallest explicit, reversible, per-player Hexerei fallback for the 12 verified GC 7.0 characters. A manual record may enable the existing Hexerei proud/open-config, predicate, SGV, and team-packet effects, but it never represents or mutates quest completion. Success means activation is owned, eligible, audited, persisted on `Player`, validated fail-closed, reflected after login/reconnect, removable without touching any other player state, and covered by focused unit tests.

Witch's Revelation and Lucid Revelations are out of scope and remain a separate namespace.

## Exact roster and seams

`HexereiManager` owns one immutable 12-entry map with these exact joins:

| Name | Avatar | Depot | Main / end quest | Proud group / ID | Open config |
| --- | ---: | ---: | --- | --- | --- |
| Durin | `10000123` | `12301` | `10006` / `1000606` | `12351` / `1235101` | `Durin_Hexenzirkel_1` |
| Venti | `10000022` | `2201` | `10007` / `1000707` | `2251` / `225101` | `Venti_Hexenzirkel_1` |
| Klee | `10000029` | `2901` | `10010` / `1001008` | `2951` / `295101` | `Klee_Hexenzirkel_1` |
| Albedo | `10000038` | `3801` | `10005` / `1000502` | `3851` / `385101` | `Albedo_Hexenzirkel_1` |
| Mona | `10000041` | `4101` | `10004` / `1000406` | `4151` / `415101` | `Mona_Hexenzirkel_1` |
| Fischl | `10000031` | `3101` | `10003` / `1000312` | `3151` / `315101` | `Fischl_Hexenzirkel_1` |
| Sucrose | `10000043` | `4301` | `10009` / `1000911` | `4351` / `435101` | `Sucrose_Hexenzirkel_1` |
| Razor | `10000020` | `2001` | `10008` / `1000806` | `2051` / `205101` | `Razor_Hexenzirkel_1` |
| Varka | `10000128` | `12801` | `70074` / `7007409` | `12851` / `1285101` | `Varka_Hexenzirkel_1` |
| Nicole | `10000131` | `13101` | `76159` / `7615907` | `13151` / `1315101` | `Nicole_PermanentSkill_3` |
| Lohen | `10000129` | `12901` | `70096` / `7009606` | `12951` / `1295101` | `Lohen_Hexenzirkel_1` |
| Prune | `10000132` | `13201` | `70092` / `7009203` | `13251` / `1325101` | `Prune_PermanentSkill_3` |

Planned source seams are `Player`, new `HexereiManager`, `Avatar`, `PredicateEvaluator`, `PacketPlayerEnterSceneInfoNotify`, `TeamManager`, `HandlerSceneInitFinishReq`, avatar/entity/skill-depot/proud protocol builders, enter-scene proud ability variables, and stamina proud-skill consumers. The new dedicated online command is `hexerei`; no quest, talent, debug, open-state, or generated-protocol surface is reused.

## Persistence, migration, and reconnect

`Player` persists one `HexereiManager` namespace. The manager persists a map keyed by avatar ID; each `schemaVersion=1` record contains `avatarId`, `endQuestId`, provenance enum `MANUAL_COMMAND`, `actorUid`, and `activatedAtEpochSecond`. Actor UID `0` is the documented server-console sentinel.

The no-argument `Player` constructor initializes the manager. `Player.@PostLoad` creates an empty manager when the field is absent and reattaches the transient `Player` reference without saving. Existing documents therefore migrate in memory to an empty namespace; the next explicit activation/reset is the only operation in this slice that saves it. Login/reconnect loads records before avatar reconstruction; the effective view becomes authoritative for derived recalculation once avatar ownership is attached.

Map presence alone never activates. Validation requires schema version, key/avatar ID, roster avatar/end-quest IDs, and exact provenance to match. A bad or mismatched record fails closed, is reported as invalid, and remains untouched until `reset` explicitly removes it. Activation is idempotent and preserves the first valid audit record; it refuses to overwrite an invalid record.

No `Avatar` save occurs during activation/reset. `Avatar.proudSkillList` stays persisted/raw diagnostic state. Quest state, `GameQuest`, quest globals, open states, talent IDs, skill levels, and all other existing player/avatar fields are neither repurposed nor overwritten.

## Effective state and runtime effects

Eligibility requires an entry in the fixed roster, the runtime `AVATAR_TAG_HEXENZIRKEL` tag, ownership, and the mapped depot. Effective activation additionally requires a valid manual record. Static tag membership is eligibility only.

`Avatar.getEffectiveProudSkillList()` returns the raw list except that, for a mapped Hexerei avatar lacking effective activation, it filters only that avatar's mapped special proud-skill ID. It does not add a missing proud skill and does not filter any non-Hex or unrelated quest proud skill. Stat/open-config application, avatar/entity/depot/proud protocol builders, enter-scene ability variables, `ByUnlockTalentParam`, and relevant stamina consumers use this view. Raw reconstruction and inventory materialization keep using `proudSkillList`.

`BJJDEAIEIGP` keeps its current entity-selection behavior and changes only its final gate from static tag presence to effective activation. This slice does not resolve the resource `Self`/`Target` distinction or change unknown-predicate behavior.

Hex team count is centralized in `HexereiManager` and used by `PacketPlayerEnterSceneInfoNotify`, `TeamManager`, and `HandlerSceneInitFinishReq`. Existing `SGV_HexenzirkelLevel` and `PacketTeamHexenzirkelChangeNotify` are updated; hand-coded packet fields are unchanged. Activate/reset recalculates the affected owned avatar, sends existing proud/depot client state, and refreshes only existing Hex SGV/team packet effects when the avatar is in the active team.

## Command and consistency contract

`hexerei` is `ONLINE` with normal self/`@uid` targeting and separate targeted permission. Supported forms are:

- `status [all|<avatarId>]`
- `activate <avatarId>`
- `reset <avatarId|all>`

There is no activate-all. Activation requires the target to own the exact eligible mapped avatar. Reset is idempotent and removes only records in this namespace. Mutation saves `Player` only and never invokes `QuestManager` or `GameQuest`.

Status is deterministic and always says `authoritative_completion=UNAVAILABLE_RESOURCE`. It reports mapping, ownership, eligibility/tag/depot checks, manual record validity, effective activation, raw/effective proud presence, provenance/audit, and one consistency value: `NOT_OWNED`, `ELIGIBILITY_MISMATCH`, `DEPOT_MISMATCH`, `INVALID_RECORD`, `INACTIVE_RAW_FILTERED`, `INACTIVE_NO_RAW`, `ACTIVE_CONSISTENT`, or `ACTIVE_MISSING_RAW`. Manual active is never labeled quest completed.

## Tests, verification boundary, and rollback

Focused `HexereiManagerTest` coverage:

- exactly 12 unique avatar/depot/end-quest/proud mappings;
- eligibility or raw proud presence alone never activates or implies completion;
- a valid record activates;
- schema, provenance, avatar-ID, and end-quest mismatches fail closed and remain until reset;
- unrelated special proud skills remain in the effective view;
- repeat activation preserves the first audit record;
- targeted reset preserves other records;
- reset-all clears only this manager's records;
- all listed consistency states.

The permitted test command is `gradlew.bat test --tests emu.grasscutter.game.player.HexereiManagerTest -x generateProto -x processResources --no-daemon`. Static Gradle inspection confirms `test` does not depend on `jar`, `build`, publishing, `run`, server/client startup, runtime resources, or root-JAR output when those two generation/resource tasks are excluded. It may update ordinary Gradle compilation/test outputs under ignored `build/`; it must not touch `resources/`, MongoDB/player data, Cultivation, the game install, or the root JAR.

No runtime verification, deployment, JAR/package build, server/client start, player-data mutation, resource installation, or CodeGraph refresh is authorized. Later UAT remains gated; the minimum eventual UAT is login/reconnect persistence, inactive/active proud and ability behavior for one mapped avatar, two-active-member SGV behavior, idempotent activation, reset restoration, and unaffected non-Hex/other quest proud skills.

Source rollback is reviewable reversion of the new manager/command/test and the named integration hunks, plus removal of the `Player` namespace if no persisted documents have been deployed. After any later deployment, data rollback is `hexerei reset all @<uid>` per affected player before reverting code; no quest or avatar-field restoration should be necessary.
