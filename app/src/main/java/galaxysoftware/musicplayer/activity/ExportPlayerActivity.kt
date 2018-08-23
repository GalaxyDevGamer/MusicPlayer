package galaxysoftware.musicplayer.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.SeekBar
import android.widget.Toast
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.MusicCallback
import galaxysoftware.musicplayer.service.MusicService
import kotlinx.android.synthetic.main.activity_export_player.*

class ExportPlayerActivity : AppCompatActivity(), ServiceConnection, MusicCallback {
    var musicService: MusicService? =  null
    private lateinit var showPlayTime: Handler

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x01

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_player)
        showPlayTime = Handler()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // アクセス権がOFFならここでONにしてもらうようリクエストしてもらう
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // ダイアログを表示する場合。ここで一発アクセスする目的を説明してからリクエストするのが推奨されている。
                // 説明してOKならrequestPermissionsを呼ぶ。
                return
            }
            //ダイアログを表示しない場合。ダイアログで「今後は確認しない」にチェックをした場合に実行される。
            ActivityCompat.requestPermissions(this, Array(1) { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_READ_EXTERNAL_STORAGE)
        } else {
            // アクセス権がONならそのまま処理をすすめる
            setService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可するを選択した場合の処理を実装。
                setService()
            } else {
                // 許可しないを選択した場合を実装
                finish()
            }
            return
        }
    }

    override fun onServiceConnected(component: ComponentName?, iBinder: IBinder?) {
        val binder = iBinder as MusicService.MusicServiceBinder
        musicService = binder.getService()
        initVariable()
        musicService?.playFromFile(intent.data.path!!)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {

    }

    private fun setService() {
        val serviceIntent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

    private fun initVariable() {
        musicService!!.musicCallback = this
        playtimeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                this.progress = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {musicService!!.player.seekTo(progress)}
        })
        volumeBar.apply {
            max = 100
            progress = getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("volume", 50)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                var volume = 0
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    volume = progress
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) { musicService!!.changeVolumeTo(volume) }
            })
        }
        play_pause.apply {
            setImageResource(if (musicService!!.player.isPlaying) R.mipmap.baseline_pause_circle_filled_black_48 else R.mipmap.baseline_play_circle_filled_black_48)
            setOnClickListener {
                setImageResource(musicService!!.playOrPause())
                if (musicService!!.player.isPlaying) {
                    startCounting()
                } else {
                    showPlayTime.removeCallbacksAndMessages(null)
                }
            }
        }
        musicService!!.player.apply {
            setOnCompletionListener { seekTo(0) }
        }
    }

    private fun setSongInfo() {
        playtimeBar.max = musicService!!.player.duration
        song_title.text = intent.data.path
        duration.text = getTimeString(musicService!!.player.duration.toLong())
        play_pause.setImageResource(R.mipmap.baseline_pause_circle_filled_black_48)
    }

    override fun errorPlayingSong() {
        Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show()
        musicService!!.playNext()
    }

    override fun onMusicStart() {
        setSongInfo()
        startCounting()
    }

    override fun onResume() {
        super.onResume()
        startCounting()
    }

    override fun onPause() {
        super.onPause()
        showPlayTime.removeCallbacksAndMessages(null)
    }

    private fun startCounting() {
        if (musicService != null && musicService!!.player.isPlaying){
            showPlayTime.postDelayed(object : Runnable{
                override fun run() {
                    playtimeBar.progress = musicService!!.player.currentPosition
                    playtime.text = getTimeString(musicService!!.player.currentPosition.toLong())
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
}