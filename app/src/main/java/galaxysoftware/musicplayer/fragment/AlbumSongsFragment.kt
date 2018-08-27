package galaxysoftware.musicplayer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.AlbumSongsAdapter
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper
import kotlinx.android.synthetic.main.fragment_playlist_songs.*
import kotlinx.android.synthetic.main.fragment_song_list.*

class AlbumSongsFragment : BaseFragment(), SongSelectedListener {

    private var index = 0

    /**
     * Called when Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        index = arguments!!.getInt(ARG_COLUMN_COUNT)
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AlbumSongsAdapter(this@AlbumSongsFragment, PlaylistHelper.instance.albums[index].title!!)
        }
        playlist_cover.setImageBitmap(PlaylistHelper.instance.albums[index].thumbnail)
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_playlist_songs

    override fun updateFragment() {

    }

    /**
     * Called when song selected to play music
     */
    override fun onClick(position: Int) = playSelectedSong(position)

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        /**
         * Creating the instance of this Fragment
         *
         * receiving which Artist is selected by item as Int
         */
        @JvmStatic
        fun newInstance(item: Any) = AlbumSongsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COLUMN_COUNT, item as Int)
            }
        }
    }
}