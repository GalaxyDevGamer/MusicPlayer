package galaxysoftware.musicplayer.type

/**
 * Defining Fragment Types
 */
enum class FragmentType(hasToAdd: Boolean) {
    LIBRARY_TAB(true),
    ALBUM_TAB(true),
    ARTIST_TAB(true),
    PLAYLIST_TAB(true),
    ALBUM_LIST(true),
    ARTIST_LIST(true),
    PLAYLIST(false),
    PLAYER(true),
    ADD_TO_PLAYLIST(false);
}