# Agent memory

`current_task.md` is the pointer-level source of truth and points to at most one active handoff. Put material unfinished state and durable continuation evidence in `handoffs/`; keep generated graph output and temporary investigation results out of continuity files.

Context-gate evidence, the adopted `110,000 / 130,000 / 150,000` policy, and the rejected or deferred `160,000 / 180,000 / 200,000` experimental alternative are recorded in `context-quality-gates-review.md`. Operative thresholds remain in `AGENTS.md` and the context helper until explicitly changed.

`codex-workflow-cost-policy.md` records the adopted operating rules for keeping
Codex context proportional to the active LunaGC phase. It does not establish
provider pricing, cache, or tool-behavior claims.

## Proportional handoff template

Use only the sections relevant to the work. A material unfinished phase, risky state change, several durable decisions, or work that cannot be resumed reliably requires a full enough handoff to preserve safe ownership and rollback. Irrelevant sections may be omitted.

```markdown
# <task/phase> handoff

Relation: Active | Updates <path> | Supersedes <path> | Historical
Updated: <absolute date/time and timezone>

## Goal
## Constraints and authority
## Decisions
## Status and completed evidence
## Changed files/artifacts
## Verification performed
## Failures and working fallbacks
## Rollback
## Risks and open questions
## Key terms and symbols
## Exact next step
## NEW CHAT SEED (only when a genuinely fresh task is expected)
```

Create or update a handoff when a material phase changes, several durable decisions accumulate, risky or durable files/build/index/UAT state changes, substantial unfinished work remains, or context loss would make resumption unreliable.

Do not require a handoff for simple answers, bounded read-only checks, minor deterministic edits, targeted deterministic verification, evidence recording that already has a durable home, or ordinary final closeout. Do not duplicate an entire handoff as a second `NEW CHAT SEED` unless a genuinely fresh task is expected.

When context quality is declining, close locally if only bounded deterministic completion remains and pause without creating an idle task for a named user or external dependency. At the route gate, use compaction or create only the minimum continuation necessary when substantial ambiguous or multi-step work remains and authorization permits it. Context thresholds inform this proportional route selection but do not automatically select or execute a route.

Bounded same-task worker/subagent delegation is pre-authorized when it materially helps independent, in-scope work. Give each worker explicit scope and completion criteria, retain one primary owner, and review its evidence before relying on it. Delegation does not broaden mutation, runtime, deployment, external-service, or risk authority. Durable agents and user-visible fresh tasks/threads remain governed by the approval and continuity rules below.

Fresh-task creation requires explicit user approval by default. When the user has stated a concrete goal and given a terminal instruction such as “go ahead,” “work until completion,” “finish,” or equivalent, that instruction grants standing authorization for the minimum necessary safe same-project continuation task or tasks needed to pursue the already-authorized goal. The source task must not stop solely to request the same approval again. If no terminal authorization exists, the normal explicit-approval requirement remains.

Terminal authorization does not make task creation automatic or broaden the goal, scope, permissions, destructive authority, runtime authority, deployment authority, or approval for consequential unresolved choices. Ask when continuation would materially change scope, require new authority, cross projects or environments, introduce a risky or destructive action, or resolve a consequential ambiguity not settled by the user.

Before transfer, finish active mutations and verification to a safe boundary; update the handoff and task pointer; preserve the goal, constraints, decisions, changed artifacts, verification, rollback, risks, and the exact next step; verify destination readiness; transfer sole ownership; then stop substantive work in the source task. Prevent recursive task creation and task litter: keep only one active owner, and do not let a destination already at or above the route gate automatically create another continuation.

Context measurement is mandatory and best-effort, while routing remains proportional. Run `.agents/scripts/active_context.py --json` before every user-facing response; for non-trivial work, also run it at turn start and immediately before deciding whether to begin each broad or output-heavy phase. Use raw active input for the `110,000` warning, `130,000` continuity-preparation, and `150,000` route-decision gates. At warning, reduce unnecessary context growth and confirm durable state. At preparation, finish the current bounded slice, update resumable state, and do not begin a broad ambiguous phase without a fresh route decision. The 150,000 gate is a conservative LunaGC project-quality threshold, not a model context limit, proven universal quality cliff, or automatic transfer trigger. Qualitative continuity failures and material phase changes may require earlier routing. An `unknown` or failed measurement blocks a broad new phase but not bounded deterministic closeout.

End every user-facing response, including progress updates and final responses, with the helper's current footer on its own final line: `Context: ~<rounded active_input_tokens> / 150k project limit`. If measurement is unavailable, use `Context: unknown / 150k project limit` and report the failure. The footer reports the measurement; it does not select the route or replace the required pre-phase decision.
