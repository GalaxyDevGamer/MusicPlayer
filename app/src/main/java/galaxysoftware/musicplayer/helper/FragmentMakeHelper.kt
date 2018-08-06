package galaxysoftware.musicplayer.helper

import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.fragment.*
import galaxysoftware.musicplayer.type.FragmentType

class FragmentMakeHelper {
    companion object {
        fun makeFragment(fragmentType: FragmentType, any: Any):BaseFragment = when(fragmentType) {
            FragmentType.ALBUM_LIST -> AlbumSongsFragment.newInstance(any)
            FragmentType.ARTIST_LIST -> ArtistSongsFragment.newInstance(any)
            FragmentType.PLAYLIST -> PlaylistSongsFragment.newInstance(any)
            FragmentType.PLAYER -> PlayerFragment.newInstance()
            FragmentType.ADD_TO_PLAYLIST -> AddToPlaylistFragment.newInstance(any)
        }
    }
}