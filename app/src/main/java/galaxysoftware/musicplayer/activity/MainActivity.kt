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
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.ContextData
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.ChangeFragmentListener
import galaxysoftware.musicplayer.callback.MusicCallback
import galaxysoftware.musicplayer.fragment.*
import galaxysoftware.musicplayer.helper.FragmentMakeHelper
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.service.MusicService
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import galaxysoftware.musicplayer.type.TabType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ChangeFragmentListener, ServiceConnection, MusicCallback {

    private val rootHistory = ArrayList<BaseFragment>()

    private var currentTabType = TabType.LIBRARY

    private val fragmentTypeHistory = HashMap<TabType, ArrayList<FragmentType>>()
    private val tabHistory = HashMap<TabType, ArrayList<BaseFragment>>()
    var musicService: MusicService? = null

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x01

    private lateinit var navController: NavController
    /**
     * Called when app is started. (ANDROID'S LIFECYCLE
     * Check for the permission to make sure that READ_EXTERNAL_STORAGE is allowed for getting device files
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        navController = findNavController(R.id.nav_host_fragment)
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

    /**
     * Called when permission dialog is pressed. (ANDROID'S CALLBACK WHEN REQUESTING PERMISSION
     */
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

    /**
     * Initialize the required components and setting data for each tabs
     */
    private fun initVariable() {
        ContextData.instance.applicationContext = applicationContext

//        DialogHelper.init(this)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(bottom_navigation, navController)
//        tabHistory[TabType.LIBRARY] = ArrayList()
//        tabHistory[TabType.LIBRARY]?.add(FragmentMakeHelper.makeFragment(FragmentType.LIBRARY_TAB, ""))
//        tabHistory[TabType.ALBUM] = ArrayList()
//        tabHistory[TabType.ALBUM]?.add(FragmentMakeHelper.makeFragment(FragmentType.ALBUM_TAB, ""))
//        tabHistory[TabType.ARTIST] = ArrayList()
//        tabHistory[TabType.ARTIST]?.add(FragmentMakeHelper.makeFragment(FragmentType.ARTIST_TAB, ""))
//        tabHistory[TabType.PLAYLIST] = ArrayList()
//        tabHistory[TabType.PLAYLIST]?.add(FragmentMakeHelper.makeFragment(FragmentType.PLAYLIST_TAB, ""))
//        fragmentTypeHistory[TabType.LIBRARY] = ArrayList()
//        fragmentTypeHistory[TabType.ALBUM] = ArrayList()
//        fragmentTypeHistory[TabType.ARTIST] = ArrayList()
//        fragmentTypeHistory[TabType.PLAYLIST] = ArrayList()
//        fragmentTypeHistory[TabType.LIBRARY]?.add(FragmentType.LIBRARY_TAB)
//        fragmentTypeHistory[TabType.ALBUM]?.add(FragmentType.ALBUM_TAB)
//        fragmentTypeHistory[TabType.ARTIST]?.add(FragmentType.ARTIST_TAB)
//        fragmentTypeHistory[TabType.PLAYLIST]?.add(FragmentType.PLAYLIST_TAB)
        setButton()
        setService()
    }

    override fun onNavigateUp() = navController.navigateUp()

    /**
     * Set onClickListener for ButtonNavigationView and SmallController(It's on the layout file)
     */
    private fun setButton() {
        play.setOnClickListener { play.setImageResource(musicService?.playOrPause()!!) }
        skip.setOnClickListener { musicService?.playNext() }
//        bottom_navigation.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.libraryTab -> {
//                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.LIBRARY)
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.albumTab -> {
//                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.ALBUM)
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.artistTab -> {
//                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.ARTIST)
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.playlistTab -> {
//                    this@MainActivity.changeTab(galaxysoftware.musicplayer.type.TabType.PLAYLIST)
//                    return@setOnNavigationItemSelectedListener true
//                }
//            }
//            false
//        }
        small_controller.setOnClickListener {
            if (PlaylistHelper.instance.playlist.size > 0) {
                rootHistory.add(PlayerFragment.newInstance())
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.root_container, rootHistory[0])
                    commit()
                }
            }
        }
    }

    /**
     * Set the service
     * Careful on the method for setting service. It's changed from Android 8.0
     */
    private fun setService() {
        val serviceIntent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

    /**
     * Called when the service is connected (ANDROID'S CALLBACK WHEN USING SERVICE
     */
    override fun onServiceConnected(component: ComponentName?, iBinder: IBinder?) {
        val binder = iBinder as MusicService.MusicServiceBinder
        musicService = binder.getService()
        musicService?.musicCallback = this@MainActivity
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

    /**
     * Called when the service is disconnected (ANDROID'S CALLBACK WHEN USING SERVICE
     */
    override fun onServiceDisconnected(p0: ComponentName?) {

    }

    /**
     * Loading the SplashFragment
     * This is called from onServiceConnected()
     */
    private fun loadSplashFragment() {
        rootHistory.add(SplashFragment.newInstance())
        supportFragmentManager.beginTransaction().add(R.id.root_container, rootHistory[0]).commit()
    }

    /**
     * Switch to Library Tab.
     * Called from SplashFragment.
     */
    fun loadLibrary() {
//        supportFragmentManager.beginTransaction().apply {
//            remove(rootHistory[0])
//            replace(R.id.libraryTabContainer, tabHistory[TabType.LIBRARY]!![0])
//            replace(R.id.albumTabContainer, tabHistory[TabType.ALBUM]!![0])
//            replace(R.id.artistTabContainer, tabHistory[TabType.ARTIST]!![0])
//            replace(R.id.playlistTabContainer, tabHistory[TabType.PLAYLIST]!![0])
//            commit()
//        }
//        rootHistory.remove(rootHistory[0])
//        changeTab(TabType.LIBRARY)
    }

    /**
     * Called when changing the tab.
     * Called from callback on bottom_navigation
     */
//    private fun changeTab(tabType: TabType) {
//        currentTabType = tabType
//        updateToolbar()
//        libraryTabContainer.visibility = if (currentTabType == TabType.LIBRARY) View.VISIBLE else View.INVISIBLE
//        albumTabContainer.visibility = if (currentTabType == TabType.ALBUM) View.VISIBLE else View.INVISIBLE
//        artistTabContainer.visibility = if (currentTabType == TabType.ARTIST) View.VISIBLE else View.INVISIBLE
//        playlistTabContainer.visibility = if (currentTabType == TabType.PLAYLIST) View.VISIBLE else View.INVISIBLE
//    }

    /**
     * Playing selected music.
     * Called when the music on the list is selected.
     */
    fun playMusic(position: Int) {
        var index = position
        PlaylistHelper.instance.playingIndex = position
        if (PlaylistHelper.instance.shuffleEnabled) {
            PlaylistHelper.instance.shuffle()
            index = 0
        }
        musicService?.playMusic(index)
    }

    /**
     * Called when music is started.
     * Called from musicService
     */
    override fun onMusicStart() {
        updatePlayer()
        updatePlayStateButton()
    }

    /**
     * Called if the error occurred when try to play music
     */
    override fun errorPlayingSong() {
        Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show()
        musicService!!.playNext()
    }

    /**
     * Updating the song information
     */
    private fun updatePlayer() {
        sc_thumbnail.setImageBitmap(PlaylistHelper.instance.getAlbumArt())
        titleView.text = PlaylistHelper.instance.getTitle()
    }

    /**
     * Updating the button image by play state of MediaPlayer
     */
    private fun updatePlayStateButton() = if (musicService?.player?.isPlaying!!) play.setImageResource(R.mipmap.baseline_pause_circle_filled_black_48) else play.setImageResource(R.mipmap.baseline_play_circle_filled_black_48)

    /**
     * Called when the app is returned (ANDROID'S DEFAULT LIFECYCLE
     */
    override fun onResume() {
        super.onResume()
        if (PlaylistHelper.instance.playlist.size > 0) {
            updatePlayStateButton()
            updatePlayer()
        }
    }

    /**
     * Updating the menu. (ANDROID'S CALLBACK
     * Called if invalidateOptionsMenu() is called.
     */
//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu?.clear()
//        menuInflater.inflate(currentFragmentType().menu, menu)
//        return super.onPrepareOptionsMenu(menu)
//    }

    /**
     * Called from outside of the MainActivity for changing fragment
     */
    override fun onChangeFragment(fragmentType: FragmentType, any: Any, title: String) {
        val fragment = FragmentMakeHelper.makeFragment(fragmentType, any)
        fragmentType.title = title
        tabHistory[currentTabType]?.add(fragment)
        fragmentTypeHistory[currentTabType]?.add(fragmentType)
//        replaceFragment(getCurrentFragment())
//        updateToolbar()
    }

    /**
     * Return current Fragment on current tab
     */
    private fun getCurrentFragment() = tabHistory[currentTabType]!![tabHistory[currentTabType]!!.size - 1]

    /**
     * Returns current FragmentType based on each tabs
     */
//    fun currentFragmentType() = fragmentTypeHistory[currentTabType]!![fragmentTypeHistory[currentTabType]!!.size - 1]

    /**
     * Back Fragment
     * Called when back key pressed or navigation icon(On the Toolbar) is pressed
     */
    fun backFragment() {
        tabHistory[currentTabType]!!.removeAt(tabHistory[currentTabType]!!.size - 1)
        fragmentTypeHistory[currentTabType]!!.removeAt(fragmentTypeHistory[currentTabType]!!.size - 1)
//        replaceFragment(getCurrentFragment())
//        updateToolbar()
    }

    /**
     * Replacing Fragment
     */
//    private fun replaceFragment(fragment: BaseFragment) = supportFragmentManager.beginTransaction().apply {
//        when (currentTabType) {
//            TabType.LIBRARY -> replace(R.id.libraryTabContainer, fragment)
//            TabType.ALBUM -> replace(R.id.albumTabContainer, fragment)
//            TabType.ARTIST -> replace(R.id.artistTabContainer, fragment)
//            TabType.PLAYLIST -> replace(R.id.playlistTabContainer, fragment)
//        }
//        commit()
//    }

    /**
     * Called when back key is pressed (ANDROID'S CALLBACK
     */
    override fun onBackPressed() {
        if (rootHistory.size > 0) {
            closePLayerLayout()
            return
        }
        if (currentTabType == TabType.LIBRARY && tabHistory[currentTabType]!!.size == 1) {
            finish()
            return
        }
        if (currentTabType != TabType.LIBRARY && tabHistory[currentTabType]!!.size == 1) {
//            changeTab(TabType.LIBRARY)
            return
        }
        backFragment()
    }

    /**
     * Updating Toolbar
     *
     * *Icon: BACK or CLOSE
     * *Title: Title
     * invalidateOptionsMenu(): Update Menu
     */
//    private fun updateToolbar() {
//        when (currentFragmentType().navigation) {
//            NavigationType.BACK -> {
//                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.mipmap.baseline_keyboard_arrow_left_black_24)
//                toolbar.setNavigationOnClickListener { backFragment() }
//            }
//            NavigationType.CLOSE -> {
//                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.mipmap.baseline_clear_black_48)
//                toolbar.setNavigationOnClickListener { closePLayerLayout() }
//            }
//            else -> {
//                toolbar.navigationIcon = null
//                toolbar.setNavigationOnClickListener(null)
//            }
//        }
//        toolbar.title = currentFragmentType().title
//        invalidateOptionsMenu()
//    }

    /**
     * Close Player layout
     */
    private fun closePLayerLayout() {
        supportFragmentManager.beginTransaction().remove(rootHistory[0]).commit()
        rootHistory.removeAt(0)
//        updateToolbar()
    }

    /**
     * Called if any KEY is pressed (ANDROID'S CALLBACK
     * Using for detecting volume key press
     */
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

    /**
     * Called when app is finishing (ANDROID'S LIFECYCLE
     */
    override fun onDestroy() {
        super.onDestroy()
        if (musicService != null)
            unbindService(this)
    }
}