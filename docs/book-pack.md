# Book pack format

A book pack is one JSON file, `<book-id>.json`, pushed to the phone at
`/sdcard/Android/data/<app package>/files/packs/`. The app imports every pack
it finds there on launch.

```json
{
  "version": 1,
  "id": "gutenberg-2680",
  "title": "Meditations",
  "author": "Marcus Aurelius",
  "language": "English",
  "edition": "Standard Ebooks",
  "year": 180,
  "origin": "Rome",
  "source_url": "https://www.gutenberg.org/ebooks/2680",
  "summary": "Two sentences on what the book is and why it's worth dipping into.",
  "playlist_prompts": {"thematic": "…", "geographic": "…", "mood": "…"},
  "video_queries": ["searches used to find the videos"],
  "videos": [{"youtube_id": "abc123", "title": "…", "channel": "…", "seconds": 540, "why": "One line."}],
  "blocks": [
    {
      "id": "gutenberg-2680-b07",
      "order": 7,
      "title": "Book IV, 1–18",
      "hook": "One line on why this passage is worth ten minutes.",
      "recap": "Context a reader needs when starting here, or null.",
      "entry_point": true,
      "words": 1850,
      "paragraphs": ["Plain text of each paragraph, in order."],
      "playlist_prompt": "A Spotify AI Playlist prompt matched to this passage's mood, theme and place.",
      "videos": [
        {"youtube_id": "abc123", "title": "…", "channel": "…", "seconds": 540, "why": "One line."}
      ]
    }
  ]
}
```

- `blocks` covers the whole book in reading order; `order` is its position.
- `entry_point` marks blocks worth starting on cold. Those get a `recap`.
- Paragraph text is plain Unicode. Emphasis and footnotes are dropped. Headings
  start with `## `; verse keeps its line breaks as `\n`.
- Hooks, recaps, summaries and playlist prompts are in the book's `language`.
- Book-level `videos` lists every pick; a block's `videos` holds the ones matched to it.
- Videos are 600 seconds or less.
