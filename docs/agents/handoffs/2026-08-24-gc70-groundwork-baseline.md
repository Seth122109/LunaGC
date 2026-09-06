# GC 7.0 groundwork baseline handoff

Relation: Active  
Updated: 2026-08-26 Asia/Manila  
Source task: `01a03150-5813-7292-b466-c4f29e943936`
Destination task: `01a0315b-73fd-72f0-9ec1-7973c9db034e`  
Transfer route: fresh same-project local task; destination active and guidance read
Policy-correction task: `01a03277-5bb0-7380-877a-cc82e037b2eb`
Current continuation task: `01a03d79-1b5a-7b93-add6-4d8c4c3e74e0` (`GC 7.0 Hexerei Venti Proven-Seam Repair`), created from source `01a033ec-6367-7eb0-86f8-64b6c1eaaa1a`

## Goal

Finish project groundwork and an evidence-backed architecture understanding for LunaGC GC 7.0 before implementing Hexerei or other character gameplay changes. Required end state: healthy documented CodeGraph index; architecture workflow map; Furina reference analysis; Hexerei inventory/status matrix; testing/UAT/deployment/rollback guidance; no gameplay/runtime changes.

## Constraints and authority

- General Java/Gradle project, not Power Platform or Excel.
- Local private server/runtime only. No official services, retail executable edits, account-security bypass, launcher/config/JAR/player-data changes, server start, client connection, or gameplay UAT during groundwork.
- Preserve all dirty work and avoid destructive Git.
- Project-local docs, context helper, and `.codegraph/` index are authorized. Global installs, PATH, Codex/MCP configuration, runtime actions, and gameplay edits require approval.
- User approved exactly one specialist worker for CodeGraph documentation. Worker completed and is no longer needed.
- User confirmed this is GC 7.0; `6.6.0` strings are stale metadata.
- The user explicitly authorized the fresh task that completed the continuity-policy correction described below. This authority covers project-local documentation only; it does not authorize gameplay, runtime, build, deployment, global configuration, or agent/subagent work.
- The user then authorized documentation groundwork through the codebase-map ability/predicate/Hexenzirkel phase and required a context-token footer on every user-facing response. That authority remained documentation/read-only inspection only and did not authorize gameplay, runtime, build, deployment, global configuration, or agent/subagent work.

## Decisions

1. Use CodeGraph v1.5.0 through version-pinned `npx`, not a global install or agent installer.
2. Keep CodeGraph telemetry/update checks off per PowerShell session with `$env:DO_NOT_TRACK='1'`.
3. Add `/.codegraph/` to root `.gitignore` before initialization; generated graph data is local-only.
4. Treat CodeGraph as discovery evidence and verify source-critical conclusions directly.
5. Treat the checked-out repository as the previously modified fork: remote is `Seth122109/LunaGC`, current branch is one Furina commit ahead of remote `7.0.0`, and the commit author is Seth122109.
6. Treat GC 7.0 as the code/protocol/client target and `6.6.0` Gradle/startup/JAR strings as stale naming to correct only in a separately scoped change.
7. Do not run `jar` or `build` during groundwork: the Gradle `jar` task writes a fat JAR to the repository root and can overwrite the active `LunaGC-6.6.0.jar` used by Cultivation.
8. Use one concise task pointer, one active handoff, explicit evidence labels, workflow-level checks, artifact hashes, and named rollback paths from the reference projects. Do not import Excel, Power Platform, SharePoint, organization, or reference-specific token tooling rules wholesale.
9. Continuity measurement is mandatory and best-effort. The operative `110,000 / 130,000 / 150,000` warning/preparation/route policy supersedes the prior operative `135,000 / 140,000 / 150,000` thresholds. Run the helper before every user-facing response, at the start of non-trivial work, and immediately before deciding whether to begin each broad or output-heavy phase. End every user-facing response—including progress updates and final responses—with the current helper footer, or `Context: unknown / 150k project limit` when measurement fails. Routing remains proportional: bounded deterministic closeout finishes locally, named user/external waits pause without an idle task, and compaction or a fresh task is appropriate only for substantial ambiguous or multi-step work when authorization permits it. The thresholds are conservative project-quality gates, not a model limit, universal quality cliff, or automatic transfer trigger; qualitative continuity failures and material phase changes may require earlier routing. The footer reports telemetry but never selects the route or replaces the pre-phase decision.
10. Fresh-task creation requires explicit user approval by default. Exception: when the user has stated a concrete goal and given a terminal instruction such as “go ahead,” “work until completion,” “finish,” or equivalent, that instruction grants standing authorization to create the minimum necessary safe same-project continuation task or tasks needed to pursue that already-authorized goal. The source task must not stop solely to request the same approval again. Without terminal authorization, the normal explicit-approval requirement remains.
11. Terminal authorization does not make task creation automatic and does not broaden the goal, scope, permissions, destructive authority, runtime authority, deployment authority, or approval for consequential unresolved choices. Ask when continuation would materially change scope, require new authority, cross projects or environments, introduce a risky or destructive action, or resolve a consequential ambiguity not already settled by the user.
12. Before a standing-authority transfer, finish active mutations and verification to a safe recorded boundary; update the active handoff and task pointer proportionally; preserve the goal, constraints, decisions, changed artifacts, verification, rollback, open risks, and one exact next step; create only the minimum necessary safe same-project continuation; verify destination readiness; transfer sole ownership; then stop substantive execution in the source task. Never create an idle task for a named user/external dependency or enter a recursive task-creation loop. Keep only one active owner, and do not let a destination already at or above the route gate automatically create another continuation.
13. Programmatic task creation appeared unreliable while the user was connected through mobile remote: the attempted create call did not yield a usable task in the task list. The working fallback was a paste-ready prompt backed by this durable handoff; never claim that a destination exists unless a real task ID is returned and visible. This operational fallback does not change Decisions 9–12.
14. The user designated `LunaGC-Resources-7.0.zip` with SHA-256 `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF` as the project-authoritative A2 resource baseline and approved `AVATAR_TAG_HEXENZIRKEL` as the resource-matrix inclusion rule.
15. Official first-party notices establish 12 playable Hexerei characters through Version 7.0: Durin, Venti, Klee, Albedo, Mona, Fischl, Sucrose, Razor, Varka, Nicole, Lohen, and Prune. Hexerei gameplay status is not interchangeable with lore membership in the Hexenzirkel, and Luna VIII's Witch's Revelation roster is a separate system.
16. Preserve eligibility and per-player activation as separate matrix fields. Official wording says the character becomes Hexerei after completing character-specific homework/quests; the static resource tag alone does not prove per-player completion.
17. After completing the Hexerei roster, cover the separate Witch's Revelation roster: Wriothesley, Yae Miko, Cyno, Yumemizuki Mizuki, Qiqi, Diona, and Beidou. The official enhancement term is `Lucid Revelations`; do not merge this roster into Hexerei.
18. Later approved implementation must expose per-player Witch's Homework/Hexerei activation and Lucid Revelation activation through the server's unlock-state commands. This is a requirement to map and plan, not gameplay-edit authority in the resource-reconciliation phase.
19. The user confirmed the README credit link labeled `hk4e-protos` points to a purged GitLab repository. Do not keep retrying it as a public source; preserve protocol fields as unresolved until an immutable replacement schema is supplied or approved.
20. The special-condition parameter namespace is resolved: each of the 12 values is exactly the character's Hexenzirkel chapter `endQuestId`. The approved archive lacks the matching terminal quest rows and quest BinOutput, so completion status must fail closed as unavailable rather than being inferred from tags or materialized proud skills.
21. The read-only per-player status contract keeps eligibility, authoritative quest completion, derived proud/open-config state, and effective activation separate. Witch's Revelation/Lucid Revelations remains a separate namespace.
22. Bounded same-task worker/subagent delegation is pre-authorized when it materially helps independent, in-scope work. It does not broaden mutation, runtime, deployment, external-service, or risk authority. Durable agents and user-visible fresh tasks/threads retain the existing approval and continuity rules.
23. Public web data is insufficient as an authoritative LunaGC completion baseline. Project Amber is a complete ID/name/objective mapping source; immutable Dimbreath GC 7.0 data supplies all 12 obfuscated BinOutput payloads but no terminal quest Excel rows; public LunaGC resources match the approved archive and still lack the payloads. Do not synthesize missing records.
24. The user authorized a fresh continuation to attempt deterministic, version-matched deobfuscation and recovery first. If complete authoritative quest rows cannot be proven, implement the smallest explicit, reversible per-player fallback with provenance that distinguishes manual/derived activation from authoritative quest completion. Never synthesize `GameQuest` rows or infer completion from eligibility, tags, or materialized proud skills. Source-only changes and targeted tests are in scope; runtime resources, live player data, deployment, gameplay UAT, and unsafe packaging remain gated.
25. Deterministic GC 7.0 deobfuscation proved that the archived payloads omit authoritative Excel-only accept conditions, combiners, begin execs, and other exact fields. Authoritative LunaGC-loadable recovery is `NO`; no candidate quest JSON may be generated or installed.
26. The approved fallback is one player-owned `HexereiManager` namespace with validated schema-v1 `MANUAL_COMMAND` audit records. Raw `Avatar.proudSkillList` remains diagnostic/materialized state; runtime and protocol consumers use a filtered effective view. Static tags are eligibility only, authoritative completion always remains `UNAVAILABLE_RESOURCE`, and Witch's Revelation/Lucid Revelations remains separate.

