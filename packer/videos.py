"""Find a few short, good YouTube videos about a book."""

import re

import httpx

from .llm import _env, ask_json

MAX_SECONDS = 600


class QuotaError(RuntimeError):
    pass
API = "https://www.googleapis.com/youtube/v3"

SCREEN_PROMPT = """A reader is dipping into "{title}" by {author}. {summary}
Pick up to 4 of these YouTube videos that would genuinely enrich their reading: accurate,
well made, about the book, its author, its world or its ideas. Reject clickbait, reaction
videos, AI-generated slideshows, audiobook excerpts and anything off-topic. It is fine to
pick none.

For each pick, give "why": one line on what the viewer will get from it, and "block":
the index of the block it fits best, or null if it suits the book in general.

Blocks:
{blocks}

Videos:
{videos}

Reply with JSON only: {{"picks": [{{"id": "", "why": "", "block": null}}]}}
"""


def _seconds(iso: str) -> int:
    parts = re.fullmatch(r"P(?:(\d+)D)?T?(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?", iso or "")
    if not parts:
        return 0
    d, h, m, s = (int(x or 0) for x in parts.groups())
    return ((d * 24 + h) * 60 + m) * 60 + s


def find(title: str, author: str, summary: str, queries: list[str], blocks: list[dict],
         lang: str = "en") -> list[dict]:
    key = _env("YOUTUBE_API_KEY")
    ids: list[str] = []
    # Each search costs 100 of the 10,000 daily quota units: four medium (4-20 min)
    # searches plus one short (under 4 min) keeps a book at 500.
    searches = [(q, "medium", 8) for q in queries[:4]] + [(q, "short", 4) for q in queries[:1]]
    for query, duration, count in searches:
        resp = httpx.get(f"{API}/search", timeout=30, params={
            "part": "id", "q": query, "type": "video", "videoDuration": duration,
            "maxResults": count, "relevanceLanguage": lang, "key": key})
        if resp.status_code in (403, 429):
            raise QuotaError(f"YouTube search refused ({resp.status_code}); daily quota is likely used up")
        resp.raise_for_status()
        ids += [item["id"]["videoId"] for item in resp.json().get("items", [])]
    ids = list(dict.fromkeys(ids))
    candidates = []
    for i in range(0, len(ids), 50):
        resp = httpx.get(f"{API}/videos", timeout=30, params={
            "part": "snippet,contentDetails,statistics", "id": ",".join(ids[i:i + 50]), "key": key})
        resp.raise_for_status()
        for item in resp.json().get("items", []):
            seconds = _seconds(item["contentDetails"].get("duration"))
            if 90 <= seconds <= MAX_SECONDS:
                candidates.append({
                    "id": item["id"], "title": item["snippet"]["title"],
                    "channel": item["snippet"]["channelTitle"], "seconds": seconds,
                    "views": int(item.get("statistics", {}).get("viewCount", 0)),
                    "description": item["snippet"].get("description", "")[:200].replace("\n", " "),
                })
    if not candidates:
        return []
    block_lines = "\n".join(f"{i}: {b['title']} — {b.get('hook', '')}" for i, b in enumerate(blocks))
    video_lines = "\n".join(
        f"{c['id']} | {c['title']} | {c['channel']} | {c['seconds'] // 60} min | {c['views']} views | {c['description']}"
        for c in candidates)
    reply = ask_json(SCREEN_PROMPT.format(title=title, author=author, summary=summary or "",
                                          blocks=block_lines, videos=video_lines), max_tokens=4000)
    by_id = {c["id"]: c for c in candidates}
    picks = []
    for pick in reply.get("picks", [])[:4]:
        video = by_id.get(pick.get("id"))
        if video:
            picks.append({"youtube_id": video["id"], "title": video["title"], "channel": video["channel"],
                          "seconds": video["seconds"], "why": pick.get("why", ""), "block": pick.get("block")})
    return picks
