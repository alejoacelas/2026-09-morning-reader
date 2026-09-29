# Morning reader

An Android reading prototype: finite daily sessions of book passages, long blog
posts and short videos. See [README.md](README.md) for adopter setup and
[DECISIONS.md](DECISIONS.md) for design constraints.

## Layout

- `app/`: Kotlin/Jetpack Compose app; fetches feeds and calls OpenRouter directly.
- `packer/` and `./pack`: Python book search, EPUB extraction, model segmentation,
  YouTube discovery and transfer through ADB.
- `docs/book-pack.md`: shared JSON format; keep producer, consumer and docs aligned.
- `cache/`, `packs/`, `.env`: ignored downloads, personal data and keys.

## Setup and checks

Use the reader's JDK 17 and Android SDK, with Platform 37 installed. Run commands
from the repository root. Use `adb` from PATH; `ANDROID_SERIAL` selects a device.
The packer accepts `ADB=/path/to/wrapper` for an optional connection helper.
Do not assume the author's tools, accounts or filesystem paths exist.

- `uv sync --locked` and `./pack --help`: install and inspect the Python CLI.
- `./pack search "frankenstein"`: public catalog check, without paid API calls.
- `./gradlew assembleDebug`: compile the Android app; no paid API calls.
- With permission to use the target device, `adb install -r
  app/build/outputs/apk/debug/app-debug.apk`, then open the app once before transfer.

There is no automated test suite. For changes to book transfer, check the ADB
arguments with a stub before using a device. For changes to the reading flow,
verify on Android and distinguish checks run from behavior inferred from code.

## Credentials and external actions

The reader supplies `OPENROUTER_API_KEY` and `YOUTUBE_API_KEY` in ignored `.env`,
using `.env.example` as the template. Never retrieve the author's credentials.
Private password-manager rebuild notes belong outside this repository.

OpenRouter keys are embedded in APKs, including release builds (which currently
use debug signing). Never commit or publish APKs or keys. Rebuild after key changes.
The configured model appears in `packer/llm.py` and the app's `Ai.kt`.

Book preparation, video retries and phone AI features incur API usage. Obtain the
reader's authorization before spending their credit or changing cloud resources.
Do not add feeds, reading lists, downloads or generated packs to Git. The owner
still needs to choose an app-wide reuse license; do not choose one on their behalf.

## Reading constraints

The daily budget limits which blocks may start; the current block may finish.
Preserve whole stories and poems even when they exceed the usual 15-minute target.
Write hooks, recaps, summaries and explanations in the text's language. Keep cold
starts among the earliest unread entry points to avoid late-book spoilers.