## Status and completed evidence

### Paths

- Repository exists: `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev`.
- Cultivation exists: `C:\Program Files\Cultivation`.
- Game directory exists: `C:\Games\Genshin\Client\HoYoPlay\games\Genshin Impact game`.
- Supplied EDS2 path had separator placement errors. Intended read-only project found at `\\Tnsp-hdd-seth-d\c\Users\SP1148.AP\Desktop\PROJECTS (desktop)\EDS2_Automation FY26\号口見積書_Automation`.
- DRRS reference exists at `\\Tnsp-hdd-seth-d\d\Projects\DRRS_App`.
- SMB host resolves to `192.168.254.112`, TCP/445 succeeds. Reference reads were read-only.

### Git and repository classification

- First command was `git status --short --branch --untracked-files=all`; initial checkout was clean.
- Branch: `fix/furina-salon-lifecycle`, tracking `origin/fix/furina-salon-lifecycle`.
- Remote: `https://github.com/Seth122109/LunaGC.git`.
- Local clone is shallow (`true`) with one local commit, `5a50a40` (`Fix Furina summon limits and lifetime`).
- Remote default branch: `7.0.0` at `90cfd929fbd2fe56f7c913078cb2c1e79f35051a`. No remote tags were listed.
- GitHub compare/API read-only evidence: `5a50a40` has parent/merge base `90cfd92`, is one commit ahead/zero behind, and changes 10 files with 244 additions/16 deletions.
- Submodules are declared but not initialized: `docs/wiki` and `src/handbook/data/assets`.
- README says `LunaGC-7.0.0 WIP` and requires client `REL7.0.0`. User confirmed 7.0. `build.gradle` version, startup log, and existing root artifact remain `6.6.0`.

### Build/runtime baseline

- Gradle wrapper distribution: 8.5. Java source/target: 17. Installed `java`/`javac`: Oracle 17.0.9.
- Single Gradle root project named `Grasscutter`; main class `emu.grasscutter.Grasscutter`.
- `jar` shades runtime dependencies, sets `Main-Class`, uses base name `LunaGC`, and writes to project root. Root JARs are ignored.
- Existing ignored root JAR: `LunaGC-6.6.0.jar`, 79,838,788 bytes, timestamp 2026-08-23 22:25:55.
- Tracked JARs: Gradle wrapper plus `lib/bcrypt-0.8.0.jar`, `lib/bytes-1.3.0.jar`, and `lib/kcp-1.5.1.jar`. Tracked patch: `patch/Astrolabe.dll`.
- Tracked generated protocol Java: 1,908 files. Raw `proto/` contains only three checked-in `.proto` files. Tests found: `FurinaGadgetPolicyTest` and `GrasscutterTest`.
- Areas counted: `src` 3,682 files; `proto` 3; `data` 4; `patch` 1; `lib` 3; ignored `build` 11,621 files. Runtime `resources/` is absent and ignored, so avatar tag/config/ability data needed for a complete Hexerei inventory is not locally present in this checkout.

### Reference-project lessons

EDS2 applicable patterns: immutable baselines, fail-closed evidence, explicit scope/rollback/verification before risky changes, one active handoff pointer, artifact hashes, unique build/UAT outputs, focused end-to-end acceptance. Not applicable: workbook/VBA/Excel/COM/template rules and domain-specific source precedence.

DRRS applicable patterns: summary-first context order, graph before broad source reads, source verification, workflow ownership maps, high-risk side-effect gates, one active handoff, exact deployment artifacts, focused UAT, and durable failure/fallback notes. Not applicable: Power Apps/SharePoint/flow/package/schema/threshold rules and organization-specific token/task automation.

### Preliminary architecture facts (source-verified, incomplete)

- Startup: `Grasscutter.main` loads keys/arguments, initializes MongoDB via `DatabaseManager`, constructs `GameServer`/`HttpServer`, calls `ResourceLoader.loadAll`, then starts servers. Static initialization loads and may update/save `config.json`; do not start the server during groundwork.
- Resource path: `FileUtils` resolves configured `./resources/` (directory or zip) plus built-in default `data/`. `ResourceLoader.loadAll` loads avatar/gadget configs, ability embryos, talents, open configs, ability modifiers, Excel resources, quests/scripts, caches, and mappings into `GameData`.
- Avatar state: Morphia entity `Avatar` stores `avatarId`, `skillDepotId`, `skillLevelMap`, `talentIdList`, and other state. `AvatarStorage.loadFromDatabase` loads avatars with `DatabaseHelper`, reattaches `AvatarData`/`AvatarSkillDepotData`, sets owner, recalculates constellations, and reconstructs maps.
- New avatar construction selects its skill depot, adds skills at level 1, adds promote-gated inherent proud skills, recalculates stats, and saves when added through `AvatarStorage.addAvatar`.
- Talent/constellation representation: `skillLevelMap` is talent level state; `talentIdList` is constellation IDs; `proudSkillList` stores passives; `proudSkillBonusMap` is transient and recalculated from constellations. Avatar protocol builders send these through talent IDs, skill levels, inherent proud skills, core proud skill level, and proud-skill extra levels.
- Ability runtime: data-driven abilities/modifiers are loaded from external `BinOutput`/`ExcelBinOutput`; `AbilityManager` processes client invoke entries and dispatches actions/mixins. `PredicateEvaluator` handles supported predicate types. Packet handlers forward invoke traffic through `InvokeHandler`.
- Hexenzirkel source seams currently found: `PredicateEvaluator.hasHexenzirkelTag`; tagged avatar discovery in `PacketPlayerEnterSceneInfoNotify`; team count and `SGV_HexenzirkelLevel` updates in `TeamManager`; initial scene count in `HandlerSceneInitFinishReq`; `PacketTeamHexenzirkelChangeNotify`/opcode 1950; normal-attack ratio handling in `HandlerCombatInvocationsNotify`.
- The current tag membership comes from runtime `AvatarData.avatarTags` containing `AVATAR_TAG_HEXENZIRKEL`. Because the external `resources/` tree is absent, an evidence-backed character-name/ID inventory is not complete yet.

