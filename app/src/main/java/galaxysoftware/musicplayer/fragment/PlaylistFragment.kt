package galaxysoftware.musicplayer.fragment

import android.app.Dialog
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.EditText
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.PlaylistAdapter
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.callback.PlaylistSelectedListener
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_song_list.*
import kotlinx.android.synthetic.main.long_press_menu.*

class PlaylistFragment : BaseFragment(), PlaylistSelectedListener {

    lateinit var adapter: PlaylistAdapter
    private val playlistHistory = ArrayList<BaseFragment>()

    /**
     * Called when Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        adapter = PlaylistAdapter(this)
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlaylistFragment.adapter
        }
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_song_list

    override fun updateFragment() {

    }

    /**
     * Checking if any playlist are made
     */
    private fun checkEmpty() {
        if (adapter.itemCount == 0) {
            playlistHistory.add(NoItemFragment.newInstance(R.string.no_playlist))
            fragmentManager!!.beginTransaction().add(R.id.playlistTabContainer, playlistHistory[0]).commit()
        } else {
            if (playlistHistory.size > 0) {
                fragmentManager!!.beginTransaction().remove(playlistHistory[0]).commit()
                playlistHistory.removeAt(playlistHistory.size - 1)
            }
        }
    }

    /**
     * Called when the Fragment is resumed
     */
    override fun onResume() {
        super.onResume()
        adapter.refresh()
        checkEmpty()
    }

    /**
     * Called when the playlist is selected
     * Setting the data for next fragment, then changing fragment.
     */
    override fun onPlaylistSelected(name: String) {
        updateToolbar(FragmentType.PLAYLIST, NavigationType.BACK, name, R.menu.playlist_content)
        requestChangeFragment(FragmentType.PLAYLIST, name)
    }

    /**
     * Called when the menu icon is clicked (ANDROID'S CALLBACK
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_playlist -> {
                val name = EditText(context)
                AlertDialog.Builder(context!!).apply {
                    setMessage(R.string.playlist_name)
                    setView(name)
                    setPositiveButton("OK") { _, _ ->
                        if (Realm.getDefaultInstance().where(Playlist::class.java).equalTo("name", name.text.toString()).count() > 0) {
                            AlertDialog.Builder(context).apply {
                                setMessage(R.string.playlist_exists)
                                setPositiveButton("OK", null)
                                show()
                            }
                        } else {
                            val playlist = Playlist()
                            playlist.name = name.text.toString()
                            Realm.getDefaultInstance().executeTransaction {
                                it.insertOrUpdate(playlist)
                            }
                            adapter.notifyDataSetChanged()
                            updateToolbar(FragmentType.PLAYLIST, NavigationType.BACK, name.text.toString(), R.menu.playlist_content)
                            requestChangeFragment(FragmentType.PLAYLIST, name.text.toString())
                        }
                    }
                    setNegativeButton("Cancel", null)
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Called when the playlist long pressed
     */
    override fun onLongSelection(item: Playlist) {
        Dialog(context!!).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.long_press_menu)
            delete.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setMessage(R.string.confirm_delete)
                    setPositiveButton("OK") { p0, p1 ->
                        Realm.getDefaultInstance().executeTransaction {
                            item.deleteFromRealm()
                        }
                        adapter.notifyDataSetChanged()
                        checkEmpty()
                        dismiss()
                    }
                    setNegativeButton("Cancel", null)
                    show()
                }
                dismiss()
            }
            show()
        }
    }

    companion object {

        /**
         * Creating instance of this Fragment
         */
        @JvmStatic
        fun newInstance() = PlaylistFragment()
    }
}