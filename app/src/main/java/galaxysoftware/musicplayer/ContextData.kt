package galaxysoftware.musicplayer

import android.content.Context
import android.support.v7.app.AppCompatActivity
import galaxysoftware.musicplayer.activity.MainActivity

class ContextData {
    var activity: AppCompatActivity? = null
    var applicationContext: Context? = null
    var mainActivity: MainActivity? = null

    companion object {
        private var instance: ContextData? = null

        fun getInstance(): ContextData {
            if (instance == null)
                instance = ContextData()

            return instance!!
        }
    }
}