### Furina remote-history facts

Commit `5a50a40` adds `FurinaGadgetPolicy` and `FurinaGadgetPolicyTest` and changes `AbilityManager`, `ActionCreateGadget`, `EntityClientGadget`, four ability-invoke handlers, and `HandlerEvtCreateGadgetNotify`.

The patch centralizes gadget IDs `41089010`–`41089014`, enforces a 30-second lifecycle, suppresses duplicate server creation for client-managed Furina skill gadgets, prevents echoing Furina gadget invokes to the owning client, removes/replaces mutually exclusive owned summons, and destroys expired client gadgets. The unit test covers ID bounds, Ousia/Pneuma replacement, controller uniqueness, shared expiry, echo suppression, and server-creation suppression. This is character-specific lifecycle policy integrated through generic ability/gadget packet seams; it is not evidence that Hexerei fixes should use the same pattern.

### CodeGraph research

- Official-source research is complete in `docs/architecture/codegraph.md`.
- Latest pinned decision: v1.5.0, version-pinned `npx --yes @colbymchenry/codegraph@1.5.0` with session-scoped telemetry disable.
- At the first transfer, the worker observed Node 24.15.0 and npm/npx 11.12.1, no `codegraph` on PATH, and no `.codegraph/` directory.
- At that first-transfer baseline, no installation, initialization, index, query, `.gitignore` change, global config, or runtime action had occurred; the continuation results are recorded below.

### Historical context-meter evidence

- Added `.agents/scripts/active_context.py`, adapted from the general read-only reference logic.
- It dynamically discovers `state_*.sqlite`, validates the `threads` schema, resolves this exact task through `CODEX_THREAD_ID`, reads its rollout, handles compaction/zero samples, and gates on raw active input without double-counting cached tokens.
- `python .agents/scripts/active_context.py --self-test` passed historically.
- The prior live result before transfer was `active_input_tokens=162586`, gate `route`, footer `Context: ~163k / 150k project limit`.
- The helper remains a best-effort diagnostic over internal Codex telemetry. Future tasks must run it before every user-facing response and, for non-trivial work, at turn start and immediately before deciding whether to begin each broad or output-heavy phase. Every user-facing response must end with the current helper footer (or the documented `unknown` footer on failure). Fixed thresholds trigger a proportional route decision rather than automatic transfer; the footer itself never selects a route.

### Continuation task: CodeGraph initialization and partial architecture trace

- Continuation task `01a0315b-73fd-72f0-9ec1-7973c9db034e` re-read the required guidance and confirmed exactly the six documented untracked artifacts with no overlap.
- With session-only `DO_NOT_TRACK=1`, pinned `npx --yes @colbymchenry/codegraph@1.5.0 version` returned `1.5.0`; no global `codegraph` command or pre-existing `.codegraph/` directory was present.
- Root `.gitignore` now has one reviewable tracked-file edit: a `# Local CodeGraph index` comment and `/.codegraph/` entry after `.gradle`.
- Pinned `init` completed without unexpected prompts or hooks. It indexed 3,570 files into 251,229 nodes and 519,546 edges.
- Health status is valid: initialized `true`; built/current version `1.5.0`; extraction version `24`; exact project and index paths; 972.21 MB database (`1,019,441,152` bytes); backend `node-sqlite`; journal `wal`; state `complete`; pending refs `0`; re-index not recommended; pending added/modified/removed all `0`; worktree mismatch `null`.
- Indexed languages are Java (3,530 files), TSX (25), TypeScript (8), JavaScript (2), YAML (2), properties (1), Python (1), and XML (1). `.codegraph/` is ignored and absent from Git status.
- All 12 representative `explore` prompts in `docs/architecture/codegraph.md` were run. Exact `query` calls also resolved `Grasscutter.main`, `ResourceLoader.loadAll`, `GameServerPacketHandler`, `AvatarStorage.loadFromDatabase`, `Avatar.recalcConstellations`, `AbilityManager`, `AbilityModifierController`, `PredicateEvaluator`, `FurinaGadgetPolicy`, `PacketTeamHexenzirkelChangeNotify`, and `HandlerCombatInvocationsNotify`.
- Material graph discrepancies were confirmed directly against source: the semantic startup/avatar queries over-weighted generic `load` methods; packet and constellation prompts over-weighted generated protocol classes; `callees` for `Grasscutter.main` was empty even though source calls database/resource/server setup; `GameServerPacketHandler.handle` callees omitted its event and dynamic handler dispatch; impact for `Avatar.recalcConstellations` included unrelated `Player`/`HomeNPCItem` paths. Continue using the graph only to find candidates.

### Continuation task: source-verified workflow facts

- Startup/resource flow: the `Grasscutter` static initializer loads and may update `config.json`; `main` loads keys/arguments, initializes MongoDB, creates servers/plugins, calls `ResourceLoader.loadAll`, then starts servers. `ResourceLoader.loadAll` loads config, embryos, talents, open configs, modifiers, reflected Excel resources, quests/scripts, caches, mappings, and entity controllers. `FileUtils` freezes paths from `ConfigContainer.folderStructure`; default resources are `./resources/` and scripts are `resources:Scripts/`.
- Local resource baseline: root `config.json` is absent, root `resources/` is absent, and the checked-in default resource path is `./resources/`. No server or class initializer was run.
- Packet flow: `GameServer` constructs `GameServerPacketHandler(PacketHandler.class)`; reflection finds `@Opcodes` handlers; `GameSession.handleReceive` decrypts/parses framed packets and calls `GameServerPacketHandler.handle`; the handler enforces session state, fires `ReceivePacketEvent`, then dynamically invokes the registered handler. `GameSession.send` builds/encrypts after `SendPacketEvent` and writes to the KCP tunnel. `HandlerUnionCmdNotify` recursively dispatches embedded packets and flushes combat/ability invoke queues.
- Avatar/persistence flow: persisted fields include `skillLevelMap` and `talentIdList`; transient state includes `AvatarData`, `AvatarSkillDepotData`, `proudSkillBonusMap`, and skill-charge bonuses. Construction selects the depot, seeds skills at level 1, adds promotion-gated proud skills, recalculates stats, and later saves through `DatabaseHelper.saveAvatar`. Database load reattaches resource objects and ownership, recalculates constellation bonuses, rebuilds maps, and fixes/saves traveler depots. `Avatar.toProto` sends talent IDs, skill levels, inherent proud skills, core proud-skill level, extra levels, and charge counts.
- Talent/constellation flow: `HandlerUnlockAvatarTalentReq` parses GUID/talent ID and calls `Avatar.unlockConstellation(int)`. Current source looks up any global `AvatarTalentData`, optionally charges one `mainCostItemId`, adds the ID, sends unlock/response packets, applies the referenced open config, recalculates stats, and saves. It does not directly validate depot membership, predecessor/order, duplicate status, or `mainCostItemCount`; this is a candidate validation seam, not yet a confirmed GC 7.0 bug or approved bypass.
- Ability flow: four client ability-notify handlers call `AbilityManager.onAbilityInvoke`, then queue forwarding through `InvokeHandler`; union commands flush the queues. Action/mixin handlers are registered reflectively. Local-ID invokes resolve instanced ability/modifier data and asynchronously execute data-driven actions. Modifier changes resolve parent ability by string/hash or instance index, add/remove transient controllers, and may run `onAdded` orchestration.
- Condition flow: `ActionPredicated` and `ActionApplyModifier` call `PredicateEvaluator`. Explicitly supported types are obfuscated `BJJDEAIEIGP` (implemented as Hexenzirkel tag membership), `ByUnlockTalentParam`, `ByHasModifier`, `ByTargetGlobalValue`, and `ByTargetHPRatio`; unknown types currently return `true`. This fail-open behavior is source fact and a high-risk candidate seam, but external GC 7.0 predicates are missing, so correctness is unresolved.
- Hexenzirkel membership is read from `AvatarData.tags` containing `AVATAR_TAG_HEXENZIRKEL` (the earlier handoff wording `avatarTags` was imprecise). `PacketPlayerEnterSceneInfoNotify` builds a static cached avatar-ID set from loaded `GameData`, sends `SGV_HexenzirkelLevel` in team ability state, and `TeamManager.updateTeamProperties` sends both the SGV and team-change packet after team changes. `HandlerSceneInitFinishReq` separately sends the team-change packet.
- `PacketTeamHexenzirkelChangeNotify` is hand-encoded at opcode `1950`: outer field 1 contains an inner message whose field 6 is the level. Constant `F_TYPE=9` is unused, and no generated `TeamHexenzirkel` class exists. Field correctness cannot be established from this checkout and requires an approved GC 7.0 protocol/resource source or later runtime capture.
- Combat handling has a Hexenzirkel-specific zero-damage fallback for client gadgets: it reads `Hexenzirkel_NormalAttack_Ratio` and a `NormalAttack_*_Damage_Percentage`, may retarget to the nearest living monster, and computes damage from the current avatar's attack/anemo bonus/crit before forwarding. This is source-verified but untested and not yet tied to named characters because resources are absent.
- Furina remains a bounded reference: policy IDs `41089010`-`41089014` control server-creation suppression, owner echo, replacement, and shared 30-second expiry across `AbilityManager`, `ActionCreateGadget`, `HandlerEvtCreateGadgetNotify`, and `EntityClientGadget`. `FurinaGadgetPolicyTest` covers only pure policy rules; no packet/entity/runtime integration was run.
- Build source confirms `test` uses JUnit 5, while `processResources` depends on `generateProto` and generated output is rooted at tracked `src/generated/`. The fat `jar` still writes to repository root. At that earlier architecture phase, no Gradle task had been run; the later fallback phase used only the isolated focused test command recorded below.

