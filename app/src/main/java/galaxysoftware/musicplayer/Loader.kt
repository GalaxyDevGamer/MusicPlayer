package galaxysoftware.musicplayer

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import galaxysoftware.musicplayer.helper.PlaylistHelper

class Loader(context: Context) : AsyncTaskLoader<String>(context) {
    override fun loadInBackground(): String? {
        return ""
    }
}