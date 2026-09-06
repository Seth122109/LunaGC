#!/usr/bin/env python3
"""Report this Codex task's active input context from read-only local telemetry."""

from __future__ import annotations

import argparse
import json
import os
import re
import sqlite3
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Iterable


DEFAULT_WARNING = 110_000
DEFAULT_PREPARE = 130_000
DEFAULT_LIMIT = 150_000
STATE_RE = re.compile(r"^state_(\d+)\.sqlite$")
REQUIRED_THREAD_COLUMNS = {
    "id",
    "rollout_path",
    "created_at",
    "updated_at",
    "source",
    "model_provider",
    "title",
    "tokens_used",
}


@dataclass(frozen=True)
class TokenSample:
    timestamp: str | None
    input_tokens: int
    cached_input_tokens: int | None


def compact_tokens(value: int) -> str:
    return f"{value // 1_000}k" if value % 1_000 == 0 else f"{value:,}"


def rounded_thousand(value: int) -> int:
    return ((value + 500) // 1_000) * 1_000


def gate_for(
    active_input: int | None,
    warning: int = DEFAULT_WARNING,
    prepare: int = DEFAULT_PREPARE,
    limit: int = DEFAULT_LIMIT,
) -> str:
    if active_input is None:
        return "unknown"
    if active_input >= limit:
        return "route"
    if active_input >= prepare:
        return "prepare"
    if active_input >= warning:
        return "warning"
    return "normal"


def footer_for(active_input: int | None, limit: int = DEFAULT_LIMIT) -> str:
    active = "unknown" if active_input is None else compact_tokens(rounded_thousand(active_input))
    return f"Context: ~{active} / {compact_tokens(limit)} project limit"


def resolve_codex_home(explicit: str | None) -> Path:
    if explicit:
        return Path(explicit).expanduser().resolve()
    configured = os.environ.get("CODEX_HOME")
    return Path(configured).expanduser().resolve() if configured else (Path.home() / ".codex").resolve()


def discover_state_db(codex_home: Path) -> tuple[Path, int]:
    candidates: list[tuple[int, int, Path]] = []
    for path in codex_home.glob("state_*.sqlite"):
        match = STATE_RE.match(path.name)
        if path.is_file() and match:
            candidates.append((int(match.group(1)), path.stat().st_mtime_ns, path))
    if not candidates:
        raise RuntimeError(f"No state_*.sqlite found directly under {codex_home}")
    suffix, _, selected = max(candidates, key=lambda item: (item[0], item[1]))
    return selected.resolve(), suffix


def open_read_only(path: Path) -> sqlite3.Connection:
    connection = sqlite3.connect(f"{path.as_uri()}?mode=ro", uri=True)
    connection.row_factory = sqlite3.Row
    return connection


def validate_threads_schema(connection: sqlite3.Connection) -> None:
    columns = {row["name"] for row in connection.execute("PRAGMA table_info(threads)")}
    missing = REQUIRED_THREAD_COLUMNS - columns
    if missing:
        raise RuntimeError(
            "Selected state database has an incompatible threads schema; "
            f"missing: {', '.join(sorted(missing))}"
        )


def resolve_thread(connection: sqlite3.Connection, thread_id: str) -> tuple[Path, int]:
    row = connection.execute(
        "SELECT rollout_path, tokens_used FROM threads WHERE id = ?", (thread_id,)
    ).fetchone()
    if row is None:
        raise RuntimeError(f"Current thread ID was not found in selected state DB: {thread_id}")
    rollout_path = Path(row["rollout_path"]).expanduser().resolve()
    if not rollout_path.is_file():
        raise RuntimeError(f"Current thread rollout does not exist: {rollout_path}")
    return rollout_path, int(row["tokens_used"])


def iter_rollout_records(path: Path) -> Iterable[dict[str, Any]]:
    with path.open("r", encoding="utf-8") as handle:
        for line_number, line in enumerate(handle, 1):
            try:
                record = json.loads(line)
            except json.JSONDecodeError as error:
                raise RuntimeError(f"Invalid rollout JSON at {path}:{line_number}: {error}") from error
            if isinstance(record, dict):
                yield record


def active_sample(records: Iterable[dict[str, Any]]) -> tuple[TokenSample | None, bool, int]:
    latest: TokenSample | None = None
    compaction_pending = False
    compaction_markers = 0
    for record in records:
        record_type = record.get("type")
        payload = record.get("payload")
        payload = payload if isinstance(payload, dict) else {}
        payload_type = payload.get("type")
        if record_type == "compacted" or (
            record_type == "event_msg" and payload_type == "context_compacted"
        ):
            latest = None
            compaction_pending = True
            compaction_markers += 1
            continue
        if record_type != "event_msg" or payload_type != "token_count":
            continue
        info = payload.get("info")
        info = info if isinstance(info, dict) else {}
        usage = info.get("last_token_usage")
        usage = usage if isinstance(usage, dict) else {}
        input_tokens = usage.get("input_tokens")
        if not isinstance(input_tokens, int) or input_tokens <= 0:
            continue
        cached = usage.get("cached_input_tokens")
        latest = TokenSample(
            timestamp=record.get("timestamp"),
            input_tokens=input_tokens,
            cached_input_tokens=cached if isinstance(cached, int) else None,
        )
        compaction_pending = False
    return latest, compaction_pending, compaction_markers


def snapshot(args: argparse.Namespace) -> dict[str, Any]:
    thread_id = args.thread_id or os.environ.get("CODEX_THREAD_ID")
    if not thread_id:
        raise RuntimeError(
            "Current thread ID is unavailable. Set CODEX_THREAD_ID or pass --thread-id; "
            "never infer the task from its title."
        )
    codex_home = resolve_codex_home(args.codex_home)
    state_db, state_suffix = discover_state_db(codex_home)
    with open_read_only(state_db) as connection:
        validate_threads_schema(connection)
        rollout_path, cumulative_tokens = resolve_thread(connection, thread_id)
    sample, compaction_pending, marker_count = active_sample(iter_rollout_records(rollout_path))
    active_input = None if compaction_pending or sample is None else sample.input_tokens
    cached = None if sample is None else sample.cached_input_tokens
    return {
        "codex_home": str(codex_home),
        "state_db": str(state_db),
        "state_db_discovered_dynamically": True,
        "state_db_numeric_suffix": state_suffix,
        "threads_schema_validated": True,
        "thread_id": thread_id,
        "rollout_path": str(rollout_path),
        "rollout_queried_from_current_thread_row": True,
        "timestamp": None if sample is None else sample.timestamp,
        "active_input_tokens": active_input,
        "rounded_active_input_tokens": (
            None if active_input is None else rounded_thousand(active_input)
        ),
        "cached_input_tokens_subset": cached,
        "cached_input_was_added": False,
        "cumulative_thread_tokens_not_used_for_gate": cumulative_tokens,
        "compaction_markers_seen": marker_count,
        "compaction_pending": compaction_pending,
        "warning_threshold": args.warning,
        "prepare_threshold": args.prepare,
        "project_limit": args.limit,
        "gate": gate_for(active_input, args.warning, args.prepare, args.limit),
        "footer": footer_for(active_input, args.limit),
    }


def self_test() -> None:
    assert rounded_thousand(109_999) == 110_000
    assert gate_for(109_999) == "normal"
    assert gate_for(110_000) == "warning"
    assert gate_for(129_999) == "warning"
    assert gate_for(130_000) == "prepare"
    assert gate_for(149_999) == "prepare"
    assert gate_for(150_000) == "route"
    assert gate_for(None) == "unknown"
    assert footer_for(109_999) == "Context: ~110k / 150k project limit"
    records = [
        {"type": "event_msg", "payload": {"type": "token_count", "info": {"last_token_usage": {"input_tokens": 244_548, "cached_input_tokens": 200_000}}}},
        {"type": "compacted"},
        {"type": "event_msg", "payload": {"type": "context_compacted"}},
        {"type": "event_msg", "payload": {"type": "token_count", "info": {"last_token_usage": {"input_tokens": 0, "cached_input_tokens": 0}}}},
        {"type": "event_msg", "payload": {"type": "token_count", "info": {"last_token_usage": {"input_tokens": 32_813, "cached_input_tokens": 20_000}}}},
    ]
    sample, pending, markers = active_sample(records)
    assert sample is not None and sample.input_tokens == 32_813 and not pending and markers == 2
    sample, pending, markers = active_sample(records[:-1])
    assert sample is None and pending and markers == 2
    print(
        "PASS: raw thresholds, display rounding, duplicate compaction markers, "
        "transitional zero, post-compaction baseline, and unknown state"
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Report this Codex task's active input context from local telemetry."
    )
    parser.add_argument("--codex-home")
    parser.add_argument("--thread-id")
    parser.add_argument("--warning", type=int, default=DEFAULT_WARNING)
    parser.add_argument("--prepare", type=int, default=DEFAULT_PREPARE)
    parser.add_argument("--limit", type=int, default=DEFAULT_LIMIT)
    parser.add_argument("--json", action="store_true", dest="as_json")
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()
    if not (0 < args.warning <= args.prepare <= args.limit):
        parser.error("Thresholds must satisfy 0 < warning <= prepare <= limit")
    return args


def main() -> int:
    args = parse_args()
    if args.self_test:
        self_test()
        return 0
    try:
        result = snapshot(args)
    except (OSError, RuntimeError, sqlite3.Error) as error:
        print(f"active-context error: {error}", file=sys.stderr)
        return 2
    print(json.dumps(result, indent=2, sort_keys=True) if args.as_json else result["footer"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
