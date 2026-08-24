# GC 7.0 Hexerei character verification matrix

Status date: 2026-08-24  
Scope: PR #1 runtime-validation plan after rebasing onto `7.0.0` commit `4b43902`  
Evidence boundary: source/resource-derived expectations plus user-controlled UAT; no authoritative quest completion is available

## Pass gate

PR #1 stays draft until the shared contract and the Durin tracer pass. A manual activation is successful only when all of these remain true:

- `authoritative_completion=UNAVAILABLE_RESOURCE`; `MANUAL_COMMAND` is never reported as quest completion;
- activation changes only the mapped effective proud/open-config view and Hex team effects;
- raw proud state, `skillLevelMap`, `talentIdList`, quest state, open states, and unrelated proud skills are unchanged;
- one active Hexerei member reports/sends team level `1`, two active members report/send `2`, and inactive or reset members are excluded;
- reconnect restores the validated manual record and derived effects; reset removes only the Hexerei record and restores the inactive view;
- observable gameplay matches the named GC 7.0 resource contribution below. A missing or different effect is evidence to investigate, not permission to invent mechanics.

## Shared state matrix

Run each row on the same user-controlled test account and record command output, active party, constellation, talent levels, and the observable oracle. Do not use a static tag, raw proud presence, or a materialized ability as the activation oracle.

| State | Setup | Required status/state | Team SGV / packet expectation | Required invariant |
| --- | --- | --- | --- | --- |
| Inactive baseline | Mapped owned avatar, no manual record | `effective_activation=false`; raw mapped proud may exist but effective mapped proud is absent | Count excludes avatar | Base skills/talents/constellations work; no mapped Hex contribution |
| Activate once | `hexerei activate <avatarId>` | Valid schema-v1 `MANUAL_COMMAND`; `effective_activation=true`; completion still unavailable | Count includes avatar | Mapped Hex contribution appears; skill levels/talent IDs/quest state unchanged |
| Activate twice | Repeat the same command | `ALREADY_ACTIVE`; original actor/time audit remains | Count unchanged | No duplicated modifiers, abilities, packets, or stat contribution |
| One-member party | Exactly one effectively active mapped avatar in active party | Active member remains consistent | `SGV_HexenzirkelLevel=1` and team-change level `1` | No two-member predicate/effect fires |
| Two-member party | Add a second effectively active mapped avatar | Both active/consistent | Level `2` | Documented `>=2`/`>1` effects fire once; no count from inactive tagged members |
| Mixed party | One active plus one inactive tagged Hexerei avatar | Only one effective activation | Level `1` | Static eligibility cannot raise the team level |
| Reconnect | Disconnect/reconnect after activation | Same valid record/audit; active/consistent | Same count after scene initialization | Derived proud/open-config and team state are restored without reactivation |
| Reset one | `hexerei reset <avatarId>` | Record absent; effective mapped proud filtered | Count decrements if avatar is in party | Base kit and unrelated proud skills remain; mapped Hex effect disappears |
| Reset all | `hexerei reset all` | Hexerei namespace empty | Count `0` | No quest/avatar field restoration is needed because none was changed |
| Invalid record | Only if a controlled fixture exists; do not corrupt live data | `INVALID_RECORD`; fail closed until reset | Excluded | Activation refuses to overwrite the invalid record |

## Talent and C0-C6 protocol

For every character, capture `skillLevelMap`, `talentIdList`, raw/effective proud IDs, and status before activation. Repeat the inactive/active/reset comparison at C0, C1, C2, C3, C4, C5, and C6. Each constellation cell passes only if:

1. the base normal attack, skill, burst, inherent passives, and that constellation's ordinary behavior work at the unchanged recorded talent levels;
2. activation does not add/remove constellation IDs or change skill levels;
3. the named Hex contribution below is absent while inactive, present once while active, survives reconnect, and disappears after reset;
4. two-member behavior is tested separately from one-member behavior; and
5. any constellation-specific Hex predicate/modifier is asserted only where the resource inventory names one. No effect may be inferred from a constellation name.

