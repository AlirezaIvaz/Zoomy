package ir.alirezaivaz.zoomy.sample

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerSpaceDecoration(private val mSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect[mSpace, mSpace, mSpace] = mSpace
    }
}
