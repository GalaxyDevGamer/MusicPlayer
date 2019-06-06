package galaxysoftware.musicplayer.helper

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import galaxysoftware.musicplayer.ContextData
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.model.Album
import galaxysoftware.musicplayer.model.Artist
import galaxysoftware.musicplayer.model.LibraryWithSelection
import galaxysoftware.musicplayer.model.Song
import galaxysoftware.musicplayer.realm.Playlist
import galaxysoftware.musicplayer.realm.Songs
import io.realm.RealmList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.ArrayList

/**
 * A Singleton Class Managing and Providing Song datas by Arraylist
 */
class PlaylistHelper {
    var playlist = ArrayList<Song>()
    val library = ArrayList<Song>()
    val albums = ArrayList<Album>()
    val artists = ArrayList<Artist>()
    val libraryWithSelection = ArrayList<LibraryWithSelection>()
    val shuffleList = ArrayList<Song>()

    var playingIndex = 0
    var shuffleEnabled = false
    var shuffleIndex = 0

    var context = ContextData.instance.applicationContext

    var contentResolver = context?.contentResolver

    /**
     * Initializing arrays
     * Called from SplashFragment
     */
    fun initialize() = GlobalScope.async {
        loadLibrary()
        loadAlbums()
        loadArtists()
        if (shuffleEnabled)
            shuffle()
    }

    /**
     * Loading all songs on Library
     */
    private fun loadLibrary() {
        val libraryCursor = contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Array(1) { "*" }, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, "title")
        if (libraryCursor!!.moveToFirst()) {
            do {
                val song = Song()
                song.title = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                song.artist = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                song.album = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                song.albumArt = getCoverArt(libraryCursor.getLong(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
                song.path = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                library.add(song)
                val selectionLibrary = LibraryWithSelection()
                val songs = Songs()
                songs.title = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                songs.artist = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                songs.album = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                songs.albumArt = libraryCursor.getLong(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                songs.path = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                selectionLibrary.song = songs
                libraryWithSelection.add(selectionLibrary)
            } while (libraryCursor.moveToNext())
        }
        libraryCursor.close()
        playlist = library
    }

    /**
     * Loading Album data
     */
    private fun loadAlbums() {
        val albumCursor = contentResolver?.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Array(1) { "*" }, null, null, null)
        if (albumCursor!!.moveToFirst()) {
            do {
                val album = Album()
                album.title = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                try {
                    album.thumbnail = getCoverArt(albumCursor.getLong(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)))
                } catch (e: IllegalStateException) {
                    album.thumbnail = BitmapFactory.decodeResource(context?.resources, R.mipmap.baseline_music_video_black_48)
                }
                albums.add(album)
            } while (albumCursor.moveToNext())
        }
        albumCursor.close()
    }

    /**
     * Loading Artist data
     */
    private fun loadArtists() {
        val artistCursor = contentResolver?.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, Array(1) { MediaStore.Audio.Artists.ARTIST }, null, null, null)
        if (artistCursor!!.moveToFirst()) {
            do {
                val artist = Artist()
                artist.title = artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                artist.thumbnail = BitmapFactory.decodeResource(context?.resources, R.mipmap.baseline_music_video_black_48)
                artists.add(artist)
            } while (artistCursor.moveToNext())
        }
        artistCursor.close()
    }

    /**
     * Making playlist array by given cursor
     */
    fun makePlaylist(cursor: Cursor): ArrayList<Song> {
        val playlist = ArrayList<Song>()
        if (cursor.moveToFirst()) {
            do {
                val song = Song()
                song.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                song.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                song.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                song.albumArt = getCoverArt(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
                song.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                playlist.add(song)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return playlist
    }

    /**
     * Making playlist from songs stored on Realm (RealmList for the playlist song
     */
    fun makePlaylistFromRealm(list: RealmList<Songs>): ArrayList<Song> {
        val playlist = ArrayList<Song>()
        list.forEach {
            val song = Song().apply {
                title = it.title
                album = it.album
                artist = it.artist
                albumArt = getCoverArt(it.albumArt)
                path = it.path
            }
            playlist.add(song)
        }
        return playlist
    }


    fun getSongPath(position: Int) = if (shuffleEnabled) shuffleList[position].path else playlist[position].path

    fun getTitle(position: Int) = if (shuffleEnabled) shuffleList[position].title else playlist[position].title

    fun getAlbumArt(position: Int) = if (shuffleEnabled) shuffleList[position].albumArt else playlist[position].albumArt

    fun getSongPath() = if (shuffleEnabled) shuffleList[shuffleIndex].path else playlist[playingIndex].path

    fun getTitle() = if (shuffleEnabled) shuffleList[shuffleIndex].title else playlist[playingIndex].title

    fun getAlbumArt() = if (shuffleEnabled) shuffleList[shuffleIndex].albumArt else playlist[playingIndex].albumArt

    /**
     * Provide CovertArt
     */
    fun getCoverArt(albumId: Long): Bitmap {
        val albumCursor = contentResolver?.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                MediaStore.Audio.Albums._ID + " = ?",
                arrayOf(java.lang.Long.toString(albumId)),
                null
        )
        var result: String? = null
        if (albumCursor!!.moveToFirst()) {
            result = albumCursor.getString(0)
        }
        albumCursor.close()
//        Log.e("result", result+"")
        return if (BitmapFactory.decodeFile(result) != null) {
            BitmapFactory.decodeFile(result)
        } else {
            BitmapFactory.decodeResource(context?.resources, R.mipmap.baseline_music_video_black_48)
        }
    }

    /**
     * Return whether next song available
     */
    fun isNextSongAvailable(): Boolean {
        if (shuffleEnabled) {
            if (shuffleIndex < shuffleList.size - 1) {
                return true
            }
        } else {
            if (playingIndex < playlist.size - 1) {
                return true
            }
        }
        return false
    }

    /**
     * Refresh playlist
     *
     * Reset the playing index to zero
     * or
     * Remake shuffle()
     */
    fun refreshPlaylist() = if (shuffleEnabled) shuffle() else playingIndex = 0

    /**
     * Create Shuffle List
     *
     * Clone from the current playlist, randomly choose the song and add to the shuffle list.
     */
    fun shuffle() {
        shuffleList.clear()
        shuffleIndex = 0
        val clone = playlist.clone() as ArrayList<Song>
        clone.removeAt(playingIndex)
        shuffleList.add(playlist[playingIndex])
        do {
            val randomSong = if (clone.size == 1) {
                clone[0]
            } else {
                clone[Random().nextInt(clone.size - 1)]
            }
            val song = Song()
            song.title = randomSong.title
            song.artist = randomSong.artist
            song.album = randomSong.album
            song.albumArt = randomSong.albumArt
            song.path = randomSong.path
            shuffleList.add(song)
            clone.remove(randomSong)
        } while (clone.size > 0)
    }

    /**
     * Uncheck all songs checked on AddToPlaylist
     */
    fun uncheckAll() {
        libraryWithSelection.forEach {
            it.isSelected = false
        }
    }

    companion object {
        /**
         * Singleton
         */
        val instance = PlaylistHelper()
    }
}