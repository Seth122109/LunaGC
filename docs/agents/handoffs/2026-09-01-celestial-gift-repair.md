# Celestial Gift and Angelos repair handoff

Relation: Active  
Updated: 2026-09-02 Asia/Manila

## Goal and authority

Preserve the now-UAT-green Celestial Gift artifact-set repair and diagnose and
repair Angelos' Heptades weapon `14523`. Every later candidate must continue to
contain both artifact-set repairs; Angelos must be verified separately through
its hidden generic damage property rather than inferred from total damage.

Preserve every dirty/untracked artifact and the deployed MajoKai resource
overlay. Do not change the selected runtime JAR, launcher/configuration,
runtime resources, player data, retail executable, official services, or any
process. Build only into a unique isolated output and stop before deployment.

## Prior result

The user reports that the isolated MajoKai attack-boundary candidate passed
gameplay UAT: conditional CRIT worked. Its CRIT fallback is wearer-only because
both the landed-hit trigger and transient stat modifier use the attacking
artifact wearer's `Avatar`; it does not grant CRIT to teammates.

Current rollback/selected JAR remains:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-7.0.0\LunaGC-7.0.0\LunaGC-Klee-terminal-bridge-16b5949-20260831-1932-fixed.jar`

SHA-256 `2DDB15095E21C55D341D6EFDE715DC6E38F13354D9E080405CF5C140FE3F8407`.

## User equipment

Nicole currently wears five Celestial Gift pieces:

- `45513 10004 501234,4 501054,3 501204,1 501224,1 lv20`
- `45523 10003 501234,1 501064,6 501204,1 501054,1 lv20`
- `45533 10004 501234,4 501054,3 501204,1 501244,1 lv20`
- `45543 10001 501234,1 501064,6 501204,1 501054,1 lv20`
- `45553 10004 501234,4 501054,3 501204,1 501224,1 lv20`

The runtime `ReliquarySetExcelConfigData` maps these to set `15045`, whose
thresholds are 2/4 pieces and whose equip-affix base is `215045`.

## Authoritative behavior contract

The user supplied the HoYoWiki entry:

`https://wiki.hoyolab.com/pc/genshin/entry/10759?lang=en-us`

The page is JavaScript-rendered and its body was unavailable to the read-only
browser fetch. Search results and the exact runtime resources agree on this
contract:

- 2-piece: Energy Recharge +20% (already represented directly by
  `FIGHT_PROP_CHARGE_EFFICIENCY=.20` in affix `2150450`).
- 4-piece requires the equipping character's Witch's Homework completion.
- After that wearer uses an Elemental Skill, Light's Guidance lasts 20 seconds.
- All nearby party members receive +20% Elemental DMG matching the wearer's
  element. Same-name artifact bonuses do not stack.
- The effect is permitted while the wearer is off-field.
- With party `Hexerei: Secret Rite`, Mortal Hymn instead grants +40% for both
  the wearer's element and the current active avatar's element.
- If those elements match, the bonus remains +40%, not +80%.

The runtime graph additionally shows that the current-active element is meant
to be followed dynamically through `CurLocalAvatarMixinV2`, rather than being
permanently snapshotted only at cast time.

## Exact resource graph

Read-only inspection of the already deployed matching resources resolved:

- set: `15045`
- two-piece affix: `2150450`
- four-piece affix: `2150451`
- open config / ability: `Relic_6.6_Nicole`
- parameters: `AddHurtDelta=.20`, `Dura=20`,
  `Hexenzirkel_Ratio=.40`
- main handler: `UNIQUE_Relic_Nicole_Handler`
- timer: `Relic_Nicole_Timer`
- base fan-out: `Relic_Nicole_BuffHandler`
- team handler: `UNIQUE_Relic_Nicole_TeamHandler`
- Secret Rite handler: `UNIQUE_Relic_Nicole_TeamHandler_Hexenzirkel`
- active-avatar bridge: `UNIQUE_Relic_Nicole_BuffHandler_Predicate`

