package galaxysoftware.musicplayer.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.callback.ItemSelectedListener
import galaxysoftware.musicplayer.realm.Playlist
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_song.view.*

class PlaylistAdapter(private val listener: ItemSelectedListener) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    var playlist: RealmResults<Playlist> = Realm.getDefaultInstance().where(Playlist::class.java).findAll()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = playlist[position]
        holder.thumbnail.setImageResource(R.mipmap.baseline_music_video_black_48)
        holder.title.text = item!!.name
        holder.itemView.setOnClickListener {
            listener.onItemSelected(item.name!!)
        }
    }

    override fun getItemCount() = playlist.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.thumbnail
        val title: TextView = mView.title
    }
}