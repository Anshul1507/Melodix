package tech.anshul1507.melodix.SongPlayingScreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import tech.anshul1507.melodix.DB.LocalDB
import tech.anshul1507.melodix.EqualizerScreen.EqualizerActivity
import tech.anshul1507.melodix.FavoriteScreen.FavoriteFragment
import tech.anshul1507.melodix.HomeScreen.HomeFragment
import tech.anshul1507.melodix.Models.CurrentSongHelper
import tech.anshul1507.melodix.Models.Songs
import tech.anshul1507.melodix.R
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class SongPlayingFragment : Fragment() {

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f
    var audioManager: AudioManager? = null

    object InitObject {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var favButton: ImageButton? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseButton: ImageButton? = null
        var previousButton: ImageButton? = null
        var nextButton: ImageButton? = null
        var loopButton: ImageButton? = null
        var shuffleButton: ImageButton? = null
        var equalizerButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var instanceDB: LocalDB? = null
        var fetchSongs: ArrayList<Songs>? = null
        var audioVisulization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var currentSongHelper: CurrentSongHelper? = null
        var songIdx: Int = 0

        var seekBarVolume: SeekBar? = null

        var shakeFlag: Int = 0
        var MY_PREFS_NAME = "ShakeFeature"
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null

        var updateSongTime = object : Runnable {
            override fun run() {
                seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromuser: Boolean
                    ) {
                        if (fromuser) {
                            mediaPlayer?.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })
                val getCurrent = mediaPlayer?.currentPosition as Int
                val totalDuration = mediaPlayer?.duration as Int
                val currentDuration = mediaPlayer?.currentPosition as Int

                startTimeText?.text = String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong()) % 60
                )
                seekBar?.max = totalDuration
                seekBar?.progress = currentDuration
                Handler().postDelayed(this, 1000)
            }
        }

    }

    object SongPlayingObject {
        var PREFS_SHUFFLE = "Shuffle Feature"
        var PREFS_LOOP = "Loop Feature"
        fun checkAndSetFavIcon() {
            if (InitObject.instanceDB?.checkIfIdExists(InitObject.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                InitObject.favButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        InitObject.myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                InitObject.favButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        InitObject.myActivity!!,
                        R.drawable.favorite_off
                    )
                )
            }
        }

        fun playNext(check: String) {

            if (check.equals("PlayNextNormal", true)) {
                InitObject.songIdx = InitObject.songIdx + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {

                val randomPosition =
                    Random().nextInt(InitObject.fetchSongs?.size?.plus(1) as Int)
                InitObject.songIdx = randomPosition
            }

            if (InitObject.songIdx == InitObject.fetchSongs?.size) {
                InitObject.songIdx = 0
            }
            InitObject.currentSongHelper?.isLoop = false

            val nextSong = InitObject.fetchSongs?.get(InitObject.songIdx)
            InitObject.currentSongHelper?.songPath = nextSong?.songData
            InitObject.currentSongHelper?.songTitle = nextSong?.songTitle
            InitObject.currentSongHelper?.songArtist = nextSong?.artist
            InitObject.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextViews(
                InitObject.currentSongHelper?.songTitle as String,
                InitObject.currentSongHelper?.songArtist as String
            )

            InitObject.mediaPlayer?.reset()
            try {
                InitObject.mediaPlayer?.setDataSource(
                    InitObject.myActivity as Context,
                    Uri.fromFile(File(InitObject.currentSongHelper?.songPath as String))
                )
                InitObject.mediaPlayer?.prepare()
                InitObject.mediaPlayer?.start()

                processInformation(InitObject.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            checkAndSetFavIcon()
        }

        fun playPrevious() {
            InitObject.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            InitObject.songIdx = InitObject.songIdx - 1

            if (InitObject.songIdx == -1) {
                InitObject.songIdx = 0
            }
            if (InitObject.currentSongHelper?.isPlaying as Boolean) {
                InitObject.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                InitObject.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            InitObject.currentSongHelper?.isLoop = false

            val nextSong = InitObject.fetchSongs?.get(InitObject.songIdx)
            InitObject.currentSongHelper?.songPath = nextSong?.songData
            InitObject.currentSongHelper?.songTitle = nextSong?.songTitle
            InitObject.currentSongHelper?.songArtist = nextSong?.artist
            InitObject.currentSongHelper?.songId = nextSong?.songID as Long

            updateTextViews(
                InitObject.currentSongHelper?.songTitle as String,
                InitObject.currentSongHelper?.songArtist as String
            )
            InitObject.mediaPlayer?.reset()
            try {
                InitObject.mediaPlayer?.setDataSource(
                    InitObject.myActivity as Context,
                    Uri.parse(InitObject.currentSongHelper?.songPath)
                )
                InitObject.mediaPlayer?.prepare()
                InitObject.mediaPlayer?.start()

                processInformation(InitObject.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            checkAndSetFavIcon()
        }

        fun onSongComplete() {
            if (InitObject.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                InitObject.currentSongHelper?.isPlaying = true
            } else {

                if (InitObject.currentSongHelper?.isLoop as Boolean) {
                    InitObject.currentSongHelper?.isPlaying = true
                    val nextSong = InitObject.fetchSongs?.get(InitObject.songIdx) as Songs
                    InitObject.currentSongHelper?.songIdx = InitObject.songIdx
                    InitObject.currentSongHelper?.songPath = nextSong.songData
                    InitObject.currentSongHelper?.songTitle = nextSong.songTitle
                    InitObject.currentSongHelper?.songArtist = nextSong.artist
                    InitObject.currentSongHelper?.songId = nextSong.songID

                    updateTextViews(
                        InitObject.currentSongHelper?.songTitle as String,
                        InitObject.currentSongHelper?.songArtist as String
                    )
                    InitObject.mediaPlayer?.reset()
                    try {
                        InitObject.mediaPlayer?.setDataSource(
                            InitObject.myActivity as Context,
                            Uri.fromFile(File(InitObject.currentSongHelper?.songPath as String))
                        )
                        InitObject.mediaPlayer?.prepare()
                        InitObject.mediaPlayer?.start()
                        processInformation(InitObject.mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    InitObject.currentSongHelper?.isPlaying = true
                }
            }
            checkAndSetFavIcon()
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var updatedSongTitle = songTitle
            var updatedSongArtist = songArtist
            if (updatedSongArtist.equals("<unknown>", true)) {
                updatedSongArtist = "unknown"
            }
            if (updatedSongTitle.equals("<unknown>", true)) {
                updatedSongTitle = "unknown"
            }
            InitObject.songTitleView?.text = updatedSongTitle
            InitObject.songArtistView?.text = updatedSongArtist
        }

        fun processInformation(mediaPlayer: MediaPlayer) {

            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition

            InitObject.startTimeText?.text = String.format(
                "%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                )
            )

            InitObject.endTimeText?.text = String.format(
                "%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())
                )
            )

            Handler().postDelayed(InitObject.updateSongTime, 1000)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_playing, container, false)
        activity?.title = "Now Playing"
        setHasOptionsMenu(true)
        InitObject.seekBar = view.findViewById(R.id.seek_bar)
        InitObject.startTimeText = view.findViewById(R.id.start_time)
        InitObject.endTimeText = view.findViewById(R.id.end_time)
        InitObject.playPauseButton = view.findViewById(R.id.btn_play_pause)
        InitObject.nextButton = view.findViewById(R.id.next_btn)
        InitObject.previousButton = view.findViewById(R.id.prev_btn)
        InitObject.loopButton = view.findViewById(R.id.loop_btn)
        InitObject.shuffleButton = view.findViewById(R.id.shuffle_btn)
        InitObject.songArtistView = view.findViewById(R.id.song_artist)
        InitObject.songTitleView = view.findViewById(R.id.song_title)
        InitObject.glView = view.findViewById(R.id.visualizer_view)
        InitObject.favButton = view.findViewById(R.id.favorite_icon)
        InitObject.seekBarVolume = view.findViewById(R.id.seek_bar_volume)
        InitObject.equalizerButton = view.findViewById(R.id.equalizer_btn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InitObject.audioVisulization = InitObject.glView as AudioVisualization //After View Creation

        /*SeekBar Volume*/
        audioManager =
            InitObject.myActivity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        InitObject.seekBarVolume?.progress =
            audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC) //set pre-volume in seekbar
        InitObject.seekBarVolume?.max =
            audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC) //set max length
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        InitObject.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        InitObject.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        InitObject.audioVisulization?.onResume()
        InitObject.mSensorManager?.registerListener(
            InitObject.mSensorListener,
            InitObject.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        handleShakeFeature()
    }

    override fun onPause() {
        InitObject.audioVisulization?.onPause()
        super.onPause()
        InitObject.mSensorManager?.unregisterListener(InitObject.mSensorListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InitObject.mSensorManager =
            InitObject.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onDestroyView() {
        InitObject.audioVisulization?.release()
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu.findItem(R.id.action_redirect)
        item?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_redirect -> {
                InitObject.myActivity?.onBackPressed()
                return false
            }
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        InitObject.instanceDB = LocalDB(InitObject.myActivity)
        InitObject.currentSongHelper = CurrentSongHelper()

        InitObject.currentSongHelper?.isPlaying = true
        InitObject.currentSongHelper?.isLoop = false
        InitObject.currentSongHelper?.isShuffle = false
        var path: String? = null
        val songTitle: String?
        val songArtist: String?
        val songId: Long

        try {
            path = arguments?.getString("path")
            songTitle = arguments?.getString("songTitle")
            songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()

            /*Here we fetch the received bundle data for current position and the list of all songs*/
            InitObject.songIdx = arguments?.getInt("songPosition")!!
            InitObject.fetchSongs = arguments?.getParcelableArrayList("songData")

            /*Now store the song details to the current song helper object so that they can be used later*/
            InitObject.currentSongHelper?.songPath = path
            InitObject.currentSongHelper?.songTitle = songTitle
            InitObject.currentSongHelper?.songArtist = songArtist
            InitObject.currentSongHelper?.songId = songId
            InitObject.currentSongHelper?.songIdx = InitObject.songIdx
            SongPlayingObject.updateTextViews(
                InitObject.currentSongHelper?.songTitle as String,
                InitObject.currentSongHelper?.songArtist as String
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fromFavBottomBar = arguments!!.getString("FavBottomBar")
        val fromMainBottomBar = arguments!!.getString("MainBottomBar")
        when {
            fromFavBottomBar != null -> {
                InitObject.mediaPlayer = FavoriteFragment.FavObject.mediaPlayer
            }
            fromMainBottomBar != null -> {
                InitObject.mediaPlayer = HomeFragment.HomeObject.mediaPlayer
            }
            else -> {
                if (InitObject.mediaPlayer != null) {
                    if (InitObject.mediaPlayer?.isPlaying!!) {
                        InitObject.mediaPlayer?.pause()
                    }
                }
                InitObject.mediaPlayer = MediaPlayer()
                InitObject.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

                try {

                    InitObject.mediaPlayer?.setDataSource(
                        InitObject.myActivity as Context,
                        Uri.fromFile(File(path as String))
                    )
                    InitObject.mediaPlayer?.prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                InitObject.mediaPlayer?.start()
            }
        }
        SongPlayingObject.processInformation(InitObject.mediaPlayer as MediaPlayer)
        if (InitObject.currentSongHelper?.isPlaying as Boolean) {
            InitObject.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            InitObject.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }

        /*Handling the event when media player finishes a song*/
        InitObject.mediaPlayer?.setOnCompletionListener {
            SongPlayingObject.onSongComplete()
        }

        clickHandler()
        SongPlayingObject.checkAndSetFavIcon()

        val visualizationHandler =
            DbmHandler.Factory.newVisualizerHandler(
                InitObject.myActivity as Context,
                0
            )
        InitObject.audioVisulization?.linkTo(visualizationHandler)

        val shufflePrefs = InitObject.myActivity?.getSharedPreferences(
            SongPlayingObject.PREFS_SHUFFLE,
            Context.MODE_PRIVATE
        )
        val loopPrefs = InitObject.myActivity?.getSharedPreferences(
            SongPlayingObject.PREFS_LOOP,
            Context.MODE_PRIVATE
        )
        val isShuffleON = shufflePrefs?.getBoolean("feature", false)
        val isLoopON = loopPrefs?.getBoolean("feature", false)
        if (isShuffleON as Boolean) {
            InitObject.currentSongHelper?.isShuffle = true
            InitObject.currentSongHelper?.isLoop = false
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
            InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle_active)
        } else {
            InitObject.currentSongHelper?.isShuffle = false
            InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle)
        }
        if (isLoopON as Boolean) {
            InitObject.currentSongHelper?.isShuffle = false
            InitObject.currentSongHelper?.isLoop = true
            InitObject.loopButton?.setBackgroundResource(R.drawable.rotate_active)
            InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle)
        } else {
            InitObject.currentSongHelper?.isLoop = false
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
        }

    }

    private fun clickHandler() {

        InitObject.favButton?.setOnClickListener {
            if (InitObject.instanceDB?.checkIfIdExists(InitObject.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                InitObject.favButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        InitObject.myActivity!!,
                        R.drawable.favorite_off
                    )
                )
                InitObject.instanceDB?.deleteFavorite(InitObject.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(InitObject.myActivity!!, "Removed from Favorite", Toast.LENGTH_SHORT)
                    .show()
            } else {
                InitObject.favButton?.setImageDrawable(
                    ContextCompat.getDrawable(
                        InitObject.myActivity!!,
                        R.drawable.favorite_on
                    )
                )
                InitObject.instanceDB?.storeFavorite(
                    InitObject.currentSongHelper?.songId?.toInt() as Int,
                    InitObject.currentSongHelper?.songTitle,
                    InitObject.currentSongHelper?.songArtist,
                    InitObject.currentSongHelper?.songPath
                )
                Toast.makeText(InitObject.myActivity!!, "Added to Favorite", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        InitObject.playPauseButton?.setOnClickListener {
            if (InitObject.mediaPlayer?.isPlaying as Boolean) {
                InitObject.mediaPlayer?.pause()
                InitObject.currentSongHelper?.isPlaying = false
                InitObject.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                InitObject.mediaPlayer?.start()
                InitObject.currentSongHelper?.isPlaying = true
                InitObject.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

        InitObject.shuffleButton?.setOnClickListener {
            val editorShuffle = InitObject.myActivity?.getSharedPreferences(
                SongPlayingObject.PREFS_SHUFFLE,
                Context.MODE_PRIVATE
            )?.edit()
            val editorLoop = InitObject.myActivity?.getSharedPreferences(
                SongPlayingObject.PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()

            if (InitObject.currentSongHelper?.isShuffle as Boolean) {
                InitObject.currentSongHelper?.isShuffle = false
                InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                InitObject.currentSongHelper?.isShuffle = true;
                InitObject.currentSongHelper?.isLoop = false
                InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle_active)
                InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        }
        InitObject.nextButton?.setOnClickListener {
            InitObject.currentSongHelper?.isPlaying = true
            InitObject.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            val prefs = InitObject.myActivity?.getSharedPreferences(
                SongPlayingObject.PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()
            prefs?.putBoolean("feature", false)
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
            InitObject.currentSongHelper?.isLoop = false

            if (InitObject.currentSongHelper?.isShuffle as Boolean) {
                SongPlayingObject.playNext("PlayNextLikeNormalShuffle")
            } else {
                SongPlayingObject.playNext("PlayNextNormal")
            }
        }
        InitObject.previousButton?.setOnClickListener {
            InitObject.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            val prefs = InitObject.myActivity?.getSharedPreferences(
                SongPlayingObject.PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()
            prefs?.putBoolean("feature", false)
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
            InitObject.currentSongHelper?.isLoop = false
            InitObject.currentSongHelper?.isPlaying = true

            if (InitObject.currentSongHelper?.isLoop as Boolean) {
                InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
            }

            SongPlayingObject.playPrevious()
        }
        InitObject.loopButton?.setOnClickListener {
            val editorShuffle = InitObject.myActivity?.getSharedPreferences(
                SongPlayingObject.PREFS_SHUFFLE,
                Context.MODE_PRIVATE
            )?.edit()
            val editorLoop = InitObject.myActivity?.getSharedPreferences(
                SongPlayingObject.PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()
            if (InitObject.currentSongHelper?.isLoop as Boolean) {
                InitObject.currentSongHelper?.isLoop = false
                InitObject.loopButton?.setBackgroundResource(R.drawable.loop)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                InitObject.currentSongHelper?.isLoop = true
                InitObject.currentSongHelper?.isShuffle = false
                InitObject.loopButton?.setBackgroundResource(R.drawable.rotate_active)
                InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle)
            }
        }
        InitObject.equalizerButton?.setOnClickListener {
            InitObject.myActivity?.startActivity(
                Intent(
                    InitObject.myActivity,
                    EqualizerActivity::class.java
                )
            )
        }

        InitObject.seekBarVolume?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newVolume: Int, b: Boolean) {
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun handleShakeFeature() {

        InitObject.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = sqrt(((x * x + y * y + z * z).toDouble())).toFloat()

                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if (mAcceleration > 12) {
                    val prefs = InitObject.myActivity?.getSharedPreferences(
                        InitObject.MY_PREFS_NAME,
                        Context.MODE_PRIVATE
                    )
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        SongPlayingObject.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }

}