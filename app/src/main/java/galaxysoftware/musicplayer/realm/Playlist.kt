package galaxysoftware.musicplayer.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Playlist: RealmObject() {
    @PrimaryKey
    var name: String? = null
    var songs = RealmList<Songs>()
}