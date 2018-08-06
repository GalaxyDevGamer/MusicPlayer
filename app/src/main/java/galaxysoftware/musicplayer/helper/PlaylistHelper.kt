package galaxysoftware.musicplayer.helper

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.util.Log
import galaxysoftware.musicplayer.ContextData
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.data.Album
import galaxysoftware.musicplayer.data.Artist
import galaxysoftware.musicplayer.data.LibraryWithSelection
import galaxysoftware.musicplayer.data.Song
import galaxysoftware.musicplayer.realm.Songs
import io.realm.RealmList
import java.util.*
import kotlin.collections.ArrayList

class PlaylistHelper: LoaderManager.LoaderCallbacks<Boolean> {
    var playlist = ArrayList<Song>()
    val library = ArrayList<Song>()
    val albums = ArrayList<Album>()
    val artists = ArrayList<Artist>()
    val libraryWithSelection = ArrayList<LibraryWithSelection>()
    val shuffleList = ArrayList<Song>()

    var playingIndex = 0
    var shuffleEnabled = false
    var shuffleIndex = 0

    fun initialize() {
        Log.e("place", "initialize")
        loadLibrary()
        loadAlbums()
        loadArtists()
        if (shuffleEnabled)
            shuffle()
    }


    fun setPlaylist(cursor: Cursor) {
        playlist.clear()
        if (cursor.moveToFirst()) {
            val song = Song()
            song.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            song.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            song.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            song.albumArt = getCoverArt(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
            song.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            playlist.add(song)
        }
        cursor.close()
    }

    private fun loadLibrary() {
        val libraryCursor = ContextData.getInstance().applicationContext?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Array(1) { "*" }, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, "title")
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
                selectionLibrary.title = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                selectionLibrary.artist = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                selectionLibrary.album = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                selectionLibrary.albumArt = libraryCursor.getLong(libraryCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                selectionLibrary.path = libraryCursor.getString(libraryCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                libraryWithSelection.add(selectionLibrary)
            } while (libraryCursor.moveToNext())
        }
        libraryCursor.close()
    }

    private fun loadAlbums() {
        val albumCursor = ContextData.getInstance().applicationContext?.contentResolver?.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Array(1) {"*"}, null, null, null)
        if (albumCursor!!.moveToFirst()) {
            do {
                val album = Album()
                album.title = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                try {
                    album.thumbnail = getCoverArt(albumCursor.getLong(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)))
                } catch (e: IllegalStateException) {
                    album.thumbnail = BitmapFactory.decodeResource(ContextData.getInstance().applicationContext?.resources, R.mipmap.baseline_music_video_black_48)
                }
                albums.add(album)
            } while (albumCursor.moveToNext())
        }
        albumCursor.close()
    }

    private fun loadArtists() {
        val artistCursor = ContextData.getInstance().applicationContext?.contentResolver?.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, Array(1) {MediaStore.Audio.Artists.ARTIST}, null, null, null)
        if (artistCursor!!.moveToFirst()) {
            do {
                val artist = Artist()
                artist.title = artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                artist.thumbnail = BitmapFactory.decodeResource(ContextData.getInstance().applicationContext?.resources, R.mipmap.baseline_music_video_black_48)
                artists.add(artist)
            } while (artistCursor.moveToNext())
        }
        artistCursor.close()
    }

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

    fun makePlaylistFromRealm(list: RealmList<Songs>): ArrayList<Song> {
        val playlist = ArrayList<Song>()
        list.forEach {
            val song = Song()
            song.title = it.title
            song.album = it.album
            song.artist = it.artist
            song.albumArt = getCoverArt(it.albumArt)
            song.path = it.path
            playlist.add(song)
        }
        return playlist
    }

    fun getSongPath(position: Int) = if (shuffleEnabled) shuffleList[position].path else playlist[position].path

    fun getTitle(position: Int) = if (shuffleEnabled) shuffleList[position].title else playlist[position].title

    fun getAlbumArt(position: Int) = if (shuffleEnabled) shuffleList[position].albumArt else playlist[position].albumArt

    fun getCoverArt(albumId: Long): Bitmap {
        val albumCursor = ContextData.getInstance().applicationContext!!.contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                MediaStore.Audio.Albums._ID + " = ?",
                arrayOf(java.lang.Long.toString(albumId)),
                null
        )
        var result: String? = null
        if (albumCursor.moveToFirst()) {
            result = albumCursor.getString(0)
        }
        albumCursor.close()
//        Log.e("result", result+"")
        return if (BitmapFactory.decodeFile(result) != null) {
            BitmapFactory.decodeFile(result)
        } else {
            BitmapFactory.decodeResource(ContextData.getInstance().applicationContext?.resources, R.mipmap.baseline_music_video_black_48)
        }
    }

    fun shuffle() {
        shuffleList.clear()
        shuffleIndex = 0
        val clone = playlist.clone() as ArrayList<Song>
        clone.removeAt(playingIndex)
        Log.e("clone count", clone.count().toString())
        do {
            Log.e("clone size", clone.size.toString())
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

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Boolean> = ShuffleMaker(library, ContextData.getInstance().applicationContext!!)

    override fun onLoadFinished(p0: Loader<Boolean>, p1: Boolean?) {

    }

    override fun onLoaderReset(p0: Loader<Boolean>) {

    }

    class ShuffleMaker(library: ArrayList<Song>, context: Context): AsyncTaskLoader<Boolean>(context) {
        override fun onStartLoading() {
            super.onStartLoading()
            forceLoad()
        }
        override fun loadInBackground(): Boolean? {
            PlaylistHelper.getInstance().library.clone()
            return true
        }

        override fun deliverResult(data: Boolean?) {
            super.deliverResult(data)

        }
    }

    companion object {
        private val instance = PlaylistHelper()

        fun getInstance() = instance
    }
}