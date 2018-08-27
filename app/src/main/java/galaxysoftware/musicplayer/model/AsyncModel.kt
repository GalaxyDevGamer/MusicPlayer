package galaxysoftware.musicplayer.model

import galaxysoftware.musicplayer.helper.PlaylistHelper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

object AsyncModel {
    fun initializeLibrary() = async(CommonPool) {
        PlaylistHelper.instance.initialize()
    }
}