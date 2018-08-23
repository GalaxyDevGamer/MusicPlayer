package galaxysoftware.musicplayer.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.callback.PlaylistSelectedListener
import galaxysoftware.musicplayer.realm.Playlist
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_song.view.*

class PlaylistAdapter(private val listener: PlaylistSelectedListener) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    var playlist: RealmResults<Playlist> = Realm.getDefaultInstance().where(Playlist::class.java).findAll()

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
        val item = playlist[position]
        holder.thumbnail.setImageResource(R.mipmap.baseline_music_video_black_48)
        holder.title.text = item!!.name
        holder.itemView.setOnClickListener {
            listener.onPlaylistSelected(item.name!!)
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongSelection(item)
            return@setOnLongClickListener true
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
        playlist = Realm.getDefaultInstance().where(Playlist::class.java).findAll()
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