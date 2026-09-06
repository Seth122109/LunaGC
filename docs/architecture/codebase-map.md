# LunaGC GC 7.0 codebase map

Status date: 2026-08-24  
Scope: groundwork through startup/resources/packets, avatar persistence, talents and constellations, abilities and predicates, and current Hexenzirkel seams  
Runtime status: source-verified only; no server, client, Gradle, JAR, launcher, resource, config, or player-data action was performed

## How to use this map

This document records current checked-in source behavior, not intended GC 7.0 mechanics. Use it to choose the next narrow evidence or implementation seam. Verify any gameplay conclusion against authoritative GC 7.0 runtime data or protocol evidence before changing code.

Evidence labels used here:

- **Source fact** — directly confirmed in the checked-in Java/config source named in the entry.
- **Checkout observation** — confirmed from the local repository state, but not runtime behavior.
- **Candidate risk** — a source behavior that may be unsafe or incomplete, but is not yet a confirmed GC 7.0 defect.
- **Unresolved** — cannot be established from the current checkout or approved evidence.

CodeGraph v1.5.0 is a navigation aid only. Its current index is healthy (3,570 files, 251,229 nodes, 519,546 edges, WAL, complete, zero pending changes/references), but direct calls, reflection, generated protocol classes, and name collisions have already produced incomplete or noisy graph results. The source paths below are authoritative for this map.

## System ownership at a glance

| Concern | Primary owner | Downstream state or boundary |
| --- | --- | --- |
| Process bootstrap and run mode | `Grasscutter` | database, servers, plugins, resources, console |
| Runtime/default path resolution | `ConfigContainer`, `FileUtils` | external `resources/`, scripts, built-in defaults |
| Data/config loading | `ResourceLoader`, `GameData` | Excel resources, ability configs, talents, mappings, caches |
| Packet registration and dispatch | `GameServer`, `GameServerPacketHandler`, `GameSession` | reflected packet handlers and session-state gates |
| Batched invoke forwarding | `InvokeHandler`, `HandlerUnionCmdNotify` | scene/host broadcasts and combat/ability flushes |
| Avatar persistence and reconstruction | `Avatar`, `AvatarStorage`, `DatabaseHelper` | MongoDB entity state plus reconstructed runtime data |
| Talent/constellation state | `Avatar`, `HandlerUnlockAvatarTalentReq` | talent IDs, skill levels, open-config effects, protocol state |
| Ability/action/modifier runtime | `AbilityManager`, `AbilityModifierController` | instanced abilities/modifiers, action/mixin dispatch |
| Predicate evaluation | `PredicateEvaluator` | action eligibility from entity, talent, modifier, SGV, and HP state |
| Hexenzirkel team coordination | scene/team packets and handlers | tagged membership, team SGV, team-change packet, combat fallback |

## 1. Startup and configuration

Source-verified flow:

```text
Grasscutter class initialization
  -> loadConfig()
  -> ConfigContainer.updateConfig()
  -> loadLanguage()
  -> Utils.startupCheck()

Grasscutter.main(args)
  -> Crypto.loadKeys()
  -> StartupArguments.parse(args)
  -> DatabaseManager.initialize()
  -> construct GameServer and/or HttpServer for run mode
  -> construct PluginManager and HTTP routes
  -> ResourceLoader.loadAll() unless dispatch-only
  -> generate handbooks and gacha mappings
  -> start game/HTTP/dispatch server(s)
  -> enable plugins and open console
```

Evidence:

- **Source fact:** the static initializer loads and updates configuration before `main` (`src/main/java/emu/grasscutter/Grasscutter.java:70`). Loading the class is therefore not a harmless runtime probe.
- **Source fact:** `main` initializes MongoDB before constructing server instances (`Grasscutter.java:92`).
- **Source fact:** resource loading precedes game-server start (`Grasscutter.java:153`).
- **Source fact:** run mode controls whether game, HTTP, and dispatch servers are constructed or started (`Grasscutter.java:118`, `Grasscutter.java:165`, `Grasscutter.java:215`).
- **Candidate risk:** the startup log still reports `6.6.0` (`Grasscutter.java:109`) even though this checkout targets GC 7.0. Treat this as stale metadata, not runtime-version evidence.

