# Morning reader

Turn a book you keep meaning to read into something you can start over breakfast:
a self-contained passage with a one-line hook and an estimated reading time.
This personal Android app mixes book passages, long blog posts and short videos
under a daily budget of 45 minutes, adjustable in Settings. A passage can start
only if its estimate fits the remaining budget; you can always finish one already
in progress. Estimates adapt to your reading speed.

Books are prepared once on your computer. After transfer, the phone can read them
without the computer or a network connection. New blog posts, highlight explanations
and videos need internet access. There is no hosted backend to operate.

## Before you start

This is a source-build prototype for someone comfortable with a terminal and an
Android development setup. Use your own device and API keys; each APK contains its
builder's OpenRouter key and must remain private. There is no app-wide reuse license
yet; the owner needs to choose one before this can be offered under an open-source
license. The bundled [Literata font license](licenses/literata-OFL.txt) covers the font.

The full book workflow needs two services:

| Service | What it does | What you provide |
| --- | --- | --- |
| [OpenRouter](https://openrouter.ai/settings/keys) | Segments books, writes hooks, screens video suggestions and explains selected text | An API key with credit; set a spending limit when creating it |
| [YouTube Data API v3](https://developers.google.com/youtube/v3/getting-started) | Finds related videos during book preparation | A Google Cloud project with this API enabled and an API key |

The packer currently requires both keys even if you only want the book text.
Spotify is optional: the app copies a playlist prompt and opens Spotify; you create
the playlist there if your account supports it.

The configured model is [Gemini 3.8 Flash](https://openrouter.ai/google/gemini-3.8-flash).
Book preparation and phone-side AI calls spend OpenRouter credit. Start with one book
and inspect the cost printed by `./pack add`; length, retries and model prices affect
cost and elapsed time. Video discovery makes up to five YouTube searches plus a video
metadata request per book; check your project's quota before building a shelf.

Book text is sent to OpenRouter and its model provider during preparation. On the
phone, Explain sends the selection and its paragraph, and blog hooks send up to
12,000 characters of each post. Keep private feeds and reading lists out of Git.
The source sites distribute public-domain editions; check the edition's terms and
its copyright status where you live before downloading or sharing a pack.

## Build and open the app

Run all commands from the repository root. These commands use a macOS/Linux shell.

1. Install JDK 17, [Android Studio](https://developer.android.com/studio) and
   [uv](https://docs.astral.sh/uv/getting-started/installation/). In Android Studio's
   SDK Manager install Android SDK Platform 37, SDK Build-Tools and Platform-Tools.
   The Python packer requires Python 3.12 or newer; uv can provision it.
2. Set `JAVA_HOME` to your JDK 17 directory and `ANDROID_HOME` to the SDK location
   shown in Android Studio. Add its Platform-Tools to your path:

   ```bash
   export PATH="$ANDROID_HOME/platform-tools:$PATH"
   java -version
   adb version
   uv sync --locked
   ./pack --help
   ```

3. Create your local configuration, then fill in the two values with your own keys.
   Use plain, unquoted `NAME=value` lines. Do not overwrite an existing `.env`:

   ```bash
   test -f .env || cp .env.example .env
   ```

   `.env` is ignored by Git. The Android build reads this file, not exported shell
   variables. Rebuild and reinstall after changing its OpenRouter key. The packer
   also accepts exported variables, which take precedence over `.env`.
4. Connect an Android 12 (API 31) or newer phone, enable USB debugging and accept
   its authorization dialog. An emulator at API 31 or newer also meets the app's
   minimum version. Follow the [ADB connection instructions](https://developer.android.com/tools/adb)
   if needed. `adb devices` must show the target as `device`, not `unauthorized`.
   With multiple devices, set `ANDROID_SERIAL` to the target's listed serial.
5. Build and install:

   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

   Expect `BUILD SUCCESSFUL` and then `Success`. Open Morning reader once before
   transferring content so it creates its own storage directories. An empty
   reading list is expected until you add a book or feed.

## Read your first book

Search first, then choose an ID to avoid silently accepting the first title match:

```bash
./pack search "frankenstein"
./pack add 84
```

The second command downloads an EPUB, preferring Standard Ebooks when a matching
edition is found, calls the APIs, writes `packs/gutenberg-84.json` and transfers it
to the selected device. Reopen the app or tap the refresh icon on Today. Find the
book in Books or a passage on Today, then open a passage and read it. Selecting text
and tapping **Explain** exercises the phone's OpenRouter connection.

Preparation normally aims for 5–15 minute passages. Stories and poems stay whole,
so some passages are longer and may require increasing the daily budget in Settings.
EPUB emphasis and footnotes are dropped; generated hooks and explanations may be wrong.

Useful follow-up commands:

```bash
./pack add 84 --no-push   # prepare on the computer without a connected phone
./pack push               # transfer all existing packs without rebuilding
./pack videos 84          # retry video discovery for this pack (uses the APIs)
./pack push               # transfer the updated pack
```

Running `add` again rebuilds and can incur charges again. `starter` builds the fixed
eight-book shelf in [packer/__main__.py](packer/__main__.py), reusing existing packs;
choose your own books with `add` for a first trial.

## Add blog feeds

In Settings, add a feed URL or choose **Import OPML** and select a file on the phone.
Alternatively, from the computer:

```bash
./pack opml path/to/feeds.opml
```

Reopen the app to import it. **Check now** in Settings fetches posts immediately.
Background checks are scheduled every 12 hours, subject to Android scheduling.
Only substantial posts are kept: at least 1,000 words and an estimated five minutes.
If a feed imports but no posts appear, check its recent posts meet those limits.

## If setup stops

- **ADB not found or the wrong phone selected:** put Platform-Tools on `PATH` and
  check `adb devices` and `ANDROID_SERIAL`. To use a connection wrapper, set `ADB`
  to its executable path for `./pack`; it must accept ordinary ADB arguments.
- **A pack was built but transfer failed:** open the installed app once, verify
  the connection, then run `./pack push`. This avoids paying to rebuild the book.
- **OpenRouter errors:** check the key, available credit and model access. Phone
  failures after replacing a key require rebuilding and reinstalling the APK.
- **No videos:** a refused YouTube search can mean exhausted quota or a key/API
  configuration problem. The book can still be saved on that path. Correct the
  problem, then run `./pack videos ID` and `./pack push`.
- **Gradle fails before compilation:** confirm JDK 17, Platform 37 and network
  access to dependency repositories. The wrapper pins Gradle 9.7.1 and the root
  build pins Android Gradle Plugin 9.4.1; report the first download or resolution
  error before changing those versions.

## Working on the project

[AGENTS.md](AGENTS.md) describes the layout and checks for a coding agent;
[DECISIONS.md](DECISIONS.md) records the reading design, and
[docs/book-pack.md](docs/book-pack.md) describes the JSON exchanged with the phone.
Keep `.env`, `cache/`, `packs/` and APKs private. Book packs contain the downloaded
text; publishing source does not make those editions or your credentials shareable.
