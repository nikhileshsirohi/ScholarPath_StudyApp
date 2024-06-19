package com.example.scholarpath.Screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Composable
fun VideoPlayerScreen(navController: NavController, filePath: String) {
    val videoUrl = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Fetch the download URL from Firebase
    val activity = context as? Activity
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    DisposableEffect(filePath) {
        val job = coroutineScope.launch {
            try {
                Log.d("VideoPlayerScreen", "Fetching video URL for $filePath")
                val url = fetchVideoUrlFromFirebase(filePath)
                Log.d("VideoPlayerScreen", "Fetched URL: $url")
                videoUrl.value = url
                isLoading = false
            } catch (e: Exception) {
                Log.e("VideoPlayerScreen", "Error fetching video URL", e)
                errorMessage = "Failed to fetch video URL: ${e.message}"
                isLoading = false
            }
        }

        onDispose {
            job.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Text(text = "Loading video...", modifier = Modifier.align(Alignment.Center))
            }

            videoUrl.value != null -> {
                Column {
                    Row {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, contentDescription = "Back",
                            modifier = Modifier.clickable { navController.popBackStack() }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 40.dp),
                            text = filePath,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Divider()
                    OpenVideoFile(videoUrl = videoUrl.value!!)
                }
            }

            else -> {
                Text(text = errorMessage ?: "An error occurred.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}


@Composable
fun OpenVideoFile(
    videoUrl: String
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build()
    }

    DisposableEffect(videoUrl) {
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration
                    Log.d("OpenVideoFile", "Video duration: $duration ms")
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            Log.d("OpenVideoFile", "Releasing ExoPlayer")
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> exoPlayer.playWhenReady = false
                    Lifecycle.Event.ON_RESUME -> exoPlayer.playWhenReady = true
                    else -> {}
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = exoPlayer.currentPosition
            delay(1000)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

}


suspend fun com.google.android.gms.tasks.Task<Uri>.await(): Uri {
    return suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }
}

suspend fun fetchVideoUrlFromFirebase(filePath: String): String {
    val storageReference = FirebaseStorage.getInstance().reference.child(filePath)
    return storageReference.downloadUrl.await().toString()
}
