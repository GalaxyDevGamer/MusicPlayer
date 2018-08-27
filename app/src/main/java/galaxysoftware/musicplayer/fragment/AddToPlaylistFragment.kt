package galaxysoftware.musicplayer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.AddToPlaylistAdapter
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_song_list.*

class AddToPlaylistFragment : BaseFragment() {

    lateinit var adapter: AddToPlaylistAdapter
    lateinit var name: String

    /**
     * Called when Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        name = arguments!!.getString(NAME)!!
        adapter = AddToPlaylistAdapter(name)
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AddToPlaylistFragment.adapter
        }
        setHasOptionsMenu(true)
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_song_list

    override fun updateFragment() {

    }

    /**
     * Called when the menu icon is clicked (ANDROID'S CALLBACK
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.done -> {
                if (adapter.selectedSongs.count() > 0) {
                    val playlist = Playlist().apply {
                        name = this@AddToPlaylistFragment.name
                        songs = adapter.selectedSongs
                    }
                    Log.e("name", name + ": ")
                    Log.e("songs", adapter.selectedSongs.toString() + "")
                    Realm.getDefaultInstance().executeTransaction {
                        it.insertOrUpdate(playlist)
                    }
                    PlaylistHelper.instance.uncheckAll()
                    backFragment()
                } else {
                    //Show Snackbar
                    Toast.makeText(context, "No song selected", Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val NAME = "NAME"

        /**
         * Creating the instance of this Fragment
         *
         * receiving which Playlist is selected by name as String
         */
        @JvmStatic
        fun newInstance(name: Any) = AddToPlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(NAME, name as String)
            }
        }
    }
}