Groundwork boundary: do not invoke the main class to inspect behavior. It can update `config.json`, initialize MongoDB, load/generate data, and start network services.

## 2. Resource and game-data loading

Source-verified flow:

```text
ConfigContainer.Structure
  -> resources = ./resources/
  -> scripts = resources:Scripts/

FileUtils static path setup
  -> built-in /defaults/data from class/JAR
  -> external resource directory or mounted ZIP
  -> scripts resolved under resources or an explicit path

ResourceLoader.loadAll()
  -> script loader
  -> avatar/gadget config data
  -> ability embryos, talents, open configs, modifiers
  -> reflected @ResourceType Excel resources into GameData
  -> ability/talent maps and GameDepot caches
  -> spawn, quest, scene, route, activity, trial-avatar, combat mappings
  -> entity-controller scripts
```

Evidence:

- **Source fact:** default resource and script paths are declared in `src/main/java/emu/grasscutter/config/ConfigContainer.java:102`.
- **Source fact:** `FileUtils` resolves built-in defaults plus an external directory or ZIP and freezes resource/script paths during class initialization (`src/main/java/emu/grasscutter/utils/FileUtils.java:23`).
- **Source fact:** `ResourceLoader.loadAll` loads ability configs before reflected Excel resources, then builds dependent maps/caches and remaining mappings (`src/main/java/emu/grasscutter/data/ResourceLoader.java:80`).
- **Source fact:** reflected `@ResourceType` classes load in priority groups, parallel within a group, into maps owned by `GameData` (`ResourceLoader.java:133`).
- **Checkout observation:** root `config.json` and runtime `resources/` are absent; runtime-loaded avatar tags, BinOutput abilities, and Excel data are therefore not available in this checkout.
- **Unresolved:** the actual launcher-selected resource path and exact GC 7.0 resource snapshot are not established.

Change implication: Java source alone cannot prove character membership, ability-special values, predicate coverage, or complete GC 7.0 mechanics. Obtain approved authoritative resource evidence before converting candidate seams into fixes.

## 3. Packet ingress, registration, dispatch, and response

Source-verified flow:

```text
KCP tunnel bytes
  -> GameSession.handleReceive
      decrypt (when enabled)
      validate frame magic and lengths
      extract opcode/header/payload
  -> GameServerPacketHandler.handle
      locate reflected @Opcodes handler
      enforce SessionState gate
      call ReceivePacketEvent
      invoke PacketHandler.handle
  -> handler mutates domain state and/or GameSession.send
      call SendPacketEvent
      build and optionally encrypt BasePacket
      write to KCP tunnel
```

Registration and dynamic boundaries:

- **Source fact:** `GameServer` constructs `GameServerPacketHandler(PacketHandler.class)` (`src/main/java/emu/grasscutter/server/game/GameServer.java:154`).
- **Source fact:** the packet handler reflects over `PacketHandler` subtypes, reads `@Opcodes`, instantiates them, and maps opcode to handler (`src/main/java/emu/grasscutter/server/game/GameServerPacketHandler.java:16`).
- **Source fact:** dispatch applies token/login/character-pick/active session gates before firing `ReceivePacketEvent` and dynamically invoking the handler (`GameServerPacketHandler.java:47`).
- **Source fact:** `GameSession.handleReceive` parses framed packets and forwards each to the dispatcher (`src/main/java/emu/grasscutter/server/game/GameSession.java:168`).
- **Source fact:** `GameSession.send` builds headers, calls `SendPacketEvent`, encrypts when configured, and writes through the tunnel (`GameSession.java:109`).
- **Graph boundary:** reflection and numeric opcode dispatch mean CodeGraph call trees are incomplete here; inspect annotations and handler source directly.

Union and invoke batching:

