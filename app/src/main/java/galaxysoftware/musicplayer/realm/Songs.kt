package galaxysoftware.musicplayer.realm

import io.realm.RealmObject

open class Songs: RealmObject() {
    var title: String? = null
    var artist: String? = null
    var album: String? = null
    var albumArt: Long = 0
    var path: String? = null
}