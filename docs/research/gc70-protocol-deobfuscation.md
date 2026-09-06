# GC 7.0 protocol deobfuscation: evidence and recovery plan

Status: read-only source and local-input assessment. No generated Java, wire
fields, opcodes, runtime artifacts, protocol generation, client files, JARs,
configuration, player data, or processes were changed.

## Result

The checkout contains 1,905 tracked generated protobuf outer classes under
`src/generated/main/java/emu/grasscutter/net/proto`. Of those, 453 have
all-uppercase *identifier* prefixes (for example,
`ACDCLDJFDFKOuterClass`). Their generated descriptors and source-file comments
retain those obfuscated names, so they cannot establish a semantic replacement.

Only three raw `.proto` inputs are checked in:

- `AnimatorParameterValueInfo.proto`
- `EvtAnimatorParameterInfo.proto`
- `ServerGlobalValueChangeNotify.proto`

They do not provide the missing definitions for the obfuscated generated
classes. The maintainer-linked source was previously confirmed unavailable;
this inventory does not retry it.

## Exhausted local evidence

The available local sources were searched for every one of the 453 obfuscated
outer-class identifiers, excluding the generated files being classified:

- `LunaGC-7.0.0/LunaGC-7.0.0`
- `LunaGC-Archives`
- `LunaGC-Web-Research`

The sibling `LunaGC-7.0.0` checkout contains the same 1,905 outer classes and
453 all-uppercase identifiers, with only three raw `.proto` files. It has no
semantic mapping. The archives and research artifacts contain no matching
protocol schema, descriptor, opcode-to-message map, alias table, `dump.cs`, or
`nameTranslation` input. Therefore no further local identifier can be renamed
without inventing a name.

## Handwritten server source

One commented reference was recoverable with certainty:

| Obfuscated generated identifier | Semantic identity | Evidence |
| --- | --- | --- |
| `ACDCLDJFDFKOuterClass.ACDCLDJFDFK` | `PlayerEnterMapLayerNotify` payload | It appears only in `HandlerPlayerEnterMapLayerNotify`, whose registered opcode is `PacketOpcodes.PlayerEnterMapLayerNotify`; the adjacent diagnostic calls `getLayerId()`. |

The handler comment now states the semantic packet identity without retaining the
unreadable generated type. Its runtime behavior remains unchanged: the handler
does not parse or act on the payload.

## Required evidence for further recovery

The primary recovery artifact is a version-matched GC 7.0 `nameTranslation`
mapping. The claim that it is used to recover meaningful generated names remains
a supplied research finding; this assessment did not locate a `nameTranslation`
message, file, author, or permalink in Discord. The supplied finding states
that mappings must not be borrowed across client versions. Authoritative GC 7.0
`.proto` definitions, or an immutable mapping of message names and field numbers
for this exact client build, are equivalent evidence.