Use this exact result row for each constellation: `C# | inactive base | inactive Hex absent | active base unchanged | active Hex oracle | 1-member | 2-member | reconnect | reset | evidence/link | PASS/FAIL/BLOCKED`.

## Character matrix and risk ranking

Risk rank estimates validation complexity from the verified resource inventory; it is not a claim that a character is broken. Durin runs first as the shared-contract tracer even though its character-specific density is lowest.

| Risk | Character / IDs | Proud/open-config oracle | Predicate / modifier focus | C0-C6 focus | Observable oracle |
| ---: | --- | --- | --- | --- | --- |
| 1 | Albedo `10000038`, depot `3801`, proud `385101` | `Albedo_Hexenzirkel_1`: add `Avatar_Albedo_HexenzirkelSkill` and `_GadgetDetect`; unlock seven ability sites; six values | Three team-SGV predicates; unresolved expression threshold; 10 `Albedo_Hexenzirkel` predicates; 44 Hex modifiers | Run every C level; distinguish ordinary constellation output from unlocked Hex ability sites | Ability/modifier presence plus the six configured ratio/duration deltas; unresolved threshold is `BLOCKED` unless observed without guessing |
| 2 | Venti `10000022`, depot `2201`, proud `225101` | Add `Avatar_Venti_HexenzirkelSkill`; set `Buff_Ratio`, `Buff_Ratio_Burst`, `Buff_Time` | 24 team-SGV predicates across attacks/skill/burst; 26 Hex modifiers | All C levels; watch duplication across attack, skill, and burst paths | Named ability exists once; configured buff variables and one-vs-two member deltas agree with resource behavior |
| 3 | Nicole `10000131`, depot `13101`, proud `1315101` | Add/parameterize `Avatar_Nicole_PermanentSkill_3` | 14 tag predicates (12 `Self`, 2 `Target`); team `>=2`; SGV mixin `[1.5,99.0]` | All C levels; preserve permanent-skill naming and Self/Target separation | PermanentSkill_3 contribution appears once; Self and Target cases do not cross-activate; one/two-member difference is visible |
| 4 | Sucrose `10000043`, depot `4301`, proud `435101` | Add `Avatar_Sucrose_HexenzirkelSkill`; unlock three abilities; four duration/ratio values | Five `Target` tag predicates; six unlock predicates plus four constellation-specific Hex predicates; 10 modifiers | All C levels; explicitly capture the constellation-specific Hex predicate at the C level where it becomes observable | Named ability/unlocks and four values appear only while active; Target tests and the constellation-specific path remain isolated |
| 5 | Mona `10000041`, depot `4101`, proud `415101` | Add `Avatar_Mona_HexenzirkelSkill`; three buff and three StarChart extension values | Two `Self` tag predicates; two team `>=2` predicates; separate Mona Hex SGV; 19 modifiers | All C levels; separate base StarChart/constellation behavior from Hex extensions | Six configured values and named ability appear once; Self and team threshold behavior is reversible |
| 6 | Varka `10000128`, depot `12801`, proud `1285101` | Modify `Avatar_Varka_ElementalArt.cdDelta`; do not expect the defined Hex skill to be added by this open config | One `Target` tag predicate; team `>1` and unusual `== -1`; zero Hex-named modifiers | All C levels; cooldown baseline must be measured before activation | Elemental-art cooldown delta is the positive oracle; adding `Avatar_Varka_HexenzirkelSkill` is not an expected oracle; `== -1` remains evidence-only |
| 7 | Klee `10000029`, depot `2901`, proud `295101` | Add `Avatar_Klee_HexenzirkelSkill`; duration plus four PermanentSkill_1 extra-attack values | Team-SGV attachment mixin; 12 Hex modifiers | All C levels; separate ordinary extra attacks from four Hex parameters | Named ability, duration, and four configured extra-attack deltas appear once and reset cleanly |
| 8 | Fischl `10000031`, depot `3101`, proud `315101` | Add `Avatar_Fischl_HexenzirkelSkill`; four values; unlock/propagate attack and mastery to crow/aimed-shot abilities | No team-SGV/tag predicate in selected block; 21 modifiers including a named C6 modifier | All C levels; C6 explicitly checks `UNIQUE_Fischl_Hexenzirkel_Constellation_6` without assuming its numeric result | Crow and aimed-shot propagation plus four values; C6 Hex modifier only at C6 and only while active |
| 9 | Razor `10000020`, depot `2001`, proud `205101` | Add `Avatar_Razor_HexenzirkelSkill`; wolf damage, thunderfall damage, revive energy, overload cooldown | Two team `>=2`; SGV mixin `[1.5,99.0]`; six modifiers | All C levels; preserve ordinary wolf/energy/overload constellation behavior | Four configured deltas appear once; compare one/two-member and reset |
| 10 | Prune `10000132`, depot `13201`, proud `1325101` | `Prune_PermanentSkill_3`: unlock `Prune_Hexenzirkel`; self/team ratios and durations on PermanentSkill_2 | Two `Target` tag predicates; two unlock predicates; zero Hex-named modifiers | All C levels; separate self and team contributions | PermanentSkill_3 activation makes only the configured self/team ratio/duration contribution; Target cases stay isolated |
| 11 | Lohen `10000129`, depot `12901`, proud `1295101` | Unlock `Lohen_Hexenzirkel`; threshold, damage, duration on skill/burst | Two team `>=2`; two unlock predicates; one Hex modifier | All C levels; measure skill and burst independently | Named unlock plus threshold/damage/duration deltas; verify one/two-member and reset |
| 12 | Durin `10000123`, depot `12301`, proud `1235101` | Modify `Avatar_Durin_PermanentSkill_1.Hexenzirkel_1_ExtraRate` | Two team `>=2` predicates; no tag predicates/unlock aliases/Hex-named modifiers | All C levels later; first tracer uses existing C level only and records it | First tracer: inactive/active/reset change in `Hexenzirkel_1_ExtraRate`, count `1` vs `2`, reconnect persistence, no talent/constellation mutation |

