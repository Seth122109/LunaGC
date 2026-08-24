# GC 7.0 Hexerei quest deobfuscation result

Status date: 2026-08-24
Scope: deterministic, read-only analysis of the 12 archived GC 7.0 quest payloads, immutable public comparison data, and LunaGC's checked-in quest contracts
Runtime status: no resource installation, generated candidate, Java/runtime change, Gradle task, server/client start, player-data access, deployment, or CodeGraph refresh

## Decision

**Authoritative, LunaGC-loadable recovery: NO.**

The GC 7.0 payload schema can be mapped far enough to recover each terminal child's ID, main ID, order, rewind flag, `finishParent`, finish/fail conditions, and finish/fail execs. It cannot recover a complete `QuestExcelConfigData` row or a complete main-quest lifecycle. In particular, version-matched comparison proves that accept conditions, condition combiners, and begin execs present in LunaGC's transformed quest records are absent from the Dimbreath `BinOutput/Quest` child objects. The 12 corresponding Dimbreath Excel rows are absent, so those values have no authoritative source. Several other exact Excel fields are also unavailable or not losslessly represented in the payload.

Consequently, the payloads must not be converted into runtime quest rows, and no generated candidate is produced. Public labels, sequential subquest IDs, neighboring quests, or likely default logic cannot fill the missing values without invention. The result supports the separately authorized explicit per-player activation fallback; it does not support claiming authoritative quest completion.

## Immutable inputs and provenance

Primary extraction source for this attempt:

- DimbreathBot `AnimeGameData` commit `26df1dfbdf05a82bbb1d97506859f3e1c40718d8`, build identity `CNRELWin7.0.0_R47482070_S47579390_D47579390`.
- Archived copies under `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Web-Research\2026-08-24\DimbreathBot-AnimeGameData-26df1df`.
- Dimbreath Excel SHA-256: chapter `08D68A06E3C50E4B0DACAAD78B841CFB53A1408B9A9CA83458D1AFA61A4B173A`; quest `C8561299F543456573B0FA16404E210BCD2ECF84CE5006EB63DD4B2845137A87`; main quest `C1ACC16D3F4BE31924B4C5303B1673861584BA8F256010D2F6AC51F54A2B3008`.

Version-matched semantic comparison source:

- `girluh/LunaGC-Resources` commit `1744949c800cc380cd16b5d1f092a94f06a820d2`.
- Five quest files present in both immutable commits were compared: `72203`, `72204`, `72205`, `72207`, and `72209`.
- The existing sibling `LunaGC-7.0.0` copies of those named files were read only after their SHA-256 values exactly matched the same files at the immutable girluh commit. This was comparison evidence only; no sibling resource was changed or installed.

| Main ID | Dimbreath GC 7.0 SHA-256 | Named LunaGC SHA-256 |
| ---: | --- | --- |
| `72203` | `448B2E3E5FC2A75AB70C18265006A8E02B37D8D8E26A86F0EE1949340D5FA90F` | `872CF657EEE10764C63810316972ED63F7DCFFA7F556F346353220AC4BE0901D` |
| `72204` | `10DB08C66C7166532B5AAB0D6374B0F417D18F314C60188177416048ED758E08` | `F019403F0CA665E70E4D6923FA6BEF68C240C0FE5A56458D2140B81AE50CBADA` |
| `72205` | `CED4E784C11A25CF7B8F8337404BD6FF3B528265385B31407A2455FC24CCE043` | `A143D4B61514459A0B6B166B9E42AC4FEB756A8440E16443E0E9B7553FF5967B` |
| `72207` | `1781DA4DF4BEFC7405DF9DD9AFB02DA2C62F6C9F97739357780296789B5077CD` | `9409755383CD268845069ED950891EBE87E5DB6661071EEAB953D1B944CCEA87` |
| `72209` | `25BE8098305A3B0766BE9E3A390201C3449DD247BAE33F9BDA138A40835BF74C` | `5644A97C9C84BEED81DB65DE3BC61A8A052890FCE0592C1EA4E3FE2AC8A11BF7` |