- **Source fact:** `HandlerUnionCmdNotify` parses embedded commands and recursively dispatches each through the same packet handler (`src/main/java/emu/grasscutter/server/packet/recv/HandlerUnionCmdNotify.java:14`).
- **Source fact:** after embedded dispatch it flushes combat and ability invoke queues, then drains queued attack results into scene handling (`HandlerUnionCmdNotify.java:34`).
- **Source fact:** `InvokeHandler` separates forward-to-all, all-except-current, and host queues, constructs the configured packet type reflectively, broadcasts, and clears each queue (`src/main/java/emu/grasscutter/game/player/InvokeHandler.java:20`).

## 4. Avatar persistence and reconstruction

`Avatar` is a Morphia entity (`src/main/java/emu/grasscutter/game/avatar/Avatar.java:43`). Its important state splits across persistence and runtime reconstruction:

| State | Role | Lifecycle |
| --- | --- | --- |
| `avatarId` | resource identity | persisted |
| `skillDepotId` | active skill-depot identity | persisted |
| `skillLevelMap` | active-skill levels | persisted |
| `talentIdList` | unlocked constellation/talent IDs | persisted |
| `proudSkillList` | inherent/passive proud skills | persisted and rebuilt when depot changes |
| `avatarData`, `skillDepot` | loaded resource objects | transient; reattached after database load |
| `proudSkillBonusMap` | constellation-derived extra skill levels | transient; cleared and recalculated |
| `skillExtraChargeMap` | constellation-derived charge bonuses | transient; cleared and recalculated |

New-avatar flow:

```text
new Avatar(AvatarData)
  -> initialize collections and combat properties
  -> choose traveler/default AvatarSkillDepotData
  -> setSkillDepotData
      seed missing skills at level 1
      rebuild promote-gated and quest proud skills
      recalculate stats
  -> AvatarStorage.addAvatar
      attach owner
      index by avatar ID and GUID
      DatabaseHelper.saveAvatar
```

Evidence: `Avatar.java:116`, `Avatar.java:269`, and `src/main/java/emu/grasscutter/game/avatar/AvatarStorage.java:53`.

Database-load flow:

```text
AvatarStorage.loadFromDatabase
  -> DatabaseHelper.getAvatars(player)
  -> resolve AvatarData and AvatarSkillDepotData from GameData
  -> skip records missing either resource
  -> attach resource objects and owner
  -> recalcConstellations()
  -> rebuild ID/GUID maps
  -> repair and save traveler depot state
```

Evidence: `AvatarStorage.java:155`.

Protocol boundary: `Avatar.toProto` publishes talent IDs, skill-depot ID, core proud-skill level, skill levels, inherent proud skills, constellation-derived extra levels, and extra charge counts (`Avatar.java:1059`). Persisted state and client-visible state are therefore coupled through reconstruction; changing only one side risks login/reconnect drift.

## 5. Talents and constellations

Representation:

- `skillLevelMap` holds active talent/skill levels.
- `talentIdList` holds unlocked constellation IDs.
- `proudSkillList` holds inherent/passive proud skills.
- `proudSkillBonusMap` and `skillExtraChargeMap` are derived from each unlocked talent's open config.

Reconstruction:

- **Source fact:** `recalcConstellations` clears both derived maps, resolves every stored talent ID through global `GameData`, resolves its open config, and applies the config without notification (`Avatar.java:851`).
- **Source fact:** invalid/missing resource references are skipped during reconstruction rather than repaired or rejected.

Unlock paths:

```text
sequential/internal path
  Avatar.unlockConstellation([skipPayment])
  -> current core proud-skill level
  -> talent ID from this avatar's skill depot
  -> direct unlock path

client direct-ID path
  HandlerUnlockAvatarTalentReq
  -> avatar by GUID
  -> Avatar.unlockConstellation(request.talentId)

direct unlock path
  -> global AvatarTalentData lookup by requested ID
  -> optionally pay mainCostItemId with hard-coded count 1
  -> add talent ID
  -> send unlock/response packets
  -> apply open config
  -> recalculate stats
  -> save avatar
```

