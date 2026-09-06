# MajoKai relic resource candidate handoff

Relation: Active  
Updated: 2026-09-01 Asia/Manila

## Goal and boundary

Restore the missing four-piece conditional CRIT-rate branch for artifact set
`15044`, affix `2150441`, open config `Relic_6.3_MajoKai`. Matching GC 7.0
resource reconciliation remains deployed; a narrowly gated source fallback is
now built only for the conditional branch the client cannot activate from the
terminal/manual Hexerei state.

Offline acquisition, validation, the separately authorized resource deployment,
and the isolated source candidate build are complete. Candidate deployment,
launcher selection, process control, and follow-up gameplay UAT remain
unapproved and user-controlled.

## Candidate

Root:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Resource-Candidates\2026-09-01-girluh-1744949-relic-overlay`

Pinned source: `girluh/LunaGC-Resources` commit
`1744949c800cc380cd16b5d1f092a94f06a820d2`, tree
`aefa33dcaba4d24ddd0a341e1f66a8c59db41ce7`, declared “7.0 resources”. The
repository is a third-party transformed resource tree and does not publish a
generator/transformation recipe; retain this provenance limitation.

Exact overlay files:

| Relative path | Bytes | Git blob SHA-1 | SHA-256 |
| --- | ---: | --- | --- |
| `BinOutput/Talent/RelicTalents/ConfigAffix_Relic.json` | 65,400 | `1d7bbc72b4f2e4ab3ab6fffeebc725e63cc33096` | `7604BB900B294A85D6BB1CD4C0F422C1F5DB8A2D282BBA376D48183BBCAE8D68` |
| `BinOutput/Ability/Temp/EquipAbilities/ConfigAbility_Relic2.json` | 242,779 | `09b00ca1feec1c3e15b7ae6761a54d6d76697ecf` | `82E114A0B52CED0D75745F9F56EED181BA61DF31FE4D13623937326B90EA3E35` |

The approved archive remains
`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\LunaGC-Resources-7.0.zip`,
SHA-256 `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF`.
It contains 39,829 files and is a path-subset of the pinned commit's 40,066
blobs; 237 source files are absent, including both target files. The
equip-affix table, reliquary-set table, existing relic-ability file, and Klee
talent file match source/archive/runtime byte-for-byte. This supports a
target-complete overlay, not a claim that the installed pack is a complete
mirror of the commit.

## Exact behavior graph

- Open config maps `%1/%2/%3` to `Dura/Rate1/Rate2`.
- Affix parameters resolve to `6 / 0.25 / 0.20`.
- A qualifying landed attack attaches a one-second trigger-CD modifier.
- The six-second `Actor_AttackRatio = Rate1` modifier is unconditional.
- The six-second `Actor_CriticalDelta = Rate2` modifier is guarded by
  `BJJDEAIEIGP`, which this branch resolves through effective Hexerei activation.

## Verification

- `validate.ps1` with the current runtime as a read-only baseline returned
  `RESOURCE_CANDIDATE_AUDIT=GREEN`.
- External-source Gradle harness ran the real LunaGC parsers:
  `CandidateRelicResourceTest`, 1 test, 0 failures/errors/skips,
  `BUILD SUCCESSFUL`.
- Candidate hashes, raw action/predicate graph, OpenConfig parser output, and
  source/archive/runtime anchors were checked.
- After deployment, the real LunaGC parser loaded the installed runtime files:
  `CandidateRelicResourceTest`, 1 test, 0 failures/errors/skips,
  `BUILD SUCCESSFUL`.
- No `jar`, packaging, JAR, configuration, launcher, player-data, process,
  retail-file, or official-service action occurred.

## Diagnosed engine boundary

- Before this source candidate, raw action type `POIDCNKIFGD` had no LunaGC
  enum mapping and deserialized to null.
- `ByEntityTypes` and `ByAttackTags` are not implemented by the generic server
  predicate evaluator and currently fail open.
- Before this source candidate, `AbilityModifierProperty` did not model
  `Actor_AttackRatio` or `Actor_CriticalDelta`.

The candidate is therefore suitable for a controlled client-visible UAT, but
offline validation does not prove server-authoritative ATK/CRIT enforcement.

## Deployment record and rollback

At deployment, the server/client/game process gate was clear and both target
files plus the `RelicTalents` directory were absent. The complete pre-install
resource tree was preserved at:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\pre-majokai-resource-deploy-20260901-165259`