### Continuity-policy correction

- Policy-correction task `01a03277-5bb0-7380-877a-cc82e037b2eb` aligned `AGENTS.md`, `docs/agents/README.md`, `docs/agents/current_task.md`, and this handoff with Decisions 9–13.
- The operative rule now keeps explicit approval as the default, recognizes a concrete goal plus terminal instruction as standing authorization for only the minimum necessary safe same-project continuation, keeps route selection proportional rather than automatic, and preserves all existing scope, permission, risk, runtime, deployment, and consequential-choice boundaries.
- This task made documentation-only changes and stopped before the next GC architecture phase.

### Scoped codebase-map packet

- Rechecked pinned CodeGraph v1.5.0 JSON health: 3,570 files, 251,229 nodes, 519,546 edges, WAL, complete, zero pending changes/references, no worktree mismatch, and no re-index recommendation.
- Created `docs/architecture/codebase-map.md` from direct source inspection. It covers startup/configuration, resource loading, packet registration/dispatch/response, union/invoke forwarding, avatar persistence/reconstruction/protocol state, talent/constellation paths, ability/action/modifier dispatch, predicate behavior, and current Hexenzirkel coordination/combat seams.
- Recorded the direct talent-ID validation gap and fail-open predicate behavior only as candidate risks; no GC 7.0 mechanic or defect was inferred without runtime resource/protocol evidence.
- Strengthened continuity guidance so every user-facing response includes the current context footer and every broad-phase decision uses a fresh context measurement.
- No Java, generated protocol, Gradle, CodeGraph index, JAR, launcher, runtime, config, resource, player-data, or gameplay change occurred.

### Official Hexerei membership research

- Added `docs/research/gc70-hexerei-official-membership.md` with first-party HoYoLAB evidence and the official 12-character chronology through Version 7.0.
- Confirmed no additional Hexerei classification in the intervening Luna IV, Luna VI, Luna VIII, or Version 7.0 update notices.
- Recorded that Luna VIII's Witch's Revelation characters are not Hexerei on the reviewed official evidence.
- Read-only upstream checks resolved `girluh/LunaGC-Resources` `main` to `1744949c800cc380cd16b5d1f092a94f06a820d2`, but the local archive lacks Git metadata and is not proven byte-for-byte against that commit.
- The README-linked `kitkat-multiverse/genshin-protocol` GitLab project redirects to sign-in; its public API and `git ls-remote` access return not-found/inaccessible results. Packet fields remain unresolved.
- No server, client, Gradle, JAR, launcher, player-data, resource extraction, or gameplay action occurred.
- User then placed the Witch's Revelation/Lucid Revelation characters after Hexerei in the character-improvement program and required command-visible per-player unlock state for both systems.
- User confirmed the screenshot's `hk4e-protos` credit refers to the purged/inaccessible repository rather than an alternate live source.
- Fresh local project task `01a03300-b88d-79b2-9ab7-f9e0c43cd0b9` was created for the approved read-only archive reconciliation. An immediate wait snapshot confirmed it active with its first command completed; sole substantive ownership transferred there.

### Hexerei resource-roster reconciliation

- Added `docs/research/gc70-hexerei-resource-roster.md` from direct, read-only inspection of the approved archive. The computed SHA-256 matched `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF` before parsing.
- `AvatarExcelConfigData.tags` produced exactly 12 `AVATAR_TAG_HEXENZIRKEL` records. English TextMap, skill-depot, and energy-skill joins resolved an exact official 12-for-12 match with IDs, config names, elements, depot IDs, archive paths, and JSON Pointers. There are no resource-only additions, official-only omissions, duplicate/tagged Traveler/depot variants, or unresolved English display-name mappings.
- Every matching depot declares one `SPECIAL_PROUD_SKILL_OPEN_CONDITION_TYPE_QUEST_FINISH` special proud-skill group. The 12 opaque condition parameters and groups are recorded exactly, but their parameter namespace is unresolved.
- Direct source inspection found that `AvatarSkillDepotData` retains only each special entry's `proudSkillGroupId`, discards its condition type/parameters, and that `Avatar` rebuild paths add all such groups without player quest checks. The roster document records the exact resource-model, quest-persistence, avatar-rebuild, team/predicate, packet, and command seams for a later activation-state design.
- Static resource eligibility is now explicitly separate from per-player Witch's Homework/quest activation. The separate Witch's Revelation/Lucid Revelations roster remains deferred and was not merged.
- No extraction, Java, generated protocol, Gradle, CodeGraph index, JAR, launcher, runtime resource/config, database/player-data, server/client, game-installation, packet-capture, or gameplay action occurred.

### Hexerei config/predicate/status matrix

- Added `docs/research/gc70-hexerei-config-predicate-status-matrix.md` from direct archive and checked-in source inspection.
- Resolved every opaque special-condition value to the exact character-specific `ChapterExcelConfigData.endQuestId`, with main quest, begin quest, chapter ID, proud-skill group/ID, open config, and ability joins recorded for all 12 characters.
- Inventoried character-specific open-config actions, selected ability blocks, `SGV_HexenzirkelLevel` uses, `BJJDEAIEIGP` sites, Hex-specific unlock parameters, and Hex-named modifier namespaces.
- Confirmed the archive has all 12 main-quest metadata rows but lacks all 12 terminal `QuestExcelConfigData` rows and all 12 corresponding quest BinOutput files. The per-player status contract therefore fails closed as `UNAVAILABLE_RESOURCE` and never treats an unconditionally materialized proud skill as proof of completion.
- Recorded source mismatches for later review: static tag-only team counting, unconditional special-proud reconstruction, `BJJDEAIEIGP` ignoring its resource target, direct open-config string comparison for `ByUnlockTalentParam`, and fail-open unsupported predicates.
- Defined a read-only, online-only status/command contract with explicit readiness and consistency states. Witch's Revelation/Lucid Revelations remains separate. No implementation or runtime action occurred.