The main handler uses `OnAvatarUseSkillMixin.onTriggerSkill`, guarded by
`BJJDEAIEIGP`, to refresh the 20-second timer and team handlers. The base
handler chooses the wearer's element with `ByAvatarElementType`, range-gates at
40 XZ units, and targets `AllPlayerAvatars`. Per-element base modifiers apply
`Actor_<Element>AddHurtDelta=AddHurtDelta`.

Each base modifier follows team `SGV_HexenzirkelLevel` through
`AttachModifierToGlobalValueMixin`, threshold `1.5`, and adds a unique
Hexenzirkel modifier whose value is
`Hexenzirkel_Ratio - AddHurtDelta = .20`. Separately, the team handler uses
`CurLocalAvatarMixinV2` to run the same elemental fan-out from the current
active avatar. The resulting Mortal Hymn matrix is therefore `.40` for the
wearer element and `.40` for the current-active element; unique same-element
modifiers prevent `.80`.

## Diagnosed engine boundary

The read-only audit found these unsupported or incomplete server seams:

1. `OnAvatarUseSkillMixin` is parsed by `AbilityMixinData` but has no registered
   server handler.
2. Manual/effective Hexerei is evaluated server-side by `BJJDEAIEIGP`, but the
   client does not recognize the manual state, as proven during MajoKai UAT.
3. `AttachModifierToGlobalValueMixin` and `CurLocalAvatarMixinV2` are required
   by Mortal Hymn but are not implemented as this complete gameplay path.
4. The raw `Actor_Fire/Elec/Water/Grass/Wind/Rock/IceAddHurtDelta` resource
   properties are not modeled by the current generic temporary-stat fallback.

Existing reusable seams:

- `Ability.applyEquipAffixSpecials(...)` resolves four-piece values from real
  set/affix/open-config data.
- `Avatar.upsertTransientFightPropertyModifier(...)` provides non-persistent,
  refresh-safe, generation-checked temporary fight-property changes.
- `AbilityManager.onSkillStart(...)` receives accepted skill-start events and
  can distinguish Elemental Skills from attack-mode and energy/burst skills
  using `AvatarSkillDepotData`.
- `TeamManager.changeAvatar(...)` is the direct current-avatar switch boundary.
- `HexereiManager.isEffectivelyActive(wearer)` models Homework completion in
  this fallback, and effective team count / `SGV_HexenzirkelLevel >= 1.5`
  models Secret Rite.
- `PacketAvatarFightPropUpdateNotify` can publish changed elemental DMG stats.

## Ranked hypotheses / feedback loop

1. No server `OnAvatarUseSkillMixin` handler means the four-piece has no
   server trigger.
2. The client-side BJJ predicate rejects the manual Hexerei state, preventing
   the native client path.
3. Unsupported SGV/current-avatar mixins prevent the Mortal Hymn upgrade and
   element tracking.
4. Missing temporary elemental-DMG property modeling prevents observable stat
   updates even if orchestration is reached.

Before implementation, add one fast deterministic resource-driven test that
goes red on the exact missing behavior:

- four pieces + effective wearer + Elemental Skill resolve `.20/20s` for the
  wearer element across all team avatars;
- effective team count at least 2 resolves `.40` for wearer and current-active
  elements;
- matching elements resolve only `.40`;
- three pieces, inactive wearer, normal attack, and burst resolve no effect;
- refresh does not stack or extend stale expiries incorrectly;
- switching active avatars during the 20-second window replaces the tracked
  active element without losing the wearer's element;
- expiry restores all touched elemental properties and publishes updates.

## Implementation constraints and open interpretation

Prefer a narrowly named Celestial Gift controller/resolver over teaching the
generic ability engine all four unsupported mixins. It must derive `.20`,
`20`, and `.40` from the actual resource join and must never hard-code Nicole's
element.

The implementation must preserve these boundaries:

