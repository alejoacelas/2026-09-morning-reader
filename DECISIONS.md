# Decisions

## Core decisions

### Serve books as a bounded feed

- The unit is a block of about 5–15 minutes, not a book. Today's screen mixes blocks
  from several books with long blog posts and short videos, so it keeps a feed's
  variety without its endlessness.
- The daily budget limits which blocks may start, not how long you read: a block
  starts only if time used plus its estimate fits, and the block in progress can
  always be finished. There is no countdown.
- Short stories and poems are never split. A story longer than 15 minutes stays one
  block ([why](#keep-stories-and-poems-whole)).
- "Dip in" picks rotate among a book's three earliest unread entry points, so a
  cold start never lands on a novel's ending.

### Prepare books on the computer

- `./pack` segments each book with Gemini once and pushes a JSON pack. The phone
  only calls the model for highlight lookups and blog-post hooks, so mornings don't
  depend on the Mac.
- Hooks, recaps, summaries and highlight answers are written in the book's
  language.
- Gemini 3.8 Flash via OpenRouter, with minimal reasoning for lookups.

### Keep private lists out of the public repository

- Keep shareable source separate from personal data. Feed lists, reading lists
  and downloads live in ignored `cache/`; generated packs live in `packs/` and
  keys in `.env`. Built APKs contain the OpenRouter key and must stay private.

## Open questions

- **How to suggest further books.** The first shelf is a fixed example list
  (`STARTER` in `packer/__main__.py`). There are no
  criteria or recommendation logic for choosing more; add books with `./pack add`
  until this is decided.

## Details

### Keep stories and poems whole

Splitting a story or poem can break its meaning. When the packer identifies a
whole work, preserving it outranks the 15-minute target. The daily budget rule
still applies to longer blocks.

## Log

### 2026-09-25

First shelf: Middlemarch, Mill's Autobiography, Quiroga's *Cuentos de amor de
locura y de muerte*, Lorca's *Romancero gitano*, Frankenstein, Anna Karenina, The
Trial and The Mysterious Affair at Styles form the fixed example shelf.

### 2026-09-29

Use standard `adb` for transfers, with `ADB` for an alternate executable and
`ANDROID_SERIAL` for device selection, so adopters need no personal helper.
