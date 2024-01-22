package ir.alirezaivaz.zoomy.sample

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        recyclerView.addItemDecoration(RecyclerSpaceDecoration(5))
        recyclerView.adapter = ImageAdapter(this@MainActivity, mImages)
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

}
