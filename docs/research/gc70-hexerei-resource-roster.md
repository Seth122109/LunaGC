# GC 7.0 Hexerei resource roster

Status date: 2026-08-24  
Scope: read-only reconciliation of the approved GC 7.0 resource archive against the officially confirmed playable Hexerei roster  
Runtime status: documentation only; no extraction, resource installation, server/client start, Gradle, JAR, database, player-data, or gameplay action

## Conclusion

The tag-defined resource roster is an exact 12-for-12 match with the officially confirmed playable Hexerei roster:

**Durin, Venti, Klee, Albedo, Mona, Fischl, Sucrose, Razor, Varka, Nicole, Lohen, and Prune.**

There are no resource-only additions, official-only omissions, duplicate tagged names or IDs, tagged Traveler forms, tagged depot variants, or unresolved English display-name mappings in this archive.

## Archive verification and evidence paths

Approved archive:

- `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\LunaGC-Resources-7.0.zip`
- Size: `440,312,708` bytes
- Required SHA-256: `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF`
- Computed SHA-256: `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF` — **match**

The ZIP was inspected directly. The relevant entries were read fully and decoded as JSON without extracting them:

- `A` = `LunaGC-Resources-main/ExcelBinOutput/AvatarExcelConfigData.json`
- `D` = `LunaGC-Resources-main/ExcelBinOutput/AvatarSkillDepotExcelConfigData.json`
- `S` = `LunaGC-Resources-main/ExcelBinOutput/AvatarSkillExcelConfigData.json`
- `T` = `LunaGC-Resources-main/TextMap/TextMapEN.json`

Evidence references below use `entry#/JSON/Pointer`. `A` is a 165-record array. Exactly 12 records have a `tags` array containing the literal `AVATAR_TAG_HEXENZIRKEL`.

Display names are joined from `A.nameTextMapHash` to the same string key in `T`. Internal/config names are preserved from `A.iconName` and `D.talentStarName`. Element is joined from `A.skillDepotId` to `D.id`, then from `D.energySkill` to `S.id` and `S.costElemType`. Resource element names map as follows: `Fire` = Pyro, `Water` = Hydro, `Electric` = Electro, `Ice` = Cryo, `Wind` = Anemo, and `Rock` = Geo.

## Tagged roster

| Official/display name | Avatar ID | Internal/config name | Element | `skillDepotId` | Exact archive evidence |
| --- | ---: | --- | --- | ---: | --- |
| Durin | `10000123` | `UI_AvatarIcon_Durin`; `Talent_Durin` | Pyro (`Fire`) | `12301` | `A#/110/id`, `A#/110/iconName`, tag `A#/110/tags/0`; name `T#/2801077538`; depot `D#/142/id`; element `S#/535/costElemType` |
| Venti | `10000022` | `UI_AvatarIcon_Venti`; `Talent_Venti` | Anemo (`Wind`) | `2201` | `A#/11/id`, `A#/11/iconName`, tag `A#/11/tags/0`; name `T#/2466140362`; depot `D#/25/id`; element `S#/90/costElemType` |
| Klee | `10000029` | `UI_AvatarIcon_Klee`; `Talent_Klee` | Pyro (`Fire`) | `2901` | `A#/17/id`, `A#/17/iconName`, tag `A#/17/tags/0`; name `T#/3339083250`; depot `D#/31/id`; element `S#/121/costElemType` |
| Albedo | `10000038` | `UI_AvatarIcon_Albedo`; `Talent_Albedo` | Geo (`Rock`) | `3801` | `A#/26/id`, `A#/26/iconName`, tag `A#/26/tags/0`; name `T#/4108620210`; depot `D#/40/id`; element `S#/164/costElemType` |
| Mona | `10000041` | `UI_AvatarIcon_Mona`; `Talent_Mona` | Hydro (`Water`) | `4101` | `A#/28/id`, `A#/28/iconName`, tag `A#/28/tags/0`; name `T#/1113306282`; depot `D#/42/id`; element `S#/176/costElemType` |
| Fischl | `10000031` | `UI_AvatarIcon_Fischl`; `Talent_Fischl` | Electro (`Electric`) | `3101` | `A#/19/id`, `A#/19/iconName`, tag `A#/19/tags/0`; name `T#/3277782506`; depot `D#/33/id`; element `S#/128/costElemType` |
| Sucrose | `10000043` | `UI_AvatarIcon_Sucrose`; `Talent_Sucrose` | Anemo (`Wind`) | `4301` | `A#/30/id`, `A#/30/iconName`, tag `A#/30/tags/0`; name `T#/1053433018`; depot `D#/44/id`; element `S#/185/costElemType` |
| Razor | `10000020` | `UI_AvatarIcon_Razor`; `Talent_Razor` | Electro (`Electric`) | `2001` | `A#/9/id`, `A#/9/iconName`, tag `A#/9/tags/0`; name `T#/4160146730`; depot `D#/23/id`; element `S#/83/costElemType` |
| Varka | `10000128` | `UI_AvatarIcon_Varka`; `Talent_Varka` | Anemo (`Wind`) | `12801` | `A#/115/id`, `A#/115/iconName`, tag `A#/115/tags/0`; name `T#/963351866`; depot `D#/147/id`; element `S#/559/costElemType` |
| Nicole | `10000131` | `UI_AvatarIcon_Nicole`; `Talent_Nicole` | Pyro (`Fire`) | `13101` | `A#/118/id`, `A#/118/iconName`, tag `A#/118/tags/0`; name `T#/1711582634`; depot `D#/151/id`; element `S#/577/costElemType` |
| Lohen | `10000129` | `UI_AvatarIcon_Lohen`; `Talent_Lohen` | Cryo (`Ice`) | `12901` | `A#/116/id`, `A#/116/iconName`, tag `A#/116/tags/0`; name `T#/2872823194`; depot `D#/148/id`; element `S#/566/costElemType` |
| Prune | `10000132` | `UI_AvatarIcon_Prune`; `Talent_Prune` | Anemo (`Wind`) | `13201` | `A#/119/id`, `A#/119/iconName`, tag `A#/119/tags/0`; name `T#/2787504450`; depot `D#/152/id`; element `S#/581/costElemType` |