- only the set `15045` four-piece wearer can trigger;
- Homework requires that wearer to be effectively active;
- trigger only an Elemental Skill, not normal attacks or Elemental Burst;
- all active local party avatars receive the effect;
- Mortal Hymn activates only at effective team count >=2;
- the holder element remains throughout the effect;
- the second element follows the current on-field avatar during the effect;
- same element never exceeds `.40`;
- re-trigger refreshes one same-name effect rather than stacking;
- state is transient and never persisted;
- no generated protocol field changes.

The exact server observation boundary for a genuinely off-field wearer's skill
activation remains unresolved. Do not weaken the existing anti-spoof check in
`AbilityManager.onSkillStart` without evidence. The initial candidate may
correctly trigger on an accepted wearer Elemental Skill and keep/follow the
buff while the wearer is subsequently off-field; document this distinction in
UAT.

## Verification and build plan

1. Add and run the exact failing regression test before implementation.
2. Implement the smallest resource-driven controller and explicit dispatch at
   the accepted Elemental Skill seam.
3. Refresh current-element state at the existing avatar-switch/team-update
   seams without changing unrelated switching behavior.
4. Run the new focused suite plus all existing MajoKai parser, resource-join,
   lifecycle, and attack-boundary tests.
5. Run `git diff --check` and grep out every tagged probe.
6. Build through a temporary Gradle init override to a new unique non-root
   directory; hash and inspect the candidate and confirm the repository-root
   and selected runtime JAR hashes did not change.
7. Stop before deployment and provide focused user-controlled UAT.

Rollback for this source-only phase is ordinary reviewable reversion of the
new Celestial Gift class/tests and the explicit AbilityManager/TeamManager
hooks. The existing MajoKai resource backup remains
`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\pre-majokai-resource-deploy-20260901-165259`.

## Celestial implementation boundary

The initial missing-class test was captured RED. Source implementation now
exists in `CelestialGiftRelicEffect`, with explicit dispatch in
`AbilityManager.onSkillStart` after the accepted skill event and refresh calls
from the existing `TeamManager` team-update and avatar-switch boundaries.
Seven focused tests pass: resource-derived Light's Guidance, two-element and
same-element Mortal Hymn, invalid trigger rejection, teamwide apply/expiry,
generation-safe non-stacking refresh, and active-element switch following
without extending duration. Packet snapshots include properties removed by a
switch. No runtime state changed during implementation; the isolated build
artifact is recorded below.

## Final isolated candidate

Candidate:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev\build\safe-celestial-gift-candidate-20260902-002640\LunaGC-CelestialGift-16b5949-team-element-candidate-20260902-002640.jar`

- Size: `79,884,899` bytes.
- SHA-256: `E94588E0A0F1EC84D8DF1FC22E751C97081768632105367ECE01578B39DB66B3`.
- Manifest main class: `emu.grasscutter.Grasscutter`.
- Inventory contains `CelestialGiftRelicEffect`, `AbilityManager`, and
  `TeamManager` changed classes.
- Eight selected suites passed 46 tests with zero failures, errors, or skips.
- `git diff --check` passed and source/test grep found no debug probes.
- Repository-root JAR remains SHA-256
  `EEDA98211A45466D030665385BB0E7B4F337CE0B36D533A96AFD9885E5BBAD59`.
- Selected runtime JAR remains SHA-256
  `2DDB15095E21C55D341D6EFDE715DC6E38F13354D9E080405CF5C140FE3F8407`.
- The repository-wide Spotless check remains red on 205 pre-existing files;
  it made no source changes and is not a scoped acceptance gate.
- No runtime installation, launcher/configuration, resource, player-data,
  retail-file, official-service, or process action occurred.

## Combined candidate and user-controlled UAT

The combined candidate is:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-7.0.0\LunaGC-7.0.0\LunaGC-GC70-ArtifactFixes-Angelos14523-candidate-20260902-0940.jar`

- SHA-256:
  `FCB38127B2189855455BE8A6FD152F51ABDF80D5E335501EC21D9E75E92E89A1`.
