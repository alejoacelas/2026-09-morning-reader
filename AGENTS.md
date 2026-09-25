# Morning reader

An Android app that replaces a morning feed of Twitter and blogs with a finite
session of reading blocks. It explores whether books can compete with feeds when
they are served as short, self-contained passages under a daily time budget.

## How it works

- A **block** is a self-contained passage of about 5–15 minutes: a book section,
  a blog post, or a video of 10 minutes or less.
- The home screen is today's session: a list of blocks, each with a one-line hook
  and an estimated time. You approve blocks one at a time. A block can be
  approved only if the time already spent plus its estimate stays within the
  daily budget (45 minutes by default). The current block can always be finished.
- Reading-time estimates use the reader's measured words per minute.
- Highlighting text asks the model to define it (1–2 words) or explain it (longer),
  using the surrounding paragraph as context.
- Each block carries a Spotify AI Playlist prompt. The app copies it and opens
  Spotify, since Spotify has no public API for AI Playlists.

## Layout

- `app/` — the Android app (Kotlin, Jetpack Compose). It fetches blog feeds and
  calls the model itself, so mornings don't depend on the Mac.
- `prep/` — Mac-side book preparation. It downloads a book from Project Gutenberg
  (Standard Ebooks edition when one exists), asks the model for entry points,
  break points, hooks, recaps, playlist prompts and videos, and writes a book pack.
- `docs/book-pack.md` — the book pack format both sides share.

## Build and install

Connect with `~/best/fun/adb-phone`. Build with `./gradlew assembleDebug` inside
`app/`, then `~/best/fun/adb-phone install -r <apk>`. Push book packs with
`prep/push` (see its usage).

## Secrets

Load keys into the ignored `.env` with `op`, from the personal 1Password account
(`my.1password.com`), vault `Personal`, field `credential`:

- `OPENROUTER_API_KEY` — item `OPENROUTER_API_KEY — 2026-09-morning-reader`
- `YOUTUBE_API_KEY` — item `YOUTUBE_API_KEY — 2026-09-morning-reader`
  (Google Cloud project `morning-reader-27253`, account `alejoacelas@gmail.com`)

The model is `google/gemini-3.8-flash` on OpenRouter. Pass
`"reasoning": {"effort": "minimal"}` for highlight lookups to keep them fast.