- Full tar: `resources-predeploy.tar`, 39,829 files represented by the hash
  manifest, SHA-256
  `3AED1E97233B026D4F097E94DF570081C718B7BD814B3F4E85AA37505AEB9293`.
- Per-file manifest: `resources-sha256.csv`, SHA-256
  `7D9330428B27F87C79E748AD7C0F1F5FCD34B8AFC87E0B6188257C4D202D16F4`.
- Metadata: `backup-metadata.json`, SHA-256
  `F9D9123B3256E498ADBB9E3C40DA58F1CBFF065B17FB5C5FDFAC311D0BC1B807`.
- Deployment record: `deployment-record.json`, SHA-256
  `5ADBE77D69AA5C3F290AB36F82340B39B5FDA1E9E370AD948CD86D9225513DFC`.

Both target files were installed with `FileMode.CreateNew`. Runtime byte counts
and SHA-256 values match the candidate table above. The post-install structural
audit returned `RuntimeResourceAudit=GREEN`; the candidate/baseline audit
returned `RESOURCE_CANDIDATE_AUDIT=GREEN`; the runtime-targeted parser harness
returned `BUILD SUCCESSFUL`. The selected JAR, runtime configuration, launcher
selection, player data, and processes were not changed.

The final full-tree audit rehashed all 39,831 runtime resource files. All
39,829 baseline files remain present and byte-identical; the only extra paths
are the two approved files, both with their expected hashes. It returned
`FULL_TREE_POST_INSTALL_AUDIT=GREEN` while the runtime process gate remained
clear.

Focused UAT after startup:

1. Confirm startup no longer logs the missing `RelicTalents` directory and has
   no parse error for either candidate file.
2. With four pieces equipped, record Klee's idle ATK/CRIT, effective Hexerei
   activation, and active party.
3. Land one qualifying attack on a monster; verify +25% ATK and +20% CRIT for
   six seconds, then both return to baseline.
4. Repeat on a non-effectively-active control avatar: +25% ATK may apply, but
   +20% CRIT must not.
5. Stop on the first mismatch and retain logs/screenshots before repair.

Rollback removes the two create-new files and the newly created `RelicTalents`
directory only after confirming them against this deployment record, then
rehashes the full resource tree against `resources-sha256.csv`. If broader
restoration is needed, extract `resources-predeploy.tar` to a new staging
directory, verify it against the manifest, and restore only under explicit
authority. Existing JAR and configuration remain untouched.

## Gameplay UAT result and source-repair boundary

The user-controlled UAT is red only for the conditional CRIT branch:

- Idle: base ATK `853`, bonus ATK `+1100`, CRIT `79.3%`, EM `0`.
- Qualifying hit: bonus ATK `+1313`, exactly `+213` or 25% of base ATK;
  CRIT remained `79.3%`, EM remained `0`.
- A later capture showed bonus ATK `+1824` and EM `160`. The user then
  identified Klee's enhanced Chained Reactions C1 effect, which grants 60% ATK
  for 12 seconds. Its expected increment is `853 * 0.60 = 511.8`; the observed
  increment from the qualifying-hit capture is `+1313 -> +1824`, or `+511`.
  That independently explains the larger ATK value (subject to display
  rounding), so image 3 is not evidence of MajoKai stacking. The Chained
  Reactions capture SHA-256 is
  `2CFC519555A2F8286F59F9C42B349D21D935D7192FC51FE4927E40DBDA6D005D`.
- Startup reached `Finished loading resources` with no targeted relic/open-
  config/ability parse error.
- `hexerei team` reported Klee `10000029` in slot 3 with `mapped=true`,
  `eligible=true`, `effective_active=true`, expected and actual depot `2901`,
  and `ACTIVE_CONSISTENT`. The same capture reported calculated team count 3
  and cached SGV 3.0. Capture SHA-256:
  `AC4A85B1497B390831BCA53FBE49E3A9294C5C02763E785ED95A666A626E5953`.

This rules out missing resources, load failure, wrong party, depot mismatch,
and ineffective server activation. The remaining boundary is that the client
does not treat terminal/manual activation as satisfying its native
`BJJDEAIEIGP` state, while the server cannot currently execute the fallback:
`POIDCNKIFGD` is unmapped and modifier ATK/CRIT properties are unmodeled.

