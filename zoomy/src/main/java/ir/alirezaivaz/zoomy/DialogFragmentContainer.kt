package ir.alirezaivaz.zoomy

import androidx.fragment.app.DialogFragment

/**
 * Created by Álvaro Blanco Cabrero on 02/05/2017.
 * Zoomy.
 */
class DialogFragmentContainer internal constructor(dialog: DialogFragment) :
    DialogContainer(dialog.dialog)
