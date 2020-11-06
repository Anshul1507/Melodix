package tech.anshul1507.melodix.BaseActivity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Build
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
import tech.anshul1507.melodix.SongPlayingScreen.SongPlayingFragment
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    object BaseObject {
        var drawerLayout: DrawerLayout? = null
        var notificationManager: NotificationManager? = null
        var notificationChannel: NotificationChannel? = null
        var notificationBuilder: Notification? = null
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

    private var channelID = "tech.anshul1507.melodix"
    private var description = "Song Notification"

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


        val notificationIntent = Intent(this@MainActivity, MainActivity::class.java)

        BaseObject.notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingNotificationIntent = PendingIntent.getActivity(
            this@MainActivity,
            System.currentTimeMillis().toInt(),
            notificationIntent,
            0
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //For Android v. O+, we required channels for notification
            BaseObject.notificationChannel =
                NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
            BaseObject.notificationChannel?.enableLights(true)
            BaseObject.notificationChannel?.lightColor = Color.GREEN
            BaseObject.notificationChannel?.enableVibration(false)
            BaseObject.notificationManager?.createNotificationChannel(BaseObject.notificationChannel!!)
            BaseObject.notificationBuilder = Notification.Builder(this, channelID)
                .setContentTitle("A song is playing in the background")
                .setContentText("Click to open player")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingNotificationIntent).build()

        } else {
            BaseObject.notificationBuilder = Notification.Builder(this)
                .setContentTitle("A song is playing in the background")
                .setContentText("Click to open player")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingNotificationIntent).build()
        }

        BaseObject.notificationManager?.cancel(1999)

    }

    override fun onStart() {
        super.onStart()
        try {
            BaseObject.notificationManager?.cancel(1999)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (SongPlayingFragment.InitObject.mediaPlayer?.isPlaying as Boolean) {
                BaseObject.notificationManager?.notify(1999, BaseObject.notificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            BaseObject.notificationManager?.cancel(1999)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}