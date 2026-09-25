# Morning reader

A personal Android app for a 45-minute morning reading session. It serves books
as self-contained 5–15 minute blocks, mixed with long blog posts and short
videos. You approve one block at a time, and a block can start only if it fits in
the time left today.

## Build and install

Requires JDK 17 and the Android SDK (see `AGENTS.md` for paths) and the phone
reachable through `~/best/fun/adb-phone`.

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=~/Library/Android/sdk
./gradlew assembleDebug
~/best/fun/adb-phone install -r app/build/outputs/apk/debug/app-debug.apk
```

## Add books

Books come from Project Gutenberg, using the Standard Ebooks edition when there is
one. `./pack` builds a book pack on the Mac and pushes it to the phone. The app
loads new packs when it opens or when you tap refresh.

```bash
./pack search "tale of two cities"   # list Gutenberg matches with their ids
./pack add 98                        # build and push by Gutenberg id
./pack add "tale of two cities"      # or build the top search match
./pack starter                       # build and push the first shelf
./pack videos                        # add videos to packs built when YouTube quota ran out
./pack push                          # re-push every built pack
```

A pack costs about $0.02–0.65 in Gemini calls, roughly $0.13 per 100,000 words,
and takes about a minute. Finding videos uses 500 of YouTube's 10,000 free daily
quota units, so about 20 books a day can get videos.

## Blog feeds

The phone checks feeds every 12 hours and keeps posts of 5 minutes or more. To
load an OPML export (for example from Feedly), push it with
`./pack opml path/to/feeds.opml`, or use Settings → Import OPML on the phone.
Settings also adds and removes single feeds.

## Secrets

`.env` holds these variables. Both items are in the personal 1Password account
(`my.1password.com`), vault `Personal`, field `credential`.

| Variable | 1Password item |
| --- | --- |
| `OPENROUTER_API_KEY` | `OPENROUTER_API_KEY — 2026-09-morning-reader` |
| `YOUTUBE_API_KEY` | `YOUTUBE_API_KEY — 2026-09-morning-reader` (Google Cloud project `morning-reader-27191`, `alejoacelas@gmail.com`) |

```bash
for v in OPENROUTER_API_KEY YOUTUBE_API_KEY; do
  echo "$v=$(op item get "$v — 2026-09-morning-reader" --account my.1password.com --vault Personal --fields credential --reveal)"
done > .env
```

The Literata font is under the SIL Open Font License (`licenses/literata-OFL.txt`).
