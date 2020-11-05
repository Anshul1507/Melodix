package tech.anshul1507.melodix.SettingScreen

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toolbar
import tech.anshul1507.melodix.R

class SettingFragment : Fragment() {
    var myActivity: Activity? = null
    var shakeSwitch: Switch? = null
    var toolbar: Toolbar? = null

    object SettingObject {
        var flag: Int = 0
        var MY_PREFS_NAME = "ShakeFeature"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Settings"
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        shakeSwitch = view?.findViewById(R.id.switchShake)
        toolbar = view?.findViewById(R.id.toolbar)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar?.setBackgroundColor(Color.rgb(0, 3, 58))

        val prefs =
            myActivity?.getSharedPreferences(SettingObject.MY_PREFS_NAME, Context.MODE_PRIVATE)
        val isAllowed = prefs?.getBoolean("feature", false)
        shakeSwitch?.isChecked = isAllowed as Boolean
        shakeSwitch?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            var editor =
                myActivity?.getSharedPreferences(SettingObject.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    ?.edit()
            if (isChecked) {
                editor?.putBoolean("feature", true)
            } else {
                editor?.putBoolean("feature", false)
            }
            editor?.apply()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item.isVisible = false
    }

}