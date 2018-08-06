package galaxysoftware.musicplayer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.util.Log
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.MusicCallback
import galaxysoftware.musicplayer.callback.PlayStateChangedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper

class MusicService : Service() {
    private val binder = MusicServiceBinder()
    lateinit var player: MediaPlayer
    lateinit var playlistHelper: PlaylistHelper

    var volume: Float = 0f

    var repeat = 0

    var playStateChangeListener: PlayStateChangedListener? = null

    inner class MusicServiceBinder : Binder() {
        fun getService() = this@MusicService
    }

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    override fun onCreate() {
        super.onCreate()
        playlistHelper = PlaylistHelper.getInstance()
        player = MediaPlayer()
        volume = getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("volume", 50) / 100f
        repeat = getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("repeat", 0)
        player = MediaPlayer().apply {
            setVolume(volume, volume)
            setOnCompletionListener {
                playComplete()
            }
        }
        playlistHelper.shuffleEnabled = getSharedPreferences("Setting", Context.MODE_PRIVATE).getBoolean("shuffle", false)
    }


    fun playMusic(position: Int) {
        player.stop()
        player.reset()
        try {
            player.setDataSource(PlaylistHelper.getInstance().getSongPath(position))
            if (repeat == 2) {
                player.isLooping = true
            }
            player.prepare()
            player.start()
        } catch (e: Exception) {
            return
        }
        PlaylistHelper.getInstance().playingIndex = position
        playStateChangeListener?.onMusicStart(position)
    }

    fun playOrPause(): Int {
        return if (player.isPlaying) {
            player.pause()
            R.mipmap.baseline_play_circle_filled_black_48
        } else {
            player.start()
            R.mipmap.baseline_pause_circle_filled_black_48
        }
    }

    fun play() = player.start()

    fun pause() = player.pause()

    fun playNext() {
        PlaylistHelper.getInstance().playingIndex += 1
        playMusic(PlaylistHelper.getInstance().playingIndex)
    }

    fun backToPrevious() {
        if (playlistHelper.getTitle(playlistHelper.playingIndex) != null) {
            if (player.currentPosition <= 500) {
                player.stop()
                player.reset()
                var songIndex = 0
                if (playlistHelper.shuffleEnabled) {
                    if (playlistHelper.shuffleIndex > 0) {
                        playlistHelper.shuffleIndex -= 1
                        songIndex = playlistHelper.shuffleIndex
                    }
                } else {
                    if (playlistHelper.playingIndex > 0) {
                        playlistHelper.playingIndex -= 1
                        songIndex = playlistHelper.playingIndex
                    }
                }
                playMusic(songIndex)
            } else {
                player.seekTo(0)
            }
        }
    }

    fun changeRepeatMode(): Int {
        getSharedPreferences("Setting", Context.MODE_PRIVATE).apply {
            edit().apply {
                return when (repeat) {
                    0 -> {
                        putInt("repeat", 1).apply()
                        repeat = 1
                        R.mipmap.baseline_repeat_black_48_red
                    }
                    1 -> {
                        putInt("repeat", 2).apply()
                        repeat = 2
                        player.isLooping = true
                        R.mipmap.baseline_repeat_one_black_48_red
                    }
                    2 -> {
                        putInt("repeat", 0).apply()
                        repeat = 0
                        player.isLooping = false
                        R.mipmap.baseline_repeat_black_48
                    }
                    else -> {
                        R.mipmap.baseline_repeat_black_48
                    }
                }
            }
        }
        return R.mipmap.baseline_repeat_black_48
    }

    fun shuffleClick(): Int {
        return if (playlistHelper.shuffleEnabled) {
            playlistHelper.shuffleEnabled = false
            playlistHelper.shuffleList.clear()
            playlistHelper.shuffleIndex = 0
            getSharedPreferences("Setting", Context.MODE_PRIVATE).apply {
                edit().apply {
                    putBoolean("shuffle", false).apply()
                }
            }
            R.mipmap.baseline_shuffle_black_48
        } else {
            playlistHelper.shuffleEnabled = true
            playlistHelper.shuffle()
            getSharedPreferences("Setting", Context.MODE_PRIVATE).apply {
                edit().apply {
                    putBoolean("shuffle", true).apply()
                }
            }
            R.mipmap.baseline_shuffle_black_48_red
        }
    }

    fun changeVolumeTo(volume: Int) {
        this.volume = volume / 100f
        player.setVolume(this.volume, this.volume)
        getSharedPreferences("Setting", Context.MODE_PRIVATE).apply {
            edit().apply {
                putInt("volume", volume).apply()
            }
        }
    }

    fun playComplete() {
        if (PlaylistHelper.getInstance().playingIndex < PlaylistHelper.getInstance().playlist.count() - 1) {
            playNext()
        } else {
            if (repeat == 1) {
                if (playlistHelper.shuffleEnabled) {
                    playlistHelper.shuffle()
                } else {
                    playlistHelper.playingIndex = 0
                }
                playMusic(0)
            }
        }
    }
}