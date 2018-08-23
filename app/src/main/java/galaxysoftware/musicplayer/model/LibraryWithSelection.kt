package galaxysoftware.musicplayer.model

import galaxysoftware.musicplayer.realm.Songs

/**
 * Song Model with selection for addToPlaylist
 */
class LibraryWithSelection {
    var song: Songs? = null
    var isSelected = false
}