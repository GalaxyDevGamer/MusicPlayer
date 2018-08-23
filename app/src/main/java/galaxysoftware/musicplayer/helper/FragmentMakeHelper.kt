package galaxysoftware.musicplayer.helper

import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.fragment.*
import galaxysoftware.musicplayer.type.FragmentType

class FragmentMakeHelper {
    companion object {
        fun makeFragment(fragmentType: FragmentType, any: Any):BaseFragment = when(fragmentType) {
            FragmentType.LIBRARY_TAB -> LibraryFragment.newInstance()
            FragmentType.ALBUM_TAB -> AlbumFragment.newInstance()
            FragmentType.ARTIST_TAB -> ArtistFragment.newInstance()
            FragmentType.PLAYLIST_TAB -> PlaylistFragment.newInstance()
            FragmentType.ALBUM_LIST -> AlbumSongsFragment.newInstance(any)
            FragmentType.ARTIST_LIST -> ArtistSongsFragment.newInstance(any)
            FragmentType.PLAYLIST -> PlaylistSongsFragment.newInstance(any)
            FragmentType.PLAYER -> PlayerFragment.newInstance()
            FragmentType.ADD_TO_PLAYLIST -> AddToPlaylistFragment.newInstance(any)
        }
    }
}