The girluh tree contains generated resource data and a short credits README, but no mapping or generator source. Therefore its named records are a version-matched validation corpus, not independently reproducible parser provenance. LunaGC's checked-in `MainQuestData`, `QuestData`, `ResourceLoader`, `GameMainQuest`, `GameQuest`, and `QuestManager` are the authoritative local parser/runtime contracts used to judge loadability.

## Deterministic GC 7.0 field map

The map below was established by aligning child records by exact subquest ID across the five version-matched file pairs, then checking value and enum-family invariants across their children. It was rechecked against all 12 archived Hexerei payloads.

| Obfuscated GC 7.0 field | Semantic field | Evidence strength |
| --- | --- | --- |
| top `OIFGMOHKPOI` | main quest `id` | exact scalar match |
| top `ALBFHGKNMLK` | main quest `type` | exact enum match |
| top `LKAHEACOLML` | `titleTextMapHash` | exact scalar match |
| top `EBNBLBEIFFJ` | child/subquest objects | exact child count and ID set |
| child `KCGAKLCHDCC` | `subId` | exact across every aligned child |
| child `BJAAAKHKKKL` | `mainId` | exact across every aligned child |
| child `EMNMIOBCCLL` | `order` | exact across every aligned child |
| child `IEPNMBIKGGA` | `isRewind` | exact across aligned true/false cases |
| child `OABMCAIEHJL` | `finishParent` | exact on terminal and nonterminal cases |
| child `ANBEKNMDKCH` | `finishCond` | exact type/parameter sequence in paired files |
| child `HNBPDOIIEKL` | `finishExec` | exact type/parameter sequence in paired files |
| child `LHIECEAAOPH` | `failCond` | `QUEST_CONTENT_*` family and terminal rollback pattern; consistent with LunaGC field contract |
| child `IMAKBNFPANL` | `failExec` | `QUEST_EXEC_*` family and terminal rollback pattern; consistent with LunaGC field contract |
| condition/exec `ALBFHGKNMLK` | `type` | exact enum value |
| condition/exec `OPDGHDAADJC` | `param` | exact ordered parameter vector |
| condition `DPMLKCPEIJD` | `paramStr` | exact string in paired conditions |
| condition `LIKEIGNEHNP` | `count` | structural/type invariant |

This mapping is intentionally version-specific. For the same `72205.json`, Dimbreath commit `82e74382e7788e318ad41fca926739a752c0bed6` (6.7) uses top object-array names `HLCINEMBGEF` and `OBPMJEILMMK`, while GC 7.0 uses `EBNBLBEIFFJ` and `OJACLOOEAMG`. The 6.7 file SHA-256 is `8047EE35217119287744CCAC57449D0236FF1AB068EED7CA87873E6CB49BE7F2`; the GC 7.0 file SHA-256 is `CED4E784C11A25CF7B8F8337404BD6FF3B528265385B31407A2455FC24CCE043`. Old-version obfuscation names were therefore not reused.

## What the 12 terminal payload objects prove

The following values are direct payload facts after applying the version-matched map. Empty fail columns mean the serialized array is empty; they do not supply a combiner or authorize an inferred default.

| Main / terminal | Children; terminal order | Rewind / finish parent | Finish condition | Finish exec summary | Fail condition / exec |
| --- | --- | --- | --- | --- | --- |
| `10003` / `1000312` | 17; 17 | false / true | complete talk `1000312` | lock point; set global `1000301=1` | leave scene + team dead / rollback `1000317` |
| `10004` / `1000406` | 7; 7 | true / true | complete talk `1000406` | lock point; set global `1000401=1` | leave scene / rollback `1000407` |
| `10005` / `1000502` | 2; 2 | true / true | complete talks `1000513`, `1000514` | set global `1000501=1`; unregister group | empty / empty |
| `10006` / `1000606` | 10; 10 | false / true | complete talk `1000609` | set global `1000601=1`; lock point | leave scene + team dead / rollback `1000610` |
| `10007` / `1000707` | 12; 14 | true / true | complete talk `1000707` | unregister three groups; set global `1000701=1` | team dead + leave range + manual transport / rollback `1000711` |
| `10008` / `1000806` | 12; 12 | true / true | complete talk `1000806` | lock point; set global `1000801=1` | leave scene / rollback `1000811` |
| `10009` / `1000911` | 11; 14 | true / true | complete talk `1000911` | unregister group; set global `1000901=1` | empty / empty |
| `10010` / `1001008` | 9; 10 | true / true | complete talk `1001008` | unregister group; set global `1001001=1` | empty / empty |
| `70074` / `7007409` | 15; 15 | true / true | complete talk `7007409` | refresh suite; unregister group; set global `7007401=1` | empty / empty |
| `76159` / `7615907` | 8; 7 | true / true | Lua notify | notify Lua; set global `7615901=1`; lock point | leave scene / rollback `7615905` |
| `70096` / `7009606` | 9; 9 | true / true | complete talk `7009606` | set global `7009601=1` | empty / empty |
| `70092` / `7009203` | 4; 4 | true / true | complete talk `7009203` | set global `7009201=1` | empty / empty |

