# GC 7.0 Hexerei public quest-source research

Status date / web access date: 2026-08-24

Scope: public-web and local read-only research for the 12 Hexerei terminal quest conditions

Runtime status: documentation only; no resource installation or extraction into LunaGC, gameplay, persistence, packet, command, Java, generated protocol, Gradle, JAR, launcher, database/player-data, server/client, game-installation, deployment, or CodeGraph-index action

## Result

Public sources are **not sufficient as-is** to give LunaGC authoritative per-player completion for the 12 Hexerei terminal quests.

- Project Amber identifies every chapter, main quest, terminal quest, title, objective, and dialogue chain through a structured API. Its response is a presentation model, not a raw `QuestExcelConfigData` or LunaGC-loadable `MainQuestData` record. It omits `order`, accept/fail conditions and combiners, quest exec lists, `finishParent`, and `isRewind`.
- DimbreathBot `AnimeGameData` commit `26df1dfbdf05a82bbb1d97506859f3e1c40718d8` is an immutable GC 7.0 release-data source. It contains all 12 `MainQuestExcelConfigData` rows and all 12 raw `BinOutput/Quest/<mainQuestId>.json` files, but its `QuestExcelConfigData.json` contains none of the 12 terminal IDs. The BinOutput payloads are also serialized under obfuscated keys rather than the field names LunaGC's `MainQuestData` model consumes.
- `girluh/LunaGC-Resources` commit `1744949c800cc380cd16b5d1f092a94f06a820d2` is the transformed LunaGC resource source already represented by the approved archive. Its chapter, quest, and main-quest Excel files are byte-for-byte identical to the corresponding approved-archive entries; it has neither the 12 terminal rows nor the 12 quest BinOutput files.
- Honey Hunter renders corroborating chapter names, quest descriptions, objectives, rewards, and unlock wording. It does not expose the missing server resource records on the inspected pages.

The evidence is enough to validate the 12 chapter/main/terminal mappings and human-readable quest identity. It is not enough to install, load, or reconstruct authoritative completion. Missing rows must not be synthesized from Project Amber/Honey labels or from the presence of an ID inside an obfuscated Dimbreath payload.

## What LunaGC requires

Checked-in `QuestData` requires the terminal row's `subId`, `mainId`, `order`, condition combiners and accept/finish/fail condition arrays, begin/finish/fail exec arrays, guide/trial/item fields, and the `MainQuestData.SubQuestData` contribution for `isRewind` and `finishParent`. `GameMainQuest.addAllChildQuests` requires a named `subQuests` list and a matching `QuestData` record for every child. `GameQuest` sizes progress arrays from the condition lists, executes begin/finish/fail actions, and persists quest state/progress; database reload must reattach each persisted child to `QuestData`.

Consequently, a source that only identifies `1000606` as “Talk to Sebas,” for example, cannot support the LunaGC lifecycle. A source must provide a complete, provenance-controlled pair:

1. a LunaGC-compatible `QuestExcelConfigData` row for every child, including the terminal row; and
2. the matching, semantically named `BinOutput/Quest/<mainQuestId>.json` payload with ordered child records and `finishParent`/rewind metadata.

## Per-ID search result

The verified chapter IDs come from the approved archive. Project Amber was queried by chapter ID because its `/quest/<id>` endpoint uses the chapter namespace; querying the eight small main IDs returned unrelated historical chapter IDs, while the four large main IDs returned HTTP 404. Dimbreath was searched directly for both each main ID and terminal ID.

| Character | Chapter | Main quest | Terminal quest | Project Amber terminal in chapter | Dimbreath main metadata / payload | Dimbreath terminal Excel row | Final classification |
| --- | ---: | ---: | ---: | --- | --- | --- | --- |
| Durin | `2079` | `10006` | `1000606` | yes; 10 chapter subquests | yes / yes | absent | mapping/reference only |
| Venti | `2080` | `10007` | `1000707` | yes; 12 | yes / yes | absent | mapping/reference only |
| Klee | `2083` | `10010` | `1001008` | yes; 9 | yes / yes | absent | mapping/reference only |
| Albedo | `2078` | `10005` | `1000502` | yes; 2 | yes / yes | absent | mapping/reference only |
| Mona | `2077` | `10004` | `1000406` | yes; 7 | yes / yes | absent | mapping/reference only |
| Fischl | `2076` | `10003` | `1000312` | yes; 17 | yes / yes | absent | mapping/reference only |
| Sucrose | `2082` | `10009` | `1000911` | yes; 11 | yes / yes | absent | mapping/reference only |
| Razor | `2081` | `10008` | `1000806` | yes; 12 | yes / yes | absent | mapping/reference only |
| Varka | `10189` | `70074` | `7007409` | yes; 15 | yes / yes | absent | mapping/reference only |
| Nicole | `10239` | `76159` | `7615907` | yes; 8 | yes / yes | absent | mapping/reference only |
| Lohen | `10253` | `70096` | `7009606` | yes; 9 | yes / yes | absent | mapping/reference only |
| Prune | `10252` | `70092` | `7009203` | yes; 4 | yes / yes | absent | mapping/reference only |

