package galaxysoftware.musicplayer.fragment

import android.support.v7.widget.LinearLayoutManager
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.AlbumAdapter
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import kotlinx.android.synthetic.main.fragment_album_tab.*

class AlbumFragment : BaseFragment(), ItemSelectedListener {

    override fun initialize() {
        album_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AlbumAdapter(this@AlbumFragment)
        }
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_album_tab

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.NONE, getString(R.string.album), R.menu.empty)
    }
    override fun onItemSelected(item: String) {
        requestChangeFragment(FragmentType.ALBUM_LIST, item)
    }

    companion object {

        @JvmStatic
        fun newInstance() = AlbumFragment()
    }
}