### Public Hexerei quest-source research

- Added `docs/research/gc70-hexerei-public-quest-sources.md` from explicit Project Amber, GitHub/GitLab resource, and rendered-database web research.
- Project Amber's 12 chapter endpoints each expose the expected main and terminal quest IDs, titles, objectives, and dialogue/task presentation data. The API omits the raw condition, exec, order, `finishParent`, and rewind fields LunaGC needs.
- DimbreathBot `AnimeGameData` commit `26df1dfbdf05a82bbb1d97506859f3e1c40718d8` (`CNRELWin7.0.0_R47482070_S47579390_D47579390`) contains all 12 main metadata rows and raw quest payload files, but its quest Excel table contains none of the terminal IDs and its payload property names are obfuscated.
- Public `girluh/LunaGC-Resources` commit `1744949c800cc380cd16b5d1f092a94f06a820d2` has chapter, quest, and main-quest Excel files that match the approved archive entries byte-for-byte and lacks all 12 quest payload paths. A separately checked mirror uses the same relevant blobs and is not an independent complete baseline.
- Downloaded only the exact public evidence files to `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Web-Research\2026-08-24`, outside the repository and runtime resources; the companion document records every SHA-256.
- The result remains fail-closed `UNAVAILABLE_RESOURCE`. The exact next resource decision is recorded below. Witch's Revelation/Lucid Revelations was kept separate.
- Revised `AGENTS.md` and `docs/agents/README.md` so useful bounded same-task workers no longer require repeated approval; durable agents and user-visible fresh tasks remain approval-controlled.

### Hexerei deobfuscation and manual fallback implementation

- Added `docs/research/gc70-hexerei-quest-deobfuscation.md`. Version-matched field alignment recovered a useful payload subset but proved that complete terminal quest rows/lifecycles cannot be recovered without inventing missing Excel-only values. No candidate resource was written.
- Added the pre-implementation contract `docs/research/gc70-hexerei-manual-activation-design.md`, including exact IDs, persistence/migration, reconnect, command, packet/team/predicate effects, consistency states, tests, no-runtime boundary, UAT, and rollback.
- Added `HexereiManager`, embedded on `Player`. It owns the exact 12-entry GC 7.0 mapping and persists only schema version, avatar/end-quest IDs, `MANUAL_COMMAND` provenance string, actor UID (`0` for console), and activation epoch. Missing manager fields migrate to empty in memory at `Player.@PostLoad` and reattach without saving. Validation checks schema, map key/avatar ID, mapped end quest, and provenance; invalid records fail closed and are preserved until explicit reset.
- Added `Avatar.getEffectiveProudSkillList()`. Raw proud state is still reconstructed/persisted normally. The effective view filters only the mapped special Hex proud ID when activation is not effective; unrelated and non-Hex proud skills remain. Stat/open-config application, avatar/entity/depot/proud protocol builders, enter-scene proud ability variables, `ByUnlockTalentParam`, and stamina proud-skill consumers now use the view.
- `BJJDEAIEIGP` now gates on effective activation while retaining its prior caster/owner/resolved entity selection. The unresolved resource `Self`/`Target` interpretation and unknown-predicate behavior were not changed.
- `PacketPlayerEnterSceneInfoNotify`, `TeamManager`, and `HandlerSceneInitFinishReq` now use the manager's centralized effective active-team count. Existing Hex SGV/team packets remain unchanged at the wire-field level. `TeamManager.sendHexereiTeamUpdate` lets activate/reset refresh only the existing Hex team effects when the affected avatar is active.
- Added dedicated online `hexerei status [all|<avatarId>]`, `activate <avatarId>`, and `reset <avatarId|all>` with normal targeted permissions. There is no activate-all. Status always reports `authoritative_completion=UNAVAILABLE_RESOURCE`; mutation saves only `Player`, recalculates/sends affected derived avatar state, never saves `Avatar`, and never invokes `QuestManager`/`GameQuest`.
- Added focused `HexereiManagerTest`. The final permitted Gradle run completed full `compileJava`, `compileTestJava`, and 13 tests with zero failures, errors, or skips (`BUILD SUCCESSFUL in 27s`; 7 actionable tasks, 3 executed, 4 up-to-date).
- No runtime resources, root JAR, package/build task, server/client, MongoDB/player data, Cultivation/game installation, deployment, gameplay UAT, or CodeGraph index was touched.

### Furina gameplay UAT and merge

- Prepared a uniquely named candidate `LunaGC-Furina-5a50a40-test.jar` in the complete runtime directory without overwriting the existing runtime JAR. Its SHA-256 is `EEDA98211A45466D030665385BB0E7B4F337CE0B36D533A96AFD9885E5BBAD59`.
- Preserved Cultivation configuration, runtime configuration, previous runtime JAR, MongoDB service configuration, hashes, and rollback instructions under `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\2026-08-24-furina-uat-prep-5a50a40`.
- The user launched and tested the candidate in game and reported that the scoped Furina summon-limit, replacement, duplicate-suppression, and shared-lifetime fixes worked. This is user-reported gameplay evidence; no automated capture or broader Furina-kit claim is inferred.
- Recorded the result on GitHub PR #2, marked it ready, and squash-merged it into `7.0.0` as `4b43902e337c98c605553b29511d1c498de96864`.
- Hexerei PR #1 remains a separate draft at `0a4be82`; it must be refreshed onto the merged `7.0.0` base and reverified before its own runtime UAT.

### Hexerei merged-base refresh, matrix, and candidate

- Fetched remote `7.0.0` explicitly as `4b43902e337c98c605553b29511d1c498de96864` and refreshed PR #1 in a clean temporary worktree. Rebased source commit `3d5a078` has the same stable patch ID as prior commit `0a4be82`; the Furina and Hexerei changed-file sets did not overlap.
- Added `docs/research/gc70-hexerei-character-verification-matrix.md` and linked it from the activation design. It defines the exact shared state, C0-C6, per-character predicate/modifier, one/two-member SGV, reconnect/reset, observable-oracle, evidence, risk-ranking, and rollback checks. Durin is the first tracer; no mechanics were inferred beyond the verified resource contributions.
- Re-ran `gradlew.bat test --tests emu.grasscutter.game.player.HexereiManagerTest -x generateProto -x processResources --no-daemon` on the merged base: `BUILD SUCCESSFUL`; 13 tests, 0 failures/errors/skips.
- Pushed only the Hexerei patch and verification documents. PR #1 remains draft at `048a8b8c8f0dd165b9bcb84873e8a1a649492350`, based directly on `4b43902`; GitHub reports 16 Hexerei-only files.
- Built candidate commit `df3c927fe1ba5a8c621e0133517200fbf25a4f50` through a temporary Gradle init override into `build/safe-hexerei-candidate`, never the root. Copied unique candidate `LunaGC-Hexerei-df3c927-durin-uat.jar` to the complete runtime directory with SHA-256 `F6F92DBEB37300BBA4471E8E3BED2F9472CD6E8E9A78C0EEC2B657BE2846C688`.
- Preserved Cultivation/runtime configuration, the prior selected runtime JAR, MongoDB service configuration, hashes, README, and structured rollback manifest under `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\2026-08-24-hexerei-durin-uat-df3c927`.
- No Cultivation/server/client/game process, launcher selection, runtime resource, MongoDB/player data, or existing JAR/config was changed. Runtime evidence remains pending and user-controlled.
- User-controlled UAT subsequently verified Durin `10000123` and Venti `10000022` as individually `VALID`, `effective_active=true`, `effective_proud_present=true`, and `ACTIVE_CONSISTENT`; repeated Durin activation was idempotent (`ALREADY_ACTIVE`). Both were in the same active party.
- First red runtime signal: after Venti's burst, the user reported that Venti's normal attacks were not Anemo-infused. This is user-reported/captured evidence. Current command output does not expose the numeric Hex team count or `SGV_HexenzirkelLevel`, so the evidence cannot yet distinguish team-count/packet delivery failure from Venti predicate/ability execution failure. PR #1 remains draft.
- The next diagnostic gate is a read-only team/SGV command or equivalent narrowly tagged instrumentation plus a red/green Venti burst-to-normal-attack loop. Do not guess a gameplay fix or treat the client quest-lock banner as the team-level oracle.

