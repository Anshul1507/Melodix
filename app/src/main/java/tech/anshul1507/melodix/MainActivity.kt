package tech.anshul1507.melodix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import tech.anshul1507.melodix.HomeScreen.HomeFragment

class MainActivity : AppCompatActivity() {

    object BaseObject {
        var drawerLayout: DrawerLayout? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        BaseObject.drawerLayout = findViewById(R.id.drawer_layout)

        val toggleDrawer = ActionBarDrawerToggle(this@MainActivity,BaseObject.drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        BaseObject.drawerLayout?.addDrawerListener(toggleDrawer)
        toggleDrawer.syncState()

        val homeFragment = HomeFragment()
        this.supportFragmentManager.beginTransaction().add(R.id.fragment_container,homeFragment,"HomeFragment").commit()

    }

}