package tech.anshul1507.melodix.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import tech.anshul1507.melodix.BaseActivity.MainActivity
import tech.anshul1507.melodix.R
import tech.anshul1507.melodix.SongPlayingScreen.SongPlayingFragment

class NotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.BaseObject.notificationManager?.cancel(1999)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.InitObject.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.InitObject.mediaPlayer?.pause()
                    SongPlayingFragment.InitObject.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val mTelephonyManager: TelephonyManager =
                context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (mTelephonyManager.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        MainActivity.BaseObject.notificationManager?.cancel(1999)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.InitObject.mediaPlayer?.isPlaying as Boolean) {
                            SongPlayingFragment.InitObject.mediaPlayer?.pause()
                            SongPlayingFragment.InitObject.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {
                }
            }
        }

    }
}
