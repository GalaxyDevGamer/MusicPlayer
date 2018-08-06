package galaxysoftware.musicplayer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import galaxysoftware.musicplayer.activity.MainActivity
import galaxysoftware.musicplayer.callback.ChangeFragmentListener
import galaxysoftware.musicplayer.type.FragmentType
import galaxysoftware.musicplayer.type.NavigationType

abstract class BaseFragment : Fragment() {

    private lateinit var changeFragmentListener: ChangeFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun setListener(context: Context) {
        changeFragmentListener = context as ChangeFragmentListener
    }

    fun requestChangeFragment(fragmentType: FragmentType, any: Any) {
        changeFragmentListener.onChangeFragment(fragmentType, any)
    }

    fun backFragment() {
        getMainActivity().backFragment()
    }

    fun getBaseActivity(): Activity = ContextData.getInstance().activity!!

    fun getMainActivity(): MainActivity = ContextData.getInstance().mainActivity!!

    abstract fun getLayoutId():Int

    abstract fun initialize()

    abstract fun updateFragment()

    fun playSelectedSong(position: Int) = getMainActivity().playMusic(position)

    fun updateToolbar(navigationType: NavigationType, title: String, menu: Int) = getMainActivity().setData(navigationType, title, menu)
}