All 12 tagged records have `avatarIdentityType = AVATAR_IDENTITY_NORMAL` and an empty `candSkillDepotIds` array. The tagged set contains 12 distinct avatar IDs, 12 distinct display names/name hashes, and 12 distinct skill-depot IDs.

## Quest-conditioned special-talent evidence

Every tagged avatar's skill depot contains one non-zero special proud-skill entry whose resource condition type is `SPECIAL_PROUD_SKILL_OPEN_CONDITION_TYPE_QUEST_FINISH`. The obfuscated parameter key and values are retained exactly below; their semantic target is not resolved in this phase.

| Character | Exact depot pointer | Condition parameter list | Proud-skill group |
| --- | --- | --- | ---: |
| Durin | `D#/142/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000606]` | `12351` |
| Venti | `D#/25/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000707]` | `2251` |
| Klee | `D#/31/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1001008]` | `2951` |
| Albedo | `D#/40/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000502]` | `3851` |
| Mona | `D#/42/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000406]` | `4151` |
| Fischl | `D#/33/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000312]` | `3151` |
| Sucrose | `D#/44/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000911]` | `4351` |
| Razor | `D#/23/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [1000806]` | `2051` |
| Varka | `D#/147/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [7007409]` | `12851` |
| Nicole | `D#/151/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [7615907]` | `13151` |
| Lohen | `D#/148/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [7009606]` | `12951` |
| Prune | `D#/152/DAEIJGCFNLL/0` | `HNLGOIDBHAN: [7009203]` | `13251` |

This establishes a static, character-specific quest-finish condition in the resource. It does not establish that a particular player has satisfied the condition. Direct numeric lookup of these opaque parameter values found no matching `subId`/`id` in this archive's `QuestExcelConfigData.json` or `MainQuestExcelConfigData.json`; the parameter namespace and exact quest mapping remain unresolved for the config/predicate/status phase.

## Reconciliation with official evidence

The comparison source is `docs/research/gc70-hexerei-official-membership.md`.

- **Exact matches (12):** Durin `10000123`; Venti `10000022`; Klee `10000029`; Albedo `10000038`; Mona `10000041`; Fischl `10000031`; Sucrose `10000043`; Razor `10000020`; Varka `10000128`; Nicole `10000131`; Lohen `10000129`; Prune `10000132`.
- **Resource-only additions:** none.
- **Official-only omissions:** none.
- **Duplicates, Traveler forms, or tagged depot variants:** none within the tag-defined set.
- **Unresolved name mappings:** none for English display names.

