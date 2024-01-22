package ir.alirezaivaz.zoomy.sample

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ir.alirezaivaz.zoomy.DoubleTapListener
import ir.alirezaivaz.zoomy.LongPressListener
import ir.alirezaivaz.zoomy.TapListener
import ir.alirezaivaz.zoomy.Zoomy

class ImageAdapter(private val activity: Activity, private val images: List<Int>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView: ImageView = SquareImageView(activity)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        (holder.itemView as ImageView).setImageResource(images[position])
        holder.itemView.tag = holder.bindingAdapterPosition
        val builder = Zoomy.Builder(activity)
            .target(holder.itemView)
            .interpolator(OvershootInterpolator())
            .tapListener(object : TapListener {
                override fun onTap(v: View?) {
                    Toast.makeText(
                        activity, "Tap on ${v?.tag}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .longPressListener(object : LongPressListener {
                override fun onLongPress(v: View?) {
                    Toast.makeText(
                        activity, "Long press on ${v?.tag}", Toast.LENGTH_SHORT
                    ).show()
                }
            }).doubleTapListener(object : DoubleTapListener {
                override fun onDoubleTap(v: View?) {
                    Toast.makeText(
                        activity, "Double tap on ${v?.tag}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        builder.register()
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)
}
