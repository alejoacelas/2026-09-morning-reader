# Morning reader

A personal Android app for a 45-minute morning reading session. It serves books
as self-contained 5–15 minute blocks, mixed with long blog posts and short
videos. You approve one block at a time, and a block can start only if it fits in
the time left today. After a block you can mark the book keep going, maybe later or
done with this, and Today orders books by that choice.

## Build and install

Requires JDK 17 and the Android SDK (see `AGENTS.md` for paths) and the phone
reachable through `~/best/dotfiles/bin/adb-phone`.

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=~/Library/Android/sdk
./gradlew assembleDebug
~/best/dotfiles/bin/adb-phone install -r app/build/outputs/apk/debug/app-debug.apk
```

## Add books

Books come from Project Gutenberg, using the Standard Ebooks edition when there is
one. `./pack` builds a book pack on the Mac and pushes it to the phone. The app
loads new packs when it opens or when you tap refresh.

```bash
./pack search "tale of two cities"   # list Gutenberg matches with their ids
./pack add 98                        # build and push by Gutenberg id
./pack add "tale of two cities"      # or build the top search match
./pack starter                       # build and push the first shelf
./pack sources                       # find secondary material for packs that have none
./pack sources 84                    # or redo it for one book
./pack annotate                      # add character notes to packs built before they existed
./pack push                          # re-push every built pack
```

## Wish list

A Chrome bookmarks folder doubles as a wish list. The packer reads Chrome's bookmark
files each time you ask, so a book bookmarked this morning shows up the next run.

```bash
./pack wishlist                      # list wished-for books and whether each is on Gutenberg
./pack wishlist build                # build and push up to 3 that aren't built yet (--limit N)
./pack wishlist folders              # show bookmark folders that look meant for books
./pack wishlist folder "To read"     # choose the folder (saved in cache/settings.json)
```

`starter` builds the fixed first shelf and `add` builds any one book; use `wishlist
build` to pick the next books from what you've bookmarked. Only public-domain books
are on Gutenberg, so most modern titles are listed as "not on Gutenberg". The first
check of new bookmarks costs about $0.03 in Gemini calls; results are cached in
`cache/wishlist.json`.

A pack costs about $0.02–0.65 in Gemini calls, roughly $0.13 per 100,000 words,
and takes about a minute.

## Secondary material

At the end of a block the app offers one or two things to watch, listen to or read
about the book or its author. They come only from the trusted sources in
`packer/trusted-sources.toml`: YouTube channels such as the National Theatre and
Gresham College, BBC *In Our Time* and *Great Lives*, The Public Domain Review, and
biographies on Project Gutenberg. Edit that file to add or drop a source. Gemini keeps
the candidates that really concern the book or author, which costs under a cent a book.
Each YouTube channel searched costs 100 of the 10,000 free daily quota units, so with
six channels about 16 books a day can be searched; when the quota runs out the other
sources still work and `./pack sources <id>` adds videos the next day.

## Notes and highlights

Select text while reading to Explain it, Ask a question about it, or add a Note.
Notes are saved for later and aren't shown in the app. `./pack pull` copies notes,
highlights and the log of times nothing fit the minutes left into `cache/phone/`.

## Blog feeds

The phone checks feeds every 12 hours and keeps posts of 5 minutes or more. To
load an OPML export (for example from Feedly), push it with
`./pack opml path/to/feeds.opml`, or use Settings → Import OPML on the phone.
Settings also adds and removes single feeds.

## Secrets

The app and `./pack` read API keys from an ignored `.env`. `AGENTS.md` says where
each key lives in 1Password and how to rebuild the file.

The Literata font is under the SIL Open Font License (`licenses/literata-OFL.txt`).
