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
- Leaving the app mid-block keeps counting toward the budget, up to 5 minutes per
  absence, so stepping out to Spotify counts but a block left open overnight doesn't.
  Time away doesn't feed the learned reading speed.
- With 15 minutes or less left and nothing that fits, Today offers up to three
  self-contained pieces that do. Times with 5–10 minutes left and nothing fitting are
  logged for `./pack pull`.
- Swiping a card archives the whole book or post, at the user's choice, not just
  that block.

### Do the heavy work once, on the Mac

- `./pack` segments each book with Gemini once and pushes a JSON pack. The phone
  only calls the model for highlight lookups and blog-post hooks, so mornings don't
  depend on the Mac.
- Hooks, recaps, summaries and highlight answers are written in the book's
  language.
- Character notes and standalone flags are added by a separate pass
  (`./pack annotate`) that keeps block ids, so existing packs gain them without
  losing reading progress. Re-segmenting a book changes its block ids.
- Notes are private and export-only: never displayed in the app.
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

### 2026-10-03

After the first week: notes, Ask, character notes, swipe-to-archive (whole book),
5-minute cap on time away, and short "fits" picks near the end of the budget.
