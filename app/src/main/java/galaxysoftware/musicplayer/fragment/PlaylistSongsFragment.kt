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
import galaxysoftware.musicplayer.type.NavigationType
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_playlist_songs.*

/**
 * Use the [PlaylistSongsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PlaylistSongsFragment : BaseFragment(), SongSelectedListener {

    lateinit var name: String
    lateinit var adapter: PlaylistSongsAdapter

    override fun initialize() {
        arguments?.let {
            name = it.getString(NAME)
        }
        adapter = PlaylistSongsAdapter(this, name)
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlaylistSongsFragment.adapter
        }
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_playlist_songs

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.BACK, name, R.menu.playlist_content)
        adapter.refresh()
    }

    override fun onClick(position: Int) {
        playSelectedSong(position)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.add -> {
                requestChangeFragment(FragmentType.ADD_TO_PLAYLIST, name)
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
        @JvmStatic
        fun newInstance(name: Any) =
                PlaylistSongsFragment().apply {
                    arguments = Bundle().apply {
                        putString(NAME, name as String)
                    }
                }
    }
}
