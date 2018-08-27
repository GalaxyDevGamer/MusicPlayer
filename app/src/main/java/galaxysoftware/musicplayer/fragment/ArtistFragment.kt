package galaxysoftware.musicplayer.fragment

import android.support.v7.widget.LinearLayoutManager
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.ArtistAdapter
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.FragmentType
import kotlinx.android.synthetic.main.fragment_song_list.*

class ArtistFragment : BaseFragment(), ItemSelectedListener {

    /**
     * Called when the Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ArtistAdapter(this@ArtistFragment)
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
    override fun onItemSelected(index: Int) = requestChangeFragment(FragmentType.ARTIST_LIST, index, PlaylistHelper.instance.artists[index].title!!)

    override fun onLongSelection(item: Playlist) {

    }

    companion object {
        /**
         * Creating instance of this Fragment
         */
        @JvmStatic
        fun newInstance() = ArtistFragment()
    }
}
