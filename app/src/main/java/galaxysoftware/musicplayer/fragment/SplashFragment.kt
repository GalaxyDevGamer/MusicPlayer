package galaxysoftware.musicplayer.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.util.Log
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import galaxysoftware.musicplayer.helper.PlaylistHelper
import kotlinx.coroutines.experimental.async

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SplashFragment : BaseFragment(), LoaderManager.LoaderCallbacks<String> {

    /**
     * Called when Fragment is created
     * Creating adapter to show on RecyclerView and set to it
     */
    override fun initialize() {
        val arg = Bundle()
        arg.putString("data", "")
        Log.e("status", "initialize")
    }

    /**
     * Set the layout using on this Fragment
     */
    override fun getLayoutId() = R.layout.fragment_splash

    override fun updateFragment() {

    }

    /**
     * Called when Fragment resumed. Initializing background task.
     */
    override fun onResume() {
        super.onResume()
        val arg = Bundle()
        arg.putString("data", "")
//        async {
//            PlaylistHelper.instance.initialize()
//        }.await()
        loaderManager.initLoader(1, arg, this)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<String> {
        Log.e("status", "onCreateLoader")
        return LibraryLoader(context!!)
    }

    override fun onLoadFinished(p0: Loader<String>, p1: String?) {
        Log.e("status", "complete")
        getMainActivity().loadLibrary()
    }

    override fun onLoaderReset(p0: Loader<String>) {

    }

    class LibraryLoader(context: Context) : AsyncTaskLoader<String>(context) {

        override fun onStartLoading() {
            super.onStartLoading()
            if (PlaylistHelper.instance.playlist.size > 0) {
                deliverResult("")
                return
            }
                forceLoad()
        }
        override fun loadInBackground(): String? {
            Log.e("status", "loadinbackground")
            PlaylistHelper.instance.initialize()
            return ""
        }

        override fun deliverResult(data: String?) {
            Log.e("status", "deliverResult")
            super.deliverResult(data)
        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}
