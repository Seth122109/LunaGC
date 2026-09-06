# LunaGC-Dev agent guidance

This is a long-running Java/Gradle private-server project targeting Genshin Client 7.0. The immediate program is to repair character talents and constellations, prioritizing Hexerei behavior, while preserving unrelated systems. Do not infer mechanics from names or other versions.

## Read order

1. `docs/agents/current_task.md`
2. The single active handoff named there
3. `docs/architecture/codebase-map.md` and `docs/architecture/codegraph.md`
4. Relevant testing, gameplay-change, and decision documents
5. Fresh CodeGraph results
6. Targeted checked-in source and external resource data

CodeGraph is an orientation and impact-analysis index, not source of truth. Check freshness, use it before broad source searches when useful, and verify every critical conclusion against current source. Generated protocol Java, reflection, annotations, numeric/string ID dispatch, Gradle behavior, and runtime data require direct inspection.

## Modes

- `/explore`: investigate options and tradeoffs; label assumptions; do not edit gameplay or runtime artifacts without approval.
- `/execute`: make the smallest approved project-local change and verify it in proportion to risk. Ask only when ambiguity changes behavior, data, risk, or rollback.
- `/strict`: confirm before every material action and keep outputs and carryover especially tight.

## Scope and evidence

- Work only in this local private-server repository and its explicitly approved local runtime.
- Never interact with official game services, bypass account security, or modify the retail game executable.
- Distinguish checked-in source facts, Git/remote-history facts, graph inferences, external design assumptions, runtime observations, and unresolved questions.
- GC 7.0 is the user-confirmed target. `6.6.0` strings in Gradle/startup/JAR naming are stale metadata until separately corrected and verified.
- Trace complete workflows. Do not copy a character-specific workaround into another character without confirming the shared seam and data contract.

## Change control

- Start every work phase with `git status --short --branch --untracked-files=all`; inspect relevant diffs and preserve all user work.
- Keep edits scoped, reversible, and directly traceable to the requested behavior. Avoid unrelated refactors, formatting churn, dependency changes, or generated-source rewrites.
- Do not use destructive Git commands. Do not commit unless explicitly asked.
- Bounded same-task worker/subagent delegation is pre-authorized when it materially helps independent, in-scope work. Give each worker explicit scope and completion criteria, keep one primary owner, and review its evidence before relying on it. Delegation does not broaden mutation, runtime, deployment, external-service, or risk authority.
- Creating or changing durable agents, or creating user-visible fresh tasks/threads, still requires explicit user approval by default, subject only to the terminal-authorization continuity exception below.
- Ask before global installs, PATH changes, Codex/MCP configuration changes, or machine-wide settings.

Before gameplay implementation, record the goal, constraints, success criteria, affected files/symbols/data IDs, packet and persistence impact, tests, UAT, active-JAR deployment plan, and rollback.

## Build, test, and deployment gates

- Java 17 is the declared and locally available toolchain.
- Prefer targeted unit tests and compile checks. Verify behavior by workflow, not syntax alone.
- `build.gradle` writes the fat JAR to the repository root and currently names it `LunaGC-6.6.0.jar`; assume this can replace the JAR used by Cultivation until the launcher target is independently verified.
- Do not run `jar`, `build`, packaging, or any task that may overwrite the active root JAR without a separate output path or explicit approval.
- Do not start the server, replace the active JAR, launch/connect the client, or run gameplay UAT without explicit approval.
- Do not modify Cultivation, launcher configuration, the game installation, MongoDB/player data, runtime `resources/`, or live config during groundwork.
- Preserve pre-change artifacts for any later approved deployment. Verify candidate hash/name, stop state, startup logs, targeted gameplay behavior, and rollback before declaring a JAR usable.

## CodeGraph

- Follow `docs/architecture/codegraph.md`; pin the documented version and disable telemetry per session.
- Do not install globally or run CodeGraph's agent installer under standing authority.
- Before `init`, verify `.codegraph/` is absent and add `/.codegraph/` to `.gitignore` as a separate reviewable edit.
- Use `sync` for normal source changes and full `index` only for structural/config/version changes or a diagnosed stale/corrupt index.
- Record version, status, counts, representative queries, graph/source discrepancies, and the supported `uninit` rollback command.

