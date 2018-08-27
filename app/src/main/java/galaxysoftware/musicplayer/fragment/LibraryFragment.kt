package galaxysoftware.musicplayer.fragment

import android.support.v7.widget.LinearLayoutManager
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.LibraryAdapter
import galaxysoftware.musicplayer.callback.SongSelectedListener
import kotlinx.android.synthetic.main.fragment_song_list.*

class LibraryFragment : BaseFragment(), SongSelectedListener {

    /**
     * Called when the Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = LibraryAdapter(this@LibraryFragment)
        }
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_song_list

    override fun updateFragment() {

    }

    /**
     * Called when song selected to play music
     */
    override fun onClick(position: Int) = playSelectedSong(position)

    companion object {

        /**
         * Creating instance of this Fragment
         */
        @JvmStatic
        fun newInstance() = LibraryFragment()
    }
}