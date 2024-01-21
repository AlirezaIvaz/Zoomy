package ir.alirezaivaz.zoomy

import android.graphics.PointF
import android.view.MotionEvent

/**
 * Created by Álvaro Blanco Cabrero on 11/02/2017.
 * Zoomy.
 */
internal object MotionUtils {
    @JvmStatic
    fun midPointOfEvent(point: PointF, event: MotionEvent) {
        if (event.pointerCount == 2) {
            val x = event.getX(0) + event.getX(1)
            val y = event.getY(0) + event.getY(1)
            point[x / 2] = y / 2
        }
    }
}
