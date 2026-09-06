# Context quality and continuity gate review

Status: Adopted policy review  
Recorded: 2026-08-24 Asia/Manila  
Scope: Codex Desktop tasks using GPT-5.6 Sol, Terra, or Luna in LunaGC-Dev

## Conclusion

The claim that 150,000 active context tokens is a universal optimal maximum for high-quality agent work is not verified. No reviewed official OpenAI documentation identifies 150,000 tokens as an optimum or documents a quality cliff immediately above it.

LunaGC adopts `110,000 / 130,000 / 150,000` active input tokens for warning, continuity preparation, and route decision. The 150,000 gate is an intentionally conservative project-quality threshold. It is not a model context limit, a proven universal quality cliff, or an automatic transfer trigger.

## Verified evidence

- Official OpenAI model pages advertise a 1,050,000-token API context window for [GPT-5.6 Sol](https://developers.openai.com/api/docs/models/gpt-5.6-sol), [GPT-5.6 Terra](https://developers.openai.com/api/docs/models/gpt-5.6-terra), and [GPT-5.6 Luna](https://developers.openai.com/api/docs/models/gpt-5.6-luna). This API capacity is not the active Codex Desktop task window.
- The local Codex Desktop model catalog currently configures Sol, Terra, and Luna with `context_window=272000` and `effective_context_window_percent=95`. Live task telemetry reports `model_context_window=258400`.
- Official [GPT-5.6 model guidance](https://developers.openai.com/api/docs/guides/latest-model) says to track context as conversations grow because long sessions can amplify repeated prompt and tool content. It recommends validating configuration changes on representative tasks.
- The same guidance reports that leaner system prompts improved scores by roughly 10–15% in one internal coding-agent sample while reducing tokens by 41–66%. OpenAI labels these results directional and workload-dependent. They do not establish a 150,000-token threshold.
- Local LunaGC Sol tasks have operated above 150,000 active input tokens without compaction. Recorded examples reached approximately 167,000, 203,000, 217,000, and 228,000 tokens. These observations demonstrate technical and operational feasibility, not preserved quality; no controlled quality evaluation was performed.
- One non-LunaGC local Terra task compacted after approximately 196,000 active input tokens. A single observation does not establish a Terra quality boundary or a stable automatic-compaction threshold.

## Current-policy assessment

Adopted gates:

| State | Active input tokens | Share of current 258,400 effective window |
|---|---:|---:|
| Warning | 110,000 | 43% |
| Continuity preparation | 130,000 | 50% |
| Route decision | 150,000 | 58% |

At warning, reduce unnecessary context growth and confirm durable state. At preparation, finish the current bounded slice, update resumable state, and avoid beginning a broad ambiguous phase without a fresh route decision. At route decision, close locally for bounded deterministic completion; pause for a named user or external dependency without creating an idle task; or use compaction or a fresh continuation only when substantial ambiguous or multi-step work remains and authorization permits it. Qualitative continuity failures and material phase changes may require earlier routing.

This policy reflects LunaGC's quality-first posture: the cost of unnoticed degradation is asymmetric, project context is high-entropy, controlled handoff cost is acceptable, and LunaGC-specific quality evaluation above 150,000 tokens is insufficient. The observed ability to operate above 150,000 establishes feasibility only, not preserved quality.

## Rejected or deferred experimental alternative

The following alternative is not operative policy. It remains rejected or deferred unless a future controlled LunaGC evaluation supports a new explicit policy decision:

| State | Proposed tokens | Share of current effective window | Remaining headroom |
|---|---:|---:|---:|
| Warning | 160,000 | 62% | 98,400 |
| Continuity checkpoint | 180,000 | 70% | 78,400 |
| Route decision | 200,000 | 77% | 58,400 |

Use the same initial trial gates for Sol and Terra. Their current Codex Desktop context allocation is identical, and no reviewed evidence supports different numerical quality cliffs. Model-specific gates should follow measured LunaGC outcomes, not the models' general capability labels.

The alternative retains roughly 23% of the effective task window at route review, gives 40,000 tokens of warning runway, and aligns with completed local Sol work around 200,000 tokens. It remains an unevaluated heuristic for this project.

## Evaluation required before reconsideration

Run representative LunaGC tasks at the adopted and experimental gates. At approximately 110,000, 130,000, 150,000, 160,000, 180,000, and 200,000 active input tokens, compare the agent's output against durable project state for:

- goal and scope retention;
- hard constraints and approval boundaries;
- material decisions and rejected approaches;
- changed files, verification state, and rollback;
- exact next step;
- repeated work, contradictions, invented certainty, or missed evidence;
- task completion quality, correction count, latency, and transfer overhead.

Route earlier at any token count when those continuity checks fail, when a material phase changes, or when the active state cannot be resumed reliably. Close locally above a gate when only bounded deterministic work remains.

## Possible implementation only after a future policy change

- Update `AGENTS.md`, `docs/agents/README.md`, and `.agents/scripts/active_context.py` together.
- Parse and report the telemetry `model_context_window` instead of presenting the route threshold as a model limit.
- Rename `project_limit` to `route_threshold`, retaining a temporary compatibility alias if needed.
- Change the footer to distinguish active capacity from policy, for example: `Context: ~129k / ~258k active window; route review at 200k`.
- Use compact one-line measurement before routine responses and reserve JSON output for turn starts and broad-phase route decisions.
- Keep the active handoff concise; archive completed historical evidence so mandatory startup reads do not repeatedly consume context.

## Decision status

- Operative LunaGC gates are `110,000 / 130,000 / 150,000`, superseding the prior operative `135,000 / 140,000 / 150,000` thresholds.
- The 150,000 quality-optimum claim is unverified.
- The `160,000 / 180,000 / 200,000` gates are a rejected or deferred experimental alternative, not adopted policy.
