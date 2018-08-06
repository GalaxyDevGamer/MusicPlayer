package galaxysoftware.musicplayer.fragment

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.EditText
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.adapter.PlaylistAdapter
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_playlist.*

class PlaylistFragment : BaseFragment(), ItemSelectedListener {

    lateinit var adapter: PlaylistAdapter

    override fun initialize() {
        adapter = PlaylistAdapter(this)
        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PlaylistFragment.adapter
        }
        setHasOptionsMenu(true)
    }

    override fun getLayoutId() = R.layout.fragment_playlist

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        updateToolbar(NavigationType.NONE, getString(R.string.playlist), R.menu.playlist_tab)
    }

    override fun onItemSelected(item: String) {
        requestChangeFragment(FragmentType.PLAYLIST, item)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val name = EditText(context)
        AlertDialog.Builder(context!!).apply {
            setMessage(R.string.playlist_name)
            setView(name)
            setPositiveButton("OK") { _, _ ->
                if (Realm.getDefaultInstance().where(Playlist::class.java).equalTo("name", name.text.toString()).count() > 0) {
                    AlertDialog.Builder(context).apply {
                        setMessage(R.string.playlist_exists)
                        setPositiveButton("OK", null)
                        show()
                    }
                } else {
                    val playlist = Playlist()
                    playlist.name = name.text.toString()
                    Realm.getDefaultInstance().executeTransaction {
                        it.insertOrUpdate(playlist)
                    }
                    adapter.notifyDataSetChanged()
                    requestChangeFragment(FragmentType.PLAYLIST, name.text.toString())
                }
            }
            setNegativeButton("Cancel", null)
            show()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun newInstance() = PlaylistFragment()
    }
}