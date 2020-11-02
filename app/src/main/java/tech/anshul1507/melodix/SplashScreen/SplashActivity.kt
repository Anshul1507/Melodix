package tech.anshul1507.melodix.SplashScreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import tech.anshul1507.melodix.BaseActivity.MainActivity
import tech.anshul1507.melodix.R

class SplashActivity : AppCompatActivity() {

    private var permissionsArray = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.PROCESS_OUTGOING_CALLS,
        android.Manifest.permission.RECORD_AUDIO
    )

    private var magicNumber = 201

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!isPermissionGranted(this@SplashActivity, *permissionsArray)) {
            /* ASK FOR PERMISSION */
            ActivityCompat.requestPermissions(this@SplashActivity, permissionsArray, magicNumber)
        } else {
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                this.finish()
            }, 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            magicNumber -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* ALL PERMISSIONS ARE GRANTED */
                    Handler().postDelayed({
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        this.finish()
                    }, 1000)
                } else {
                    Toast.makeText(
                        this@SplashActivity,
                        "Please grant all the permissions to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                this.finish()
                return
            }
        }
    }

    private fun isPermissionGranted(ctx: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ctx.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}