The user authorized a scoped source repair and isolated candidate JAR build.
Deployment remains unapproved and user-controlled. Implementation contract:

- Goal: make the raw `POIDCNKIFGD` action parse and provide a server-side
  fallback only for its `BJJDEAIEIGP`-guarded `Actor_CriticalDelta` modifier.
- Affected seams: `AbilityModifier` raw action/property parsing,
  `AbilityManager` orchestration recognition, `ActionApplyModifier`, equip-
  affix ability parameters, and transient avatar fight-property modifiers.
- Data IDs: set `15044`, four-piece affix `2150441`, open config/ability
  `Relic_6.3_MajoKai`, modifier `UNIQUE_Relic_MajoKaiBuff2`.
- Packet impact: publish the temporary CRIT fight-property change and expiry
  to the owning client. No generated protocol or hand-coded field changes.
- Persistence impact: none; the temporary modifier and expiry token are
  transient and must never be saved as player state.
- Anti-double-apply boundary: do not server-apply the unconditional
  `Actor_AttackRatio` branch, which already works client-side.
- Tests: raw-action parse, equip-affix parameter join, conditional-only
  property selection, refresh-without-stacking, stale-expiry rejection, and
  recalc preservation/removal.
- Build: focused tests first, then a uniquely named JAR through a separate
  output path. Do not overwrite the repository-root or selected runtime JAR.
- UAT: separately user-controlled; confirm CRIT `79.3% -> 99.3%` for six
  seconds, ATK remains a single `+213` branch, refresh does not stack, and an
  inactive control receives no CRIT.
- Rollback: source changes revert by their named files; deployment, if later
  approved, restores the current selected JAR
  `LunaGC-Klee-terminal-bridge-16b5949-20260831-1932-fixed.jar` without
  changing the already audited resource overlay.

## Source repair candidate

The source repair is complete and built, but not deployed.

Changed source files:

- `AbilityModifier.java` recognizes raw action enum `POIDCNKIFGD` and parses
  `Actor_AttackRatio` / `Actor_CriticalDelta` as dynamic floats.
- `Ability.java` joins equipped relic set thresholds to the matching equip
  affix, open-config setters, and parameter list. For four MajoKai pieces this
  resolves `Dura=6`, `Rate1=.25`, and `Rate2=.20`; three pieces do not expose
  the four-piece values.
- `AbilityManager.java` recognizes `POIDCNKIFGD` as server orchestration when
  instancing the parent ability.
- `ActionApplyPredicatedModifier.java` hard-gates the fallback to exact ability
  `Relic_6.3_MajoKai`, modifier `UNIQUE_Relic_MajoKaiBuff2`, target `Self`, a
  present/passing `BJJDEAIEIGP` predicate, and positive finite CRIT/duration
  values. It never applies `UNIQUE_Relic_MajoKaiBuff1` or any ATK property.
- `Avatar.java` owns a transient keyed fight-property modifier lifecycle:
  refresh replaces rather than stacks, generations reject stale expiry, and
  active modifiers are reapplied after static stat recalculation. No player
  persistence schema changed.

Added focused tests:

- `MajoKaiRelicResourceTest.java`
- `AbilityEquipAffixSpecialsTest.java`
- `ActionApplyPredicatedModifierTest.java`
- `AvatarTransientFightPropertyModifierTest.java`

Red/green and regression evidence:

- Parser tests first failed because `POIDCNKIFGD` and
  `Actor_CriticalDelta` were absent.
- Join/dispatch tests next failed because `applyEquipAffixSpecials` and the
  action handler did not exist.
- Expiry integration next failed because the generation-checked helper did not
  exist.
- The final selected regression command covered the four new suites plus
  `ResourceLoaderOpenConfigTest`, `HexereiManagerTest`, and
  `FurinaGadgetPolicyTest`: 7 suites, 36 tests, 0 failures/errors/skips,
  `BUILD SUCCESSFUL`.
- `git diff --check` passed. Repository-wide `spotlessJavaCheck` remains red on
  its pre-existing 203-file formatting baseline; no formatting task was
  auto-applied and no broad formatting churn was introduced.

