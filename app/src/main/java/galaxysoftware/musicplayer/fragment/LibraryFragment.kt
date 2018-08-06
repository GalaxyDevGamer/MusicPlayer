package galaxysoftware.musicplayer.fragment

import android.support.v7.widget.LinearLayoutManager
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.LibraryAdapter
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_library.*

class LibraryFragment : BaseFragment(), SongSelectedListener {

    override fun initialize() {
        libraryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = LibraryAdapter(this@LibraryFragment)
        }
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_library

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.NONE, getString(R.string.library), R.menu.empty)
    }

    override fun onClick(position: Int) {
        playSelectedSong(position)
    }

    companion object {

        @JvmStatic
        fun newInstance() = LibraryFragment()
    }
}