Dimbreath's chapter table independently contains exactly one `endQuestId` match for each mapping, with the same Hexenzirkel chapter ID, begin quest, terminal quest, and `UI_ChapterIcon_Hexenzirkel` values already verified in the approved archive.

## Source provenance and exact URLs

### Project Amber

Provenance: third-party Genshin database API derived from game data and localized text. The inspected API response has top-level data keys `info` and `storyList`; terminal child records have only `id`, `isHidden`, `title`, `stepDescription`, and `taskData`. This is structured processed data, not raw resource data.

Version identity: the public changelog endpoint included a `7.0` record on access. The deployed client assets were `quest.e0076450.js` and `app.17fc1b9f.js`; the app's request parameter was `vh=60F1A`. No public source commit or immutable archive identity was exposed. The canonical API URLs below returned HTTP 200. Adding `?vh=60F1A` to the `ambr.top` proxy URLs returned HTTP 404, so the archived responses use the canonical URLs without that query.

- Version evidence: `https://ambr.top/api/v2/static/changelog?vh=60F1A`
- API implementation asset: `https://ambr.top/js/quest.e0076450.js`
- Durin: `https://ambr.top/api/v2/en/quest/2079`
- Venti: `https://ambr.top/api/v2/en/quest/2080`
- Klee: `https://ambr.top/api/v2/en/quest/2083`
- Albedo: `https://ambr.top/api/v2/en/quest/2078`
- Mona: `https://ambr.top/api/v2/en/quest/2077`
- Fischl: `https://ambr.top/api/v2/en/quest/2076`
- Sucrose: `https://ambr.top/api/v2/en/quest/2082`
- Razor: `https://ambr.top/api/v2/en/quest/2081`
- Varka: `https://ambr.top/api/v2/en/quest/10189`
- Nicole: `https://ambr.top/api/v2/en/quest/10239`
- Lohen: `https://ambr.top/api/v2/en/quest/10253`
- Prune: `https://ambr.top/api/v2/en/quest/10252`

Sufficiency: **sufficient only as a mapping/reference**.

### DimbreathBot AnimeGameData

Provenance: third-party extraction of Genshin release data, published as raw JSON in Git. Repository: `https://github.com/DimbreathBot/AnimeGameData`. Immutable identity:

- commit: `26df1dfbdf05a82bbb1d97506859f3e1c40718d8`
- commit URL: `https://github.com/DimbreathBot/AnimeGameData/commit/26df1dfbdf05a82bbb1d97506859f3e1c40718d8`
- commit date: `2026-08-16T20:59:21Z`
- commit message/build identity: `CNRELWin7.0.0_R47482070_S47579390_D47579390`
- chapter table: `https://raw.githubusercontent.com/DimbreathBot/AnimeGameData/26df1dfbdf05a82bbb1d97506859f3e1c40718d8/ExcelBinOutput/ChapterExcelConfigData.json`
- quest table: `https://raw.githubusercontent.com/DimbreathBot/AnimeGameData/26df1dfbdf05a82bbb1d97506859f3e1c40718d8/ExcelBinOutput/QuestExcelConfigData.json`
- main-quest table: `https://raw.githubusercontent.com/DimbreathBot/AnimeGameData/26df1dfbdf05a82bbb1d97506859f3e1c40718d8/ExcelBinOutput/MainQuestExcelConfigData.json`
- quest payload URL form: `https://raw.githubusercontent.com/DimbreathBot/AnimeGameData/26df1dfbdf05a82bbb1d97506859f3e1c40718d8/BinOutput/Quest/<mainQuestId>.json`, tested for all 12 main IDs.

The three Excel files expose raw structured records. The 12 BinOutput files also expose structured JSON, but the relevant property names are obfuscated. All 12 main metadata rows exist; all 12 payload files exist and contain their terminal numeric IDs; none of the terminal IDs occurs as a standalone number in the published quest Excel table.

Sufficiency: **partial raw reference, insufficient as-is**.

### girluh LunaGC-Resources and mirror check

Provenance: third-party transformed resource tree intended for LunaGC. Repository: `https://github.com/girluh/LunaGC-Resources`. Immutable identity:

