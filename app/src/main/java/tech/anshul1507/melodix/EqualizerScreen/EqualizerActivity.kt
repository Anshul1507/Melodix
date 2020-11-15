package tech.anshul1507.melodix.EqualizerScreen

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bullhead.equalizer.EqualizerFragment
import tech.anshul1507.melodix.BaseActivity.MainActivity
import tech.anshul1507.melodix.R
import tech.anshul1507.melodix.SongPlayingScreen.SongPlayingFragment

class EqualizerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)

        try {
            val sessionId: Int = SongPlayingFragment.InitObject.mediaPlayer?.audioSessionId as Int

            val equalizerFragment: EqualizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#6aefae"))
                .setAudioSessionId(sessionId as Int)
                .build()
            supportFragmentManager.beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit()
            SongPlayingFragment.InitObject.mediaPlayer?.isLooping = true

        } catch (e: Exception) {
            startActivity(Intent(this@EqualizerActivity, MainActivity::class.java))
            Toast.makeText(this, "Track is not playing", Toast.LENGTH_SHORT).show()
        }
    }
}