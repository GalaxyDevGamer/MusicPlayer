package galaxysoftware.musicplayer.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.data.LibraryWithSelection
import galaxysoftware.musicplayer.helper.PlaylistHelper
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.realm.Songs
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_song_with_selection.view.*

class AddToPlaylistAdapter(val name: String) : RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder>() {

    val selectedSongs = RealmList<Songs>()
    var songList: ArrayList<LibraryWithSelection> = PlaylistHelper.getInstance().libraryWithSelection

    init {
        val playlistSongs = Realm.getDefaultInstance().where(Playlist::class.java).equalTo("name", name).findFirst()
        playlistSongs?.songs?.forEach {
            val song = LibraryWithSelection()
            song.title = it.title
            song.artist = it.artist
            song.album = it.album
            song.albumArt = it.albumArt
            song.path = it.path
            if (songList.contains(song))
                songList.remove(song)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song_with_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.thumbnailView.setImageBitmap(PlaylistHelper.getInstance().getCoverArt(item.albumArt))
        holder.titleView.text = item.title
        holder.itemView.setOnClickListener {
            val song = Songs()
            song.title = item.title
            song.artist = item.artist
            song.album = item.album
            song.albumArt = item.albumArt
            song.path = item.path
            if (item.isSelected) {
                selectedSongs.remove(song)
                item.isSelected = false
            } else {
                selectedSongs.add(song)
                item.isSelected = true
            }
            Log.e("songs", selectedSongs.toString())
            holder.checkMark.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun getItemCount() = songList.size

    fun selectedItemCount() = selectedSongs.count()

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val thumbnailView: ImageView = mView.thumbnail
        val titleView: TextView = mView.title
        val checkMark: ImageView = mView.checkMark
    }
}