- commit: `1744949c800cc380cd16b5d1f092a94f06a820d2`
- commit URL: `https://github.com/girluh/LunaGC-Resources/commit/1744949c800cc380cd16b5d1f092a94f06a820d2`
- commit date: `2026-08-23T11:02:19Z`
- commit message: `stellar swirl and heal tags fixed`
- quest table: `https://raw.githubusercontent.com/girluh/LunaGC-Resources/1744949c800cc380cd16b5d1f092a94f06a820d2/ExcelBinOutput/QuestExcelConfigData.json`
- main-quest table: `https://raw.githubusercontent.com/girluh/LunaGC-Resources/1744949c800cc380cd16b5d1f092a94f06a820d2/ExcelBinOutput/MainQuestExcelConfigData.json`
- chapter table: `https://raw.githubusercontent.com/girluh/LunaGC-Resources/1744949c800cc380cd16b5d1f092a94f06a820d2/ExcelBinOutput/ChapterExcelConfigData.json`

The three downloaded files exactly match the entries in approved archive `LunaGC-Resources-7.0.zip`, whose approved and recomputed whole-archive SHA-256 remains `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF`. All 12 `BinOutput/Quest/<mainQuestId>.json` URLs returned HTTP 404 at this commit. The separately inspected `capyb2222/LunaGC-Resources` commit `55edbff0150c68bee839be47ac13631d38b3e489` uses the same Git blobs for the quest and main-quest tables and also lacks all 12 payload paths; it is not an independent complete baseline.

Sufficiency: **insufficient; identical to the approved missing-data baseline for the relevant Excel files**.

### Honey Hunter

Provenance: third-party rendered database pages. Exact inspected URLs:

- tutorial wording: `https://gensh.honeyhunterworld.com/tut_13922/?lang=EN`
- Mona chapter: `https://gensh.honeyhunterworld.com/ch_2077/?lang=EN`
- Razor chapter/version view: `https://gensh.honeyhunterworld.com/ch_2081/?lang=EN&ver=6_6_0`
- Lohen chapter: `https://gensh.honeyhunterworld.com/ch_10253/?lang=EN`
- Nicole talent/unlock wording: `https://gensh.honeyhunterworld.com/p_1315101/?lang=EN`

The pages corroborate names, objectives, rewards, and the fact that completion unlocks character-specific Hexerei behavior. They expose rendered labels rather than LunaGC-loadable raw rows or payloads and have no commit/archive identity on the inspected pages.

Sufficiency: **human-readable mapping/reference only**.

## Downloaded evidence outside runtime resources

All downloads are outside the repository and runtime resource tree under:

`C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Web-Research\2026-08-24`

No file was installed or extracted into LunaGC `resources/`.

### Dimbreath immutable files

Directory: `DimbreathBot-AnimeGameData-26df1df`

| Archived filename | SHA-256 |
| --- | --- |
| `ExcelBinOutput__ChapterExcelConfigData.json` | `08D68A06E3C50E4B0DACAAD78B841CFB53A1408B9A9CA83458D1AFA61A4B173A` |
| `ExcelBinOutput__QuestExcelConfigData.json` | `C8561299F543456573B0FA16404E210BCD2ECF84CE5006EB63DD4B2845137A87` |
| `ExcelBinOutput__MainQuestExcelConfigData.json` | `C1ACC16D3F4BE31924B4C5303B1673861584BA8F256010D2F6AC51F54A2B3008` |
| `BinOutput__Quest__10003.json` | `CDF175FEF9526177130D412B518ADC7F8BA5DBA4DC2C228C4323B3F5C0A437EF` |
| `BinOutput__Quest__10004.json` | `7DBF9AB14FC71A0A9A9979B71B655912F567B89D10979578AEB6D8FAA5E89A40` |
| `BinOutput__Quest__10005.json` | `7268B10B2476B88B8C7B856CC33655835B653641CCA76636A86C372486CF6E7D` |
| `BinOutput__Quest__10006.json` | `7B3EC296BA554032F033582528C96D1AAD192282ACCC0CAE15DDF24D4B81FCE4` |
| `BinOutput__Quest__10007.json` | `B5ACE9A5430E4547D9ADA57748F1469F89FA0E8E411A780BC9A0784902DF7476` |
| `BinOutput__Quest__10008.json` | `C99C5252E797AA29CC8B277999456A4EA24AF967AA5AEAE4408840B68790614F` |
| `BinOutput__Quest__10009.json` | `1AC752A9A9A1297BC0C8BA4CC21BDFCE3EFE1B2885FAA026FAF3C39CD0AD3E0B` |
| `BinOutput__Quest__10010.json` | `A68DF7BCDB9C8AD8B43FC075217E506B8009E91F670D036D01D70ED934147E7C` |
| `BinOutput__Quest__70074.json` | `99A2DDB2AAFC90EBD8C4DDB0F3408FD4E2B847127760460F18BC96D233A73E2A` |
| `BinOutput__Quest__70092.json` | `2F93093C428DF0431833C04BC7A845F9E087E83E37BAC16307039D0E0082E7AB` |
| `BinOutput__Quest__70096.json` | `2E155FFB0B185AF7CD98AEEEC18D368C24C1508B78838654C668712C6CBB25BD` |
| `BinOutput__Quest__76159.json` | `09E44ACCBE3280023E1FE5A2BEAC7AAA5689094E49AB0F0A246E1ADB07CD8F77` |

