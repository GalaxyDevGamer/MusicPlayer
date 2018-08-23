package galaxysoftware.musicplayer.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import galaxysoftware.musicplayer.BaseFragment
import galaxysoftware.musicplayer.R
import kotlinx.android.synthetic.main.no_music.*

/**
 * A simple [Fragment] subclass.
 * Use the [NoItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * THIS FRAGMENT IS ONLY FOR SHOWING NOTHING AVAILABLE
 */
class NoItemFragment : BaseFragment() {

    override fun initialize() {
        textView.setText(arguments!!.getInt(ARG))
    }

    override fun getLayoutId() = R.layout.no_music

    override fun updateFragment() {

    }

    companion object {
        private const val ARG = "ARG"

        @JvmStatic
        fun newInstance(param1: Any) = NoItemFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG, param1 as Int)
            }
        }
    }
}