## Continuity

Keep `docs/agents/current_task.md` concise and point to at most one active handoff. Create or refresh a handoff when unfinished work crosses a material phase boundary, accumulates several durable decisions, changes build/runtime/UAT state, or can no longer be resumed reliably from the current task.

Minor deterministic work does not require a handoff. This includes simple answers, bounded read-only checks, small documentation corrections, targeted tests, evidence recording, and final closeout when no material ambiguity or risky action remains.

Measure this exact task before every user-facing response with the read-only helper below. For non-trivial work, also check at turn start and immediately before deciding whether to begin each broad or output-heavy phase. The helper reads internal Codex telemetry whose schema may change, so measurement is mandatory but best-effort; it is a project quality signal, not the model's context limit, an automatic interrupt, or the sole routing authority.

```powershell
python .agents/scripts/active_context.py --json
```

Use raw `active_input_tokens`; cached input is already included. Treat `110,000` as warning, `130,000` as continuity preparation, and `150,000` as the route-decision gate. Warning means reduce unnecessary context growth and confirm that durable state is current. Preparation means finish the current bounded slice, update resumable state, and avoid beginning a broad ambiguous phase without a fresh route decision. The 150,000 gate is an intentionally conservative LunaGC project-quality threshold, not a model context limit, a proven universal quality cliff, or an automatic transfer trigger. Qualitative continuity failures or a material phase change may require an earlier route decision. Compaction starts a new active generation. Ignore transitional zero samples and cumulative totals. If the result is `unknown` or the helper fails, report that condition, do not reuse a pre-compaction value, avoid starting a broad new phase, and allow bounded deterministic closeout.

End every user-facing response, including progress updates and final responses, with the helper's current footer as its own final line: `Context: ~<rounded active_input_tokens> / 150k project limit`. If measurement is unavailable, use `Context: unknown / 150k project limit` and state the measurement failure in the response. A footer never substitutes for the pre-phase route decision.

When context quality is declining:

- finish any active mutation or verification to a safe recorded boundary;
- avoid opening unrelated work;
- close normally when only deterministic completion remains;
- pause without creating an idle task when waiting for the user or external state;
- at or above the route-decision gate, close locally for bounded deterministic completion, pause for a named user or external dependency without creating an idle task, or use compaction or a fresh continuation only when substantial ambiguous or multi-step work remains and authorization permits it.

Fresh-task creation requires explicit user approval by default. When the user has already stated a concrete goal and given a terminal instruction such as “go ahead,” “work until completion,” “finish,” or equivalent, that instruction grants standing authorization to create the minimum necessary safe same-project continuation task or tasks needed to pursue that already-authorized goal. The source task must not stop solely to request the same approval again. If no terminal authorization exists, retain the normal explicit-approval requirement.

Terminal authorization does not make task creation automatic. Context thresholds inform proportional routing but do not automatically select or execute a route. Close locally when only bounded deterministic completion remains; pause without creating an idle task when waiting for a named user or external dependency; create only the minimum continuation necessary when substantial ambiguous or multi-step work remains. Ask before continuation when it would materially change scope, require new authority, cross projects or environments, introduce a risky or destructive action, or resolve a consequential ambiguity the user has not settled.

Terminal authorization does not broaden the goal, scope, permissions, destructive authority, runtime authority, deployment authority, or approval for consequential unresolved choices. Before transfer, finish active mutations and verification to a safe boundary; update the handoff and task pointer; preserve the goal, constraints, decisions, changed artifacts, verification, rollback, risks, and one exact next step; verify destination readiness; transfer sole ownership; then stop substantive work in the source task. Prevent recursive task creation and task litter: there must be only one active owner, and a destination already at or above the route gate must not automatically create another continuation. Do not re-ask resolved questions.

## Verification and rollback

- State what was verified and what was not.
- If local runtime verification is prohibited or unavailable, provide the smallest focused UAT checklist and leave status unresolved.
- Documentation/tooling changes roll back through ordinary reviewable Git reversion of named files.
- CodeGraph rolls back with the exact supported `uninit` command in its guide; do not manually recurse-delete an unresolved path.
- Gameplay rollback must restore the preserved prior JAR/config/data state and must be planned before deployment.