The separate Witch's Revelation / Lucid Revelations roster—Wriothesley, Yae Miko, Cyno, Yumemizuki Mizuki, Qiqi, Diona, and Beidou—was not merged into this roster and was not analyzed here.

## Eligibility is not per-player activation

The archive proves two static facts:

1. These 12 avatar records are resource-eligible by the approved `AVATAR_TAG_HEXENZIRKEL` inclusion rule.
2. Each corresponding skill depot declares a quest-finish-conditioned special proud-skill group.

It does **not** prove which players completed Witch's Homework or the character-specific quest, whether the special proud skill is active for a player, or whether tag-based team SGV/predicate behavior should run before activation. Official wording requires character-specific completion before the character becomes Hexerei, so the later status model must keep at least `eligible` and `activated_for_player` separate.

Current source does not preserve that distinction:

- `src/main/java/emu/grasscutter/data/excels/avatar/AvatarSkillDepotData.java:27-72` deserializes `DAEIJGCFNLL`, but `SpecialProudSkillOpens` retains only `proudSkillGroupId`; the condition type and parameter list are not modeled. `onLoad` consequently collects every non-zero special group as `questProudSkillGroupIds` without evaluating a player.
- `src/main/java/emu/grasscutter/game/avatar/Avatar.java:278-288` and `Avatar.java:702-718` add every configured quest proud skill to `proudSkillList` when setting/rebuilding the depot, without checking quest completion.
- `Avatar.java:43-78` makes `proudSkillList` a persisted avatar field, while the two rebuild paths clear and repopulate it from static resource data. It therefore cannot currently serve as reliable proof of character-specific player activation.
- `src/main/java/emu/grasscutter/game/inventory/Inventory.java:688-690` and `src/main/java/emu/grasscutter/game/avatar/AvatarStorage.java:196-204` invoke post-load avatar stat reconstruction, so login reconstruction reaches the same unconditional special-proud-skill path.
- `src/main/java/emu/grasscutter/game/ability/PredicateEvaluator.java:66-71`, `src/main/java/emu/grasscutter/server/packet/send/PacketPlayerEnterSceneInfoNotify.java:33-70`, `src/main/java/emu/grasscutter/game/player/TeamManager.java:359-363`, and `src/main/java/emu/grasscutter/server/packet/recv/HandlerSceneInitFinishReq.java:35-37` gate/count Hexenzirkel by static avatar tag/ID only.

These are source facts and later inspection seams, not implementation authority or a completed design decision.

## Exact later source, persistence, and command seams