Evidence: `src/main/java/emu/grasscutter/server/packet/recv/HandlerUnlockAvatarTalentReq.java:13` and `Avatar.java:966`.

Candidate validation seam—not yet a confirmed GC 7.0 bug:

- The direct-ID path does not visibly require the requested talent to belong to the avatar's active skill depot.
- It does not visibly enforce predecessor/order or reject an already-present ID before payment.
- It pays one item rather than reading `mainCostItemCount` from the talent data.
- Because the intended bypass and authoritative GC 7.0 rules are not yet established, do not change this path from source inspection alone.

## 6. Ability, action, modifier, and invoke runtime

Data ownership:

- Ability embryos, ability modifiers, open configs, and talent mappings are loaded from external BinOutput/ExcelBinOutput by `ResourceLoader` into `GameData`.
- Runtime entities hold instanced abilities and instanced modifier controllers.

Dispatch flow:

```text
ability notify packet handlers
  -> AbilityManager.onAbilityInvoke(entry)
      lazily initialize avatar abilities when needed
      localId != 0 -> handleServerInvoke
          resolve modifier-owned or instanced ability
          map local ID to action or mixin
          execute registered handler
      otherwise -> switch on invoke argument type
          override/global values/modifier change/stamina/energy/etc.
  -> enqueueForwardedInvoke in InvokeHandler
  -> union or packet-specific flush broadcasts queued entries
```

Evidence and boundaries:

- **Source fact:** action and mixin handlers are registered reflectively from `@AbilityAction` and `@AbilityMixin` annotations (`src/main/java/emu/grasscutter/game/ability/AbilityManager.java:137`).
- **Source fact:** `executeAction` looks up the handler by action type and submits execution to the ability event executor (`AbilityManager.java:168`).
- **Source fact:** `onAbilityInvoke` routes local IDs to server action/mixin execution and other argument types to dedicated handlers (`AbilityManager.java:245`).
- **Source fact:** `handleServerInvoke` resolves the ability from instanced modifier/ability IDs, maps the local ID, and executes the configured action or mixin (`AbilityManager.java:378`).
- **Source fact:** modifier-change handling resolves parent ability data by name/hash or instance position, validates local modifier indices, creates `AbilityModifierController`, stores it on the entity, and may execute `onAdded` orchestration (`AbilityManager.java:699`).
- **Source fact:** forwarding semantics are adjusted through `enqueueForwardedInvoke`; Furina-specific echo suppression is a bounded policy inside this generic seam (`AbilityManager.java:323`). It is reference evidence, not a Hexenzirkel template.
- **Graph boundary:** string/hash IDs, numeric local IDs, reflection, and external data determine runtime behavior. Static call results cannot prove which action executes for a named character.

## 7. Predicate evaluation

`PredicateEvaluator` is used by predicated actions/modifier application to decide whether configured behavior runs.

Current explicit support (`src/main/java/emu/grasscutter/game/ability/PredicateEvaluator.java:26`):

| Predicate type | Checked state |
| --- | --- |
| obfuscated `BJJDEAIEIGP` | caster/owner/resolved avatar has `AVATAR_TAG_HEXENZIRKEL` |
| `ByUnlockTalentParam` | proud skill or constellation open config matches `talentParam` |
| `ByHasModifier` | target instanced ability contains named modifier |
| `ByTargetGlobalValue` | target SGV compared with configured bound/operator |
| `ByTargetHPRatio` | target current/max HP compared with ability-special threshold |

Candidate risk:

- A missing/non-string `$type` returns `true`.
- Unknown predicate types return `true`.
- Several malformed/missing `ByTargetHPRatio` inputs also return `true`.

This is fail-open source behavior. It may incorrectly enable actions, but the external GC 7.0 predicate corpus is absent, so the affected characters/actions and correct fallback are unresolved. Inventory real predicate types before changing the default.

## 8. Hexenzirkel seams

