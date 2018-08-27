package galaxysoftware.musicplayer.fragment

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.PlaylistSongsAdapter
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.FragmentType
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_song_list.*

/**
 * Use the [PlaylistSongsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PlaylistSongsFragment : BaseFragment(), SongSelectedListener {

    lateinit var name: String
    lateinit var adapter: PlaylistSongsAdapter
    private val history = ArrayList<BaseFragment>()

    /**
     * Called when Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        arguments?.let {
            name = it.getString(NAME)!!
        }
        adapter = PlaylistSongsAdapter(this, name)
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlaylistSongsFragment.adapter
        }
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_playlist_songs

    override fun updateFragment() {

    }

    /**
     * Checking if any songs are added
     */
    private fun checkEmpty() {
        if (adapter.itemCount == 0) {
            if (history.size == 0) {
                history.add(NoItemFragment.newInstance(R.string.no_music))
                fragmentManager!!.beginTransaction().add(R.id.content, history[0]).commit()
            }
        } else {
            if (history.size > 1) {
                fragmentManager?.beginTransaction()?.remove(history[0])?.commit()
                history.removeAt(history.size - 1)
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
     * Called when song selected to play music
     */
    override fun onClick(position: Int) {
        playSelectedSong(position)
    }

    /**
     * Called when the menu icon is clicked (ANDROID'S CALLBACK
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.edit_playlist -> {
                requestChangeFragment(FragmentType.ADD_TO_PLAYLIST, name, FragmentType.ADD_TO_PLAYLIST.title)
            }
            R.id.delete -> {
                AlertDialog.Builder(context!!).apply {
                    setMessage(R.string.confirm_delete)
                    setPositiveButton("OK") { _, _ ->
                        Realm.getDefaultInstance().executeTransaction {
                            it.where(Playlist::class.java).equalTo("name", name).findFirst()?.deleteFromRealm()
                        }
                        backFragment()
                    }
                    setNegativeButton("Cancel", null)
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val NAME = "Name"

        /**
         * Creating the instance of this Fragment
         *
         * receiving which Playlist is selected by name as String
         */
        @JvmStatic
        fun newInstance(name: Any) = PlaylistSongsFragment().apply {
            arguments = Bundle().apply {
                putString(NAME, name as String)
            }
        }
    }
}
