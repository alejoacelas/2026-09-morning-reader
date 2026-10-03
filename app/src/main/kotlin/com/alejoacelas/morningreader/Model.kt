package com.alejoacelas.morningreader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A book prepared on the Mac by `./pack` (format in docs/book-pack.md). */
@Serializable
data class BookPack(
    val version: Int = 1,
    val id: String,
    val title: String,
    val author: String = "",
    val language: String = "English",
    val year: Int? = null,
    val origin: String? = null,
    val edition: String = "",
    @SerialName("source_url") val sourceUrl: String = "",
    val summary: String = "",
    @SerialName("playlist_prompts") val playlistPrompts: Map<String, String> = emptyMap(),
    val videos: List<Video> = emptyList(),
    val blocks: List<Block> = emptyList(),
)

@Serializable
data class Block(
    val id: String,
    val order: Int,
    val title: String,
    val hook: String = "",
    val recap: String? = null,
    @SerialName("entry_point") val entryPoint: Boolean = false,
    val words: Int,
    val paragraphs: List<String>,
    @SerialName("playlist_prompt") val playlistPrompt: String = "",
    val videos: List<Video> = emptyList(),
    /** Readable cold on its own: a whole story, poem, essay or self-contained episode. */
    val standalone: Boolean = false,
    val characters: List<Character> = emptyList(),
)

/** A main character in a block, described as of that point in the book (no spoilers). */
@Serializable
data class Character(val name: String, val note: String)

/** A private note on a passage, saved for export and never shown while reading. */
@Serializable
data class Note(
    val at: Long,
    val itemId: String,
    val bookId: String? = null,
    val source: String,
    val blockTitle: String,
    val paragraph: Int,
    val start: Int,
    val end: Int,
    val selection: String,
    val note: String,
)

/** A time the session had minutes left but nothing short enough to fill them. */
@Serializable
data class Shortfall(val day: String, val minutesLeft: Int, val at: Long)

@Serializable
data class Video(
    @SerialName("youtube_id") val youtubeId: String,
    val title: String,
    val channel: String = "",
    val seconds: Int,
    val why: String = "",
)

/** A blog post long enough to be a block, fetched on the phone from the user's feeds. */
@Serializable
data class BlogPost(
    val id: String,
    val feedTitle: String,
    val title: String,
    val url: String,
    val published: Long,
    val hook: String = "",
    val playlistPrompt: String = "",
    val words: Int,
    val paragraphs: List<String>,
)

@Serializable
data class Feed(val url: String, val title: String)

@Serializable
data class Highlight(
    val text: String,
    val answer: String,
    val source: String,
    val at: Long,
)

/**
 * The block currently being read. It can always be finished, even past the budget.
 * `activeSeconds` is time in the reader (used to learn reading speed); `awaySeconds` is time
 * spent in other apps mid-block, which counts toward the budget but not the speed.
 */
@Serializable
data class InProgress(
    val itemId: String,
    val activeSeconds: Long = 0,
    val scroll: Int = 0,
    val awaySince: Long? = null,
    val awaySeconds: Long = 0,
)

@Serializable
data class AppState(
    val budgetMinutes: Int = 45,
    val wordsPerMinute: Double = 238.0,
    val speedSamples: Int = 0,
    val day: String = "",
    val usedSecondsToday: Long = 0,
    val inProgress: InProgress? = null,
    val finished: Set<String> = emptySet(),
    val watched: Set<String> = emptySet(),
    val highlights: List<Highlight> = emptyList(),
    val feeds: List<Feed> = emptyList(),
    val seenPostUrls: List<String> = emptyList(),
    val lastBlogRefresh: Long = 0,
    val blogStatus: String = "",
    val shortfalls: List<Shortfall> = emptyList(),
    /** Swiped away today; archived at the next reload. Book ids, post ids or "video-<id>". */
    val pendingArchive: Set<String> = emptySet(),
    val archived: Set<String> = emptySet(),
)
