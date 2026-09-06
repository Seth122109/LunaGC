# Weapon/artifact validation handoff

Relation: Active  
Updated: 2026-08-29 Asia/Manila

## Goal

Perform the next bounded Hexerei UAT phase: user-controlled validation of
weapon and artifact behavior after the green Durin/Venti team-activation run.

## Constraints and authority

- Runtime remains user-controlled. Do not start or stop the server/client/game,
  change launcher or Cultivation selection, replace a JAR, alter runtime
  resources/configuration, or modify MongoDB/player data.
- Preserve all existing tracked, untracked, and ignored worktree state.
- Do not change Java source, tests, generated protocol files, JARs, or project
  configuration during validation. Capture evidence first; any repair requires
  separate authorization.
- Defer the Durin-then-Venti C0-C6 matrix.

## Status and completed evidence

The user-controlled Venti UAT is green: `hexerei team` recorded Venti
`10000022` and Durin `10000123` as `ACTIVE_CONSISTENT`, with calculated/send/
cache values `2`/`2.0`/`2.0`; the user reported post-burst normal attacks were
Anemo-infused. The run lacks only the second unchanged-party repeat snapshot.

The approved candidate remains
`LunaGC-Hexerei-16b5949-venti-green-uat.jar`, SHA-256
`6FEE2A9FAE946588049424683F4C56D5DFF000494F5C13B542297A050D530784`.
Rollback/configuration provenance remains under
`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\2026-08-26-hexerei-venti-green-uat-16b5949`.

## Workflow policy

Use `docs/agents/codex-workflow-cost-policy.md`: keep evidence scoped to this
phase, bound tool output, and preserve durable results once. The prior
workflow-cost review is complete.

## Exact next step

Ask the user to run the weapon/artifact validation with the preserved candidate
and report/capture the first observed result. Stop on the first failure, retain
the evidence, and do not repair source until separately authorized.
