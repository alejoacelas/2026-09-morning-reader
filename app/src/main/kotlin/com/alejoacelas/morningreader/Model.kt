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
)

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

/** The block currently being read. It can always be finished, even past the budget. */
@Serializable
data class InProgress(val itemId: String, val activeSeconds: Long = 0, val scroll: Int = 0)

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
)
