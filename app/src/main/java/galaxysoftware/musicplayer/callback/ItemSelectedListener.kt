package galaxysoftware.musicplayer.callback

import galaxysoftware.musicplayer.realm.Playlist

interface ItemSelectedListener {
    fun onItemSelected(index: Int)
    fun onLongSelection(item: Playlist)
}