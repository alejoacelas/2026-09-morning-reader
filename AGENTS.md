# Morning reader

An Android app that replaces a morning feed of Twitter and blogs with a finite
session of reading blocks. It explores whether books can compete with feeds when
they are served as short, self-contained passages under a daily time budget.

## How it works

- A **block** is a passage of about 5–15 minutes from a book, a blog post of 5
  minutes or more, or a video of 10 minutes or less. Short stories and poems stay
  whole, so a block is one or more whole stories or a few whole poems.
- The home screen is today's session: blocks from several books and blogs, each
  with a one-line hook and an estimated time. A block can start only if time used
  today plus its estimate fits the daily budget (45 minutes by default). The block
  in progress can always be finished.
- Estimates use the reader's measured words per minute.
- Selecting text and tapping **Explain** defines 1–2 words or explains a longer
  passage, in the language of the text.
- Each block and book has Spotify AI Playlist prompts. The app copies one and opens
  Spotify, since Spotify has no API for AI Playlists.

## Layout

- `app/` — the Android app (Kotlin, Jetpack Compose). It fetches blog feeds and
  calls the model itself, so mornings don't need the Mac.
- `packer/` and `./pack` — Mac-side book preparation: Gutenberg search, Standard
  Ebooks edition when one exists, Gemini segmentation, YouTube picks, push to phone.
- `docs/book-pack.md` — the book pack format both sides share.
- `cache/` and `packs/` are ignored. `cache/` holds downloads and the user's
  private feed and reading lists; never commit it.

## Build and install

Connect with `~/best/fun/adb-phone`. Set `JAVA_HOME` to
`/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home` and `ANDROID_HOME`
to `~/Library/Android/sdk`, run `./gradlew assembleDebug`, then
`~/best/fun/adb-phone install -r app/build/outputs/apk/debug/app-debug.apk`.
The README covers adding books and feeds.

## Secrets

Load keys into the ignored `.env` as the README shows, from the personal
1Password account (`my.1password.com`), vault `Personal`, field `credential`:

- `OPENROUTER_API_KEY` — item `OPENROUTER_API_KEY — 2026-09-morning-reader`
- `YOUTUBE_API_KEY` — item `YOUTUBE_API_KEY — 2026-09-morning-reader`
  (Google Cloud project `morning-reader-27191`, account `alejoacelas@gmail.com`)

The OpenRouter key is compiled into the APK, so never publish a built APK. The
model is `google/gemini-3.8-flash`.