## Changed files/artifacts

### Venti SGV diagnostic continuation

- Commit `0978865ec4c70f6f8689f4ccd28cd30138162f03` adds read-only `hexerei team`, which reports ordered party membership, mapping/eligibility/effective state, depots/consistency, calculated count, exact float SGV send value, server-cached predicate value, and Venti's effective ability/runtime-value boundary. It performs no save or SGV/player-data write.
- Temporary Venti instrumentation was centralized in one helper and limited to team SGV sync, two verified runtime flags, and the primary normal-attack predicate/base-vs-Hex bullet branch. It was removed after cause proof and is absent from the green-UAT candidate.
- Forced focused verification passed `HexereiManagerTest` 15/15 with zero failures/errors/skips. PR #1 remains draft, based on `4b43902`, with head `0978865`.
- Unique diagnostic candidate: `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-7.0.0\LunaGC-7.0.0\LunaGC-Hexerei-0978865-venti-diagnostic.jar`; SHA-256 `5682F87859DD55E4CD287260DA9EE6FC3002116B0335A53D0F92C5B4C09A6063`; size `79,867,901` bytes.
- New rollback bundle: `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\2026-08-24-hexerei-venti-diagnostic-0978865`. It carries verified copies of the prior selected JAR and Cultivation/runtime/MongoDB configurations plus a new manifest/README.
- No Cultivation/server/client/game process, runtime resource, configuration, existing JAR, CodeGraph index, MongoDB/player data, or launcher selection was changed. No gameplay fix was attempted because the runtime boundary has not yet been captured.
- Runtime boundary captured. `hexerei team` showed active IDs `10000022,10000123`, calculated count `2`, send value `2.0`, and no server-cached SGV. Both members were mapped/eligible/effectively active/depot-consistent. Venti's Hex extra embryo and instanced ability were absent and `_ABILITY_Venti_Is_Hexenzirkel` was unset. The tagged burst value transitioned `0.0 -> 1.0 -> 0.0`; no tagged normal-attack predicate/branch event appeared.
- User-corrected action order: Durin E -> normal attack -> Durin burst -> Venti burst -> Venti E -> normal attacks.
- Read-only `latest.log` and source inspection proved two independent seams: missing optional `BinOutput/Talent/RelicTalents/` makes `ResourceLoader.loadOpenConfig()` return and discard already loaded AvatarTalent entries; Hex team SGV packet paths calculate/send the correct value but do not cache it in the team entity map used by server predicates.
- The red boundary added `ResourceLoaderOpenConfigTest.keepsLoadedAvatarTalentsWhenOptionalDirectoryIsMissing`, `HexereiManagerTest.cachesExactEffectiveTeamCountForServerPredicates`, and a behavior-preserving loader extraction. Repair commit `16b5949e387ea8b3f08f748973c707fbeb289268` makes both green, removes the Venti-only command boundary and all temporary probes/helper/tests, and retains generic read-only `hexerei team`.
- Forced focused verification passed 16 selected tests with zero failures/errors/skips. The commit is pushed to PR #1, which remains open and draft on base `7.0.0` at `4b43902`.
- Green-UAT candidate: `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-7.0.0\LunaGC-7.0.0\LunaGC-Hexerei-16b5949-venti-green-uat.jar`; SHA-256 `6FEE2A9FAE946588049424683F4C56D5DFF000494F5C13B542297A050D530784`; size `79,862,545` bytes. Append-only rollback/configuration provenance is under `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\2026-08-26-hexerei-venti-green-uat-16b5949`.
- The safe init override built away from the repository root; existing diagnostic candidate/archive paths and the root JAR were unchanged. No Cultivation/server/client/game process, launcher selection, runtime resource/configuration, CodeGraph index, MongoDB/player data, or existing artifact was changed.

The groundwork documents/helper remain untracked project-local additions:

- `.agents/scripts/active_context.py`
- `AGENTS.md`
- `docs/agents/README.md`
- `docs/agents/context-quality-gates-review.md`
- `docs/agents/current_task.md`
- `docs/agents/handoffs/2026-08-24-gc70-groundwork-baseline.md`
- `docs/architecture/codebase-map.md`
- `docs/architecture/codegraph.md`
- `docs/research/gc70-hexerei-official-membership.md`
- `docs/research/gc70-hexerei-resource-roster.md`
- `docs/research/gc70-hexerei-config-predicate-status-matrix.md`
- `docs/research/gc70-hexerei-public-quest-sources.md`
- `docs/research/gc70-hexerei-quest-deobfuscation.md`
- `docs/research/gc70-hexerei-manual-activation-design.md`

New source/test files:

- `src/main/java/emu/grasscutter/game/player/HexereiManager.java`
- `src/main/java/emu/grasscutter/command/commands/HexereiCommand.java`
- `src/test/java/emu/grasscutter/game/player/HexereiManagerTest.java`

One existing tracked file is now modified:

- `.gitignore` — adds `/.codegraph/` under a dedicated local-index comment.

Existing tracked Java files modified by the fallback:

- `src/main/java/emu/grasscutter/game/player/Player.java`
- `src/main/java/emu/grasscutter/game/avatar/Avatar.java`
- `src/main/java/emu/grasscutter/game/entity/EntityAvatar.java`
- `src/main/java/emu/grasscutter/game/ability/PredicateEvaluator.java`
- `src/main/java/emu/grasscutter/game/managers/stamina/StaminaManager.java`
- `src/main/java/emu/grasscutter/game/player/TeamManager.java`
- `src/main/java/emu/grasscutter/server/packet/recv/HandlerSceneInitFinishReq.java`
- `src/main/java/emu/grasscutter/server/packet/send/PacketAvatarSkillDepotChangeNotify.java`
- `src/main/java/emu/grasscutter/server/packet/send/PacketPlayerEnterSceneInfoNotify.java`
- `src/main/java/emu/grasscutter/server/packet/send/PacketProudSkillChangeNotify.java`

Generated local-only artifact:

- `.codegraph/` — healthy ignored v1.5.0 index; not visible in Git status.

No gameplay, runtime, launcher, active-JAR, config, resource, or player-data file was changed.

## Verification performed

