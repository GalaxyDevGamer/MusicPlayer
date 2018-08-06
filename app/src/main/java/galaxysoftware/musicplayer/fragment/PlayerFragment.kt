package galaxysoftware.musicplayer.fragment

import android.content.Context
import android.os.Handler
import android.support.v4.app.Fragment
import android.widget.SeekBar
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.service.MusicService
import galaxysoftware.musicplayer.type.NavigationType
import kotlinx.android.synthetic.main.playerlayout.*

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : BaseFragment() {

    lateinit var musicService: MusicService
    private lateinit var showPlayTime: Handler

    override fun initialize() {
        musicService = getMainActivity().musicService!!
        playtimeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                this.progress = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {musicService.player.seekTo(progress)}
        })
        volumeBar.apply {
            max = 100
            progress = getMainActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("volume", 50)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                var volume = 0
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    volume = progress
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) { musicService.changeVolumeTo(volume) }
            })
        }
        previous.setOnClickListener {
            musicService.backToPrevious()
            updateSongInfo()
        }
        play_pause.apply {
            setImageResource(if (musicService.player.isPlaying) R.mipmap.baseline_pause_circle_filled_black_48 else R.mipmap.baseline_play_circle_filled_black_48)
            setOnClickListener {
                setImageResource(musicService.playOrPause())
                updateSongInfo()
                if (musicService.player.isPlaying) {
                    startCounting()
                } else {
                    showPlayTime.removeCallbacksAndMessages(null)
                }
            }
        }
        skip.setOnClickListener {
            musicService.playNext()
            updateSongInfo()
        }
        shuffle.apply {
            setImageResource(if (PlaylistHelper.getInstance().shuffleEnabled) R.mipmap.baseline_shuffle_black_48_red else R.mipmap.baseline_shuffle_black_48)
            setOnClickListener { setImageResource(musicService.shuffleClick()) }
        }
        repeat.apply {
            setImageResource( when(musicService.repeat) {
                1 -> R.mipmap.baseline_repeat_black_48_red
                2 -> R.mipmap.baseline_repeat_one_black_48_red
                else -> R.mipmap.baseline_repeat_black_48
            })
            setOnClickListener { setImageResource(musicService.changeRepeatMode()) }
        }
        showPlayTime = Handler()
    }

    override fun getLayoutId() = R.layout.playerlayout

    override fun updateFragment() {

    }

    fun updateSongInfo() {
        if (!musicService.player.isPlaying) {
            play_pause.setImageResource(R.mipmap.baseline_play_circle_filled_black_48)
            return
        }
        song_title.text = PlaylistHelper.getInstance().getTitle(PlaylistHelper.getInstance().playingIndex)
        thumbnail.setImageBitmap(PlaylistHelper.getInstance().getAlbumArt(PlaylistHelper.getInstance().playingIndex))
        duration.text = getTimeString(musicService.player.duration.toLong())
        playtime.text = getTimeString(musicService.player.currentPosition.toLong())
        playtimeBar.max = musicService.player.duration
        playtimeBar.progress = musicService.player.currentPosition
    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.CLOSE, "", R.menu.empty)
        updateSongInfo()
        startCounting()
    }

    override fun onPause() {
        super.onPause()
        showPlayTime.removeCallbacksAndMessages(null)
    }

    private fun startCounting() {
        if (musicService.player.isPlaying){
            showPlayTime.postDelayed(object : Runnable{
                override fun run() {
                    playtimeBar.progress = musicService.player.currentPosition
                    playtime.text = getTimeString(musicService.player.currentPosition.toLong())
                    showPlayTime.postDelayed(this, 1000)
                }
            }, 1000)
        }
    }

    private fun getTimeString(millis: Long): String {
        val buf = StringBuffer()
        if (millis >= 6000000) {
            val hours = millis / (1000*60*60)
            val minutes = (millis % (1000*60*60)) / (1000*60)
            val seconds = ((millis % (1000*60*60)) % (1000 * 60)) / 1000
            buf.append(String.format("%02d", hours)).append(":").append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds))
        } else {
            val minutes = (millis % (1000*60*60)) / (1000*60)
            val seconds = ((millis % (1000*60*60)) % (1000 * 60)) / 1000
            buf.append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds))
        }
        return buf.toString()
    }

    fun updateVolume(volume: Int) { volumeBar.progress = volume }

    companion object {

        @JvmStatic
        fun newInstance() = PlayerFragment()
    }
}