Read-only Discord observations on 2026-08-28 independently support the
version-matching constraint, but not the existence of a GC 7.0 mapping. In
[`#development`](https://discord.com/channels/965284035985305680/965619953900326973),
on 2026-07-30, `jiyu` stated that a field matching version 6.5's `CMDID` was
not included in 6.6; `Izikiwi` then withdrew the assumption that a `nahida`
proto set was fully updated to 6.6. The same thread included a 6.5
`CombineDataNotify` snippet and a report that its resolution was incorrect for
the 6.6 target. This is community discussion, not authoritative protocol proof,
but it is direct evidence against cross-version reuse.

The Development category was checked read-only: `#development` had the above
history; `#development-general`, `#development-zh`, and `#plugin-dev` had no
loaded history in the inspected view. Discord's search focus was unreliable, so
no search query was completed and no message was sent after the user's removal
of the earlier accidental post. No files were downloaded.

The same `#development` history provides limited workflow leads for an
independent, exact-build recovery effort. Participants described client-binary
analysis as the route when a matching proto is unavailable, and the 6.6 thread
shows the useful validation unit: one message's command ID, field number, and
field shape, checked against that exact build. It does not provide a Genshin
IL2CPP toolchain, `dump.cs`, mapping method, or reusable source artifact.
Treat general comments about Ghidra/IDA as unrelated platform discussion, not a
validated GC 7.0 procedure.

The channel also contains a participant's account of takedowns affecting
protocol/resource and reverse-engineering repositories. It is an unverified
community report, not legal advice, but supports the existing scope boundary:
keep any future investigation private, read-only, provenance-recorded, and
separate from distribution or publication decisions.

A name inferred from a nearby handler, a prior game version, or field shape is
insufficient. It can silently misidentify a packet and make a later regeneration
appear compatible while changing its wire contract.

The historical Grasscutter commit
`bba7addf222a2ad1e0dfba3c084331516b59c6ef` is not available in this local Git
object database. Per the supplied historical finding, it added obfuscated
generated definitions only; it is not a name mapping and supplies no recovery
evidence.

## Locally viable recovery path

The only locally viable next investigation is a read-only feasibility assessment
of the installed GC 7.0 client's IL2CPP inputs:

- Two byte-identical-sized `global-metadata.dat` candidates are present under
  `GenshinImpact_Data/Managed/Metadata/` and
  `GenshinImpact_Data/Native/Data/Metadata/` (80,479,832 bytes each).
- No local `dump.cs`, `GameAssembly.dll`, or `nameTranslation` input was found
  in the assessed game, repository, archive, or research locations.
- A future approved analysis may determine whether the exact matching IL2CPP
  binary and metadata can be identified without altering the retail install,
  then derive a proto-like structural representation from a read-only `dump.cs`
  or equivalent output. Deterministic matching against the generated descriptor
  structure may then produce *candidate* semantic names.

That path is feasibility research, not a deobfuscator implementation. It does
not establish authoritative names by itself; any candidates require recorded
structural evidence and an independently version-matched translation or schema
before generated-source rename work.

## Safe milestones and stop conditions

| Milestone | Required evidence and verification | Stop condition |
| --- | --- | --- |
| 1. Pin the target build | Read-only record of client build identity, metadata hash, and matching IL2CPP binary availability. | Build identity or matching binary is unavailable or ambiguous. |
| 2. Establish inputs | Version-matched `nameTranslation`, or read-only metadata plus an equivalent type dump whose provenance and version match are recorded. | Inputs come from another version, lack provenance, or require retail-client modification. |
| 3. Normalize structures | Reproducible conversion to message/type, field-number, wire-type, nesting, and enum-shape records; retain raw hashes and tool/version logs. | Conversion is lossy, nondeterministic, or cannot preserve field-number evidence. |
| 4. Match deterministically | One-to-one structural matches with scores, collisions, unmatched entries, and reproducible inputs; no source edits. | Any proposed rename depends on a heuristic tie-breaker, nearby handler, or prior-version name. |
| 5. Review recovery set | Independent review of exact-build evidence and a dry-run mapping report. | Mapping is incomplete, conflicting, or lacks authoritative confirmation. |
| 6. Separate implementation approval | Explicitly approved, reversible source-only rename plan and compile verification in a clean worktree. | Approval is absent, generated files would be edited prematurely, or any runtime/client action is needed. |

Explicit stop conditions apply throughout: do not contact official services;
do not launch a client, server, or tool against the game; do not modify the
retail installation, generated protocol files, runtime resources, JARs,
configuration, player data, or server processes. Stop and report rather than
infer names if exact-build provenance, a matching binary, deterministic
structure, or authoritative translation evidence is missing.

## Scope, verification, and rollback

Scope is documentation and read-only evidence assessment only. The plan may
produce inventories, hashes, and candidate-match reports, but no protocol
rewrite. A future mapping is acceptable only when its inputs, tool versions,
hashes, structural-match results, collisions, and unresolved identifiers are
recorded and reproducible from read-only copies or source locations.

This assessment verified the generated-class and raw-proto inventory, the
sibling-checkout parity, the absence of local mapping/dump inputs in the named
research locations, the two metadata candidates, and absence of the cited
historical commit from the local object database. It did not parse metadata,
inspect a client binary, obtain a version-matched mapping, or validate protocol
semantics.

Documentation changes roll back by reverting this document. The earlier
handwritten handler-comment change rolls back independently by reverting
`HandlerPlayerEnterMapLayerNotify.java`. No generated file, build output, JAR,
runtime resource, client installation, configuration, player data, or process
was modified by this assessment.

## Execution chunks and status

This execution remains documentation and read-only feasibility work. Each chunk
must be recorded here before the next begins; no chunk authorizes deobfuscator
implementation, generated-source edits, or any client/server process launch.

| Chunk | Scope | Status | Evidence / next boundary |
| --- | --- | --- | --- |
| 1. Lock the inventory | Recheck generated-class, raw-proto, sibling, and local-mapping facts. | **Complete** | 1,905 outer classes, 453 uppercase identifier prefixes, three raw `.proto` files; no local mapping, `dump.cs`, or `GameAssembly.dll` in the assessed locations. |
| 2. Pin local build inputs | Read-only hashes and file-version metadata for client package and metadata candidates; independently assess matching IL2CPP-binary availability. | **Complete** | Both metadata paths hash to `D361FF11F6CA123F4126004DB13CDF1956E00D95231F84C1E5437F560E79128B`; `pkg_version` hashes to `401D56E4E7CA43FCA1F5624F2BBF6F03674005D293B739C6B16E03E36413B336`. The executable's `2017.4.30.0` file version is Unity metadata, not proof of the GC client build. |
| 3. Make feasibility decision | Compare the metadata evidence with any exact matching local IL2CPP binary and record provenance, or stop. | **Complete — stopped** | The executable is a credible co-located candidate but exact binary-to-metadata pairing and exact-build identity cannot be demonstrated from filesystem evidence alone. Stop before conversion or matching. |
| 4. Recovery prerequisites | Obtain a version-matched `nameTranslation` or authoritative schema and, only with separate approval, normalize/match structures. | **Blocked** | No approved or available mapping/schema; do not begin implementation. |

### Chunk 2–3 evidence

Read-only inspection found `GenshinImpact.exe` as the only plausible local code
binary: 430,973,352 bytes, SHA-256
`A1A23CB76D941DF28C5156CA3152FA49421EC98842221B7D71633D42EE76CA45`,
modified `2026-08-21T10:16:52Z`. It has a valid COGNOSPHERE signature
(signer thumbprint `9EF2E501861D2FB723B62D20B7ED9ACBD1574A0D`) and generic Unity
file/product version `2017.4.30.0`. Both metadata candidates were modified
within roughly one minute of that executable, but this co-location and timing
are not proof of an IL2CPP pairing.

No `GameAssembly.dll`, `UserAssembly.dll`, `UnityPlayer.dll`, `libil2cpp.dll`,
`dump.cs`, or `nameTranslation` was present anywhere below the retail game
directory. `mhypbase.dll` and `rtlbase.dll` are present but have no evidence
connecting them to the metadata as IL2CPP code containers. Consequently,
milestone 1 is conditionally evidenced but fails its exact-provenance gate;
chunks 2 and 3 are complete with a fail-closed stop, not an authorization to
inspect, convert, or match binary content.

### Continuation contract

The user has authorized bounded workers for these documented chunks and for
subsequent continuation tasks needed after a project context gate. Any such
task must inherit this document's scope and stop conditions, record the status
of its assigned chunk here (or in a clearly linked successor research record),
and use workers only for independent read-only evidence slices. The continuation
must stop rather than create an implementation task if authoritative
version-matched mapping evidence is still absent. This authorization does not
permit retail-client changes, process launches, generated-source changes, or
external-service interaction.
