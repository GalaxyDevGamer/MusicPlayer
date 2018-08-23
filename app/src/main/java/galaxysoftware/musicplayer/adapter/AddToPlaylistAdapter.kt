package galaxysoftware.musicplayer.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.model.LibraryWithSelection
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.realm.Songs
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_song_with_selection.view.*

class AddToPlaylistAdapter(val name: String) : RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder>() {

    val selectedSongs = RealmList<Songs>()
    var songList = PlaylistHelper.getInstance().libraryWithSelection.clone() as ArrayList<LibraryWithSelection>

    init {
        PlaylistHelper.getInstance().uncheckAll()
    }

    /**
     * Called when ViewHolder is created.
     * Define the layout using on ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song_with_selection, parent, false)
        return ViewHolder(view)
    }

    /**
     * Set the info to show
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.thumbnailView.setImageBitmap(PlaylistHelper.getInstance().getCoverArt(item.song!!.albumArt))
        holder.titleView.text = item.song!!.title
        if (Realm.getDefaultInstance().where(Playlist::class.java).equalTo("name", name).and().equalTo("songs.path", item.song!!.path).count() > 0) {
            holder.checkMark.visibility = View.VISIBLE
            selectedSongs.add(item.song)
            item.isSelected = true
        }
        holder.itemView.setOnClickListener {
            if (item.isSelected) {
                selectedSongs.remove(item.song)
            } else {
                selectedSongs.add(item.song)
            }
            item.isSelected =! item.isSelected
            Log.e("songs", selectedSongs.toString())
            holder.checkMark.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    /**
     * Returning the item count in the list
     */
    override fun getItemCount() = songList.size

    /**
     * ViewHolder
     * Defining the views using on List(RecyclerView)
     */
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnailView: ImageView = mView.thumbnail
        val titleView: TextView = mView.title
        val checkMark: ImageView = mView.checkMark
    }
}