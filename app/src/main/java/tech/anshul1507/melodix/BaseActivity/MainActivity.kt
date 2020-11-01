package tech.anshul1507.melodix.BaseActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.melodix.HomeScreen.HomeFragment
import tech.anshul1507.melodix.R

class MainActivity : AppCompatActivity() {

    object BaseObject {
        var drawerLayout: DrawerLayout? = null
    }

    private var imagesNavDrawerList = intArrayOf(
        R.drawable.nav_songs,
        R.drawable.nav_favs,
        R.drawable.nav_settings
    )
    private var itemsNavDrawerList = arrayOf(
        "All Songs",
        "Favorite",
        "Settings"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        BaseObject.drawerLayout = findViewById(R.id.drawer_layout)

        val toggleDrawer = ActionBarDrawerToggle(
            this@MainActivity,
            BaseObject.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        BaseObject.drawerLayout?.addDrawerListener(toggleDrawer)
        toggleDrawer.syncState()

        val homeFragment = HomeFragment()
        this.supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, homeFragment, "HomeFragment").commit()

        val navDrawerAdapter = NavDrawerAdapter(itemsNavDrawerList, imagesNavDrawerList, this)
        navDrawerAdapter.notifyDataSetChanged()

        val navDrawerRV = findViewById<RecyclerView>(R.id.nav_recycler_view)
        navDrawerRV.layoutManager = LinearLayoutManager(this)
        navDrawerRV.itemAnimator = DefaultItemAnimator()
        navDrawerRV.adapter = navDrawerAdapter
        navDrawerRV.setHasFixedSize(true)

    }

}