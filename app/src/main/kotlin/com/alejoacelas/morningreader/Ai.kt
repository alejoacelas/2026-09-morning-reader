package com.alejoacelas.morningreader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/** Gemini Flash through OpenRouter. */
object Ai {
    private const val MODEL = "google/gemini-3.8-flash"
    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    suspend fun ask(prompt: String, maxTokens: Int = 600, jsonReply: Boolean = false): String =
        withContext(Dispatchers.IO) {
            val body = buildJsonObject {
                put("model", MODEL)
                putJsonArray("messages") {
                    add(buildJsonObject { put("role", "user"); put("content", prompt) })
                }
                putJsonObject("reasoning") { put("effort", "minimal") }
                put("max_tokens", maxTokens)
                if (jsonReply) putJsonObject("response_format") { put("type", "json_object") }
            }
            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .header("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                .header("X-Title", "morning-reader")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()
            http.newCall(request).execute().use { response ->
                val text = response.body.string()
                check(response.isSuccessful) { "OpenRouter ${response.code}: ${text.take(200)}" }
                Store.json.parseToJsonElement(text).jsonObject["choices"]!!.jsonArray[0]
                    .jsonObject["message"]!!.jsonObject["content"]!!.jsonPrimitive.content.trim()
            }
        }

    /** The single highlight prompt: define short selections, explain longer ones. */
    suspend fun explain(selection: String, paragraph: String, source: String, language: String?): String {
        val words = selection.trim().split(Regex("\\s+")).size
        val task = if (words <= 2)
            "Define the highlighted word or phrase as it is used here, in one or two sentences."
        else
            "Explain the highlighted passage in two to four sentences: what it means here, and any " +
                "allusion, reference, historical background or step in the argument a reader might miss."
        val prompt = """
            Someone is reading "$source" and highlighted: "$selection"

            The paragraph it comes from:
            $paragraph

            $task Answer in ${language ?: "the language of the paragraph"}. Plain text, no preamble, no markdown.
        """.trimIndent()
        return ask(prompt, maxTokens = 400)
    }

    /** A hook line and a Spotify AI Playlist prompt for a blog post. */
    suspend fun postHook(title: String, feed: String, text: String): Pair<String, String> {
        val prompt = """
            A blog post from "$feed" titled "$title". Write, in the post's language:
            - "hook": one line, at most 20 words, saying concretely what the post argues or shows. Plain words, no hype.
            - "playlist_prompt": a Spotify AI Playlist prompt under 25 words matched to the post's mood and subject.
            Reply with JSON only: {"hook": "", "playlist_prompt": ""}

            POST:
            ${text.take(12000)}
        """.trimIndent()
        val reply = Store.json.parseToJsonElement(ask(prompt, maxTokens = 300, jsonReply = true)) as JsonObject
        return (reply["hook"]?.jsonPrimitive?.content ?: "") to (reply["playlist_prompt"]?.jsonPrimitive?.content ?: "")
    }
}
