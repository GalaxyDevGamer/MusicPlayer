package galaxysoftware.musicplayer.type

import galaxysoftware.musicplayer.R

/**
 * Defining Fragment Types
 */
enum class FragmentType(val navigation: NavigationType, var title: String, val menu: Int) {
    LIBRARY_TAB(NavigationType.NONE, "Library", R.menu.empty),
    ALBUM_TAB(NavigationType.NONE, "Album", R.menu.empty),
    ARTIST_TAB(NavigationType.NONE, "Artist", R.menu.empty),
    PLAYLIST_TAB(NavigationType.NONE, "Playlist", R.menu.playlist_tab),
    ALBUM_LIST(NavigationType.BACK, "", R.menu.empty),
    ARTIST_LIST(NavigationType.BACK, "", R.menu.empty),
    PLAYLIST(NavigationType.BACK, "", R.menu.playlist_content),
    PLAYER(NavigationType.CLOSE, "", R.menu.empty),
    ADD_TO_PLAYLIST(NavigationType.BACK, "Edit", R.menu.done);
}