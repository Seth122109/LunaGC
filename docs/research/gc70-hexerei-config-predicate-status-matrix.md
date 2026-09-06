# GC 7.0 Hexerei config, predicate, and status matrix

Status date: 2026-08-24  
Scope: read-only analysis of the approved GC 7.0 archive and checked-in LunaGC source  
Runtime status: documentation only; no gameplay, persistence, packet, command, runtime, build, JAR, launcher, player-data, or CodeGraph-index action

## Outcome

The 12 resource condition parameters are resolved. `HNLGOIDBHAN[0]` is the character's Hexenzirkel chapter `endQuestId`, not an open-state ID, quest-global variable, or main-quest ID. Each value joins exactly to one `ChapterExcelConfigData` row whose icon is `UI_ChapterIcon_Hexenzirkel`; that row also supplies the main-quest namespace, begin quest, and chapter ID.

The approved archive is not sufficient to evaluate those completion predicates in LunaGC. All 12 main-quest metadata rows exist in `MainQuestExcelConfigData.json`, but all 12 terminal sub-quest rows are absent from `QuestExcelConfigData.json` and all 12 corresponding `BinOutput/Quest/<mainQuestId>.json` files are absent. Current `QuestManager.getQuestById` requires `QuestData`, and `QuestManager.loadFromDatabase` deletes a persisted main quest when any child lacks `QuestData`. Therefore a per-player Hexerei status command must report completion as unavailable with this resource baseline; it must not infer completion from the tag, proud-skill presence, open state, or a missing quest object.

All 12 proud-skill/open-config joins resolve. Current avatar reconstruction nevertheless adds every special proud skill unconditionally, so current materialized state is not proof of quest completion.

The public-web follow-on is complete in `docs/research/gc70-hexerei-public-quest-sources.md`. Project Amber identifies all 12 terminal quests through processed API records, and immutable Dimbreath GC 7.0 data supplies all 12 raw quest BinOutput files, but no inspected public source supplies the complete paired, LunaGC-loadable terminal quest rows and semantically named main-quest payloads. The fail-closed result therefore remains `UNAVAILABLE_RESOURCE`.

## Evidence aliases and selection rule

- `D` = `LunaGC-Resources-main/ExcelBinOutput/AvatarSkillDepotExcelConfigData.json`
- `P` = `LunaGC-Resources-main/ExcelBinOutput/ProudSkillExcelConfigData.json`
- `C` = `LunaGC-Resources-main/ExcelBinOutput/ChapterExcelConfigData.json`
- `Q` = `LunaGC-Resources-main/ExcelBinOutput/QuestExcelConfigData.json`
- `M` = `LunaGC-Resources-main/ExcelBinOutput/MainQuestExcelConfigData.json`
- `T/<Character>` = `LunaGC-Resources-main/BinOutput/Talent/AvatarTalents/ConfigTalent_<Character>.json`
- `B/<Character>` = `LunaGC-Resources-main/BinOutput/Ability/Temp/AvatarAbilities/ConfigAbility_Avatar_<Character>.json`

Ability blocks below are selected when their subtree contains `Hexenzirkel`, `BJJDEAIEIGP`, or `SGV_HexenzirkelLevel`. Modifier counts are the distinct declared or referenced `modifierName` values containing `Hexenzirkel`; generic modifiers reached by the open-config action are recorded through their owning ability block rather than renamed as Hexenzirkel modifiers.

## Character join and open-config matrix

`proudSkillId` is `proudSkillGroupId * 100 + 1`, matching current `Avatar` reconstruction.