| Concern | Exact seam to inspect next | Required question |
| --- | --- | --- |
| Resource condition model | `AvatarSkillDepotData.java:27-72`, especially `SpecialProudSkillOpens` | What do `DOJJHGDGLFF` and `HNLGOIDBHAN` represent, and how should their quest-finish condition be deserialized without relying on obfuscated guesses? |
| Per-player quest persistence | `src/main/java/emu/grasscutter/game/quest/GameMainQuest.java:24-34`; `GameQuest.java:23-30`; `QuestManager.java:372-381,626-649`; `src/main/java/emu/grasscutter/database/DatabaseHelper.java:477-489` | Which persisted quest/main-quest state is the authoritative completion predicate for each opaque resource parameter? |
| Generic player unlock maps | `src/main/java/emu/grasscutter/game/player/Player.java:122-123`; `src/main/java/emu/grasscutter/game/quest/exec/ExecSetOpenState.java:9-17` | Do the relevant quest resources write a durable open state or quest global variable? Do not select either generic map without resource evidence. |
| Activation application lifecycle | `GameQuest.finish` at `GameQuest.java:207-267`; `Avatar.setSkillDepotData` and stat rebuilds at `Avatar.java:269-288,680-735` | When a qualifying quest finishes or is rolled back, how should the special proud skill, ability embryos, stats, and client notification be updated and reconstructed on login? |
| Avatar persistence/protocol | `Avatar.java:43-78,1059-1094`; `src/main/java/emu/grasscutter/server/packet/send/PacketProudSkillChangeNotify.java:7-18`; `PacketAvatarSkillDepotChangeNotify.java:9-24` | Should activation be derived from quest state or persisted explicitly, and how is login/reconnect drift prevented? |
| Hexerei predicates and team state | `PredicateEvaluator.java:31-71`; `PacketPlayerEnterSceneInfoNotify.java:33-70`; `TeamManager.java:358-363`; `HandlerSceneInitFinishReq.java:34-37` | Which behaviors use static eligibility, and which must require the per-player activation predicate? |
| Status commands | `src/main/java/emu/grasscutter/command/commands/QuestCommand.java:12-104`; `TalentCommand.java:11-118`; `DebugCommand.java:132-161`; `src/main/java/emu/grasscutter/command/CommandMap.java:339-350`; `Command.java:17-25` | Add a later read-only status surface that reports, per avatar, eligibility, condition mapping, quest completion, special proud-skill presence, and effective activation. Existing commands expose only fragments. Decide online-only versus offline semantics before implementation. |
| Command readiness/offline loading | `src/main/java/emu/grasscutter/game/player/Player.java:1311-1325`; `src/main/java/emu/grasscutter/server/game/GameServer.java:218-240` | Quest loading is asynchronous and not included in the shown avatar/inventory readiness wait; offline lookup loads only the player document here. A status command must not claim authoritative quest state until those lifecycle boundaries are resolved. |
| Separate Lucid state | Candidate-only MoonPhase seams: `PacketPlayerEnterSceneInfoNotify.java:30-49,75-99`; `TeamManager.java:345-356`; `src/main/java/emu/grasscutter/game/ability/AbilityManager.java:1100-1137` | `AVATAR_TAG_MOONPHASE` is a later inspection candidate, not proof that MoonPhase equals Lucid Revelation. Keep Lucid Revelations in a separate roster/status namespace and do not infer or reuse Hexerei mappings. |

`TeamHexenzirkelChangeNotify` protocol fields remain unresolved. The purged README-linked `hk4e-protos` repository must not be retried as a live source.

## Success criteria and verification

Success criteria for this phase are met:

- archive hash verified before parsing;
- every tagged record resolved to avatar ID, internal/config names, English display name, element, and skill-depot ID with exact ZIP entry and JSON Pointer evidence;
- all 12 official characters reconciled with additions, omissions, duplicates/variants, and unresolved mappings stated explicitly;
- static eligibility separated from per-player activation;
- later source/persistence/command seams identified without implementing them;
- Witch's Revelation / Lucid Revelations kept separate.

Verification performed:

- PowerShell SHA-256 computation matched the approved digest exactly.
- Python standard-library ZIP reads parsed the four relevant entries directly from the archive.
- The inclusion predicate was evaluated over all 165 avatar records.
- Join keys were checked for all 12 records; tagged IDs, names/name hashes, and skill-depot IDs were distinct.
- A set comparison against the official 12-character list produced 12 exact matches and empty resource-only/official-only sets.
- Targeted checked-in Java inspection confirmed the current condition-model, avatar-rebuild, tag-predicate/team-count, quest-persistence, and command seams.

Not verified or performed: runtime resource loading, player quest completion, database contents, packet schema fields, server/client behavior, gameplay UAT, full ability/config/predicate inventory, Gradle, JAR, deployment, or CodeGraph refresh/re-index.

## Follow-on status

The config/predicate/status follow-on is completed in `docs/research/gc70-hexerei-config-predicate-status-matrix.md`.

Resolved there:

1. The 12 `HNLGOIDBHAN` values are character-specific Hexenzirkel chapter `endQuestId` values.
2. Every proud-skill/open-config/ability join is present and inventoried.
3. The approved archive lacks the terminal quest rows and quest BinOutput required for authoritative per-player completion, so the status contract fails closed.

Still unresolved for later approved work:

1. The authoritative complete quest-resource baseline.
2. Whether every Hexerei gameplay predicate/team effect requires activation or whether some intentionally use eligibility alone.
3. The canonical per-player activation representation and login/reconnect/quest-rollback behavior.
4. Static tagged-ID cache invalidation after supported resource reload.
5. `TeamHexenzirkelChangeNotify` field schema.
6. Whether `AVATAR_TAG_MOONPHASE` and its SGV/ability seams represent the deferred Lucid Revelations system.

Exact next action: review the completed matrix and make the resource/semantics decisions above before any gameplay, persistence, packet, command, runtime, or build implementation.
