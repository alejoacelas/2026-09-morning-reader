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

### Do the heavy work once, on the Mac

- `./pack` segments each book with Gemini once and pushes a JSON pack. The phone
  only calls the model for highlight lookups and blog-post hooks, so mornings don't
  depend on the Mac.
- Hooks, recaps, summaries and highlight answers are written in the book's
  language.
- Gemini 3.8 Flash via OpenRouter, with minimal reasoning for lookups, at the
  user's request.

### Keep private lists out of the public repository

- The repository is public. Feed lists, reading lists and downloads live in the
  ignored `cache/`, and keys live in `.env`.

## Open questions

- **How to suggest further books.** The first shelf is a fixed, hand-picked list
  from the user's to-read list (`STARTER` in `packer/__main__.py`). There are no
  criteria or recommendation logic for choosing more; add books with `./pack add`
  until this is decided.

## Details

### Keep stories and poems whole

The user asked that a block be one or more whole stories, or a few whole poems.
This outranks the 15-minute ceiling: in Quiroga's collection, two stories run
about 26 and 31 minutes. The budget rule still applies to them.

## Log

### 2026-09-25

First shelf: Middlemarch, Mill's Autobiography, Quiroga's *Cuentos de amor de
locura y de muerte*, Lorca's *Romancero gitano*, Frankenstein, Anna Karenina, The
Trial and The Mysterious Affair at Styles, chosen from the user's to-read list.
