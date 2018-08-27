package galaxysoftware.musicplayer.realm

import galaxysoftware.musicplayer.helper.PlaylistHelper
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Realm Model for storing playlist
 */
open class Playlist: RealmObject() {
    @PrimaryKey
    var name: String? = null
    var songs = RealmList<Songs>()

    companion object {
        fun songs(name: String) = Realm.getDefaultInstance().use {
            return@use PlaylistHelper.instance.makePlaylistFromRealm(it.where(Playlist::class.java).equalTo("name", name).findFirst()?.songs!!)
        }

        fun names() = Realm.getDefaultInstance().use {
            val list: MutableList<Playlist> = mutableListOf()
            it.where(Playlist::class.java).findAll().forEach {
                list.add(it)
            }
            return@use list
        }
    }
}