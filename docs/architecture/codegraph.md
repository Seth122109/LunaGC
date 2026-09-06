# CodeGraph workflow

Status date: 2026-08-24  
Repository: `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev`

This note defines the reproducible CodeGraph workflow for architecture discovery. It does not authorize gameplay edits, server startup, JAR deployment, launcher changes, or player-data changes.

## Current local baseline

- The official GitHub `releases/latest` endpoint resolves to **v1.5.0** (released 2026-07-21, commit `ea72e1b`). Pin that version for this groundwork so graph output does not change halfway through the investigation. [Official v1.5.0 release](https://github.com/colbymchenry/codegraph/releases/tag/v1.5.0)
- Windows x64 and arm64 are supported. The CLI ships with its own runtime; the programmatic API, which this project does not need, has a separate Node 22.5+ requirement. [v1.5.0 platform and runtime notes](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#supported-platforms)
- Local initialization completed on 2026-08-24 through version-pinned `npx`; `codegraph` remains absent from `PATH`. The generated index is project-local under `.codegraph/` and ignored by Git.
- Latest JSON health check on 2026-08-24: initialized `true`; version/build `1.5.0`; 3,570 files; 251,229 nodes; 519,546 edges; backend `node-sqlite`; journal `wal`; state `complete`; zero pending file changes/references; no worktree mismatch; no re-index recommendation.
- The repository root `.gitignore` contains a dedicated `/.codegraph/` entry. It also excludes `build/`, `out/`, Gradle caches, root JARs, runtime resources/caches/logs, and generated `BuildConfig.java`. CodeGraph honors `.gitignore`, has built-in dependency/build/cache exclusions, and skips files larger than 1 MB. [v1.5.0 indexing configuration](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#configuration)

## Invocation decision

Use the published npm package through a **version-pinned `npx` command**. This is the least-invasive supported route on this machine:

- it does not put `codegraph` on `PATH`;
- it does not run the interactive `codegraph install` flow;
- it does not write global or project-local Codex/MCP configuration;
- it does not require a machine-wide package installation;
- it does use npm's normal download cache and creates the requested project-local `.codegraph/` index.

The official installer paths (`install.ps1` or `npm i -g`) deliberately install a CLI on `PATH`, and `codegraph install` configures detected agents. Those are outside the current approval. The official documentation explicitly supports npm/npx and distinguishes CLI installation, agent wiring, and per-project initialization. [v1.5.0 setup and uninstall behavior](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#get-started)

Set telemetry off for each PowerShell session before invoking CodeGraph. This also disables the daily update check and avoids storing or sending CodeGraph usage telemetry:

```powershell
$env:DO_NOT_TRACK = '1'
```

The documented alternatives are `CODEGRAPH_TELEMETRY=0` and `codegraph telemetry off`; the environment override is preferable here because it is session-scoped and does not persist a global preference. [Official telemetry controls](https://github.com/colbymchenry/codegraph/blob/v1.5.0/TELEMETRY.md#turning-it-off)

Use this literal command prefix throughout:

```powershell
npx --yes @colbymchenry/codegraph@1.5.0
```

Do not run the package with no CodeGraph subcommand: no arguments launches the interactive agent installer.

## Preflight and initialization

Before initialization:

```powershell
Set-Location -LiteralPath 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
git status --short --branch
Get-Command codegraph -ErrorAction SilentlyContinue
Test-Path -LiteralPath '.codegraph'
npx --yes @colbymchenry/codegraph@1.5.0 version
```

Record the status output. Stop if `.codegraph/` appeared since the baseline or if `git status` shows an overlapping user artifact.

Before running `init`, add `/.codegraph/` to the root `.gitignore` as a separate reviewable edit, or explicitly approve a repository-local `.git/info/exclude` entry for a local-only index. Check `git status` again after initialization. Do not assume an internal ignore file will hide the directory itself.

Initialize and build the first full graph in one operation:

```powershell
npx --yes @colbymchenry/codegraph@1.5.0 init 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
```

In v1.5.0, `init` always builds the initial index; the old `-i`/`--index` option is accepted only for backward compatibility and is a no-op. The command refuses a home directory or filesystem root unless `--force` is used. Never use `--force` for this repository. [v1.5.0 `init` implementation](https://github.com/colbymchenry/codegraph/blob/v1.5.0/src/bin/codegraph.ts#L3185-L3260)

If `init` unexpectedly offers to include ignored nested repositories or install Git sync hooks, decline and keep refresh manual until the proposed paths/hooks have been reviewed. The current local `C:` path is expected to use the normal watcher policy, and this no-MCP workflow explicitly uses `sync`; no hook is required. [v1.5.0 watcher-fallback source](https://github.com/colbymchenry/codegraph/blob/v1.5.0/src/installer/index.ts#L660-L713)

Initialization is local code indexing only. It does not compile Gradle, build or replace a JAR, start the server, invoke Cultivation, touch the game installation, or update player data.

## Health verification

Run both human-readable and machine-readable status:

```powershell
npx --yes @colbymchenry/codegraph@1.5.0 status 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
npx --yes @colbymchenry/codegraph@1.5.0 status 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev' --json
git status --short
```

A healthy groundwork index should show:

- `initialized: true` and version `1.5.0`;
- the exact LunaGC-Dev project and index paths;
- non-zero Java file, node, and edge counts;
- `java` among indexed languages;
- `journalMode: wal` on this local NTFS path;
- no pending added, modified, or removed files after a completed sync;
- no worktree mismatch;
- index state complete, no re-index recommendation, and zero pending references at rest;
- no database/index files in `git status`.

The JSON status implementation reports project/index paths, last-indexed time, counts, backend, journal mode, languages, pending changes, worktree mismatch, extraction/build versions, index state, and pending references. A non-zero pending-reference count at rest means an interrupted resolution pass and missing call edges. [v1.5.0 `status --json` implementation](https://github.com/colbymchenry/codegraph/blob/v1.5.0/src/bin/codegraph.ts#L3704-L3843)

Keep the index on the local `C:` drive. The project documents WAL/locking problems for network shares and WSL-mounted Windows paths. [v1.5.0 troubleshooting](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#troubleshooting)

## Refresh workflow

For ordinary source changes, run an incremental sync:

```powershell
npx --yes @colbymchenry/codegraph@1.5.0 sync 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
```

Use a full re-index only after changing CodeGraph version, changing `codegraph.json` include/exclude/extension rules, suspecting an incomplete/stale graph, or when status recommends it:

```powershell
npx --yes @colbymchenry/codegraph@1.5.0 index 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
```

`index` discards and recreates CodeGraph's database, but does not alter repository source. It is slower and less conservative than `sync`; capture status before and after it. The official CLI reference distinguishes full `index` from incremental `sync`. [v1.5.0 CLI reference](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#cli-reference)

For a CodeGraph version update, do not use an unpinned `latest` package mid-session. Review the new official release, update every command in this file to the chosen version in one documentation change, run `version`, then perform a full `index` and re-run the representative query checks. `codegraph upgrade` is intended for installed CLI copies; changing the explicit npx version is the reproducible update path for this project.

## Query workflow

Start broad with `explore`, then confirm exact symbols with `query`, `node`, `callers`, `callees`, and `impact`. Commands can be run from any directory because the index is discovered by the explicit project path for lifecycle commands; for query commands, first set the repository as the working directory:

```powershell
Set-Location -LiteralPath 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
npx --yes @colbymchenry/codegraph@1.5.0 explore 'server startup and configuration flow'
npx --yes @colbymchenry/codegraph@1.5.0 query 'GameServer' --json
npx --yes @colbymchenry/codegraph@1.5.0 node 'qualified.symbol.from-query'
npx --yes @colbymchenry/codegraph@1.5.0 callers 'qualified.symbol.from-query' --json
npx --yes @colbymchenry/codegraph@1.5.0 callees 'qualified.symbol.from-query' --json
npx --yes @colbymchenry/codegraph@1.5.0 impact 'qualified.symbol.from-query' --depth 3 --json
```

Replace the placeholder only with a qualified symbol returned by `query`; do not guess overloaded Java method identities. The supported CLI surface and options are recorded in the official v1.5.0 reference. [v1.5.0 CLI reference](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#cli-reference)

Run and save findings for these representative `explore` prompts, refining each with exact symbol queries:

1. `server startup and configuration loading flow`
2. `packet registration dispatch parsing and response flow`
3. `avatar creation loading and reconstruction flow`
4. `talent unlock representation and skill execution flow`
5. `constellation activation and effect application flow`
6. `ability modifier add update serialize and remove flow`
7. `condition and unlock requirement evaluation flow`
8. `game data and resource loading flow`
9. `avatar and character state persistence flow`
10. `Gradle build and runnable JAR packaging flow`
11. `Furina implementation summon lifecycle talent constellation and modifiers`
12. `Hexerei symbols configs conditions buffs modifiers handlers and protocol fields`

For every material conclusion, record the query, returned symbol/path, and direct source lines that confirm or contradict the graph.

## Expected artifacts and source coverage

CodeGraph stores its per-project SQLite graph under `.codegraph/`, with the main database at `.codegraph/codegraph.db`. It indexes supported source languages and resolves calls, imports, inheritance, and selected framework conventions. Java `.java` is listed as full support. [v1.5.0 graph model](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#how-it-works) [v1.5.0 language support](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#supported-languages)

Repository-specific implications:

- Java under `src` is the primary graph target.
- `build/`, `out/`, root JARs, caches, runtime resources, and generated `BuildConfig.java` are already ignored and should remain out of the graph.
- `.proto` is not listed as a supported language in v1.5.0; inspect protocol definitions and generated Java directly rather than assuming protocol relationships are complete.
- bundled JARs and runtime-loaded data/config are not Java source graphs. Inspect their declared integration points and loading code directly.
- files larger than 1 MB and ignored paths are absent by design; a missing symbol is not evidence that behavior is absent from the runtime.

Do not add `codegraph.json` unless status/query evidence shows a concrete coverage problem. If it becomes necessary, document the exact include/exclude/extension rule, why it is needed, and re-index afterward.

## Limitations and evidence rules

CodeGraph is static code intelligence, not runtime proof. The official coverage notes explicitly identify runtime dynamic dispatch, reflection/DI containers, framework-convention entry points, and vendored third-party code as a remaining static-analysis frontier; measured Java cross-file coverage is high but not complete. [v1.5.0 measured coverage and limitations](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#measured-cross-file-coverage)

For LunaGC-Dev, treat these as graph boundaries requiring direct inspection:

- reflection, annotation scanning, registries, event buses, dependency injection, and service loaders;
- ability/config/data-driven dispatch where string or numeric IDs choose behavior;
- generated protocol Java, protobuf serialization, and packet opcodes;
- Gradle task configuration, shading, manifest selection, and filesystem copy/deploy steps;
- runtime resource discovery, MongoDB reconstruction, caches, plugins, and dynamically loaded classes;
- overload resolution and interface-to-implementation edges where several same-named Java methods exist.

Graph output is discovery evidence, not the final authority. Confirm critical findings against checked-in source, Git history, Gradle configuration, and—only in a separately approved phase—safe runtime observation. Distinguish verified source facts, graph inferences, runtime observations, external mechanics assumptions, and unresolved questions.

## Removal and rollback

Project index removal is intentionally destructive to generated CodeGraph data and must be targeted to the exact repository path. First record status and verify the resolved target, then use the supported prompted command:

```powershell
Resolve-Path -LiteralPath 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
npx --yes @colbymchenry/codegraph@1.5.0 status 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev' --json
npx --yes @colbymchenry/codegraph@1.5.0 uninit 'C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Dev'
git status --short
```

Do not pass `--force`; keep the confirmation prompt. `uninit` deletes that project's `.codegraph/` directory and removes CodeGraph-installed Git sync hooks if any. It does not uninstall a CLI or alter agent configuration. [v1.5.0 `uninit` implementation](https://github.com/colbymchenry/codegraph/blob/v1.5.0/src/bin/codegraph.ts#L3341-L3420)

Then remove the project ignore entry only if CodeGraph will no longer be used, via an ordinary reviewable edit to `.gitignore` or `.git/info/exclude` matching where it was added. Do not run a recursive manual deletion and do not clear the shared npm cache as rollback.

Because this workflow never runs `codegraph install` and never globally installs the package, there is no CodeGraph CLI/MCP configuration to uninstall. If a later approved phase uses the standalone/global installer, the supported rollback is `codegraph uninstall`; it leaves project indexes in place, which still require `codegraph uninit`. [v1.5.0 uninstall behavior](https://github.com/colbymchenry/codegraph/blob/v1.5.0/README.md#uninstall)

## Verification record template

Record this after initialization and after each full refresh:

```text
CodeGraph version:
Invocation method:
Project path:
Index path:
Initialized / last indexed:
File / node / edge counts:
Languages:
Backend / journal mode:
Index state / pending references:
Pending file changes:
Worktree mismatch:
Representative queries run:
Graph/source discrepancies:
Generated artifacts visible to Git:
Removal command verified:
```
