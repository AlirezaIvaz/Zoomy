package ir.alirezaivaz.zoomy.sample

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ir.alirezaivaz.zoomy.DoubleTapListener
import ir.alirezaivaz.zoomy.LongPressListener
import ir.alirezaivaz.zoomy.TapListener
import ir.alirezaivaz.zoomy.Zoomy
import java.util.Arrays

class MainActivity : AppCompatActivity() {
    private val mImages by lazy {
        arrayListOf(
            R.drawable.img1, R.drawable.img2,
            R.drawable.img3, R.drawable.img4,
            R.drawable.img5, R.drawable.img6,
            R.drawable.img7, R.drawable.img8,
            R.drawable.img9, R.drawable.img10
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        val recyclerView = findViewById<View>(R.id.rv) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.addItemDecoration(CommonItemSpaceDecoration(5))
        recyclerView.adapter = Adapter(mImages)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_github -> {
                launchUrl("https://github.com/AlirezaIvaz/Zoomy")
            }
            R.id.action_issues -> {
                launchUrl("https://github.com/AlirezaIvaz/Zoomy/issues")
            }
        }
        return false
    }

    private fun launchUrl(url: String) {
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.github))
            .build()
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(params)
            .setShowTitle(true)
            .build()
            .launchUrl(this@MainActivity, Uri.parse(url))
    }

    private inner class Adapter(private val images: List<Int>) :
        RecyclerView.Adapter<ImageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val imageView: ImageView = SquareImageView(this@MainActivity)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            return ImageViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            (holder.itemView as ImageView).setImageResource(images[position])
            holder.itemView.setTag(holder.adapterPosition)
            val builder = Zoomy.Builder(this@MainActivity)
                .target(holder.itemView)
                .interpolator(OvershootInterpolator())
                .tapListener(object : TapListener {
                    override fun onTap(v: View?) {
                        Toast.makeText(
                            this@MainActivity, "Tap on "
                                    + v!!.tag, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                .longPressListener(object : LongPressListener {
                    override fun onLongPress(v: View?) {
                        Toast.makeText(
                            this@MainActivity, "Long press on "
                                    + v!!.tag, Toast.LENGTH_SHORT
                        ).show()
                    }
                }).doubleTapListener(object : DoubleTapListener {
                    override fun onDoubleTap(v: View?) {
                        Toast.makeText(
                            this@MainActivity, "Double tap on "
                                    + v!!.tag, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            builder.register()
        }

        override fun getItemCount(): Int {
            return images.size
        }
    }

    private inner class ImageViewHolder internal constructor(itemView: View?) :
        RecyclerView.ViewHolder(
            itemView!!
        )

    inner class CommonItemSpaceDecoration internal constructor(private val mSpace: Int) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect[mSpace, mSpace, mSpace] = mSpace
        }
    }
}