| Character | Avatar / depot | Main / begin / end quest; chapter | Proud group / ID; open config | Open-config contribution | Relevant ability blocks (`B/<Character>#/.../Default`) |
| --- | --- | --- | --- | --- | --- |
| Durin | `10000123` / `12301` | `10006` / `1000601` / `1000606`; `C#/366`, chapter `2079` | `12351` / `1235101`; `Durin_Hexenzirkel_1` | Modify `Avatar_Durin_PermanentSkill_1.Hexenzirkel_1_ExtraRate` | `#/21` |
| Venti | `10000022` / `2201` | `10007` / `1000701` / `1000707`; `C#/367`, chapter `2080` | `2251` / `225101`; `Venti_Hexenzirkel_1` | Add `Avatar_Venti_HexenzirkelSkill`; set `Buff_Ratio`, `Buff_Ratio_Burst`, `Buff_Time` | `#/2,4-9,20-25,28-29,37-48` |
| Klee | `10000029` / `2901` | `10010` / `1001001` / `1001008`; `C#/370`, chapter `2083` | `2951` / `295101`; `Klee_Hexenzirkel_1` | Add `Avatar_Klee_HexenzirkelSkill`; set its duration and four `Avatar_Klee_PermanentSkill_1` extra-attack values | `#/38` |
| Albedo | `10000038` / `3801` | `10005` / `1000501` / `1000502`; `C#/365`, chapter `2078` | `3851` / `385101`; `Albedo_Hexenzirkel_1` | Add `Avatar_Albedo_HexenzirkelSkill` and `_GadgetDetect`; unlock `Albedo_Hexenzirkel` on seven abilities; set six ratio/duration values | `#/14-20` |
| Mona | `10000041` / `4101` | `10004` / `1000401` / `1000406`; `C#/364`, chapter `2077` | `4151` / `415101`; `Mona_Hexenzirkel_1` | Add `Avatar_Mona_HexenzirkelSkill`; set three buff and three `Avatar_Mona_StarChart` extension values | `#/22,26-27,29` |
| Fischl | `10000031` / `3101` | `10003` / `1000301` / `1000312`; `C#/363`, chapter `2076` | `3151` / `315101`; `Fischl_Hexenzirkel_1` | Add `Avatar_Fischl_HexenzirkelSkill`; set four values; unlock `Fischl_Hexenzirkel` and propagate attack/mastery values to crow and aimed-shot abilities | `#/43` |
| Sucrose | `10000043` / `4301` | `10009` / `1000901` / `1000911`; `C#/369`, chapter `2082` | `4351` / `435101`; `Sucrose_Hexenzirkel_1` | Add `Avatar_Sucrose_HexenzirkelSkill`; unlock `Sucrose_Hexenzirkel` on three abilities; set four duration/ratio values | `#/7,10-11,14` |
| Razor | `10000020` / `2001` | `10008` / `1000801` / `1000806`; `C#/368`, chapter `2081` | `2051` / `205101`; `Razor_Hexenzirkel_1` | Add `Avatar_Razor_HexenzirkelSkill`; set wolf damage, thunderfall damage, revive energy, and overload cooldown | `#/4,8,20` |
| Varka | `10000128` / `12801` | `70074` / `7007401` / `7007409`; `C#/388`, chapter `10189` | `12851` / `1285101`; `Varka_Hexenzirkel_1` | Modify `Avatar_Varka_ElementalArt.cdDelta`; `Avatar_Varka_HexenzirkelSkill` exists as a definition but is not added by this open config | `#/3,19` |
| Nicole | `10000131` / `13101` | `76159` / `7615901` / `7615907`; `C#/393`, chapter `10239` | `13151` / `1315101`; `Nicole_PermanentSkill_3` | Add and parameterize `Avatar_Nicole_PermanentSkill_3` | `#/2,15,17` |
| Lohen | `10000129` / `12901` | `70096` / `7009601` / `7009606`; `C#/399`, chapter `10253` | `12951` / `1295101`; `Lohen_Hexenzirkel_1` | Unlock `Lohen_Hexenzirkel` and set threshold, damage, and duration on elemental skill and burst | `#/3,6` |
| Prune | `10000132` / `13201` | `70092` / `7009201` / `7009203`; `C#/398`, chapter `10252` | `13251` / `1325101`; `Prune_PermanentSkill_3` | Unlock `Prune_Hexenzirkel` and set self/team ratios and durations on `Avatar_Prune_PermanentSkill_2` | `#/8` |

Every named open-config key is present in `T/<Character>`, every group has exactly one level-1 `P` record, and every `P.openConfig` resolves to that key. Nicole and Prune intentionally use `PermanentSkill_3` names even though their proud-skill display class is `PROUD_SKILL_DISPLAY_HEXENZIRKEL`.

## Predicate, SGV, and modifier matrix

| Character | `SGV_HexenzirkelLevel` references | Obfuscated Hex-tag predicate `BJJDEAIEIGP` | Hex-specific unlock parameters | Hex-named modifiers |
| --- | --- | ---: | --- | ---: |
| Durin | Two `ByTargetGlobalValue`, team `>= 2` | 0 | none | 0 |
| Venti | 24 `ByTargetGlobalValue`, team `>= 2` across normal attacks, skill, burst, and Hexerei ability | 0 | none | 26 |
| Klee | One `AttachModifierToGlobalValueMixin` on the team SGV | 0 | none | 12 |
| Albedo | Three `ByTargetGlobalValue`; threshold is serialized as an unresolved expression object rather than a numeric literal | 0 | `Albedo_Hexenzirkel` (10 predicate sites) | 44 |
| Mona | Two `ByTargetGlobalValue`, team `>= 2`; separate `SGV_ABILITY_Mona_Hexenzirkel_Buff` | 2, both `target=Self` | none | 19 |
| Fischl | none | 0 | none in predicates; open config declares `Fischl_Hexenzirkel` | 21 |
| Sucrose | Two `ByTargetGlobalValue`, team `>= 2` | 5, all `target=Target` | `Sucrose_Hexenzirkel` (6); constellation-specific Hex parameter (4) | 10 |
| Razor | Two `ByTargetGlobalValue`, team `>= 2`; one mixin with steps `[1.5, 99.0]` | 0 | none | 6 |
| Varka | One team `> 1` and one team `== -1` predicate | 1, `target=Target` | none | 0 |
| Nicole | One team `>= 2` predicate; one mixin with steps `[1.5, 99.0]` | 14: 12 `Self`, 2 `Target` | none | 0 |
| Lohen | Two `ByTargetGlobalValue`, team `>= 2` | 0 | `Lohen_Hexenzirkel` (2) | 1 |
| Prune | none | 2, both `target=Target` | `Prune_Hexenzirkel` (2) | 0 |

