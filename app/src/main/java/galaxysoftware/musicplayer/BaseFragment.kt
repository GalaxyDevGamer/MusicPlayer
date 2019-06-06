package galaxysoftware.musicplayer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import galaxysoftware.musicplayer.activity.MainActivity
import galaxysoftware.musicplayer.callback.ChangeFragmentListener
import galaxysoftware.musicplayer.type.FragmentType

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
        setHasOptionsMenu(true)
    }

    /**
     * Called when Fragment creating the view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(getLayoutId(), container, false)

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
    fun requestChangeFragment(fragmentType: FragmentType, any: Any, title: String) = changeFragmentListener.onChangeFragment(fragmentType, any, title)

    /**
     * Called when back called
     */
    fun backFragment() = getMainActivity().backFragment()

    /**
     * Provide activity
     */
    fun getMainActivity() = activity as MainActivity

    abstract fun getLayoutId():Int

    abstract fun initialize()

    abstract fun updateFragment()

    /**
     * Used for telling the activity which song is selected
     */
    fun playSelectedSong(position: Int) = getMainActivity().playMusic(position)
}