# Codex workflow-cost operating policy

Status: Adopted for the current LunaGC workflow review  
Scope: Codex Desktop task workflow and documentation only; not a pricing, cache,
or provider-feature policy

## Purpose

Reduce avoidable active-context growth without weakening source, test, runtime,
or user-controlled UAT evidence.

## Ranked operating rules

1. **Route at real phase boundaries.** When the goal, evidence set, or
   approval/risk posture materially changes, close the completed phase with a
   concise handoff and begin the next phase with only its relevant state.
   Preserve: goal, constraints, decisions, verification, rollback, open risks,
   and one exact next step. Do not carry closed investigation detail forward by
   default.

2. **Bound tool input and output.** Before a shell, test, log, or artifact
   read, use the narrowest selector and request only the relevant lines or
   result. Summarize durable evidence once in its evidence document; do not
   repeatedly load whole handoffs, broad diffs, generated files, or artifacts.

3. **Keep standing instructions compact and non-duplicative.** Put durable
   policy in `AGENTS.md`, pointer state in `current_task.md`, and phase evidence
   in one active handoff. State a rule once unless a task needs an explicit
   exception. Preserve requirements that encode safety, evidence quality, or a
   known project failure mode.

4. **Delegate only an independent, high-volume slice.** A worker is justified
   only when its source material can be isolated, its return format is explicit,
   and its expected reduction in parent-context growth exceeds coordination and
   evidence-review overhead. Otherwise work directly.

5. **Keep monitoring isolated and finite.** Use narrow monitoring with explicit
   stop conditions. Return only changed state or a failure that needs action;
   do not attach routine monitoring output to an investigation thread.

6. **Treat configuration-cost claims as experiments.** Do not change model,
   reasoning effort, plugins, tools, or scheduling based on provider-specific
   claims. A change is justified only after a representative comparison shows
   equivalent task success and evidence completeness, with measured tokens,
   latency, and cost where those measurements are available.

## Mandatory continuity control

Run `python .agents/scripts/active_context.py --json` at turn start and before
starting a broad or output-heavy phase. The operative warning, preparation, and
route-decision thresholds remain `110,000`, `130,000`, and `150,000` active
input tokens. They are LunaGC project-quality routing thresholds, not model
capacity, billing, cache, or automatic-transfer claims. Route earlier when a
material phase change or continuity failure makes the active state unreliable.

## Claude-oriented transcript: approved adaptations

- Adopt instruction-size discipline, relevant-tool discipline, bounded command
  output, and cautious subtask use.
- Do not assume Claude slash commands, MCP tool deferral, proxy effects,
  automatic model-switch cache behavior, prompt-cache lifetimes, session-log
  billing fields, or its pricing rules apply to Codex Desktop.
- Use the local context helper for continuity. Do not turn its telemetry into a
  billing report without Codex-supported usage semantics.
- Revisit this policy after a material expansion of standing instructions,
  connected tools, or monitoring; do not rerun a broad audit for ordinary small
  changes.

## Verification standard

Count a workflow change as an improvement only when representative LunaGC work
still preserves hard constraints, source and runtime evidence, verification
results, rollback information, and the exact next step. Lower context growth,
tokens, latency, or cost alone is insufficient.

## Evidence

- `AGENTS.md`: existing project continuity, evidence, and context-measurement
  controls.
- `docs/agents/context-quality-gates-review.md`: local threshold evidence and
  limits of the project-quality claim.
- [OpenAI model guidance](https://developers.openai.com/api/docs/guides/latest-model):
  lean prompts, relevant tools, context tracking, and representative-workload
  validation.