These facts are useful reference evidence, but they are not complete quest rows.

## Proven recovery blockers

### Accept conditions and begin execs are not in the payload child objects

Across paired files `72203`, `72204`, `72205`, `72207`, and `72209`, the named LunaGC records contain nonempty `acceptCond` values such as `QUEST_COND_STATE_EQUAL`, `QUEST_COND_ITEM_GIVING_FINISHED`, and `QUEST_COND_TIME_VAR_PASS_DAY`, plus nonempty `beginExec` values such as `QUEST_EXEC_ACTIVE_ITEM_GIVING`, `QUEST_EXEC_REGISTER_DYNAMIC_GROUP`, and `QUEST_EXEC_REFRESH_GROUP_SUITE`. No `EBNBLBEIFFJ` child array in the corresponding Dimbreath GC 7.0 payload contains any `QUEST_COND_*` enum. Its typed child arrays contain only `QUEST_CONTENT_*` and `QUEST_EXEC_*` values mapped above. A direct content check also showed named accept-condition parameter `7217344` in `72205` is absent from the entire matching Dimbreath payload.

Those values ordinarily come from the Excel row. All 12 Hexerei terminal rows are absent from Dimbreath `QuestExcelConfigData.json`, so there is no authoritative source for their `acceptCond`, `acceptCondComb`, or `beginExec`.

### Condition combiners are not recoverable

LunaGC evaluates `acceptCondComb`, `finishCondComb`, and `failCondComb` through `LogicType.calculate`. The paired named records distinguish explicit `LOGIC_AND` from omitted/default fields even when condition counts look similar. The aligned payload children do not carry those subquest combiners. Inferring `LOGIC_AND`, `LOGIC_OR`, or `LOGIC_NONE` from list length or neighboring quests would be synthesis.

### Exact Excel-only values are unavailable or lossy

The payload field `OANENPOPCFO` is not a lossless substitute for Excel `descTextMapHash`. In paired `72205`, some values match, some are zero while the Excel hash is nonzero, and some differ by 512 (for example subquest `7220503`: payload `1401618292`, Excel/named `1401617780`). This is enough to reject an exact-field reconstruction from that payload field.

The attempt also did not establish authoritative terminal values for every `QuestData` field outside the mapped lifecycle subset: `guide`, `trialAvatarList`, `gainItems`, and their exact nested semantics. Their absence from a proven map is independently disqualifying for a complete row; defaults must not be invented.

### No public mapping/generator implementation closes the gap

The immutable girluh resource tree supplies named output files but no parser, mapping, or generator source. Exact-key public search produced no usable indexed mapping, and unauthenticated GitHub code search returned HTTP 401. The inaccessible/purged `hk4e-protos` source was not retried, per instruction.

## LunaGC loadability and lifecycle impact

Checked-in source makes the missing fields material:

