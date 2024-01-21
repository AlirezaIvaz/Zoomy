package ir.alirezaivaz.zoomy

import android.app.Dialog
import android.view.ViewGroup

/**
 * Created by Álvaro Blanco Cabrero on 01/05/2017.
 * Zoomy.
 */
open class DialogContainer internal constructor(private val mDialog: Dialog) : TargetContainer {
    override fun getDecorView(): ViewGroup? {
        return mDialog.window?.decorView as ViewGroup?
    }
}
