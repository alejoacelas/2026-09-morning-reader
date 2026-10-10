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
- Selecting text offers **Explain** (defines 1–2 words, explains longer passages),
  **Ask** (a typed question) and **Note**. Explain and Ask send the selection's
  paragraph and its neighbours as context and answer in the language of the text.
  Notes go to `files/exports/notes.jsonl` on the phone and are never shown in the app.
- Each block can list a few main characters, described only with what the book has
  revealed by then.
- Swiping a card right to left archives its whole book (or post) at the next reload.
- After a block, the reader can mark its book **keep going** (Today lists it first),
  **maybe later** (Today lists it last) or **done with this** (archived at once). The
  choice is optional; a book with none keeps its place in the daily shuffle. The
  book page shows and changes the same choice.
- The end of a block offers the best one or two pieces of **secondary material** not
  yet opened: videos, radio episodes, essays and biographies about the book or its
  author, found only in the trusted sources listed in `packer/trusted-sources.toml`
  and screened by Gemini. Videos of 10 minutes or less are also blocks on Today; longer
  material opens outside the session and doesn't count toward the budget.
- The top of Settings takes feature requests. Each is filed as a GitHub issue on this
  repository when sent; if that fails it stays on the phone and a background job
  retries once there's network.
- Each block and book has Spotify AI Playlist prompts. The app copies one and opens
  Spotify, since Spotify has no API for AI Playlists.

## Layout

- `app/` — the Android app (Kotlin, Jetpack Compose). It fetches blog feeds and
  calls the model itself, so mornings don't need the Mac.
- `packer/` and `./pack` — Mac-side book preparation: Gutenberg search, Standard
  Ebooks edition when one exists, Gemini segmentation, secondary material from
  trusted sources, push to phone.
- `packer/wishlist.py` reads a Chrome bookmarks folder as a wish list
  (`./pack wishlist`). The folder name is a setting in `cache/settings.json`; the
  bookmarks are personal, so everything derived from them stays in `cache/`.
- `docs/book-pack.md` — the book pack format both sides share.
- `cache/` and `packs/` are ignored. `cache/` holds downloads and the user's
  private feed and reading lists; never commit it.

## Build and install

Connect with `~/best/dotfiles/bin/adb-phone`. Set `JAVA_HOME` to
`/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home` and `ANDROID_HOME`
to `~/Library/Android/sdk`, run `./gradlew assembleDebug`, then
`~/best/dotfiles/bin/adb-phone install -r app/build/outputs/apk/debug/app-debug.apk`.
The README covers adding books and feeds.

## Secrets

`.env` (ignored) holds the keys. All are in the personal 1Password account
(`my.1password.com`), vault `Personal`, field `credential`.

| Variable | 1Password item (ID) |
| --- | --- |
| `OPENROUTER_API_KEY` | `OPENROUTER_API_KEY — 2026-09-morning-reader` (`7x77hjicvqnnhnj7bpa46vh6ya`) |
| `YOUTUBE_API_KEY` | `YOUTUBE_API_KEY — 2026-09-morning-reader` (`xwm7xqceh6je7wnnghhfqmpfue`; Google Cloud project `morning-reader-27191`, `alejoacelas@gmail.com`) |
| `GITHUB_ISSUES_TOKEN` | `GITHUB_ISSUES_TOKEN — 2026-09-morning-reader` (`n2bc52jki6ebzrstlw2hg25ure`; fine-grained token for this repository only, Issues read and write, expires 2027-10-09) |

Two items share each of the first two names, so fetch by ID. Rebuild `.env` with:

```bash
for pair in OPENROUTER_API_KEY:7x77hjicvqnnhnj7bpa46vh6ya YOUTUBE_API_KEY:xwm7xqceh6je7wnnghhfqmpfue \
            GITHUB_ISSUES_TOKEN:n2bc52jki6ebzrstlw2hg25ure; do
  echo "${pair%%:*}=$(op item get "${pair#*:}" --account my.1password.com --fields credential --reveal)"
done > .env
```

The OpenRouter key and the GitHub token are compiled into the APK, so never publish
a built APK. The model is `google/gemini-3.8-flash`. API spending is approved:
build packs and rerun model calls without asking about cost. OpenRouter tops up
automatically.
