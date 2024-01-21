package ir.alirezaivaz.zoomy

import android.view.View

/**
 * Created by Álvaro Blanco Cabrero on 12/02/2017.
 * Zoomy.
 */
interface ZoomListener {
    fun onViewStartedZooming(view: View?)
    fun onViewEndedZooming(view: View?)
}
