package galaxysoftware.musicplayer.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.ContextData
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.ChangeFragmentListener
import galaxysoftware.musicplayer.callback.PlayStateChangedListener
import galaxysoftware.musicplayer.fragment.*
import galaxysoftware.musicplayer.helper.FragmentMakeHelper
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.service.MusicService
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import galaxysoftware.musicplayer.type.TabType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.small_controller.*


class MainActivity : AppCompatActivity(), ChangeFragmentListener, ServiceConnection, PlayStateChangedListener {

    private val libraryTabHistory = ArrayList<BaseFragment>()
    private val albumTabHistory = ArrayList<BaseFragment>()
    private val artistTabHistory = ArrayList<BaseFragment>()
    private val playlistTabHistory = ArrayList<BaseFragment>()
    private val rootHistory = ArrayList<BaseFragment>()

    private var currentTabType = TabType.LIBRARY

    private val libraryFragment = LibraryFragment.newInstance()
    private val albumFragment = AlbumFragment.newInstance()
    private val artistFragment = ArtistFragment.newInstance()
    private val playlistFragment = PlaylistFragment.newInstance()

    private val navData = HashMap<TabType, NavigationType>()
    private val titleData = HashMap<TabType, String>()
    private val menuData = HashMap<TabType, Int>()
    private lateinit var serviceIntent: Intent
    var musicService: MusicService? = null

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x01
    var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // アクセス権がOFFならここでONにしてもらうようリクエストしてもらう
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                // ダイアログを表示する場合。ここで一発アクセスする目的を説明してからリクエストするのが推奨されている。
                // 説明してOKならrequestPermissionsを呼ぶ。
                return
            }
            //ダイアログを表示しない場合。ダイアログで「今後は確認しない」にチェックをした場合に実行される。
            ActivityCompat.requestPermissions(this, Array(1) { READ_EXTERNAL_STORAGE }, REQUEST_CODE_READ_EXTERNAL_STORAGE)
        } else {
            // アクセス権がONならそのまま処理をすすめる
            initVariable()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可するを選択した場合の処理を実装。
                initVariable()
            } else {
                // 許可しないを選択した場合を実装
                finish()
            }
            return
        }
    }

    private fun loadSplashFragment() {
        rootHistory.add(SplashFragment.newInstance())
        supportFragmentManager.beginTransaction().apply {
            add(R.id.root_container, rootHistory[0])
            commit()
        }
    }

    fun loadLibrary() {
        supportFragmentManager.beginTransaction().apply {
            remove(rootHistory[0])
            commit()
        }
        rootHistory.remove(rootHistory[0])
        fragmentTransaction(libraryFragment)
        initialized = true
    }

    override fun onServiceConnected(component: ComponentName?, iBinder: IBinder?) {
        val binder = iBinder as MusicService.MusicServiceBinder
        musicService = binder.getService()
        musicService?.playStateChangeListener = this@MainActivity
        loadSplashFragment()
        musicService?.player?.setOnCompletionListener {
            musicService?.playComplete()
            updatePlayer()
            if (rootHistory.size > 0) {
                val player = rootHistory[0] as PlayerFragment
                player.updateSongInfo()
            }
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {

    }

    override fun onMusicStart(position: Int) {
        updatePlayer()
        updatePlayStateButton()
    }

    private fun initVariable() {
        ContextData.getInstance().activity = this
        ContextData.getInstance().mainActivity = this@MainActivity
        ContextData.getInstance().applicationContext = applicationContext
        setService()
//        DialogHelper.init(this)
        libraryTabHistory.add(libraryFragment)
        albumTabHistory.add(albumFragment)
        artistTabHistory.add(artistFragment)
        playlistTabHistory.add(playlistFragment)
        menuData[TabType.LIBRARY] = R.menu.empty
        menuData[TabType.ALBUM] = R.menu.empty
        menuData[TabType.ARTIST] = R.menu.empty
        menuData[TabType.PLAYLIST] = R.menu.empty
        setButton()
    }

    private fun setService() {
        serviceIntent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

    private fun setButton() {
        play.setOnClickListener { play.setImageResource(musicService?.playOrPause()!!) }
        skip.setOnClickListener { musicService?.playNext() }
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.libraryTab -> {
                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.LIBRARY)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.albumTab -> {
                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.ALBUM)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.artistTab -> {
                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.ARTIST)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.playlistTab -> {
                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.PLAYLIST)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
        small_controller.setOnClickListener {
            if (PlaylistHelper.getInstance().playlist.size > 0) {
                rootHistory.add(PlayerFragment.newInstance())
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.root_container, rootHistory[0])
                    commit()
                }
            }
        }
    }

    fun playMusic(position: Int) {
        if (PlaylistHelper.getInstance().shuffleEnabled)
            PlaylistHelper.getInstance().shuffle()
        musicService?.playMusic(position)
    }

    private fun updatePlayer() {
        val helper = PlaylistHelper.getInstance()
//        thumbnail.setImageBitmap(PlaylistHelper.getInstance().getAlbumArt(PlaylistHelper.getInstance().playingIndex))
        thumbnail.setImageBitmap(if (helper.shuffleEnabled) helper.shuffleList[helper.playingIndex].albumArt else helper.playlist[helper.playingIndex].albumArt)
        titleView.text = PlaylistHelper.getInstance().getTitle(PlaylistHelper.getInstance().playingIndex)
    }

    fun updatePlayStateButton() = if (musicService?.player?.isPlaying!!) play.setImageResource(R.mipmap.baseline_pause_circle_filled_black_48) else play.setImageResource(R.mipmap.baseline_play_circle_filled_black_48)

    override fun onResume() {
        super.onResume()
        if (PlaylistHelper.getInstance().playlist.size > 0) {
            Log.e("shuffle is ", PlaylistHelper.getInstance().shuffleEnabled.toString())
            updatePlayStateButton()
            updatePlayer()
        }
    }

    private fun changeTab(tabType: TabType) {
        currentTabType = tabType
        fragmentTransaction(getCurrentFragment())
        updateToolbar()
        libraryTabContainer.visibility = if (currentTabType == TabType.LIBRARY) View.VISIBLE else View.INVISIBLE
        albumTabContainer.visibility = if (currentTabType == TabType.ALBUM) View.VISIBLE else View.INVISIBLE
        artistTabContainer.visibility = if (currentTabType == TabType.ARTIST) View.VISIBLE else View.INVISIBLE
        playlistTabContainer.visibility = if (currentTabType == TabType.PLAYLIST) View.VISIBLE else View.INVISIBLE
        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater?.inflate(menuData[currentTabType]!!, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onChangeFragment(fragmentType: FragmentType, any: Any) = changeFragment(fragmentType, any)

    private fun changeFragment(fragmentType: FragmentType, any: Any) {
        val fragment = FragmentMakeHelper.makeFragment(fragmentType, any)
        when (currentTabType) {
            TabType.LIBRARY -> {
                libraryTabHistory.add(fragment)
            }
            TabType.ALBUM -> {
                albumTabHistory.add(fragment)
            }
            TabType.ARTIST -> {
                artistTabHistory.add(fragment)
            }
            TabType.PLAYLIST -> {
                playlistTabHistory.add(fragment)
            }
        }
        fragmentTransaction(fragment)
        invalidateOptionsMenu()
    }

    private fun getCurrentFragment(): Fragment {
        return when (currentTabType) {
            TabType.LIBRARY -> libraryTabHistory[libraryTabHistory.size - 1]
            TabType.ALBUM -> albumTabHistory[albumTabHistory.size - 1]
            TabType.ARTIST -> artistTabHistory[artistTabHistory.size - 1]
            TabType.PLAYLIST -> playlistTabHistory[playlistTabHistory.size - 1]
        }
    }

    fun backFragment() {
        when (currentTabType) {
            TabType.LIBRARY -> libraryTabHistory.removeAt(libraryTabHistory.size - 1)
            TabType.ALBUM -> {
                if (albumTabHistory.size > 1) {
                    albumTabHistory.removeAt(albumTabHistory.size - 1)
                } else
                    changeTab(TabType.LIBRARY)
            }
            TabType.ARTIST -> {
                if (artistTabHistory.size > 1) {
                    artistTabHistory.removeAt(artistTabHistory.size - 1)
                } else
                    changeTab(TabType.LIBRARY)
            }
            TabType.PLAYLIST -> {
                if (playlistTabHistory.size > 1) {
                    playlistTabHistory.removeAt(playlistTabHistory.size - 1)
                } else
                    changeTab(TabType.LIBRARY)
            }
        }
        fragmentTransaction(getCurrentFragment())
        invalidateOptionsMenu()
    }

    private fun fragmentTransaction(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        when (currentTabType) {
            TabType.LIBRARY -> transaction.replace(R.id.libraryTabContainer, fragment)
            TabType.ALBUM -> transaction.replace(R.id.albumTabContainer, fragment)
            TabType.ARTIST -> transaction.replace(R.id.artistTabContainer, fragment)
            TabType.PLAYLIST -> transaction.replace(R.id.playlistTabContainer, fragment)
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        if (rootHistory.size > 0) {
            closePLayerLayout()
            return
        }
        if (currentTabType == TabType.LIBRARY && libraryTabHistory[libraryTabHistory.size - 1] == libraryFragment) {
            finish()
            return
        }
        backFragment()
    }

    private fun updateToolbar() {
        when (navData[currentTabType]) {
            NavigationType.BACK -> {
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.mipmap.baseline_keyboard_arrow_left_black_24)
                toolbar.setNavigationOnClickListener { backFragment() }
            }
            NavigationType.CLOSE -> {
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.mipmap.baseline_clear_black_48)
                toolbar.setNavigationOnClickListener {
                    closePLayerLayout()
                }
            }
            else -> {
                toolbar.navigationIcon = null
                toolbar.setNavigationOnClickListener(null)
            }
        }
        toolbar.title = titleData[currentTabType]
    }

    fun setData(navigationType: NavigationType, title: String, menu: Int) {
        navData[currentTabType] = navigationType
        titleData[currentTabType] = title
        menuData[currentTabType] = menu
        updateToolbar()
    }

    fun closePLayerLayout() {
        supportFragmentManager.beginTransaction().apply {
            remove(rootHistory[0])
            commit()
        }
        rootHistory.removeAt(0)
        setData(NavigationType.NONE, getString(R.string.library), R.menu.empty)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                var volume = getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("volume", 50)
                if (volume > 0) {
                    volume -= 10
                } else {
                    volume = 0
                }
                if (rootHistory.size > 0) {
                    val player = rootHistory[0] as PlayerFragment
                    player.updateVolume(volume)
                }
                musicService?.changeVolumeTo(volume)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                var volume = getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("volume", 50)
                if (volume < 100) {
                    volume += 10
                } else {
                    volume = 100
                }
                if (rootHistory.size > 0) {
                    val player = rootHistory[0] as PlayerFragment
                    player.updateVolume(volume)
                }
                musicService?.changeVolumeTo(volume)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}