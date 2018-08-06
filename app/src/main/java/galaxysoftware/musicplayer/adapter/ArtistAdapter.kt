package galaxysoftware.musicplayer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper
import kotlinx.android.synthetic.main.fragment_album.view.*

class ArtistAdapter(private val listener: ItemSelectedListener) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = PlaylistHelper.getInstance().artists[position]
        holder.thumbnail.setImageBitmap(item.thumbnail)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            listener.onItemSelected(item.title!!)
        }
    }

    override fun getItemCount() = PlaylistHelper.getInstance().artists.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.thumbnail
        val title: TextView = mView.title
    }
}