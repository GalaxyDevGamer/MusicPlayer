package galaxysoftware.musicplayer.realm

import io.realm.RealmObject

/**
 * Realm Model for storing list of the song
 */
open class Songs: RealmObject() {
    var title: String? = null
    var artist: String? = null
    var album: String? = null
    var albumArt: Long = 0
    var path: String? = null
}