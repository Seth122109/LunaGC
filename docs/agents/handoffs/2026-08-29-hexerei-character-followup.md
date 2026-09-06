# Hexerei character talents and constellations: continuation handoff

## Goal

Verify and repair, where evidence proves a defect, Hexerei talent and
constellation behavior for all 12 mapped characters after manual activation.

## Constraints

Source/tests/docs only unless the user separately performs runtime UAT. Do not
modify generated protocol code, runtime resources, client/game files, JARs,
configuration, player data, or processes. Preserve all pre-existing worktree
changes. Do not infer character mechanics from names or prior versions.

## Baseline and changed files

- `docs/research/gc70-protocol-tooling-repository-assessment.md` now records
  the incremental workflow-based compatibility path; full name translation is
  not a prerequisite for targeted client/server repair.
- `HexereiManagerTest` now covers all 12 roster entries: inactive view filters
  only the character's own mapped Hex proud skill, active restores it, and the
  raw/unrelated state is unchanged.
- Focused verification passed on 2026-08-29:
  `gradlew.bat test --tests emu.grasscutter.game.player.HexereiManagerTest --tests emu.grasscutter.data.ResourceLoaderOpenConfigTest -x generateProto -x processResources --no-daemon`;
  16 Hexerei tests + 1 loader test, zero failures/errors/skips.

## Source facts

- Activation records are isolated in `HexereiManager`; ordinary talent levels,
  talent IDs, constellation bonus maps, and charge maps remain owned by
  `Avatar`.
- `Avatar.recalcStats` applies effective proud-skill open configs and all
  unlocked constellation open configs; `Avatar.toProto` and depot-change
  notifications include talent IDs, skill levels, proud-skill bonuses, and
  effective proud skills.
- Potential character-specific failures remain data-driven: ability loading,
  instancing, predicates, modifiers, team SGV, and client-visible actions.
  `Self`/`Target` predicate semantics and direct constellation-unlock validation
  are unresolved; do not change them without a focused repro.

## Required workflow

1. Preserve the existing Venti green-UAT candidate and run its user-controlled
   loop first: Durin E -> normal -> Durin burst -> Venti burst -> Venti E ->
   normal attacks; capture `hexerei team` before/after and the infusion result.
2. If green, use the existing character matrix for the C0-C6 pass, starting
   Durin then Venti. For each cell record baseline/active/reset behavior,
   one/two-member state, reconnect, constellation and talent snapshots, and
   an observable oracle.
3. On the first failure, create a narrow deterministic source test at the real
   failing seam before a repair. Do not add generic character workarounds.

## Rollback

Revert only the test and research-note hunks from this handoff if needed. The
existing preserved candidate/JAR/config rollback records remain unchanged.
