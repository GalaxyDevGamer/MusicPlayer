package galaxysoftware.musicplayer.fragment

import android.support.v7.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.AlbumAdapter
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_song_list.*

class AlbumFragment : BaseFragment(), ItemSelectedListener {

    /**
     * Called when the Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AlbumAdapter(this@AlbumFragment)
        }
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_song_list

    override fun updateFragment() {

    }

    /**
     * Called when Artist is selected
     * Setting the data for next fragment, then changing fragment.
     */
    override fun onItemSelected(index: Int) = findNavController().navigate(AlbumFragmentDirections.actionAlbumFragmentToAlbumSongsFragment(PlaylistHelper.instance.albums[index].title!!).apply { this.index = index })

    override fun onLongSelection(item: Playlist) {

    }

    companion object {

        /**
         * Creating instance of this Fragment
         */
        @JvmStatic
        fun newInstance() = AlbumFragment()
    }
}