- `QuestData.onLoad()` immediately streams accept, finish, fail, begin, finish-exec, and fail-exec lists; a complete loadable row needs correctly typed, non-null lists. It indexes accept conditions and applies `isRewind`/`finishParent` from `MainQuestData.SubQuestData`.
- `GameMainQuest.addAllChildQuests()` enumerates every `subQuests` entry and looks up a corresponding `QuestData`; absent rows omit `GameQuest` objects.
- `GameQuest` sizes persisted finish/fail progress arrays from condition-list lengths, runs `beginExec` on start, uses finish/fail conditions for progress, runs finish/fail execs, finishes the parent only through `finishParent`, and rewinds by `order`/`isRewind`.
- `QuestManager` uses accept conditions and their progress arrays to start quests. `getQuestById` returns null if either config or materialized parent/child is absent.
- `QuestManager.loadFromDatabase()` deletes a persisted main quest when any persisted child cannot be reattached to `QuestData`. Installing an incomplete reconstruction could therefore affect existing quest persistence, not merely Hexerei status.

Thus a payload-only reconstruction cannot be called loadable, lifecycle-correct, or safe for persistence.

## Hashes of the 12 archived payloads

| Main ID | SHA-256 |
| ---: | --- |
| `10003` | `CDF175FEF9526177130D412B518ADC7F8BA5DBA4DC2C228C4323B3F5C0A437EF` |
| `10004` | `7DBF9AB14FC71A0A9A9979B71B655912F567B89D10979578AEB6D8FAA5E89A40` |
| `10005` | `7268B10B2476B88B8C7B856CC33655835B653641CCA76636A86C372486CF6E7D` |
| `10006` | `7B3EC296BA554032F033582528C96D1AAD192282ACCC0CAE15DDF24D4B81FCE4` |
| `10007` | `B5ACE9A5430E4547D9ADA57748F1469F89FA0E8E411A780BC9A0784902DF7476` |
| `10008` | `C99C5252E797AA29CC8B277999456A4EA24AF967AA5AEAE4408840B68790614F` |
| `10009` | `1AC752A9A9A1297BC0C8BA4CC21BDFCE3EFE1B2885FAA026FAF3C39CD0AD3E0B` |
| `10010` | `A68DF7BCDB9C8AD8B43FC075217E506B8009E91F670D036D01D70ED934147E7C` |
| `70074` | `99A2DDB2AAFC90EBD8C4DDB0F3408FD4E2B847127760460F18BC96D233A73E2A` |
| `70092` | `2F93093C428DF0431833C04BC7A845F9E087E83E37BAC16307039D0E0082E7AB` |
| `70096` | `2E155FFB0B185AF7CD98AEEEC18D368C24C1508B78838654C668712C6CBB25BD` |
| `76159` | `09E44ACCBE3280023E1FE5A2BEAC7AAA5689094E49AB0F0A246E1ADB07CD8F77` |

## Commands and method

All analysis was read only. Representative method:

1. Recomputed archive hashes with `Get-FileHash -Algorithm SHA256`.
2. Read immutable GitHub raw URLs pinned to the two commits with `Invoke-WebRequest`; no branch-tip URL was used as evidence.
3. Parsed JSON in memory with `ConvertFrom-Json`.
4. Aligned paired child objects by named `subId` and obfuscated `KCGAKLCHDCC`; compared ordered scalars, booleans, condition/exec enum families, and parameter arrays.
5. Checked a five-file version-matched corpus, then applied the resulting GC 7.0 map to all 12 archived payloads and extracted each terminal object.
6. Compared the 6.7 and 7.0 immutable forms of `72205.json` to confirm obfuscated-name drift.
7. Read LunaGC source contracts directly and traced creation, acceptance, progress, finish/fail, rewind, lookup, database reload, and persistence behavior.

No candidate JSON was written because the authoritative-recovery condition failed.

## Review boundary and rollback

Verified: immutable identities and hashes; five exact GC 7.0 raw/named comparison pairs; mapped payload fields; 12 terminal mappings and lifecycle values present in the payload; absence of all 12 terminal Excel rows; concrete missing-field examples; LunaGC deserialization and lifecycle dependencies.

Not verified or performed: authoritative values for the missing Excel fields; a loadable reconstructed quest set; runtime resource loading; server/client behavior; database/player state; gameplay UAT; Gradle/build/JAR/deployment; Lucid Revelations. Witch's Revelation/Lucid Revelations remains a separate namespace.

Only this document was added. Rollback is deletion/reversion of `docs/research/gc70-hexerei-quest-deobfuscation.md`; no runtime or data rollback is required.