### Project Amber responses

Directory: `Project-Amber-v7.0-access`. Each filename is the response from the canonical API URL with the same chapter number.

| Archived filename | SHA-256 |
| --- | --- |
| `quest-chapter-2076.json` | `142A1AF5E7B08C68F831023E94A5A138F41709C4DFF84B9F009F2D2FE658D691` |
| `quest-chapter-2077.json` | `5CBE31B8C8082735DDE371659C656B602830A2C8F6C6530B3A7B390493735D82` |
| `quest-chapter-2078.json` | `63DBBDB59EB8DD5688B61F6172F1299761359DA2D11038A51C3186F4B995F6A8` |
| `quest-chapter-2079.json` | `9CB987B159B120C9023E56E4208CF7F6816D17DA7FA37AFE1F4DAC23D68A2D79` |
| `quest-chapter-2080.json` | `69EF67BCBF1EAE7F94174DFD1B73EA13F5B69F346FEC2B16B75B78F62D119C99` |
| `quest-chapter-2081.json` | `B71E1AB43513953635DE3635A1081E8A5649FCE1A5ADC8AE5A1D85F08323B6AF` |
| `quest-chapter-2082.json` | `1625673F9A727C94645C03B070D4C90C787D7774625E89CB31C420119CF7C95E` |
| `quest-chapter-2083.json` | `77B2797DE85ED35DE0234CC1E98321E7628F161412EC20CDB5349E0462464983` |
| `quest-chapter-10189.json` | `D307B96B1B03F3CB887FDF649BCC4C1660ADB06DF573EFD9B120DEF7A825F37E` |
| `quest-chapter-10239.json` | `4B64A4458CBE67EDF2F088AC41870889986BEF8B3F3CEAAD480736493134EE68` |
| `quest-chapter-10252.json` | `141B76B69A87B22EEB0C9C9F0DD245AFAB915D9471AEEC532F90FA3E61B2E648` |
| `quest-chapter-10253.json` | `D9E87D990F82A3874D54F64CE784D42BB5BD2323716E12C32AEF11E09FCC2B6D` |

### Public LunaGC resource cross-check

Directory: `girluh-LunaGC-Resources-1744949`

| File | SHA-256 | Exact approved-archive entry match |
| --- | --- | --- |
| `ChapterExcelConfigData.json` | `263FDD557DEA99767573ADF66F8411567F596D40C9EA2774E0EFF92505A49596` | yes |
| `QuestExcelConfigData.json` | `9F6FB28934BED619D0742C841EFA958B162D813100D6434F35022A4F49948636` | yes |
| `MainQuestExcelConfigData.json` | `851578A343B95CF3DC0D5069F9A881F8F9D8789B525CD435EBBA033D65D2F331` | yes |

## Exact next decision

Do not implement quest-derived Hexerei activation from these public sources. Obtain or designate one immutable GC 7.0 resource baseline that contains all 12 terminal `QuestExcelConfigData` rows **and** semantically named, LunaGC-loadable `BinOutput/Quest` payloads for all 12 main quests, then hash and validate it against the same chapter mappings before deciding activation/predicate/team semantics.

If no such baseline can be supplied, keep every owned Hexerei entry at `UNAVAILABLE_RESOURCE`; do not infer `ACTIVE` from Project Amber/Honey names, Dimbreath obfuscated payloads, avatar tags, or unconditionally materialized proud skills. Witch's Revelation / Lucid Revelations remains a separate namespace and was not analyzed or merged here.

## Verification boundary

Verified read-only:

- all 12 Project Amber chapter endpoints returned the expected main and terminal IDs;
- all 12 Dimbreath main metadata rows and all 12 immutable quest payload files exist;
- all 12 Dimbreath terminal IDs are absent from its quest Excel table and present numerically in the corresponding raw payload;
- Dimbreath chapter `endQuestId` mappings match the approved matrix;
- the three public LunaGC Excel files match the corresponding approved-archive entries byte-for-byte;
- all 12 public LunaGC quest payload paths are absent at the inspected commit;
- downloaded files remain outside the repository/runtime tree and have the hashes recorded above.

Not verified or performed: a semantic deobfuscation map for the Dimbreath BinOutput keys, complete terminal quest rows from any authoritative source, runtime loading, player quest state, database persistence behavior with these files, command output, gameplay, server/client behavior, build/Gradle/JAR/deployment, or Lucid Revelation mapping.
