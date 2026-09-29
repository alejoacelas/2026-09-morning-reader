# Morning reader

An Android app for a finite daily session of book passages and long blog posts.
The companion Python packer prepares books and transfers them with `adb`.
See [README.md](README.md) for adoption limits and [docs/setup.md](docs/setup.md)
for setup with the operator's own tools and accounts.

## Product constraints

- A reading block can start only when its estimate fits today's remaining budget.
  A block already in progress can always finish. There is no forced cutoff.
- Blocks aim for 5–15 minutes. Preserve whole stories and poems when identified;
  those can exceed the target duration. Estimates adapt to the reader's speed.
- Generate hooks, recaps and explanations in the text's language.
- Spotify actions copy a prompt and open Spotify. Videos open externally.
- Keep current decisions in [DECISIONS.md](DECISIONS.md), with dated entries for
  consequential changes.

## Layout and checks

- `app/`: Kotlin/Jetpack Compose app; fetches feeds and calls OpenRouter directly.
- `packer/`, `./pack`: EPUB download/extraction, segmentation and video selection.
- `docs/book-pack.md`: JSON contract shared by the packer and app.
- `cache/`, `packs/`, `.env`: ignored local downloads, generated content and keys.

From the repository root:

```sh
./pack --help
./pack search "tale of two cities"
./gradlew assembleDebug
```

The first two check CLI startup and public book search without paid APIs.
The build needs JDK 17 and Android SDK platform 37. Do not treat a successful
build as proof of device import or authenticated model calls. No automated test
suite is included. For transfer changes, test command construction with a mock
`adb` before using an authorized test device.

## Credentials and data

The operator supplies `OPENROUTER_API_KEY` and, for book preparation,
`YOUTUBE_API_KEY` in ignored `.env`; use unquoted `NAME=value` lines. Python also
reads process environment variables, but the Android build reads only `.env`.
Use the operator's password manager for originals and private instructions for
credential locations. Never put account IDs or password-manager item names in
public documentation.

The OpenRouter key is compiled into the APK: never publish a built APK or commit
keys, private feeds, reading lists, logs containing private URLs, or generated
packs. API operations can spend credits and feed refresh can send article text
to the model; use only accounts and data authorized for the task.

## Maintainer notes

`ADB` selects an executable (default `adb` on `PATH`); `ANDROID_SERIAL` selects
the device. No personal helper script is required. The model ID is configured
in both `packer/llm.py` and `app/src/main/kotlin/com/alejoacelas/morningreader/Ai.kt`;
keep these consistent if changing it.

The repository has no project-wide reuse license. Its existing history contains
personal infrastructure and credential-location references removed from current
docs. Review history and obtain the owner's publication decision before sharing
it; a cleaned source copy with fresh history is needed if those references must
remain private. Never rewrite or publish history as part of routine doc edits.
