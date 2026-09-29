# Morning reader

Turn books and long blog posts into a finite morning reading session on Android.
The Today screen mixes passages from your library, with a hook and reading-time
estimate for each. Choose one that fits the time left in your daily budget
(45 minutes by default); you can always finish a passage already in progress.

For example, after setup:

```sh
./pack search "tale of two cities"  # find an edition and its Gutenberg ID
./pack add 98                      # prepare passages and push them to your phone
```

Open Morning reader to see the book in Library and its suggested passage on
Today. Select text while reading and tap **Explain** for a definition or an
explanation in the text's language. Prepared books and saved posts stay on the
phone; reading them does not need your computer or a model call.

## Before you try it

This is a source-built personal app for developers comfortable with Android
build tools. You need Android 12 or later, JDK 17, Android SDK platform 37,
platform-tools (`adb`), and, for preparing books, Python 3.12+ and `uv`.
The setup guide uses a macOS/Linux terminal. Windows setup has not been verified.

- **Accounts:** your own funded OpenRouter API key for book preparation, Explain
  and blog hooks. The current book packer also requires a YouTube Data API key;
  it has no option to skip video lookup. A blog-only trial needs no YouTube key.
- **Effort:** allow an estimated 30–60 minutes for tools, keys and the first
  install, longer if you have no Android SDK. This is not a measured onboarding
  time. You must create the accounts and authorize USB debugging yourself.
- **Distribution:** the OpenRouter key is compiled into the APK. Build for your
  own device and never share that APK. Rotating the key requires rebuilding and
  reinstalling. There is no shared server to operate.
- **Status:** book preparation, feed import, reading, Explain and external video
  links are implemented. The debug build, CLI help and public book search pass;
  a complete new-account setup and phone install have not been verified in this
  documentation pass. See [setup verification](docs/setup.md#verification-status).

Start with [setup and your first reading session](docs/setup.md).

## Costs and limits

As checked on 2026-09-29, the configured
[Gemini 3.8 Flash model on OpenRouter](https://openrouter.ai/google/gemini-3.8-flash)
listed promotional prices of $0.75 per million input tokens and $3.75 per million
output tokens. An illustrative pack using 150,000 input and 10,000 output tokens
would cost about **$0.15**, excluding retries, extra calls and credit-purchase
fees. This is a calculation, not a measured per-book price. The packer prints
reported model cost; use your OpenRouter usage page for total spending. Blog
hooks and Explain add ongoing usage; reading prepared text adds none. No chat
subscription is used; check [OpenRouter billing](https://openrouter.ai/docs/faq)
when funding your account.

Each book makes up to five YouTube searches and one video-details request.
[Google's quota table](https://developers.google.com/youtube/v3/determine_quota_cost),
checked on 2026-09-29, lists 100 searches per day by default and a separate
10,000-unit allowance for other endpoints. That allows about 20 books using all
five searches, assuming no other search traffic. Your project's actual quota
controls availability. Video playback opens YouTube or a browser. Spotify is
optional: the app copies a playlist prompt for you to paste into Spotify; it
does not create playlists. Any Spotify subscription is separate.

Passages aim for 5–15 minutes. Stories and poems identified as whole works may
be longer; model-generated boundaries and explanations can be wrong. EPUB
formatting and footnotes are stripped. Feed import considers only recent posts,
at most four unseen entries per feed per refresh, and retains text of at least
1,000 words with a five-minute estimate. It is not a full feed archive.

## Data and maintenance

Book text is sent through OpenRouter to the model provider during preparation.
On the phone, Explain sends the selection, surrounding paragraph and source
name; blog hooks send the title, feed name and up to 12,000 characters of text.
The computer contacts Gutenberg, Standard Ebooks and YouTube; the phone contacts
feed sites directly. Review [OpenRouter's data policies](https://openrouter.ai/docs/guides/privacy/data-collection)
before using sensitive feeds.

Packs and downloads remain in ignored `packs/` and `cache/` directories on the
computer. The phone stores packs, posts, highlights and reading progress locally;
Android backup is enabled in the manifest. There is no app-provided cross-device
sync or restore workflow. Keep the computer's packs for reinstallation; uninstalling
or clearing app data can lose reading history. Ongoing work is adding books,
maintaining feed URLs and credits, and rebuilding when changing the app or key.

## Development and reuse

[AGENTS.md](AGENTS.md) covers repository layout and checks;
[book-pack.md](docs/book-pack.md) describes the shared JSON format;
[DECISIONS.md](DECISIONS.md) records product constraints.

No project-wide reuse license has been selected. The owner must choose one
before this can be offered as licensed reusable software. The bundled Literata
font has its own [SIL Open Font License](licenses/literata-OFL.txt).
