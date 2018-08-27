package galaxysoftware.musicplayer.fragment

import android.support.v4.app.Fragment
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.helper.PlaylistHelper
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SplashFragment : BaseFragment(){

    /**
     * Called when Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        launch(UI) {
            PlaylistHelper.instance.initialize().await()
            getMainActivity().loadLibrary()
        }
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_splash

    override fun updateFragment() {

    }

    companion object {

        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}
