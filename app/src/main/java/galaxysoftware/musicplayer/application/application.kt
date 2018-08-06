package galaxysoftware.musicplayer.application

import android.app.Application
import io.realm.Realm

class application: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}