# Setup and your first reading session

Run terminal commands from the repository root. This path assumes your own
computer, Android phone and service accounts. Start with the build before paying
for API access: a build without `.env` works, but Explain and blog hooks will fail.

## Build and connect the phone

1. Install JDK 17 and [Android Studio's SDK tools](https://developer.android.com/studio/intro/update#sdk-manager).
   In SDK Manager, install Android SDK Platform 37, Android SDK Build-Tools and
   Android SDK Platform-Tools. Accept their licenses. Set these paths to your
   actual installations (the SDK location appears in SDK Manager):

   ```sh
   export JAVA_HOME="/path/to/jdk-17"
   export ANDROID_HOME="/path/to/android-sdk"
   export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
   java -version
   adb version
   ./gradlew assembleDebug
   ```

   Success produces `app/build/outputs/apk/debug/app-debug.apk`. The Gradle wrapper
   downloads its pinned version and dependencies. No separate Gradle install is
   needed. If dependency resolution fails, see [Troubleshooting](#troubleshooting).

2. On an Android 12+ phone, enable Developer options and USB debugging, connect
   by USB, and accept the debugging authorization on the phone. Follow the
   [Android connection instructions](https://developer.android.com/tools/adb#Enabling)
   if needed. Run `adb devices`: the phone must show `device`, not `unauthorized`.
   If multiple devices are attached, set `ANDROID_SERIAL` to the serial displayed
   for the intended phone; this applies to both `adb` and `./pack`.

3. Install and open the app once:

   ```sh
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   adb shell am start -n com.alejoacelas.morningreader/.MainActivity
   ```

   An empty Today screen is expected. Opening the app creates the directories
   used for importing books. Do not create those phone directories manually.

## Supply your own API keys

Create an [OpenRouter key](https://openrouter.ai/settings/keys), add credits and
set a spending limit for that key. For books, also create your own Google Cloud
project, enable YouTube Data API v3, and create an API key using
[Google's getting-started guide](https://developers.google.com/youtube/v3/getting-started).
The packer uses public search, so no YouTube OAuth sign-in is required.
Restrict that key to YouTube Data API v3; Android app restrictions do not match
these requests, which run on your computer.

Create `.env` at the repository root with your actual values, without quotes or
`export` prefixes:

```dotenv
OPENROUTER_API_KEY=your-openrouter-key
YOUTUBE_API_KEY=your-youtube-data-api-key
```

For a blog-only trial, omit the YouTube line. Keep `.env` out of version control
(it is already ignored). Store the original keys in your own password manager.
The Android build reads `.env` directly; exporting shell variables alone does
not supply its key. The Python packer accepts shell variables before `.env`.

Rebuild and reinstall after adding or changing the OpenRouter key:

```sh
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Keep this APK private: it contains your key. The YouTube key stays on the computer.
Both debug and release builds currently use debug signing; this guide is for
personal installation, not app-store publication.

## Add your first book

Install [uv](https://docs.astral.sh/uv/getting-started/installation/) and Python
3.12 or later, then run:

```sh
./pack --help
./pack search "tale of two cities"
./pack add 98
```

`uv` installs the locked Python dependencies on first use. Search needs no keys
and should list matching titles with Gutenberg IDs. Prefer an ID after checking
the results: `add` with a title builds the top search match automatically.

`add` downloads an EPUB, preferring a matching Standard Ebooks edition, segments
it with Gemini and screens short YouTube videos. It writes
`packs/gutenberg-98.json`, prints its model cost, then pushes it to the phone.
This step spends API credits. Re-running `add` rebuilds the book and spends
credits again. Check the book's source page for its terms and availability where
you live before downloading.

Open or return to the app, or tap Today's refresh icon. Verify the title appears
in Library and a passage appears on Today. Open it, select text, and try Explain
to verify the phone's API key. Settings changes the daily budget; estimates start
at 238 words per minute and adapt from completed reading sessions.

Useful follow-up commands:

| Command | When to use it |
| --- | --- |
| `./pack add 98 --no-push` | Build while the phone is disconnected; still uses both APIs. |
| `./pack push` | Copy every local pack to the selected phone without rebuilding. |
| `./pack videos 98` then `./pack push` | Retry or replace videos for an existing pack. |
| `./pack starter --no-push` | Build the eight-book shelf listed in `packer/__main__.py`; existing packs are reused. Expect more API usage than one book. |

The packer uses `adb` on `PATH`. If you need a wrapper or a different executable,
set `ADB` to its path, without command-line arguments. Use `ANDROID_SERIAL` to
select the phone. Run all commands from the repository root.

## Add blog feeds

In Settings, add an RSS/Atom URL or choose **Import OPML** for a feed export
already on the phone. Alternatively, from the computer:

```sh
./pack opml /path/to/feeds.opml
```

Return to the app to import it, then use Settings → **Check now**. Check the feed
count and refresh status. Zero new posts can be normal: the app checks at most
four unseen entries per feed from the last 45 days and keeps only long posts.
Some feeds supply excerpts; it tries fetching the article page, without an
account login or paywall support. Imported OPML excludes xkcd feeds.

Background checks are scheduled every 12 hours when connected; Android controls
the actual timing. Opening the app also checks feeds if the last refresh is over
six hours old or mostly failed. Hook generation uses your OpenRouter credits.

## Troubleshooting

If using an agent, give it the failing command and error first; it can inspect
this repository. Share a screenshot if it cannot see a phone error. Remove keys
and private feed URLs from anything you share.

- **`adb` reports no device or multiple devices:** check USB authorization and
  `adb devices`; set `ANDROID_SERIAL` when selecting among devices.
- **A pack was written but pushing failed:** fix the connection, open the app,
  and run `./pack push`. Do not rebuild the book just to retry transfer.
- **OpenRouter rejects a request:** check the key, credits, spending limit and
  access to `google/gemini-3.8-flash`. Rebuild and reinstall for phone key changes.
- **YouTube reports 403/429:** the packer saves a pack without videos when search
  returns these codes. Check API enablement, key restrictions and quota; 403 does
  not prove quota exhaustion. Once fixed, run `./pack videos 98` and `./pack push`.
- **Gradle or SDK resolution fails:** check SDK paths, accepted licenses and the
  versions in `build.gradle.kts`, `app/build.gradle.kts` and
  `gradle/wrapper/gradle-wrapper.properties`. Preserve the error when reporting
  a build issue; replacing versions blindly may introduce incompatible APIs.

## Verification status

Documentation review on 2026-09-29 checked CLI help and public Gutenberg search,
and traced setup and data handling through the code. No service keys, paid
model calls, personal feed imports or device installs were used in that review.
A debug build without keys passed on macOS with JDK 17 and the available Android
SDK (38 Gradle tasks, about four minutes including downloads into a fresh Gradle
cache). This establishes compilation, not phone behavior. A new adopter still
needs to confirm authenticated book generation, phone import, Explain, and
background feed refresh with their own accounts and device.
