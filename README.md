# Morning reader

A personal Android app for a 45-minute morning reading session built from blocks
of books and long blog posts.

## Secrets

`.env` holds these variables. Rebuild it with the commands below. Both items are
in the personal 1Password account (`my.1password.com`), vault `Personal`, field
`credential`.

| Variable | 1Password item |
| --- | --- |
| `OPENROUTER_API_KEY` | `OPENROUTER_API_KEY — 2026-09-morning-reader` |
| `YOUTUBE_API_KEY` | `YOUTUBE_API_KEY — 2026-09-morning-reader` (Google Cloud project `morning-reader-27191`, `alejoacelas@gmail.com`) |

```bash
for v in OPENROUTER_API_KEY YOUTUBE_API_KEY; do
  echo "$v=$(op item get "$v — 2026-09-morning-reader" --account my.1password.com --vault Personal --fields credential --reveal)"
done > .env
```
