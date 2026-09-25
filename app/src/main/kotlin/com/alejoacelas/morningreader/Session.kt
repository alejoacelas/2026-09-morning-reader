package com.alejoacelas.morningreader

import kotlin.math.ceil

/** One option on today's screen. */
sealed interface Card {
    val key: String
    val minutes: Int

    data class Reading(val item: ReadItem, val label: String, override val minutes: Int) : Card {
        override val key get() = item.id
    }

    data class Watch(val video: Video, val book: BookPack, override val minutes: Int) : Card {
        override val key get() = "video-" + video.youtubeId
    }
}

/** Builds today's mix: what's in progress, then books and posts interleaved, with a video or two. */
object Session {
    fun cards(state: AppState, books: List<BookPack>, posts: List<BlogPost>): List<Card> = with(Store) {
        val daySeed = state.day.hashCode()
        val cards = mutableListOf<Card>()
        state.inProgress?.let { progress ->
            item(progress.itemId)?.let { cards += Card.Reading(it, "Resume", minutesLeft(it, progress)) }
        }
        val taken = cards.map { it.key }.toMutableSet()
        val busyBook = state.inProgress?.let { item(it.itemId)?.bookId }

        val bookCards = books.filter { it.id != busyBook }.mapNotNull { book ->
            val lastRead = book.blocks.filter { it.id in state.finished }.maxByOrNull { it.order }
            val next = lastRead?.let { last -> book.blocks.firstOrNull { it.order > last.order && it.id !in state.finished } }
            val block: Block
            val label: String
            if (next != null) {
                block = next; label = "Continue"
            } else {
                // Rotate among the earliest unread entry points so a dip-in never lands on the ending.
                val entries = book.blocks.filter { it.entryPoint && it.id !in state.finished }.take(3)
                if (entries.isEmpty()) return@mapNotNull null
                block = entries[Math.floorMod(daySeed + book.id.hashCode(), entries.size)]
                label = if (block.order == 0) "Start" else "Dip in"
            }
            if (block.id in taken) null else Card.Reading(block.toItem(book), label, minutesFor(block.words))
        }.shuffledBy(daySeed)

        val postCards = posts.filter { it.id !in state.finished && it.id !in taken }.take(4)
            .map { Card.Reading(it.toItem(), "New post", minutesFor(it.words)) }

        val videos = books.flatMap { b -> b.videos.map { it to b } }
            .filter { (v, _) -> v.youtubeId !in state.watched }
            .shuffledBy(daySeed).take(2)
            .map { (v, b) -> Card.Watch(v, b, maxOf(1, ceil(v.seconds / 60.0).toInt())) }

        val mixed = mutableListOf<Card>()
        val bookIt = bookCards.iterator()
        val postIt = postCards.iterator()
        while (bookIt.hasNext() || postIt.hasNext()) {
            if (bookIt.hasNext()) mixed += bookIt.next()
            if (postIt.hasNext()) mixed += postIt.next()
        }
        videos.forEachIndexed { i, v -> mixed.add(minOf(mixed.size, 2 + i * 4), v) }
        cards + mixed
    }

    fun minutesLeft(item: ReadItem, progress: InProgress): Int {
        val total = Store.minutesFor(item.words)
        return maxOf(1, total - (progress.activeSeconds / 60).toInt())
    }

    private fun <T> List<T>.shuffledBy(seed: Int): List<T> = shuffled(java.util.Random(seed.toLong()))
}
