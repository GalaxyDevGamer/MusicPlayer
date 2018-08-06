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
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_song.view.*

class PlaylistSongsAdapter(private val listener: SongSelectedListener, val name: String) : RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder>() {

    var playlist = Realm.getDefaultInstance().where(Playlist::class.java).equalTo("name", name).findFirst()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = playlist?.songs!![position]
        holder.thumbnail.setImageResource(R.mipmap.baseline_music_video_black_48)
        holder.title.text = item!!.title
        holder.itemView.setOnClickListener {
            PlaylistHelper.getInstance().playlist = PlaylistHelper.getInstance().makePlaylistFromRealm(playlist!!.songs)
            listener.onClick(position)
        }
    }

    override fun getItemCount(): Int = playlist?.songs!!.size

    fun refresh() {
        playlist = Realm.getDefaultInstance().where(Playlist::class.java).equalTo("name", name).findFirst()
        notifyDataSetChanged()
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnail: ImageView = mView.thumbnail
        val title: TextView = mView.title
    }
}