## Durin-first UAT sequence

Stop on the first failure and preserve command output/logs before changing another variable.

1. Select the uniquely named Hexerei candidate in Cultivation, but do not overwrite any existing JAR. Confirm the displayed path and candidate SHA-256 against the rollback manifest.
2. With Durin owned and in the active party, record `hexerei status 10000123`, constellation, talent levels, raw/effective proud state, party, and a repeatable PermanentSkill_1 observation. Completion must read `UNAVAILABLE_RESOURCE` and effective activation must be false.
3. Run `hexerei activate 10000123` once. Repeat status and the same observation. The mapped contribution must appear once; talent levels, constellation IDs, quest state, and unrelated effects must not change.
4. Repeat activation. It must be idempotent and preserve the original audit record.
5. With only Durin effectively active in the party, confirm Hex team level `1` and no two-member predicate effect.
6. Add one other owned mapped Hexerei avatar to the party but leave it inactive. Team level must remain `1`.
7. Activate that second avatar explicitly. Team level must become `2`, and Durin's documented `>=2` behavior must become observable once.
8. Reconnect without resetting. Both records, effective proud state, team level, and Durin behavior must restore.
9. Reset the second avatar. Team level must return to `1`; Durin stays active.
10. Run `hexerei reset 10000123`. Durin returns to the inactive effective view and baseline behavior; recorded skill levels, constellations, quest state, and unrelated proud skills remain unchanged.
11. If cleanup is needed, run `hexerei reset all`; verify the namespace is empty, then stop the game/server normally and restore the prior JAR path from the manifest.

## Evidence record

For every run record: date/time, candidate path/SHA/commit, UID, character/avatar ID, party composition, constellation, talent levels, command output before/after, one/two-member team level, reconnect result, reset result, gameplay observation, logs/screenshots, and `PASS`, `FAIL`, or `BLOCKED`. User observations must be labeled user-reported unless independently captured.

Witch's Revelation/Lucid Revelations is a separate namespace and is excluded from every row above.
