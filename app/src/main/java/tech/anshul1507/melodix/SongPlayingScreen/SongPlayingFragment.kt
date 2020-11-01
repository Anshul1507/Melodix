package tech.anshul1507.melodix.SongPlayingScreen

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import tech.anshul1507.melodix.Models.CurrentSongHelper
import tech.anshul1507.melodix.Models.Songs
import tech.anshul1507.melodix.R
import java.util.*
import java.util.concurrent.TimeUnit

class SongPlayingFragment : Fragment() {

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
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var fetchSongs: ArrayList<Songs>? = null
        var audioVisulization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var currentSongHelper: CurrentSongHelper? = null
        var songIdx: Int = 0
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
                    Uri.parse(InitObject.currentSongHelper?.songPath)
                )
                InitObject.mediaPlayer?.prepare()
                InitObject.mediaPlayer?.start()

                processInformation(InitObject.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //TODO:: DB check for fav icon
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
            //TODO:: DB check for fav icon
        }

        fun onSongComplete() {
            if (InitObject.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                InitObject.currentSongHelper?.isPlaying = true
            } else {

                if (InitObject.currentSongHelper?.isLoop as Boolean) {
                    InitObject.currentSongHelper?.isPlaying = true
                    val nextSong = InitObject.fetchSongs?.get(InitObject.songIdx) as Songs
                    InitObject.currentSongHelper?.sp = InitObject.songIdx
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
                            Uri.parse(InitObject.currentSongHelper?.songPath)
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
            //TODO:: DB Check for fav icon
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
            InitObject.songTitleView?.setText(updatedSongTitle)
            InitObject.songArtistView?.setText(updatedSongArtist)
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InitObject.audioVisulization = InitObject.glView as AudioVisualization //After View Creation
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
    }

    override fun onPause() {
        InitObject.audioVisulization?.onPause()
        super.onPause()
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
        //TODO:: DB CHECK for fav content

        InitObject.currentSongHelper = CurrentSongHelper()

        InitObject.currentSongHelper?.isPlaying = true
        InitObject.currentSongHelper?.isLoop = false
        InitObject.currentSongHelper?.isShuffle = false
        var path: String? = null
        var songTitle: String? = null
        var songArtist: String? = null
        var songId: Long = 0

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
            InitObject.currentSongHelper?.sp = InitObject.songIdx
            SongPlayingObject.updateTextViews(
                InitObject.currentSongHelper?.songTitle as String,
                InitObject.currentSongHelper?.songArtist as String
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fromFavBottomBar = arguments!!.getString("FavBottomBar") as? String
        val fromMainBottomBar = arguments!!.getString("MainBottomBar") as? String
        when {
            fromFavBottomBar != null -> {
                //TODO :: assign mediaplayer from fav fragment
            }
            fromMainBottomBar != null -> {
                //TODO :: assign mediaplayer from home fragment
            }
            else -> {
                if (InitObject.mediaPlayer != null) {
                    if (InitObject.mediaPlayer?.isPlaying as Boolean) {
                        InitObject.mediaPlayer?.pause()
                    }
                }
                InitObject.mediaPlayer = MediaPlayer()
                InitObject.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

                try {
                    InitObject.mediaPlayer?.setDataSource(
                        InitObject.myActivity as Context,
                        Uri.parse(path)
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

        //TODO:: Click Handlers
        val visualizationHandeler =
            DbmHandler.Factory.newVisualizerHandler(InitObject.myActivity as Context, 0)
        InitObject.audioVisulization?.linkTo(visualizationHandeler)

        //TODO:: Db check for fav icon


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
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
            InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
        } else {
            InitObject.currentSongHelper?.isShuffle = false
            InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        if (isLoopON as Boolean) {
            InitObject.currentSongHelper?.isShuffle = false
            InitObject.currentSongHelper?.isLoop = true
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop_icon)
            InitObject.shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        } else {
            InitObject.currentSongHelper?.isLoop = false
            InitObject.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

    }
}