package ir.alirezaivaz.zoomy

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.view.View

/**
 * Created by Álvaro Blanco Cabrero on 11/02/2017.
 * Zoomy.
 */
internal object ViewUtils {
    @JvmStatic
    fun getBitmapFromView(view: View): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    @JvmStatic
    fun getViewAbsoluteCords(v: View): Point {
        val location = IntArray(2)
        v.getLocationInWindow(location)
        val x = location[0]
        val y = location[1]
        return Point(x, y)
    }

    fun viewMidPoint(point: PointF, v: View) {
        val x = v.width.toFloat()
        val y = v.height.toFloat()
        point[x / 2] = y / 2
    }
}