- Runtime copy creation time: 2026-09-02 09:58:11 local; `latest.log` records
  server starts at 09:58:32 and 10:35:00.
- The log does not record the launched JAR path or hash, so the exact process
  image is not independently provable from the startup record.
- JAR inventory was previously verified to contain `CelestialGiftRelicEffect`,
  `AngelosHeptadesWeaponEffect`, `AbilityManager`, and `TeamManager`.

Celestial Gift passed the focused observed branch. Before Nicole E, active
Klee showed ATK `853 + 1313 = 2166`, EM `0`, CRIT Rate `79.3%`, CRIT DMG
`348.0%`, and Pyro DMG `75.4%`. Under Nicole's shield she showed ATK
`853 + 3346 = 4199`, EM `160`, CRIT Rate `99.3%`, unchanged CRIT DMG
`348.0%`, and Pyro DMG `115.4%`. The exact `+40` Pyro change proves the
Celestial Gift Secret Rite branch applied.

The same-target critical observation was approximately `10K` before Nicole E
and `73K` under the shield. This does not prove Angelos. The visible ATK and
Pyro changes alone account for about `1.94 * 1.23 = 2.38x`, or roughly `24K`.
The remaining increase can come from Nicole's E/Guidance and constellation
package: C2 matching-element RES reduction, C4's flat damage addition equal to
70% of Nicole's ATK, and C6's 40% DEF ignore. Klee's Hexerei status upgrades
Guidance immediately. Secret Rite's direct 300%-of-Nicole-ATK increase is
scoped to Arcane Projections, and Nicole C1's 600%-of-the-active-character-ATK
projection is a separate damage instance.

## Angelos runtime red

Nicole was level 90 C6 with level-90 R5 weapon `14523` and `4600` idle ATK.
The resource-derived R5 formula therefore caps at `.58`:

`min(.58, floor(4600 / 1000) * .22) = .58`.

On activation, active Klee must receive hidden
`FIGHT_PROP_ADD_HURT +.58` for 20 seconds. `SetStatsCommand` captures
`avatar.getFightProperty(stat.prop)` before installing a lock override, and
`Avatar.upsertTransientFightPropertyModifier` writes into that same live fight
property map. Consequently `/stats lock dmg` is a valid read oracle; the
immediate `/stats unlock dmg` removes only the temporary command override.
Do not attack between those two commands because stat recalculation reapplies
the active transient modifier after the override.

Runtime `logs/latest.log` contains six Klee (`10002`) observations, all red:

- line 19531, 10:36:05: locked to `0.0%`; unlock line 19535;
- line 19545, 10:36:33: locked to `0.0%`; unlock line 19552;
- line 19575, 10:37:00: locked to `0.0%`; unlock line 19580;
- line 19596, 10:37:31: locked to `0.0%`; unlock line 19597;
- line 19598, 10:37:53: locked to `0.0%`; unlock line 19599;
- line 19600, 10:38:23: locked to `0.0%`; unlock line 19601.

This proves the Angelos damage modifier did not activate on Klee during the
test. It rules out the total-damage inference and the command-observability
hypothesis. The log has no Angelos-specific diagnostics, so it cannot yet
separate the failing hook stage.

## Angelos ranked hypotheses

1. Nicole's shield creation never reaches the server's
   `handleModifierChange` `MODIFIER_ACTION_ADDED` path. Prediction: a tagged
   entry probe sees no matching added modifier when E creates the shield.
2. The event arrives, but the chosen local modifier/ability data does not
   contain `ShieldBarMixin`. Prediction: the entry probe appears, while the
   parsed modifier reports no shield-bar mixin.
3. A shield-bar modifier is found, but `findAngelosHeptadesSourceAbility`
   returns null or ambiguous because target/instanced ability attribution does
   not resolve Nicole. Prediction: shield detection is positive and source
   resolution is null.
