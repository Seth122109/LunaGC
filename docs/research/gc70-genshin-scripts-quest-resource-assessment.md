# GC 7.0 GenshinScripts quest-resource assessment

Status: documentation-only source review on 2026-08-31. No repository was
cloned; no LunaGC/runtime game resource, player data, server, client, build,
or process was changed.

## Decision

**NO-GO.** [ToaHartor/GenshinScripts](https://github.com/ToaHartor/GenshinScripts)
cannot provide a complete, LunaGC-loadable GC 7.0 Klee Hexerei quest chain. It
contains neither game data nor a conversion pipeline. It is an old, human-
readable quest-dialogue exporter whose caller must separately supply an
unversioned legacy-format data directory.

## Examined immutable source

- Repository head and `main`: commit
  [`f76114aa184e545199c278d6968539beb671c40f`](https://github.com/ToaHartor/GenshinScripts/commit/f76114aa184e545199c278d6968539beb671c40f), dated 2021-03-24.
- Its complete Git tree has only `README.md`, `main.py`, `translation.txt`, and
  `utils/*.py`; it has no `data/`, `Excel/`, `ExcelBinOutput/`, `BinOutput/`,
  `QuestExcelConfigData.json`, or `MainQuestExcelConfigData.json` artifact.
- The full 15-commit history ends at that same 2021-03-24 commit. Quest-related
  changes alter command dispatch and dialogue-recursion behavior; none adds a
  resource export, parser, or format converter.

## What it actually does

`README.md` requires a manual `data/` download from the then-referenced
Dimbreath repository and specifies `data/Excel/*.json`. `main.py` routes
`chapter` and `quest` commands to `utils/questlog.py`.

`utils/questlog.py` reads legacy `data/Excel/ChapterExcelConfigData.json`,
`MainQuestExcelConfigData.json`, `QuestExcelConfigData.json`, talk/dialogue,
reward, material, and text-map files. It joins `MainQuest.Id` to
`QuestExcelConfigData.MainId`, sorts existing child rows by `Order`, and writes
localized `.txt` dialogue logs beneath `res/Chapter` or `res/Quest`. It does
not emit JSON quest rows, inspect `BinOutput/Quest`, preserve lifecycle fields,
or transform either Excel rows or BinOutput payloads into LunaGC resources.

Therefore it contains no `QuestExcelConfigData`/`MainQuestExcelConfigData`
payload and no tooling that could create the paired LunaGC inputs.

## GC 7.0 compatibility and ID correction

The repository's last source revision predates the target by years and carries
no GC 7.0 build/revision pin. Supplying it a current export would also require
an unproven adapter from the documented `data/Excel` layout to current
`ExcelBinOutput` data; that still would produce text, not loadable resources.

The current local GC 7.0 primary-data record cited in
[`gc70-hexerei-public-quest-sources.md`](gc70-hexerei-public-quest-sources.md)
does independently resolve the disputed identity at immutable
[`DimbreathBot/AnimeGameData` commit `26df1dfbdf05a82bbb1d97506859f3e1c40718d8`](https://github.com/DimbreathBot/AnimeGameData/commit/26df1dfbdf05a82bbb1d97506859f3e1c40718d8):

| Record | Direct GC 7.0 fields |
| --- | --- |
| `ChapterExcelConfigData`, `id=2083` | `beginQuestId=1001001`, `endQuestId=1001008`, `PACJEJCGPLN=[10010]`, `chapterIcon=UI_ChapterIcon_Hexenzirkel` |
| `MainQuestExcelConfigData`, `id=10010` | `chapterId=2083`, `luaPath=Actor/Quest/WQ10010`, `type=WQ` |
| `BinOutput/Quest/10010.json` | ordered children include `1001001` through terminal `1001008`; each is associated with main `10010` |

This **supports**, rather than rejects, the existing Klee mapping: chapter
`2083`, main quest `10010`, terminal subquest `1001008`. It does not prove a
complete runnable chain because the exact GC 7.0 `QuestExcelConfigData` child
rows remain absent from that source. The local deobfuscation analysis records
the missing lifecycle fields and rejects synthesizing them.

## LunaGC loadability gap

LunaGC `QuestData` requires non-null accept/finish/fail condition and
begin/finish/fail-exec lists, combiners, guide/trial/item fields, plus the
`MainQuestData.SubQuestData` `isRewind`/`finishParent` contribution. On load,
`GameMainQuest.addAllChildQuests()` enumerates every named subquest and skips a
child with no `QuestData`; `QuestManager.loadFromDatabase()` deletes a persisted
main quest if a persisted child cannot reattach to `QuestData`.

GenshinScripts never has, reads, or produces the complete pair LunaGC needs:

1. every exact GC 7.0 `QuestExcelConfigData` child row for main `10010`; and
2. the matching semantically named `BinOutput/Quest/10010.json` conversion
   consumable as LunaGC `MainQuestData`.

Its output is therefore not safe to load and must not be used for `quest add`,
`quest finish`, or persistence recovery.

## Verification and rollback

Verified read-only: the exact Git head/tree/history; `README.md`, `main.py`,
and `utils/questlog.py`; the directly relevant LunaGC `QuestData`,
`MainQuestData`, `GameMainQuest`, and `QuestManager` contracts; and the pinned
GC 7.0 primary-data identity already recorded in the local quest-source
research. No runtime compatibility test was possible or appropriate because
the candidate ships no resource set or converter. Roll back by reverting this
documentation-only file.