- Initial and repeated Git status checks; no pre-existing dirty/untracked work.
- Path existence and focused SMB access checks.
- Read-only Git branch/remote/tag/ref/reflog/submodule/artifact inspection.
- Read-only GitHub API compare for exact Furina commit parent/file patch evidence.
- Java/Javac version checks and static Gradle/build task inspection.
- Source/file counts, ignore checks, tracked/generated artifact inventory.
- Read relevant governance/current-task/latest-handoff/context/acceptance/workflow material from both reference projects read-only.
- Re-read the DRRS completion/pause overrides and Mitsumori fresh-task/close/pause routing read-only; verified that LunaGC preserves the applicable proportional exceptions without importing automatic task creation, Power Platform, or Excel rules. The context-footer requirement was added later by explicit user instruction.
- Read key startup/resource/avatar/persistence/ability/Hexenzirkel source sections directly.
- CodeGraph guide link/fence structure checked by worker; official sources cited.
- Context meter self-test and live read-only telemetry query passed.
- Current policy-helper verification passed with `110000 / 130000 / 150000`; exact boundaries classified as `normal`, `warning`, `warning`, `prepare`, `prepare`, and `route` at `109999`, `110000`, `129999`, `130000`, `149999`, and `150000`. The focused stale search found the superseded `135000 / 140000` policy only in explicitly historical/supersession text.
- Pinned CodeGraph v1.5.0 preflight, initialization, human status, JSON status, and Git-ignore behavior passed.
- All 12 representative exploration prompts plus exact symbol queries/callers/callees/impact checks were run; discrepancies were checked against source.
- Directly inspected startup/resource, packet dispatch, avatar construction/load/persistence/protocol, talent/constellation, ability invoke/modifier/predicate, Hexenzirkel, Furina, and Gradle packaging source sections.
- Continuation context measurement: `active_input_tokens=203161`, gate `route`, footer `Context: ~203k / 150k project limit`; broad work stopped for transfer.
- Re-read all four changed continuity documents after alignment; searched them for approval/task-creation wording; confirmed the default approval rule, terminal-authorization exception, proportional non-automatic routing, and unchanged authority/risk boundaries are stated together.
- Rechecked pinned CodeGraph v1.5.0 JSON health and Git status before the codebase-map phase.
- Directly re-read the scoped startup/resource/packet/avatar/talent/constellation/ability/predicate/Hexenzirkel source seams and recorded file/line evidence in `docs/architecture/codebase-map.md`.
- Re-read and searched the completed map for unsupported certainty, stale pending-map wording, and the required source-fact/candidate-risk/unresolved distinctions.
- The map packet reached the `150,000` route-decision gate only after source inspection and documentation mutations were complete; because only bounded deterministic verification remained, it closed locally without creating another continuation.
- Verified the approved archive SHA-256 before parsing; decoded the relevant ZIP entries directly; evaluated all 165 avatar records; resolved all 12 tag/name/depot/element joins; and programmatically confirmed 12 exact official matches with empty resource-only and official-only sets.
- Directly inspected the resource-condition model, avatar proud-skill rebuilds, persisted quest/avatar fields, tag-only predicate/team-count paths, and existing quest/talent/debug command fragments. No runtime verification was attempted.
- Joined all 12 condition parameters to unique Hexenzirkel chapter end quests; verified the 12 main-quest metadata rows, 12 missing terminal quest rows, and 12 missing quest BinOutput files.
- Parsed all 12 proud-skill/open-config action lists and inventoried the selected character ability blocks, SGV uses, obfuscated Hex-tag predicates, Hex unlock parameters, and Hex-named modifier counts.
- Directly checked `AvatarSkillDepotData`, `Avatar`, `PredicateEvaluator`, `PacketPlayerEnterSceneInfoNotify`, `TeamManager`, `QuestManager`, `GameQuest`, command targeting, and asynchronous player-load boundaries for the status contract.
- Queried all 12 Project Amber chapter endpoints and searched all 12 main and terminal IDs; classified the response schema as processed presentation data rather than raw quest resources.
- Pinned DimbreathBot `AnimeGameData` to immutable GC 7.0 commit `26df1dfbdf05a82bbb1d97506859f3e1c40718d8`; verified 12/12 main metadata rows, 12/12 raw quest payloads, 0/12 terminal quest Excel rows, and matching chapter mappings.
- Recomputed hashes for downloaded evidence. The public LunaGC chapter, quest, and main-quest Excel files exactly matched the corresponding entries inside the approved hash-baseline archive.
- Confirmed all downloaded web artifacts are outside both the repository and runtime resource tree.
- Deterministically aligned five immutable version-matched raw/named quest pairs, applied the GC 7.0 map to all 12 payloads, and proved the missing Excel-only lifecycle fields prevent authoritative recovery.
- Inspected the Gradle graph before execution: the focused test command with `generateProto` and `processResources` excluded does not run `jar`, `build`, packaging, publishing, `run`, runtime resources, or root-JAR output.
- Ran `gradlew.bat test --tests emu.grasscutter.game.player.HexereiManagerTest -x generateProto -x processResources --no-daemon`; final result was `BUILD SUCCESSFUL in 27s`, 13 tests, 0 failures/errors/skips. Full main-source compilation passed with only pre-existing general deprecation/unchecked notes.
- Rechecked all remaining raw `getProudSkillList()` consumers: only diagnostic/debug and raw reconstruction/materialization sites remain; required runtime/protocol consumers use the effective view. Rechecked all three Hex team-count sites and `BJJDEAIEIGP` routing against current source.

## Not verified / not performed

- Architecture outside the completed scoped map, including full packaging/deployment and all character-specific workflows.
- Authoritative values for the omitted terminal quest lifecycle fields, complete loadable `QuestData` rows, player-specific authoritative Hexerei completion, or runtime behavior status.
- No broad Gradle task discovery, resource generation, packaging, or deployment command was run. Only the focused test command recorded above was executed, with `generateProto` and `processResources` explicitly excluded.
- Cultivation launcher target, active JAR hash, server startup, client connection, gameplay behavior, MongoDB/player data, or deployment.
- Furina runtime evidence beyond the focused checked-in unit test source and remote patch.
- Morphia persistence against a real existing/new player document, reconnect behavior, command registration/output in a running server, client packet acceptance, and gameplay effects of the manual fallback. These remain explicitly gated runtime UAT.

## Failures and working fallbacks

- Supplied EDS2 UNC path did not exist due to separator placement. Focused parent-directory discovery found the intended path; reads then succeeded.
- Web tool direct GitHub API opens were rejected as unsafe. Read-only PowerShell `Invoke-RestMethod` to the official GitHub API provided commit/compare evidence without changing the clone.
- Broad Git/source commands produced truncated/noisy output because the shallow root commit presents all files as additions. Replaced with targeted commands and GitHub compare evidence.
- The project token meter historically confirmed the user-reported over-limit state; the prior automatic continuity route still required manual user intervention while the task was busy. Measurement remains mandatory and best-effort, but it does not itself select or execute the route.
- An attempted same-project task creation for the continuity-policy correction produced no usable return and no visible destination in the task list while the user was on mobile remote. The user chose the manual fresh-task prompt fallback; no destination ownership transfer occurred.
- Broad CodeGraph semantic prompts frequently returned generic loaders or generated protocol symbols instead of the requested workflow. Exact symbol queries and direct source reads are the working fallback.
- CodeGraph static call/impact output missed obvious direct calls and produced unrelated name-collision paths. These discrepancies are recorded above; never promote graph output to source truth.

## Rollback