Candidate:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev\build\safe-majokai-crit-candidate\LunaGC-MajoKai-16b5949-crit-candidate-20260901-174920.jar`

- Size: `79,873,215` bytes.
- SHA-256: `CD1B8701D2942F581E1253789A22796DC3E4DD6E80C2A943B279760BE88FE7EA`.
- Manifest main class: `emu.grasscutter.Grasscutter`.
- JAR inventory contains the new action handler and changed ability/avatar
  classes.
- The temporary Gradle init override forced the JAR into the separate build
  directory and was removed afterward.
- Repository-root `LunaGC-6.6.0.jar` remains SHA-256
  `EEDA98211A45466D030665385BB0E7B4F337CE0B36D533A96AFD9885E5BBAD59`.
- Selected runtime JAR remains SHA-256
  `2DDB15095E21C55D341D6EFDE715DC6E38F13354D9E080405CF5C140FE3F8407`.
- No candidate was copied into the runtime directory. No selected JAR,
  launcher/configuration, resource, player data, retail file, official service,
  or process was changed.

## Attack-boundary repair candidate

The diagnostic UAT proved the client never emitted the modifier-change event,
so the permanent trigger now enters through the server-observed `AttackResult`
path immediately after `Scene.handleAttack` applies damage. Only an
`EntityAvatar` attacker can enter the fallback. It then requires positive
finite damage, a non-avatar target, four equipped pieces of set `15044`, and
effective Hexerei activation. The implementation traverses the loaded
`Relic_6.3_MajoKai` ability, exact `Relic_MajoKaiBuff_TriggerCD` action graph,
four-piece affix `2150441`, and open-config setters to resolve the BJJ-guarded
`UNIQUE_Relic_MajoKaiBuff2` as `Actor_CriticalDelta=.20` for `Dura=6`. It does
not apply `UNIQUE_Relic_MajoKaiBuff1` or any ATK property.

Test-first evidence:

- The initial landed-hit test failed specifically because the attack-boundary
  resolver did not exist, then passed after the resource traversal was added.
- The mutation test failed specifically because the landed-hit apply path did
  not exist, then passed with CRIT `0.793 -> 0.993`, one six-tick expiry, one
  publish, and unchanged `FIGHT_PROP_ATTACK_PERCENT`.
- Inactive Hexerei, avatar target, zero damage, and three-piece controls do not
  mutate, schedule, or publish.
- Seven selected suites passed 39 tests with zero failures, errors, or skips:
  the four MajoKai suites plus `ResourceLoaderOpenConfigTest`,
  `HexereiManagerTest`, and `FurinaGadgetPolicyTest`.
- `git diff --check` passed and source/test grep found zero
  `[DEBUG-MAJOKAI-6C31]` probes.

Final isolated candidate:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev\build\safe-majokai-attack-hit-candidate-20260901-182743\LunaGC-MajoKai-16b5949-attack-hit-crit-candidate-20260901-182743.jar`

- Size: `79,874,299` bytes.
- SHA-256: `1C840B04820B6F214DFFCDEBE70AAE67819A2CED8E175E2620EFD4E625C0F548`.
- Manifest main class: `emu.grasscutter.Grasscutter`.
- JAR inventory contains `Scene`, the action handler, `Ability`, `Avatar`,
  `AbilityManager`, and `AbilityModifier` changed classes.
- The temporary Gradle output override was removed.
- Repository-root `LunaGC-6.6.0.jar` remains SHA-256
  `EEDA98211A45466D030665385BB0E7B4F337CE0B36D533A96AFD9885E5BBAD59`.
- Selected runtime JAR remains SHA-256
  `2DDB15095E21C55D341D6EFDE715DC6E38F13354D9E080405CF5C140FE3F8407`.
- No runtime installation, launcher/configuration, resource, player-data,
  retail-file, official-service, or process change occurred.

Exact next step: user-controlled deployment/UAT only. From the complete runtime
working directory, test an effectively active four-piece avatar and confirm
CRIT `79.3% -> 99.3%` after one positive hit, return to `79.3%` after six
seconds, refresh without stacking, unchanged single client ATK branch, and no
CRIT change for an inactive control. On mismatch, retain `latest.log` and the
idle/triggered/expired stat captures. Rollback remains the selected fixed JAR
above; the deployed resource overlay is unchanged.
