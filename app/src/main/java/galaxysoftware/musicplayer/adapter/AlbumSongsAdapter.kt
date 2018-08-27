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

class AlbumSongsAdapter(private val listener: SongSelectedListener, album: String) : RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder>() {

    private val songList = PlaylistHelper.instance.makePlaylist(ContextData.getInstance().applicationContext?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Array(1) {"*"}, "album ='$album'", null, "title")!!)

    /**
     * Called when ViewHolder is created.
     * Define the layout using on ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song, parent, false)
        return ViewHolder(view)
    }

    /**
     * Set the info to show
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.thumbnail.setImageBitmap(item.albumArt)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            PlaylistHelper.instance.playlist = songList
            listener.onClick(position)
        }
    }

    /**
     * Returning the item count in the list
     */
    override fun getItemCount(): Int = songList.size

    /**
     * ViewHolder
     * Defining the views using on List(RecyclerView)
     */
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.thumbnail
        val title: TextView = mView.title
    }
}