# Workflow-cost optimization: completed handoff

## Goal

Rank and adopt the highest-impact workflow changes that reduce avoidable Codex
context growth while preserving reliable LunaGC repair and UAT evidence.

## Scope and constraints

- Documentation and workflow only. Do not change Java source, tests, generated
  protocol files, runtime resources, JARs, configuration, player data, game or
  server processes.
- The Durin-then-Venti C0-C6 matrix is deferred. The next gameplay phase after
  this review is user-controlled weapon/artifact validation.
- Preserve all existing worktree changes.
- Treat the supplied Claude-focused video transcript as a set of hypotheses.
  Do not transfer its provider-specific pricing, caching, command, or feature
  claims to Codex without evidence.

## Established local facts

- The project requires `python .agents/scripts/active_context.py --json` at
  turn start and before broad phases. It measures project context quality, not
  billing; current handoff occurs around 98k/150k.
- Current task context is intentionally long because it contains closed
  Hexerei evidence. Weapon/artifact validation is a distinct phase and should
  not inherit detailed C0-C6/UAT investigation history unnecessarily.
- Local command discipline already supports cheap deterministic reads: `rg`,
  targeted Gradle tests, bounded command output, and evidence summaries.
- No Codex-specific pricing/cache percentages or scheduled-task cost profile
  is established by the transcript.

## Initial impact ranking to verify/refine

1. End or hand off at real phase boundaries with a concise evidence handoff;
   do not keep unrelated work in one growing task.
2. Filter and bound shell/test/log output before it enters the task context.
3. Avoid unnecessary plugins, broad tools, and large artifact reads; connect
   external capabilities only when they are task-critical.
4. Keep work local unless an independent subtask has sufficiently high-volume
   detail and a long remaining parent session; subagents move context cost and
   add coordination overhead.
5. Keep recurring monitoring narrow and separate from large working contexts.
6. Avoid gratuitous model/reasoning/setting changes mid-investigation, while
   making no unproven cache-cost claim.

## Required output

Deliver a concise ranked policy for this project: evidence, expected benefit,
tradeoff, applicability, and exact operational rule. Identify any low-risk
documentation updates separately; do not make broad AGENTS.md policy edits
without user approval.

## Completed result

The review adopted the impact-ranked operating policy in
`docs/agents/codex-workflow-cost-policy.md`. It prioritizes phase-boundary
handoffs, bounded tool output, compact non-duplicative instructions, selective
delegation, isolated monitoring, and representative-workload validation before
configuration changes. The Claude-oriented transcript is treated only as a
source of workflow hypotheses: no pricing, cache, command, proxy, tool-deferral,
or scheduling claim was transferred without Codex evidence.

No Java source, tests, generated files, runtime resources, JARs, configuration,
player data, or processes were modified. `AGENTS.md` was intentionally left
unchanged.

## Next step

The active phase is now user-controlled weapon/artifact validation under
`docs/agents/handoffs/2026-08-29-weapon-artifact-validation.md`.