- Reverse the current continuity-policy correction only in `AGENTS.md`, `.agents/scripts/active_context.py`, `docs/agents/README.md`, `docs/agents/context-quality-gates-review.md`, and the policy hunk in this handoff. Restore the superseded text selectively while these files remain untracked; do not delete the pre-existing untracked documents or touch other groundwork artifacts.
- Reverse the worker-policy revision only by restoring the prior delegation bullet in `AGENTS.md`, removing the matching paragraph in `docs/agents/README.md`, and reverting Decision 22 and its status references here. This does not affect fresh-task continuity rules.
- Reverse the matrix phase only by removing the newly added `docs/research/gc70-hexerei-config-predicate-status-matrix.md` and restoring the matrix-status hunks in `docs/agents/current_task.md`, `docs/research/gc70-hexerei-resource-roster.md`, and this handoff. Do not overwrite the pre-existing roster or continuity documents wholesale.
- Reverse the public-source phase only by removing `docs/research/gc70-hexerei-public-quest-sources.md` and restoring its focused pointer/status hunks in the matrix, current task, and this handoff. The separately archived web evidence can be removed independently from `LunaGC-Web-Research\2026-08-24`; it was never installed into runtime resources.
- To remove the current CodeGraph index, first record/verify its exact path and status, then use the supported prompted `uninit` command in `docs/architecture/codegraph.md`; remove only the dedicated ignore entry if CodeGraph is permanently abandoned.
- Furina used a uniquely named runtime candidate and did not overwrite the previous JAR. Rollback remains restoring Cultivation's preserved original JAR path; no source or database rollback was reported necessary after the successful scoped UAT.
- Roll back the source-only fallback by reverting the named Java integration hunks and removing `HexereiManager.java`, `HexereiCommand.java`, `HexereiManagerTest.java`, and the manual-activation design document. Do not revert unrelated groundwork or the inherited `.gitignore` edit.
- If a later deployment persists manual records, first run `hexerei reset all @<uid>` for every affected player and verify the namespace is empty, then restore the preserved prior JAR/source. No quest, quest-global, open-state, talent, skill-level, or avatar-proud restoration is expected because this slice never overwrites those fields.

## Risks and open questions

1. The condition namespace is resolved to 12 terminal quest IDs, but neither the approved archive nor any inspected public source provides the complete paired, LunaGC-loadable terminal rows and semantically named quest payloads. Authoritative per-player completion remains unavailable; manual activation must never be relabeled as quest completion.
2. Manual activation semantics are now explicit and tested source-side, but runtime behavior remains unverified. `BJJDEAIEIGP` deliberately preserves the prior entity-selection behavior despite resource `Self`/`Target` sites; Hex-specific `ByUnlockTalentParam` aliases still do not map through direct open-config equality; unsupported predicates remain fail-open.
3. Build version metadata is stale and the default root output may overwrite the active launcher JAR. Verify the launcher target read-only before any packaging.
4. The shallow clone lacks base objects locally; use read-only remote evidence or an explicitly approved metadata fetch for further history.
5. Generated protocol Java and three raw proto files are not a complete protocol-source picture. The maintainer-linked GitLab protocol project is currently inaccessible anonymously; obtain a user-supplied immutable schema or separately approved authenticated access before confirming the hand-encoded packet fields.
6. Furina pure-policy tests plus user-reported scoped gameplay UAT now pass, and PR #2 is merged. The runtime observation was not independently captured, so it does not establish unrelated Furina-kit behavior or long-session soak stability.
7. Focused source tests are complete. Runtime UAT and safe non-root packaging/deployment remain gated and unfinished.
8. The hand-encoded team-change packet has no generated schema in this checkout; field 9 is declared but unused. Do not alter it without authoritative protocol evidence.
9. Hex team counting no longer uses a static tag cache. The independent Moonphase tag cache remains unchanged and outside this scope.
10. Checked-in `AVATAR_TAG_MOONPHASE`/`SGV_MoonPhaseLevel` seams are only a later candidate for the deferred Lucid Revelations system. Do not equate or merge them without the separate resource reconciliation.

## Key terms and symbols

- Branches/commits: `fix/furina-salon-lifecycle` / `5a50a40`; `7.0.0` / `90cfd92`.
- Startup/data: `Grasscutter.main`, `DatabaseManager.initialize`, `ResourceLoader.loadAll`, `FileUtils`, `GameData`.
- Avatar state: `Avatar`, `AvatarStorage.loadFromDatabase`, `skillLevelMap`, `talentIdList`, `proudSkillList`, `proudSkillBonusMap`.
- Ability: `AbilityManager`, `AbilityModifierController`, `PredicateEvaluator`, `InvokeHandler`.
- Hexenzirkel: `AVATAR_TAG_HEXENZIRKEL`, `SGV_HexenzirkelLevel`, `PacketTeamHexenzirkelChangeNotify`, `HandlerCombatInvocationsNotify`.
- Furina: `FurinaGadgetPolicy`, gadget IDs `41089010`–`41089014`.

## Exact next step

User selects and verifies `LunaGC-Hexerei-16b5949-venti-green-uat.jar`, then runs the exact preserved sequence Durin E -> normal attack -> Durin burst -> Venti burst -> Venti E -> normal attacks. Capture `hexerei team` before and after; expected calculated/send/cache values are `2`/`2.0`/`2.0`, and Venti's post-burst normal attacks should receive the expected Anemo infusion. Runtime remains user-controlled.

## NEW CHAT SEED

Goal: validate the GC 7.0 Hexerei activation foundation in runtime, then systematically repair and verify all 12 Hexerei characters' buffs, talents, and constellations.

Constraints: `/execute`; Java/Gradle private server only; preserve all user work; no destructive Git; no official services/retail executable changes; no global installs/PATH/Codex/MCP changes; no Cultivation/game/config/JAR/player-data edits; no server/client/gameplay run; no `jar`/`build` until active-output safety is resolved; bounded same-task workers are pre-authorized within scope, while durable agents and user-visible fresh tasks retain the approval/continuity rules; default fresh-task creation requires approval, but a concrete goal plus terminal authorization grants standing permission for the minimum necessary safe same-project continuation tasks; that exception does not make routing automatic or broaden authority; continuity measurement is mandatory and best-effort while routing is proportional; verify graph conclusions against source; never guess GC 7.0 mechanics/data.

Decisions: Furina scoped gameplay UAT passed by user report and PR #2 is squash-merged as `4b43902`; GC 7.0 is authoritative and `6.6.0` is stale metadata; authoritative Hexerei quest recovery is `NO`; manual `MANUAL_COMMAND` records never equal quest completion; raw proud state is diagnostic while effective state gates runtime/protocol effects; Witch's Revelation/Lucid Revelations stays separate; use uniquely named candidates and preserved launcher/data rollback points.

Status: Furina is closed and merged. Hexerei PR #1 remains draft on `4b43902` with repair head `16b5949`; 16/16 focused tests pass. The unique Venti green-UAT candidate, hashes, preserved configuration/selected/stable JARs, and append-only rollback manifest are complete. Authoritative completion remains `UNAVAILABLE_RESOURCE`; green runtime UAT is pending user control.

Changed artifacts: all prior groundwork listed above plus `docs/research/gc70-hexerei-quest-deobfuscation.md`, `docs/research/gc70-hexerei-manual-activation-design.md`, `HexereiManager.java`, `HexereiCommand.java`, `HexereiManagerTest.java`, and the exact tracked Java integration files listed in this handoff. `.codegraph/` remains ignored/local; public research downloads remain outside the repo.

Verification: all prior Hexerei evidence remains; its focused Gradle command passed full main/test compilation and 13/13 tests. Furina's candidate/hash/rollback were verified and the user reported the scoped fixes worked in game; PR #2 merge was verified at remote `7.0.0` SHA `4b43902e337c98c605553b29511d1c498de96864`.

Open items: user-controlled Venti proven-seam green UAT; persistence/reconnect/reset follow-through; then systematic character runs and evidence-backed repairs. `Self`/`Target`, unlock-alias, unknown-predicate, constellation, and hand-coded packet semantics remain unresolved; Lucid Revelations remains later and separate.

Key terms: `5a50a40`, `90cfd92`, `FurinaGadgetPolicy`, `AVATAR_TAG_HEXENZIRKEL`, `SGV_HexenzirkelLevel`, `AbilityManager`, `PredicateEvaluator`, `Avatar.skillLevelMap`, `Avatar.talentIdList`, `ResourceLoader.loadAll`.

Next step: select the exact green-UAT candidate, verify SHA-256 `6FEE2A9FAE946588049424683F4C56D5DFF000494F5C13B542297A050D530784`, and run the exact corrected Venti sequence. Report count/cache and gameplay evidence before any broader repair.
