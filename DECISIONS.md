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
- The reader, not an algorithm, decides which books lead the session. After a block,
  "keep going" puts the book first on Today, "maybe later" puts it last, and "done
  with this" archives it like a swipe. Skipping the choice leaves the book where it
  was, so a choice is never forced.

### Do the heavy work once, on the Mac

- `./pack` segments each book with Gemini once and pushes a JSON pack. The phone
  only calls the model for Explain and Ask and for blog-post hooks, so mornings don't
  depend on the Mac.
- Hooks, recaps, summaries and Explain and Ask answers are written in the book's
  language.
- Character notes and standalone flags are added by a separate pass
  (`./pack annotate`) that keeps block ids, so existing packs gain them without
  losing reading progress. Re-segmenting a book changes its block ids.
- The Notes tab shows what the reader wrote or asked: Notes and Asks with their
  answers. Explain lookups are quick definitions, so they are kept but not listed.
- Gemini 3.8 Flash via OpenRouter, with minimal reasoning for lookups, at the
  user's request.

### Take secondary material only from trusted sources

- Videos, radio, essays and biographies come only from the hand-picked list in
  `packer/trusted-sources.toml`, never a broad search, because a broad search can't
  guarantee quality. Gemini only filters that list; it doesn't widen it.
- The list leans toward biography and context on the author and period, which the user
  finds most interesting.
- Videos of 10 minutes or less are blocks and count toward the budget. Longer videos,
  radio episodes, essays and books open outside the session and don't count.

### File feature requests straight from the phone

- A request typed in Settings becomes a GitHub issue on this repository at once,
  through a fine-grained token limited to this repository's issues and compiled into
  the APK. A request stays on the phone until GitHub accepts it.

### Keep private lists out of the public repository

- The repository is public. Feed lists, reading lists and downloads live in the
  ignored `cache/`, and keys live in `.env`.

### Pick new books from the user's own wish list

- Further books come from a Chrome bookmarks folder the user already keeps for books,
  read when the packer is asked (`./pack wishlist`), never on a
  schedule. The folder is a setting, since Chrome has no notion of a book folder.
- The fixed first shelf (`./pack starter`) and one-off `./pack add` stay as they are.
- Anything derived from the bookmarks lives in the ignored `cache/`.

## Open questions

- **Wish-list books that aren't public domain.** Most books in the wish list are
  modern and not on Gutenberg, so they have no source yet.

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

### 2026-10-10

Added keep going / maybe later / done with this after each block, so the session
can scale to more books without the reader re-sorting them. The selection menu keeps
only Explain, Ask, Note and Copy.

Replaced the broad YouTube search with trusted sources. On the first shelf every book
got at least one item (8 of 8): In Our Time covered six, Gutenberg biographies two,
and the Spanish books came through Fundación Juan March and In Our Time's Lorca.

Added feature requests from Settings, filed as GitHub issues.

Replaced the Highlights tab with Notes, at the user's request: it lists Notes and
Asks, and Explain lookups no longer appear.

Added the wish list from a Chrome bookmarks folder. Its name and contents stay in
`cache/` because the repository is public.
