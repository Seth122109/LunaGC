# GC 7.0 protocol tooling: public-repository assessment

Status: documentation-only, web-page review on 2026-08-28. No repository was
cloned, no release/file was downloaded, and no tool, client, server, JAR,
configuration, runtime resource, player data, or process was changed.

## Scope and decision

This note assesses whether the listed public repositories document an exact
GC 7.0 route to a `nameTranslation`-like semantic mapping. A tool is relevant
only when its documented inputs can be pinned to the exact client build and
its output is either that mapping or independently verifiable equivalent
schema. No inspected repository demonstrates that result. CmdId tables and
generated `.proto` files are not a semantic-name mapping.

## Evidence from visible primary sources

| Repository | Stated purpose and visible inputs -> outputs | Version/mapping evidence | GC 7.0 assessment |
| --- | --- | --- | --- |
| [Simplxss/ProtoShift](https://github.com/Simplxss/ProtoShift) | A proto translator; README requires versioned `.proto` files and `cmdid.csv`, `cmdid.json`, or `packetIds.json`. Its visible [`proto/`](https://github.com/Simplxss/ProtoShift/tree/master/proto) directory has no corpus. | Consumes command-ID/packet-ID mappings; no visible `nameTranslation` input or output. README uses generic `v?.?.?` examples and says some features are broken for lack of protos. | Downstream-only possibility after an independently proven GC 7.0 proto corpus and IDs; not a recovery path. |
| [marin-m/pbtk](https://github.com/marin-m/pbtk) | Extracts protocol structures from Java runtimes, binaries with reflection metadata, and JsProtoUrl; emits readable `.proto`. It also documents replay/fuzzing functions. | No visible `nameTranslation` format or GC 7.0 claim; Java extraction is described as mostly for old APKs. | Generic feasibility lead only if the exact target exposes supported metadata. Do not run: replay/fuzzing and network functions are outside this scope. |
| [partypooperarchive/ProtobufDecoder](https://github.com/partypooperarchive/ProtobufDecoder) | Repository description states it dumps `.proto` files from assemblies; the visible [source tree](https://github.com/partypooperarchive/ProtobufDecoder/tree/master) is a small C# solution. | No visible mapping format, target-client version, or GC 7.0 evidence. | Historical lead only; sparse documentation cannot establish support for an obfuscated 7.0 input. |
| [nitrog0d/ProtoDumper](https://github.com/nitrog0d/ProtoDumper) | Dumps protobuf definitions from game assemblies; README requires an **unobfuscated** `Assembly-CSharp.dll` and can emit `.proto` or TypeScript. [`Program.cs`](https://github.com/nitrog0d/ProtoDumper/blob/main/Program.cs) exposes CmdId-enum output and deletion of an existing output folder unless opted out. | CmdId enum is not semantic name translation; no GC 7.0 support claim. | Not suitable before a verified unobfuscated exact-7.0 assembly exists; even then it is an extraction step, not a mapping proof. |
| [partypooperarchive/ObfProtoDecoder](https://github.com/partypooperarchive/ObfProtoDecoder) | Source describes a Unity protocol dumper for partially obfuscated assemblies. Its [`Program.cs`](https://github.com/partypooperarchive/ObfProtoDecoder/blob/master/ObfProtoDecoder/Program.cs) takes an INI-configured base class, CmdId name, and DLL, then writes protobuf or pb-inspector definitions. | No visible `nameTranslation` support; source includes a `config_2.8.0.ini` development marker, not a 7.0 support claim. | Closest conceptual extractor, but it needs separately proven 7.0 inputs/configuration and cannot itself establish exact semantic names. |
| [KingRainbow44/Grasscutter-Packets](https://github.com/KingRainbow44/Grasscutter-Packets) | Grasscutter browser packet visualizer, using Iridium. README says it supports only Grasscutter `unstable` as of commit `395f7bad`; output is visual/logged packet activity via plugin and configuration. | No `nameTranslation` input/output or client-version claim. | Not a mapping producer/consumer; runtime/plugin installation is prohibited here. |
| [Crepe-Inc/Iridium](https://github.com/Crepe-Inc/Iridium) | KCP packet sniffer/visualizer. Its README requires `packetIds.json` and `proto/`; it reads PCAP/proxy traffic and captures `.gcap`/optional JSON. | It consumes packet IDs and protos, not a documented semantic translation mapping; no GC 7.0 claim. | Analysis consumer after a known schema, not a recovery tool. Its proxy instructions require dispatch redirection and active traffic, outside scope. |
| [GrownNed/gidra](https://github.com/GrownNed/gidra) | Declared as a Genshin Impact proxy in [`pyproject.toml`](https://github.com/GrownNed/gidra/blob/master/pyproject.toml); source tree includes crypt, proxy, reader, and proto modules. | No visible README/mapping format or version support statement. | Insufficient evidence for mapping use; proxy operation is out of scope. |
| [MoonlightPS/Iridium-gidra](https://github.com/MoonlightPS/Iridium-gidra) | Forked client/server proxy and sniffer. README states captured packets are saved as `gidra/packet_dump` JSON objects containing packet ID, proto name, source, time, and decoded object. | No `nameTranslation` handling or GC 7.0 claim. | Observation/capture tooling only. It explicitly requires dispatch redirection and a patched `UserAssembly.dll`; do not use it against the retail client. |

## Evidence versus inference

The table's purpose, inputs, outputs, version statements, and source-layout
claims are direct public-repository evidence. “GC 7.0 assessment” is an
inference constrained by that evidence: none of the repositories proves an
exact-build mapping, and no derived proto/CmdId output may be treated as one.

## Risks and stop conditions

- Stop if an input is not pinned to the exact GC 7.0 client build and source
  provenance; do not borrow prior-version mappings, `.proto`, or CmdIds.
- Stop if a proposed name comes only from field shape, adjacent server code, or
  a heuristic match.
- Do not execute the dumpers against originals: ProtoDumper documents output
  deletion behavior; the others require potentially destructive or unverified
  local setup.
- Do not use the proxy/sniffer projects: their documented workflows require
  traffic interception, dispatch redirection, and in one case retail DLL
  patching, all outside the authorized read-only boundary.

## Recommended bounded next step

Do one further web/source review only: identify a primary-source artifact that
states the exact GC 7.0 mapping contract (file/schema, revision or hash, and
source-to-target name semantics). Then inspect the relevant candidate tool's
visible source only to determine whether it consumes that exact contract.
Stop if the artifact is absent or compatibility rests on an older version,
CmdId enum, or generated `.proto` names.

## Incremental operational-compatibility path

Full semantic recovery is not a prerequisite for improving local
client/server compatibility. Work by workflow and keep each result separate:

1. retain the checked-in descriptors and validate the smallest packet/state
   set that blocks the next local workflow;
2. record the message shape, opcode, direction, required fields, and observed
   state transition for that one workflow;
3. use exact-build metadata or descriptors only to constrain structural
   candidates, never to invent semantic names;
4. maintain a compatibility matrix of working workflows, missing handlers,
   schema uncertainty, and game-logic defects; and
5. promote a candidate mapping only after it is one-to-one, reproducible, and
   validated against the exact build.

This permits targeted repairs for login, scene entry, character state, combat,
inventory, and quests without waiting for a full `nameTranslation` file. It
does not authorize packet guessing, retail-client modification, interception
of official services, or treating a successful single workflow as proof of
the entire protocol.

## Rollback and verification

Verification was limited to visible GitHub README, tree, and source pages
linked above. No external claim was promoted to exact-build proof, and no local
artifact was inspected or changed. Roll back by reverting this note.
