package galaxysoftware.musicplayer

import android.content.Context
import android.support.v7.app.AppCompatActivity
import galaxysoftware.musicplayer.activity.MainActivity

/**
 * Class for storing the Contexts while the app running
 */
class ContextData {
    var applicationContext: Context? = null

    companion object {
        val instance = ContextData()
    }
}