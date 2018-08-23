package galaxysoftware.musicplayer.callback

import galaxysoftware.musicplayer.realm.Playlist

interface PlaylistSelectedListener {
    fun onPlaylistSelected(name: String)
    fun onLongSelection(item: Playlist)
}