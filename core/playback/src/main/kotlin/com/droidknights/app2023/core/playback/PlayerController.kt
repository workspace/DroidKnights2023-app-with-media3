package com.droidknights.app2023.core.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.droidknights.app2023.core.data.repository.SessionRepository
import com.droidknights.app2023.core.playback.session.MediaId
import com.droidknights.app2023.core.playback.session.MediaItemProvider
import com.droidknights.app2023.core.playback.session.PlaybackService
import com.droidknights.app2023.core.playback.session.toMediaIdOrNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.guava.asDeferred
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerController @Inject constructor(
  private val context: Context,
  private val sessionRepository: SessionRepository,
  private val mediaItemProvider: MediaItemProvider,
) {

  private var controllerDeferred: Deferred<MediaController> = newControllerAsync()

  private fun newControllerAsync() = MediaController
    .Builder(context, SessionToken(context, ComponentName(context, PlaybackService::class.java)))
    .buildAsync()
    .asDeferred()

  @OptIn(ExperimentalCoroutinesApi::class)
  private val activeControllerDeferred: Deferred<MediaController>
    get() {
      if (controllerDeferred.isCompleted) {
        val completedController = controllerDeferred.getCompleted()
        if (!completedController.isConnected) {
          completedController.release()
          controllerDeferred = newControllerAsync()
        }
      }
      return controllerDeferred
    }
  private val scope = CoroutineScope(Dispatchers.Main.immediate)

  fun setPosition(positionMs: Long) = executeAfterPrepare { controller ->
    controller.seekTo(positionMs)
  }

  fun fastForward() = executeAfterPrepare { controller ->
    controller.seekForward()
  }

  fun rewind() = executeAfterPrepare { controller ->
    controller.seekBack()
  }

  fun previous() = executeAfterPrepare { controller ->
    controller.seekToPrevious()
  }

  fun next() = executeAfterPrepare { controller ->
    controller.seekToNext()
  }

  fun play() = executeAfterPrepare { controller ->
    controller.play()
  }

  fun playPause() = executeAfterPrepare { controller ->
    if (controller.isPlaying) {
      controller.pause()
    } else {
      controller.play()
    }
  }

  fun setSpeed(speed: Float) = executeAfterPrepare { controller ->
    controller.setPlaybackSpeed(speed)
  }

  private suspend fun maybePrepare(controller: MediaController): Boolean {
    val currentPlayingSessionId = sessionRepository.getCurrentPlayingSession()?.id ?: return false
    if (controller.currentSessionId() == currentPlayingSessionId &&
      controller.playbackState in listOf(Player.STATE_READY, Player.STATE_BUFFERING)
    ) {
      return true
    }
    val currentPlayingSession = runCatching {
      sessionRepository.getSession(currentPlayingSessionId)
    }
      .getOrNull()
      ?: return false
    controller.setMediaItem(mediaItemProvider.mediaItem(currentPlayingSession))
    controller.prepare()
    return true
  }

  private fun MediaController.currentSessionId(): String? =
    (currentMediaItem?.mediaId?.toMediaIdOrNull() as? MediaId.Session)?.id

  private inline fun executeAfterPrepare(crossinline action: suspend (MediaController) -> Unit) {
    scope.launch {
      val controller = awaitConnect() ?: return@launch
      if (maybePrepare(controller)) {
        action(controller)
      }
    }
  }

  private suspend fun awaitConnect(): MediaController? {
    return runCatching {
      activeControllerDeferred.await()
    }.getOrElse { e ->
      if (e is CancellationException) throw e
      null
    }
  }
}
