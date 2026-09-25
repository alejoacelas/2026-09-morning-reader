package com.alejoacelas.morningreader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

/** Copies a playlist prompt and opens Spotify, where it can be pasted into AI Playlist. */
fun openSpotify(context: Context, prompt: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(ClipData.newPlainText("Playlist prompt", prompt))
    Toast.makeText(context, "Prompt copied. In Spotify, open AI Playlist and paste it.", Toast.LENGTH_LONG).show()
    val intent = context.packageManager.getLaunchIntentForPackage("com.spotify.music")
        ?: Intent(Intent.ACTION_VIEW, "https://open.spotify.com".toUri())
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun openVideo(context: Context, youtubeId: String) {
    val url = "https://www.youtube.com/watch?v=$youtubeId".toUri()
    val app = Intent(Intent.ACTION_VIEW, url).setPackage("com.google.android.youtube")
    val intent = if (app.resolveActivity(context.packageManager) != null) app else Intent(Intent.ACTION_VIEW, url)
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun openUrl(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