The nonzero modifier inventories are concentrated in these exact namespaces: Venti (26: `Avatar_Venti_*Hexenzirkel*`, `UNIQUE_*Hexenzirkel*`, `Hurricane_*Hexenzirkel*`, `WindBlade_*Hexenzirkel*`); Klee (12: `Klee_Hexenzirkel_*` and `UNIQUE_Klee_Hexenzirkel_*`); Albedo (44: `Albedo_*Hexenzirkel*`, `FuriousMonolith_Hexenzirkel_*`, and `UNIQUE_*Albedo*Hexenzirkel*`); Mona (19: `Avatar_Mona_Hexenzirkel_*`, `UNIQUE_Avatar_Mona_Hexenzirkel_*`, and `Hexenzirkel_Talent_AutoExtraAttack_DoAttack`); Fischl (21: `Avatar_Fischl_Hexenzirkel_*`, `UNIQUE_Avatar_Fischl_Hexenzirkel_*`, and `UNIQUE_Fischl_Hexenzirkel_Constellation_6`); Sucrose (10: four elemental `Hurricane_Mix_*_Hexenzirkel` handler/unique pairs plus two `Avatar_Sucrose` entries); Razor (6: `Razor_Hexenzirkel_*`); and Lohen (1: `UNIQUE_Lohen_Hexenzirkel_Buff`). Exact declarations and references are in the selected `B/<Character>` blocks listed above.

## Condition namespace and resource-readiness result

For all 12 characters:

1. `D.DAEIJGCFNLL[0].DOJJHGDGLFF` is `SPECIAL_PROUD_SKILL_OPEN_CONDITION_TYPE_QUEST_FINISH`.
2. `D.DAEIJGCFNLL[0].HNLGOIDBHAN[0]` equals one and only one `C.endQuestId`.
3. The same `C` row has `chapterIcon=UI_ChapterIcon_Hexenzirkel` and `BIEPMDCNIMK=[mainQuestId]`.
4. `M` contains `mainQuestId`.
5. `Q` does not contain the terminal `subId`, and `BinOutput/Quest/<mainQuestId>.json` is absent.

This resolves the semantic namespace while producing a fail-closed readiness result: `condition_mapping=RESOLVED`, `quest_completion_source=UNAVAILABLE_RESOURCE`.

## Checked-in server behavior and gaps

- `AvatarSkillDepotData.SpecialProudSkillOpens` retains only `proudSkillGroupId`; it discards the condition type and parameter list.
- `Avatar.setSkillDepotData` and `Avatar.recalcStats` add all special groups to `proudSkillList`, then add open-config ability embryos and ability variables without a quest-state check.
- `PacketPlayerEnterSceneInfoNotify`, `TeamManager.updateTeamProperties`, and `HandlerSceneInitFinishReq` count static tagged avatars in the active team. They do not test ownership-specific activation.
- `PredicateEvaluator.BJJDEAIEIGP` ignores the predicate's `target` field and tests the ability caster's static avatar tag. This differs from resource sites that explicitly request `Self` or `Target`.
- `PredicateEvaluator.ByUnlockTalentParam` compares a requested talent parameter directly with `ProudSkillData.openConfig`. Resource predicates such as `Albedo_Hexenzirkel`, `Sucrose_Hexenzirkel`, `Lohen_Hexenzirkel`, and `Prune_Hexenzirkel` are declared inside open configs whose names have `_1` or `PermanentSkill_3`; direct string equality cannot represent those declarations.
- Predicate types other than the small implemented set continue to return `true`. The selected ability blocks contain many additional `By*` types, so server-side predicate coverage is incomplete and fail-open.
- The ability files, open configs, proud-skill records, SGVs, and modifiers are present. Their presence establishes static configuration, not correct server execution or player activation.

These are source/resource facts and implementation seams, not gameplay-edit authority.

## Read-only per-player status and command contract

Recommended future command surface: `hexerei status [all|<avatarId>] [@uid]`. It is a dedicated read-only surface; it must not be added as a mutating mode of `quest`, `talent`, or `debug`.

