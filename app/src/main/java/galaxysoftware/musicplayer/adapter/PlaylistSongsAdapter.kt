package galaxysoftware.musicplayer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.realm.Playlist
import kotlinx.android.synthetic.main.song_cell.view.*

class PlaylistSongsAdapter(private val listener: SongSelectedListener, val name: String) : RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder>() {

    var playlist = Playlist.songs(name)

    /**
     * Called when ViewHolder is created.
     * Define the layout using on ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_cell, parent, false)
        return ViewHolder(view)
    }

    /**
     * Set the info to show
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = playlist[position]
        holder.thumbnail.setImageResource(R.mipmap.baseline_music_video_black_48)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            PlaylistHelper.instance.playlist = playlist
            listener.onClick(position)
        }
    }

    /**
     * Returning the item count in the list
     */
    override fun getItemCount() = playlist.size

    /**
     * Reloading data
     * notifyDataSetChanged() notifies to the Adapter that data is changed
     * This is called from Fragment
     */
    fun refresh() {
        playlist = Playlist.songs(name)
        notifyDataSetChanged()
    }

    /**
     * ViewHolder
     * Defining the views using on List(RecyclerView)
     */
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.thumbnail
        val title: TextView = mView.title
    }
}