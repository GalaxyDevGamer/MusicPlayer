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
import kotlinx.android.synthetic.main.fragment_song.view.*

class LibraryAdapter(private val listener: SongSelectedListener) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = PlaylistHelper.getInstance().library[position]
        holder.thumbnailView.setImageBitmap(item.albumArt)
        holder.titleView.text = item.title
        holder.itemView.setOnClickListener {
            PlaylistHelper.getInstance().playlist = PlaylistHelper.getInstance().library
            listener.onClick(position)
        }
    }

    override fun getItemCount() = PlaylistHelper.getInstance().library.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnailView: ImageView = mView.thumbnail
        val titleView: TextView = mView.title
    }
}