4. Nicole resolves as source, but exact equipped weapon/affix/refinement data
   fails `AngelosHeptadesWeaponEffect.resolve`. Prediction: the source is
   Nicole while resource resolution is empty.
5. The effect activates on the wrong recipient or expires before observation.
   This is lowest-ranked because six repeated Klee reads were zero; prediction:
   activation succeeds but recipient/generation diagnostics identify a
   different avatar or premature expiry.

Current tests prove the resource formula, controller lifecycle, recipients,
energy cooldown, and direct `onShieldCreated` behavior. They call the
controller directly and do not exercise the real `AbilityManager` modifier
invocation/source-attribution boundary, so they are not a regression seam for
this runtime failure.

## Angelos source cause and isolated repair

Read-only inspection of the installed GC 7.0
`BinOutput/Ability/Temp/AvatarAbilities/ConfigAbility_Avatar_Nicole.json`
identified the exact mismatch. Nicole's team shield modifier is
`UNIQUE_Avatar_Nicole_ElementalArt_Shield_Team`, whose serialized mixin type is
`GlobalMainShieldMixin`; its child modifier uses `GlobalSubShieldMixin`. The
server `AbilityMixinData.Type` enum did not define `GlobalMainShieldMixin`, so
the lenient enum adapter resolved that serialized value to null. The Angelos
hook then rejected the modifier because it recognized only `ShieldBarMixin`.

The real serialized-modifier predicate regression
`realModifierPredicateRecognizesNicolesGlobalMainShield` was captured RED at
`AngelosHeptadesWeaponEffectTest.java:35`. A first attempt to host the predicate
on `AbilityManager` caused its heavyweight static initialization to terminate
the isolated test worker before a report could be written, so the production
predicate was placed in the narrow package-local `AngelosShieldTrigger` seam
used directly by the hook. The same regression then failed normally and
deterministically before the repair.

The smallest repair is:

- add `GlobalMainShieldMixin` to `AbilityMixinData.Type`;
- route the Angelos hook through `AngelosShieldTrigger`;
- accept either the existing `ShieldBarMixin` or `GlobalMainShieldMixin` as a
  shield-creation modifier;
- deliberately reject `GlobalSubShieldMixin`, preventing Nicole's child shield
  from double-triggering the weapon or its energy cooldown.

Temporary `[DEBUG-ANGELOS-14523]` probes covered added-entry, ability lookup,
modifier lookup, source attribution, weapon resolution, activation, recipient,
and live damage boundaries. The resource mismatch plus the deterministic RED
test isolated the cause without runtime deployment, so every probe was removed
before the final build. Source/test grep confirms the tag is absent.

Changed Angelos-slice files are:

- `src/main/java/emu/grasscutter/data/binout/AbilityMixinData.java`;
- `src/main/java/emu/grasscutter/game/ability/AngelosShieldTrigger.java`;
- `src/main/java/emu/grasscutter/game/ability/AbilityManager.java`;
- `src/test/java/emu/grasscutter/game/ability/AngelosHeptadesWeaponEffectTest.java`.

Nine selected suites passed 54 tests with zero failures, errors, or skips:
the Angelos and Celestial controllers, four MajoKai/resource/transient seams,
`ResourceLoaderOpenConfigTest`, `HexereiManagerTest`, and
`FurinaGadgetPolicyTest`. `git diff --check` passed. The isolated build used a
temporary non-root Gradle destination override, which was deleted afterward.

