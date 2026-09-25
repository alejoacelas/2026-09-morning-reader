"""Command line for building and pushing book packs.

  ./pack add "voyage of the beagle"   search Gutenberg, build the top match, push it
  ./pack add 944                      same, by Gutenberg id
  ./pack search "wealth of nations"   list matches without building
  ./pack starter                      build and push the starter shelf
  ./pack push                         push every built pack to the phone
  ./pack opml FILE                    push a feed list (OPML) for the app to import
"""

import argparse
import json
import subprocess
import sys
from pathlib import Path

from . import segment, sources, videos
from .llm import Usage

ROOT = Path(__file__).resolve().parent.parent
PACKS = ROOT / "packs"
PHONE_DIR = "/sdcard/Android/data/com.alejoacelas.morningreader/files"
ADB_PHONE = Path.home() / "best/fun/adb-phone"

# The first shelf, hand-picked from the user's to-read list. How to suggest further
# books is an open question (see DECISIONS.md).
STARTER = {
    145: "Middlemarch",
    10378: "Autobiography of John Stuart Mill",
    13507: "Cuentos de amor de locura y de muerte",
    72665: "Romancero gitano",
    84: "Frankenstein",
    1399: "Anna Karenina",
    7849: "The Trial",
    863: "The Mysterious Affair at Styles",
}
LANGUAGES = {"en": "English", "es": "Spanish", "fr": "French", "de": "German", "it": "Italian", "pt": "Portuguese"}


def build(query: str) -> Path:
    matches = sources.search(query)
    if not matches:
        sys.exit(f"No Gutenberg match for {query!r}")
    book = matches[0]
    epub, edition = sources.download(book)
    text = sources.extract(epub)
    paragraphs = text["paragraphs"]
    words = sum(len(p.split()) for p in paragraphs)
    print(f"{text['title']} by {text['author']} (#{book.gutenberg_id}, "
          f"{'Standard Ebooks' if edition == 'se' else 'Project Gutenberg'}): {words:,} words")
    cost_before = Usage.cost
    language = LANGUAGES.get(text["language"].split("-")[0].lower(), text["language"])
    seg = segment.segment(text["title"], text["author"], paragraphs, language=language)
    book_id = f"gutenberg-{book.gutenberg_id}"
    blocks = []
    for n, b in enumerate(seg["blocks"]):
        blocks.append({
            "id": f"{book_id}-b{n:03d}", "order": n, "title": b.get("title") or f"Part {n + 1}",
            "hook": b.get("hook") or "", "recap": b.get("recap") if b.get("entry_point") else None,
            "entry_point": bool(b.get("entry_point")), "words": b["words"],
            "paragraphs": paragraphs[b["start"]:b["end"]],
            "playlist_prompt": b.get("playlist_prompt") or "", "videos": [],
        })
    print(f"  {len(blocks)} blocks, {sum(b['entry_point'] for b in blocks)} entry points")
    picks = videos.find(text["title"], text["author"], seg.get("summary") or "",
                        seg.get("video_queries") or [], blocks, lang=text["language"].split("-")[0].lower())
    for pick in picks:
        index = pick.pop("block")
        if isinstance(index, int) and 0 <= index < len(blocks):
            blocks[index]["videos"].append(pick)
    print(f"  {len(picks)} videos")
    pack = {
        "version": 1, "id": book_id, "title": text["title"], "author": text["author"], "language": language,
        "year": seg.get("year"), "origin": seg.get("origin"),
        "edition": "Standard Ebooks" if edition == "se" else "Project Gutenberg",
        "source_url": f"https://www.gutenberg.org/ebooks/{book.gutenberg_id}",
        "summary": seg.get("summary") or "", "playlist_prompts": seg.get("playlist_prompts") or {},
        "videos": picks, "blocks": blocks,
    }
    PACKS.mkdir(exist_ok=True)
    path = PACKS / f"{book_id}.json"
    path.write_text(json.dumps(pack, ensure_ascii=False, indent=1))
    print(f"  wrote {path.relative_to(ROOT)} (Gemini cost ${Usage.cost - cost_before:.3f})")
    return path


def push(paths: list[Path], subdir: str = "packs") -> None:
    adb = [str(ADB_PHONE)]
    subprocess.run(adb + ["shell", "mkdir", "-p", f"{PHONE_DIR}/{subdir}"], check=True)
    for path in paths:
        subprocess.run(adb + ["push", str(path), f"{PHONE_DIR}/{subdir}/{path.name}"], check=True,
                       stdout=subprocess.DEVNULL)
        print(f"  pushed {path.name}")
    print("Open the app (or pull to refresh) to import.")


def main() -> None:
    parser = argparse.ArgumentParser(prog="pack", description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)
    add = sub.add_parser("add", help="build a pack and push it")
    add.add_argument("query", nargs="+")
    add.add_argument("--no-push", action="store_true")
    sub.add_parser("search", help="list Gutenberg matches").add_argument("query", nargs="+")
    starter = sub.add_parser("starter", help="build and push the starter shelf")
    starter.add_argument("--no-push", action="store_true")
    sub.add_parser("push", help="push all built packs")
    sub.add_parser("opml", help="push an OPML feed list").add_argument("file", type=Path)
    args = parser.parse_args()

    if args.command == "search":
        for b in sources.search(" ".join(args.query), limit=10):
            print(f"{b.gutenberg_id:>6}  {b.title}  —  {b.author}")
    elif args.command == "add":
        path = build(" ".join(args.query))
        if not args.no_push:
            push([path])
    elif args.command == "starter":
        paths = []
        for gid in STARTER:
            path = PACKS / f"gutenberg-{gid}.json"
            paths.append(path if path.exists() else build(str(gid)))
        print(f"Total Gemini cost this run: ${Usage.cost:.3f}")
        if not args.no_push:
            push(paths)
    elif args.command == "push":
        push(sorted(PACKS.glob("*.json")))
    elif args.command == "opml":
        push([args.file], subdir="import")


if __name__ == "__main__":
    main()
