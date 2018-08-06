package galaxysoftware.musicplayer.adapter


import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.ContextData
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.SongSelectedListener
import galaxysoftware.musicplayer.helper.PlaylistHelper
import kotlinx.android.synthetic.main.fragment_song.view.*

class ArtistSongsAdapter(private val listener: SongSelectedListener, artist: String) : RecyclerView.Adapter<ArtistSongsAdapter.ViewHolder>() {

    private val songList = PlaylistHelper.getInstance().makePlaylist(ContextData.getInstance().applicationContext?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Array(1) {"*"}, "artist ='$artist'", null, "title")!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.thumbnail.setImageBitmap(item.albumArt)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            PlaylistHelper.getInstance().playlist = songList
            listener.onClick(position)
        }
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.thumbnail
        val title: TextView = mView.title
    }
}