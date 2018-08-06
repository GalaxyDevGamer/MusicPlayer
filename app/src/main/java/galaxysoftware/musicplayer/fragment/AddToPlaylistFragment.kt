package galaxysoftware.musicplayer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.AddToPlaylistAdapter
import galaxysoftware.musicplayer.adapter.LibraryAdapter
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.NavigationType
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_library.*

class AddToPlaylistFragment : BaseFragment() {

    lateinit var adapter: AddToPlaylistAdapter
    lateinit var name: String

    override fun initialize() {
        name = arguments!!.getString(NAME)
        adapter = AddToPlaylistAdapter(name)
        libraryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AddToPlaylistFragment.adapter
        }
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_library

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.BACK, getString(R.string.add_songs), R.menu.done)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (adapter.selectedSongs.count() > 0) {
            val playlist = Playlist().apply {
                name = this@AddToPlaylistFragment.name
                songs = adapter.selectedSongs
            }
            Log.e("name", name+": ")
            Log.e("songs", adapter.selectedSongs.toString()+"")
            Realm.getDefaultInstance().executeTransaction {
                it.insertOrUpdate(playlist)
            }
            backFragment()
        } else {
            //Show Snackbar
            Toast.makeText(context, "No song selected", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val NAME = "NAME"

        @JvmStatic
        fun newInstance(name: Any) = AddToPlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(NAME, name as String)
            }
        }
    }
}