| Seam | Current source behavior | Evidence / unresolved point |
| --- | --- | --- |
| Membership | `AvatarData.tags` contains `AVATAR_TAG_HEXENZIRKEL` | `PredicateEvaluator.java:66`; external avatar data absent |
| Tagged-ID cache | lazily builds an unmodifiable static ID set from `GameData` | `PacketPlayerEnterSceneInfoNotify.java:30`; no invalidation path is visible |
| Scene entry SGV | counts tagged active-team avatars and sends `SGV_HexenzirkelLevel` in team ability state | `PacketPlayerEnterSceneInfoNotify.java:63` |
| Team changes | recounts active team, sends SGV change and team-change packet | `src/main/java/emu/grasscutter/game/player/TeamManager.java:358` |
| Scene-init notify | separately recounts and sends team-change packet | `src/main/java/emu/grasscutter/server/packet/recv/HandlerSceneInitFinishReq.java:34` |
| Team-change packet | hand-encodes outer field 1 containing inner field 6 level at opcode 1950 | `src/main/java/emu/grasscutter/server/packet/send/PacketTeamHexenzirkelChangeNotify.java:7`; declared field 9 is unused and no generated schema is present |
| Predicate gate | obfuscated type checks Hexenzirkel tag on caster/owner/resolved avatar | `PredicateEvaluator.java:31` |
| Zero-damage gadget fallback | reads `Hexenzirkel_NormalAttack_Ratio` plus `NormalAttack_*_Damage_Percentage`, may retarget nearest living monster, and computes damage from current avatar attack/anemo/crit | `src/main/java/emu/grasscutter/server/packet/recv/HandlerCombatInvocationsNotify.java:48`; untested and not tied to named characters without resources |

Cross-seam flow:

```text
AvatarData tag
  -> cached Hexenzirkel avatar-ID set
  -> active-team count
      -> SGV_HexenzirkelLevel
      -> TeamHexenzirkelChangeNotify
  -> predicate membership check

client-gadget combat invoke
  -> ability specials / computed overrides
  -> Hexenzirkel ratio and normal-attack percentage
  -> target selection and fallback damage calculation
```

High-value unresolved questions:

1. Which GC 7.0 avatar IDs are tagged, and is the tag set complete?
2. Is static cache invalidation required after supported resource reload?
3. Are opcode 1950 and fields 1/6 correct, and should declared field 9 be serialized?
4. Which ability configs use the obfuscated predicate and damage-special names?
5. Is the combat fallback server-authoritative, a compatibility shim, or masking a packet/ability bug?

Do not answer these from symbol names or another client version.

## 9. Change-planning boundaries

Before any gameplay implementation:

1. Obtain or approve authoritative GC 7.0 resource/protocol evidence for the selected character and seam.
2. Name the owning workflow, exact classes/methods, data IDs/keys, packet impact, persistence impact, and expected client-visible state.
3. Separate shared-engine behavior from character-specific policy. Furina's gadget lifecycle policy proves a bounded integration pattern, not shared Hexenzirkel semantics.
4. Design isolated compile/test output so Gradle cannot overwrite the active root JAR or regenerate tracked protocol source unexpectedly.
5. Define focused unit/integration checks and later separately approved runtime UAT.
6. Preserve the prior deployable JAR/config/data state before any deployment.

## Verification and current limits

Verified for this map:

- Pinned CodeGraph JSON health and worktree isolation.
- Direct source paths and workflows for all sections above.
- Reflection and generated/data-driven boundaries were treated as limitations, not inferred call truth.
- The current task performed documentation edits only.

Not verified:

- Server startup, client connection, gameplay behavior, MongoDB reconstruction with real player data, or resource loading with the actual runtime tree.
- Complete GC 7.0 Hexerei membership, ability configs, predicate inventory, packet schema, or intended mechanics.
- Gradle compilation/tests, active launcher/JAR target, deployment, or rollback execution.

Rollback: this file is a documentation-only addition and can be removed or reverted independently without touching source, generated protocol, runtime data, CodeGraph, or launcher state.
