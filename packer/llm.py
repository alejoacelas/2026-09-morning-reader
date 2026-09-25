"""Gemini through OpenRouter, returning parsed JSON."""

import json
import os
import re
import time
from pathlib import Path

import httpx

MODEL = "google/gemini-3.8-flash"
ROOT = Path(__file__).resolve().parent.parent


def _env(name: str) -> str:
    if os.environ.get(name):
        return os.environ[name]
    for line in (ROOT / ".env").read_text().splitlines():
        key, _, value = line.partition("=")
        if key.strip() == name:
            return value.strip()
    raise RuntimeError(f"{name} missing from environment and .env (see README)")


class Usage:
    cost = 0.0
    prompt_tokens = 0
    completion_tokens = 0


def ask_json(prompt: str, max_tokens: int = 60000, effort: str = "low") -> dict:
    """Send one prompt and parse the JSON object in the reply. Retries transient failures."""
    body = {
        "model": MODEL,
        "messages": [{"role": "user", "content": prompt}],
        "response_format": {"type": "json_object"},
        "reasoning": {"effort": effort},
        "max_tokens": max_tokens,
        "usage": {"include": True},
    }
    headers = {"Authorization": f"Bearer {_env('OPENROUTER_API_KEY')}", "X-Title": "morning-reader"}
    last_error: Exception | None = None
    for attempt in range(4):
        try:
            resp = httpx.post("https://openrouter.ai/api/v1/chat/completions", json=body,
                              headers=headers, timeout=600)
            resp.raise_for_status()
            data = resp.json()
            usage = data.get("usage") or {}
            Usage.cost += usage.get("cost") or 0
            Usage.prompt_tokens += usage.get("prompt_tokens") or 0
            Usage.completion_tokens += usage.get("completion_tokens") or 0
            content = data["choices"][0]["message"]["content"] or ""
            content = re.sub(r"^```(?:json)?\s*|\s*```$", "", content.strip())
            return json.loads(content)
        except (httpx.HTTPError, json.JSONDecodeError, KeyError) as err:
            last_error = err
            time.sleep(5 * (attempt + 1))
    raise RuntimeError(f"Gemini call failed after retries: {last_error}")
