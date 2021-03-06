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
import kotlinx.android.synthetic.main.song_cell.view.*

class LibraryAdapter(private val listener: SongSelectedListener) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

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
        val item = PlaylistHelper.instance.library[position]
        holder.thumbnailView.setImageBitmap(item.albumArt)
        holder.titleView.text = item.title
        holder.itemView.setOnClickListener {
            PlaylistHelper.instance.playlist = PlaylistHelper.instance.library
            listener.onClick(position)
        }
    }

    /**
     * Returning the item count in the list
     */
    override fun getItemCount() = PlaylistHelper.instance.library.size

    /**
     * ViewHolder
     * Defining the views using on List(RecyclerView)
     */
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnailView: ImageView = mView.thumbnail
        val titleView: TextView = mView.title
    }
}