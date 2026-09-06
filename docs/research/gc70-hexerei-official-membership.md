# GC 7.0 official Hexerei membership

Status date: 2026-08-24  
Scope: first-party public evidence for playable Hexerei classification through Genshin Impact Version 7.0  
Runtime status: research only; no server, client, build, resource extraction, packet capture, or gameplay action

## Conclusion

Official public notices identify **12 playable Hexerei characters through Version 7.0**:

| First classified | Character | Element | Official evidence |
| --- | --- | --- | --- |
| Luna III | Durin | Pyro | The Luna III update notice defines Witch's Homework as unlocking `Witch's Eve Rite` and transforming the corresponding character into a Hexerei character, then lists Durin under “Characters with Homework.” |
| Luna III | Venti | Anemo | Same Luna III list. |
| Luna III | Klee | Pyro | Same Luna III list. |
| Luna III | Albedo | Geo | Same Luna III list. |
| Luna III | Mona | Hydro | Same Luna III list. |
| Luna III | Fischl | Electro | Same Luna III list. |
| Luna III | Sucrose | Anemo | Same Luna III list. |
| Luna III | Razor | Electro | Same Luna III list. |
| Luna V | Varka | Anemo | The Luna V update notice states that Varka becomes a Hexerei character after completing the related Hexenzirkel quest. |
| Luna VII | Nicole | Pyro | The Luna VII update notice states that Nicole becomes a Hexerei character after completing Hexenzirkel-related quests. |
| Luna VII | Lohen | Cryo | Same Luna VII classification. |
| Luna VII | Prune | Anemo | Same Luna VII classification. |

Primary sources:

- [Official Luna III update details](https://www.hoyolab.com/article_pre/21389), effective 2025-12-03: defines the gameplay transformation and names the initial eight characters.
- [Official Luna V update details](https://www.hoyolab.com/article/43935248), effective 2026-02-25: adds Varka.
- [Official Luna VII update details](https://www.hoyolab.com/article/45096719), effective 2026-05-20: adds Nicole, Lohen, and Prune.
- [GI HoYoWiki Official gameplay research](https://www.hoyolab.com/article/43988424): corroborates the two-character team condition and character-specific quest requirement.

The official [Luna IV](https://www.hoyolab.com/article/43220659), [Luna VI](https://www.hoyolab.com/article_pre/22642), [Luna VIII](https://www.hoyolab.com/article_pre/18014398241022990), and [Version 7.0](https://www.hoyolab.com/article/46233468) update notices were checked for intervening or later additions. None classifies another playable character as Hexerei.

## Important exclusions

`Hexerei character` is a gameplay classification, not a synonym for lore membership in the Hexenzirkel.

- Do not add Alice, Barbeloth, Rhinedottir, Andersdotter, or another lore member unless gameplay/resource evidence explicitly classifies a playable avatar as Hexerei.
- Do not add Jahoda, Columbina, or Sandrone merely because they are story-adjacent to witches or tea parties; the reviewed official notices do not classify them as Hexerei through Version 7.0.
- Luna VIII's `Witch's Revelation` enhancement roster—Wriothesley, Yae Miko, Cyno, Yumemizuki Mizuki, Qiqi, Diona, and Beidou—is a separate system. The Luna VIII notice does not call them Hexerei characters.

## Matrix implications

The official wording makes two states distinct:

1. A character is eligible for the Hexerei transformation.
2. The player completes the character-specific quest/homework and unlocks the additional talent, after which the character becomes Hexerei.

The GC 7.0 matrix should therefore record both `official_hexerei_eligible` and `player_hexerei_unlock_state`. The approved resource inclusion rule remains `AvatarExcelConfigData.tags` containing `AVATAR_TAG_HEXENZIRKEL`, but that static tag should be cross-checked against the 12-character official roster and must not be assumed to prove per-player completion.

This creates a focused persistence question for later source analysis: whether LunaGC's unconditional tag-based team count is intended to represent eligibility, or whether it should also depend on a persisted Witch's Homework/additional-talent unlock. This is an evidence-backed candidate seam, not a confirmed defect.

## Planned second improvement roster

After the Hexerei roster, the project will cover the separate **Witch's Revelation** system from the official [Luna VIII update notice](https://www.hoyolab.com/article_pre/18014398241022990).

The notice calls the unlocked enhancements **Lucid Revelations** and lists these eligible characters:

- Wriothesley
- Yae Miko
- Cyno
- Yumemizuki Mizuki
- Qiqi
- Diona
- Beidou

User requirement: preserve this as a second roster rather than merging it into Hexerei, and represent each character's per-player Lucid Revelation unlock state in the server's unlock-state commands. The same command-level visibility is required for Witch's Homework / Hexerei activation state.

## Resource and protocol provenance status

The user designated the following local resource artifact as the project-authoritative A2 baseline:

- `C:\Games\Genshin\Client\HoYoPlay\games\LunaGC-Archives\LunaGC-Resources-7.0.zip`
- SHA-256: `72D4556747E4FBC23031C32B5A847C00C95BBD6D4965B342CA74D507D246F9BF`

Read-only upstream checks found:

- The maintainer-linked [girluh/LunaGC-Resources](https://github.com/girluh/LunaGC-Resources) repository is public, identifies itself as 7.0 resources, and its `main` ref resolved on 2026-08-24 to `1744949c800cc380cd16b5d1f092a94f06a820d2`. The local archive does not contain Git metadata, so its exact correspondence to that commit is not yet proven.
- The README credit link labeled `hk4e-protos` points to the former `kitkat-multiverse/genshin-protocol` GitLab project. The user confirmed that this repository was purged and is no longer accessible; its page redirects to sign-in, the public API returns 404, and read-only `git ls-remote` reports that the project cannot be found or accessed. Do not retry it as a live source. Consequently, the current hand-encoded `TeamHexenzirkelChangeNotify` fields remain without an accessible authoritative protocol source.

## Evidence boundary and next step

Official web evidence establishes the 12-character gameplay roster but does not establish avatar IDs, resource tags, ability/config predicates, packet fields, or persistence representation in the LunaGC snapshot.

Exact next step: parse the approved resource archive read-only, resolve tag-defined avatar IDs and character names, and reconcile that resource roster with the 12 official characters before tracing abilities, predicates, SGVs, modifiers, packets, and persistence.
