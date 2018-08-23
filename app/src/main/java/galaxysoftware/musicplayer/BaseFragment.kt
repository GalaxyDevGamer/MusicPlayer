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

/**
 * The base of the Fragment
 */
abstract class BaseFragment : Fragment() {

    private lateinit var changeFragmentListener: ChangeFragmentListener

    /**
     * Called when Fragment created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener(context!!)
    }

    /**
     * Called when Fragment creating the view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(getLayoutId(), container, false)
    }

    /**
     * Called when the View is created. The initialize() is called on here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    /**
     * Set listener for change fragment
     */
    private fun setListener(context: Context) {
        changeFragmentListener = context as ChangeFragmentListener
    }

    /**
     * Called for changing Fragment
     */
    fun requestChangeFragment(fragmentType: FragmentType, any: Any) {
        changeFragmentListener.onChangeFragment(fragmentType, any)
    }

    /**
     * Called when back called
     */
    fun backFragment() {
        getMainActivity().backFragment()
    }

    fun getBaseActivity(): Activity = ContextData.getInstance().activity!!

    /**
     * Provide activity
     */
    fun getMainActivity(): MainActivity = ContextData.getInstance().mainActivity!!

    abstract fun getLayoutId():Int

    abstract fun initialize()

    abstract fun updateFragment()

    /**
     * Used for telling the activity which song is selected
     */
    fun playSelectedSong(position: Int) = getMainActivity().playMusic(position)

    /**
     * Used for setting the data on Toolbar
     */
    fun updateToolbar(fragmentType: FragmentType, navigationType: NavigationType, title: String, menu: Int) = getMainActivity().setData(fragmentType, navigationType, title, menu)
}