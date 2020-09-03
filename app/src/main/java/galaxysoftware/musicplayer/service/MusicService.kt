package galaxysoftware.musicplayer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.MusicCallback
import galaxysoftware.musicplayer.helper.PlaylistHelper

class MusicService : Service() {
    private val binder = MusicServiceBinder()
    lateinit var player: MediaPlayer
    private lateinit var playlistHelper: PlaylistHelper

    private var volume: Float = 0f

    var repeat = 0

    var musicCallback: MusicCallback? = null

    /**
     * Provide service as binder to control service
     */
    inner class MusicServiceBinder : Binder() {
        fun getService() = this@MusicService
    }

    /**
     * Provide binder
     */
    override fun onBind(intent: Intent?) = binder

    /**
     * Set Start mode. START_STICKY is the mode that enables service to continue working even if the app went background
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    /**
     * Called when Service is started
     *
     * Initializing MediaPlayer for playing music
     */
    override fun onCreate() {
        super.onCreate()
        playlistHelper = PlaylistHelper.instance
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

    /**
     * Play music by given position
     *
     * Also Notifies activity success or failure
     */
    fun playMusic(position: Int) {
        player.stop()
        player.reset()
        try {
            player.setDataSource(PlaylistHelper.instance.getSongPath(position))
            if (repeat == 2) {
                player.isLooping = true
            }
            player.prepare()
            player.start()
        } catch (e: Exception) {
            musicCallback?.errorPlayingSong()
        }
        musicCallback?.onMusicStart()
    }

    /**
     * Play music by given path
     *
     * This is used on ExportPlayer, which is used as providing music play service to play from music file
     */
    fun playFromFile(path: String) {
        try {
            player.setDataSource(path)
            player.prepare()
            player.start()
        } catch (e: Exception) {
            musicCallback?.errorPlayingSong()
        }
        musicCallback?.onMusicStart()
    }

    /**
     * Play or pause the player and returning the image by state
     */
    fun playOrPause(): Int {
        return if (player.isPlaying) {
            player.pause()
            R.mipmap.baseline_play_circle_filled_black_48
        } else {
            player.start()
            R.mipmap.baseline_pause_circle_filled_black_48
        }
    }

    /**
     * Start Player
     */
    fun play() = player.start()

    /**
     * Pause Player
     */
    fun pause() = player.pause()

    /**
     * Play next song if possible
     */
    fun playNext() {
        if (repeat == 2) {
            player.seekTo(0)
            return
        }
        if (playlistHelper.isNextSongAvailable()) {
            if (playlistHelper.shuffleEnabled) {
                playlistHelper.shuffleIndex += 1
                playMusic(playlistHelper.shuffleIndex)
            } else {
                playlistHelper.playingIndex += 1
                playMusic(playlistHelper.playingIndex)
            }
        } else {
            if (repeat == 1) {
                playlistHelper.refreshPlaylist()
                playMusic(0)
            }
        }

    }

    /**
     * Back to previous song if possible
     */
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

    /**
     * Change repeat mode state. Also returning the resource id depending on the state
     */
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
    }

    /**
     * Changing shuffle state. Also returning the resource id depending on the state
     */
    fun shuffleClick() = if (playlistHelper.shuffleEnabled) {
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

    /**
     * Change volume
     */
    fun changeVolumeTo(volume: Int) {
        this.volume = volume / 100f
        player.setVolume(this.volume, this.volume)
        getSharedPreferences("Setting", Context.MODE_PRIVATE).apply {
            edit().apply {
                putInt("volume", volume).apply()
            }
        }
    }

    /**
     * Called when the music play completed
     */
    fun playComplete() = if (playlistHelper.isNextSongAvailable()) {
        playNext()
    } else {
        if (repeat == 1) {
            playlistHelper.refreshPlaylist()
            playMusic(0)
        } else {

        }
    }
}