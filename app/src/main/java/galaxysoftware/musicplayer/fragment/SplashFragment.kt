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

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SplashFragment : BaseFragment(), LoaderManager.LoaderCallbacks<String> {

    override fun initialize() {
        val arg = Bundle()
        arg.putString("data", "")
        Log.e("status", "initialize")
    }

    override fun getLayoutId() = R.layout.fragment_splash

    override fun updateFragment() {

    }

    override fun onResume() {
        super.onResume()
        val arg = Bundle()
        arg.putString("data", "")
        Log.e("status", "onResume")
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
            if (PlaylistHelper.getInstance().playlist.size > 0) {
                deliverResult("")
                return
            }
                forceLoad()
        }
        override fun loadInBackground(): String? {
            Log.e("status", "loadinbackground")
            PlaylistHelper.getInstance().initialize()
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