Target and readiness contract:

- online target only until quest collections have an explicit safe offline loader;
- require avatar storage loaded and a new explicit quest-manager readiness signal;
- never treat `QuestManager.getQuestById(...) == null` as “not completed”;
- return `UNAVAILABLE_QUESTS_NOT_READY` when asynchronous quest loading is incomplete;
- return `UNAVAILABLE_RESOURCE` when the terminal `QuestData`/`MainQuestData` required by the mapping is absent;
- no implicit quest start/finish, proud-skill insertion/removal, ability change, save, packet, team refresh, or database mutation.

Per-character output fields:

| Field | Meaning |
| --- | --- |
| `avatar_id`, `name`, `owned` | Roster identity and whether the target player owns the avatar. |
| `eligible` | Static roster/tag eligibility only. Never synonymous with activation. |
| `skill_depot_id` | Active depot and whether it matches the verified join. |
| `condition_type`, `main_quest_id`, `end_quest_id`, `chapter_id` | Exact resolved condition provenance. |
| `quest_source_status`, `quest_state` | Readiness plus the persisted terminal quest state when authoritative. |
| `proud_skill_group_id`, `proud_skill_id`, `open_config` | Expected materialization chain. |
| `proud_skill_present`, `added_abilities_present`, `talent_params_present`, `ability_vars_present` | Current derived/materialized state; diagnostic only. |
| `activation_status` | One of the fail-closed states below. |
| `consistency` | Explains premature materialization, missing materialization, depot mismatch, or unavailable evidence. |

Activation-state rules:

- `NOT_OWNED`: eligible roster entry, avatar absent from player storage.
- `UNAVAILABLE_QUESTS_NOT_READY`: avatar state is ready but quest loading is not.
- `UNAVAILABLE_RESOURCE`: condition mapping is known but authoritative quest configuration is missing.
- `LOCKED`: terminal quest state is authoritatively known and is not finished.
- `ACTIVE`: terminal quest is authoritatively finished and the expected proud/open-config contribution is materialized.
- `INCONSISTENT_PREMATURE`: quest is known not finished but proud/open-config contribution is present.
- `INCONSISTENT_MISSING`: quest is finished but expected contribution is absent.

With the approved archive, every owned Hexerei avatar must currently report `UNAVAILABLE_RESOURCE`; it must not report `ACTIVE` merely because current reconstruction inserted the proud skill. A compact summary should report counts by activation state and print one deterministic line per roster entry in the official order.

The separate Witch's Revelation / Lucid Revelations roster must use a different status namespace and mapping. `AVATAR_TAG_MOONPHASE` and `SGV_MoonPhaseLevel` remain candidate evidence only and are not part of this Hexerei contract.

## Implementation and runtime-validation follow-on

The later deterministic recovery phase proved that complete authoritative terminal quest rows cannot be reconstructed. PR #1 therefore implements the separately approved explicit `MANUAL_COMMAND` fallback while preserving `authoritative_completion=UNAVAILABLE_RESOURCE`. It does not synthesize or install quest resources.

The exact post-implementation matrix is `docs/research/gc70-hexerei-character-verification-matrix.md`. It covers inactive/active/idempotent state, raw versus effective proud/open-config effects, base skill/talent invariants, C0-C6, predicates/modifiers, one/two-member SGV behavior, persistence/reconnect, reset, per-character observable oracles, evidence capture, and rollback. Durin is the first shared-contract tracer; risk ranking does not substitute for observed mechanics.

## Verification and review boundary

Verified read-only:

- all 12 depot-condition, chapter, main-quest, proud-skill, open-config, and ability-file joins;
- exact chapter/main/begin/end IDs and JSON indices;
- all 12 open-config action lists;
- Hexenzirkel-selected ability block indices, SGV use, obfuscated predicate sites, Hex unlock parameters, and Hex-named modifier counts;
- absence of all 12 terminal `Q.subId` rows and all 12 `BinOutput/Quest/<mainQuestId>.json` files;
- checked-in condition modeling, avatar reconstruction, predicate evaluation, team SGV counting, quest lookup/load, and command target/readiness seams.

Not verified or performed: authoritative missing quest payloads, player database state, runtime resource loading, command output, server/client behavior, packet schema, gameplay, persistence changes, build/Gradle/JAR/deployment, or Lucid Revelation mapping.

Review decision required before implementation: obtain or designate an immutable GC 7.0 resource baseline containing all 12 terminal `QuestExcelConfigData` rows and semantically named, LunaGC-loadable quest BinOutput payloads, validate it against these mappings, then decide the activation derivation and predicate/team semantics. Public labels and obfuscated partial payloads must not be used to synthesize missing records. No gameplay, persistence, packet, command, runtime, or build implementation is authorized by this document.
