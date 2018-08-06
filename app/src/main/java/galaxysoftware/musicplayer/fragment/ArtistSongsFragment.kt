package galaxysoftware.musicplayer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.ArtistSongsAdapter
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_song_list.*

class ArtistSongsFragment : BaseFragment(), SongSelectedListener {

    lateinit var name: String

    override fun initialize() {
        arguments?.let {
            name = it.getString(ARG_COLUMN_COUNT)
        }
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ArtistSongsAdapter(this@ArtistSongsFragment, name)
        }
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_playlist_songs

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.BACK, name, R.menu.empty)
    }

    override fun onClick(position: Int) {
        playSelectedSong(position)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(item: Any) =
                ArtistSongsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_COLUMN_COUNT, item as String)
                    }
                }
    }
}