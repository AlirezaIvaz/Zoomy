package ir.alirezaivaz.zoomy

import android.app.Activity
import android.view.ViewGroup

/**
 * Created by Álvaro Blanco Cabrero on 01/05/2017.
 * Zoomy.
 */
class ActivityContainer internal constructor(private val mActivity: Activity) : TargetContainer {
    override fun getDecorView(): ViewGroup {
        return mActivity.window.decorView as ViewGroup
    }
}