Final isolated combined candidate:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev\build\safe-angelos-global-main-shield-candidate-20260902-114535\LunaGC-GC70-ArtifactFixes-Angelos14523-GlobalMainShield-candidate-20260902-114535.jar`

- Size: `79,897,195` bytes.
- SHA-256: `98F2EA2846BA46C2D32FCE157972F337BBDD7E74288EE316F9F5D95A72EEE756`.
- Manifest main class: `emu.grasscutter.Grasscutter`.
- Inventory includes `AbilityMixinData$Type`, `AngelosShieldTrigger`,
  `AngelosHeptadesWeaponEffect`, `CelestialGiftRelicEffect`, `AbilityManager`,
  `TeamManager`, and MajoKai's `ActionApplyPredicatedModifier`.
- Repository-root JAR remains
  `EEDA98211A45466D030665385BB0E7B4F337CE0B36D533A96AFD9885E5BBAD59`.
- Previously selected combined runtime candidate remains
  `FCB38127B2189855455BE8A6FD152F51ABDF80D5E335501EC21D9E75E92E89A1`.
- No JAR selection, deployment, server/client/process, runtime-resource,
  configuration, player-data, retail-file, or official-service change was
  made.

## Angelos primary UAT and off-field observation seam

User-controlled UAT of the `20260902-114535` repair is green for the primary
Angelos branch: Klee's `/stats lock dmg` read reported `58.0%` under Nicole's
shield. This directly verifies that the real `GlobalMainShieldMixin` hook now
activates the R5 weapon effect. Celestial Gift had already passed independently
at `75.4% -> 115.4%` Pyro DMG.

The existing stats command cannot observe the half-strength off-field Hexerei
branch. It always selects `TeamManager.getCurrentAvatarEntity()`, while an
avatar switch invokes `refreshAngelosHeptadesTeam`; the switched character is
therefore promoted from the intended off-field `.29` to the current-active
`.58` before the command can inspect it. The controller regression already
proves `.58` current-active and `.29` eligible off-field, including refresh and
expiry, but a gameplay read still requires a non-switching server seam.

`SetStatsCommand` now supports read-only
`/stats read <stat> [<avatarId>]`. With an avatar ID it reads the owned
`Avatar` object's live fight property directly and does not lock, override,
recalculate, switch, or publish any stat. The isolated combined candidate is:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev\build\safe-angelos-offfield-read-candidate-20260902-121125\LunaGC-GC70-ArtifactFixes-Angelos14523-OffFieldRead-candidate-20260902-121125.jar`

- Size: `79,897,861` bytes.
- SHA-256: `D720AA9152F5764EF30F6393046D83418293D418EB25A5C09924D8FB577BCA55`.
- Manifest main class: `emu.grasscutter.Grasscutter`.
- Inventory contains `SetStatsCommand`, `AbilityMixinData$Type`,
  `AngelosShieldTrigger`, `AngelosHeptadesWeaponEffect`,
  `CelestialGiftRelicEffect`, `AbilityManager`, `TeamManager`, and MajoKai's
  `ActionApplyPredicatedModifier`.
- Nine selected suites passed 54 tests with zero failures/errors/skips;
  `git diff --check` passed.
- The temporary non-root Gradle build override was removed.
- Repository-root and prior runtime candidate hashes remain
  `EEDA98211A45466D030665385BB0E7B4F337CE0B36D533A96AFD9885E5BBAD59`
  and `FCB38127B2189855455BE8A6FD152F51ABDF80D5E335501EC21D9E75E92E89A1`.
- No deployment, JAR selection, server/client/process, runtime-resource,
  configuration, player-data, retail-file, or official-service change was
  made.

## Final Angelos UAT

User-controlled UAT of the `20260902-121125` read candidate is green.
`/stats read dmg <avatarId>` returned `.29` for an eligible off-field Hexerei
character; the current-active branch had already returned `.58`. These two
server-side fight-property observations verify Angelos' full and half-strength
damage-recipient branches. Celestial Gift independently remains green at
`75.4% -> 115.4%` Pyro DMG, and MajoKai's conditional CRIT branch had already
passed its focused UAT.

## Exact next step

Completed in commit `b609b89` (`fix: restore Angelos' Heptades effect`). The
commit contains only the scoped Angelos implementation, shield-mixin
classification, team refresh hook, read-only off-field stat seam, and Angelos
regression test. Unrelated dirty/untracked work remains preserved and unstaged.
No push was requested or performed. Rollback is `git revert b609b89` plus the
preserved prior selected JAR/configuration if runtime rollback is also needed.
