package galaxysoftware.musicplayer.callback

import galaxysoftware.musicplayer.type.FragmentType

interface ChangeFragmentListener {
    fun onChangeFragment(fragmentType: FragmentType, any: Any)
}