package tech.anshul1507.melodix.HomeScreen

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.melodix.R
import tech.anshul1507.melodix.Models.Songs
import tech.anshul1507.melodix.SongPlayingScreen.SongPlayingFragment
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private var songsList: ArrayList<Songs>? = null
    private var nowPlayingBottomBar: RelativeLayout? = null
    private var playPauseButton: ImageButton? = null
    private var songTitle: TextView? = null
    private var contentMainLayout: RelativeLayout? = null
    private var noSongsLayout: RelativeLayout? = null
    private var mainRecyclerView: RecyclerView? = null
    private var myActivity: Activity? = null
    private var homeScreenAdapter: HomeScreenAdapter? = null
    private var trackPosition: Int = 0
    private var trackTitle: TextView? = null

    object HomeObject {
        var mediaPlayer: MediaPlayer? = null
        var flag: Int? = 0
        var toolbar: Toolbar? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "All Songs"
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        setHasOptionsMenu(true)
        HomeObject.toolbar = view?.findViewById(R.id.toolbar)
        return view
    }

    private fun initViews(view: View) {
        nowPlayingBottomBar = view.findViewById(R.id.bottom_play_layout)
        playPauseButton = view.findViewById(R.id.btn_play_pause_fav)
        songTitle = view.findViewById(R.id.song_title_text_fav)
        contentMainLayout = view.findViewById(R.id.content_main_layout)
        noSongsLayout = view.findViewById(R.id.no_songs_layout)
        mainRecyclerView = view.findViewById(R.id.main_recycler_view)
        trackTitle = view.findViewById(R.id.track_title)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bottomBarSetup()
        songsList = getSongsFromDevice()

        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val actionSortAscending = prefs?.getString("action_sort_ascending", "true")
        val actionSortRecent = prefs?.getString("action_sort_recent", "false")

        if (songsList.isNullOrEmpty()) {
            contentMainLayout?.visibility = View.INVISIBLE
            noSongsLayout?.visibility = View.VISIBLE
        } else {
            homeScreenAdapter =
                HomeScreenAdapter(songsList as ArrayList<Songs>, myActivity as Context)
            mainRecyclerView?.layoutManager = LinearLayoutManager(myActivity)
            mainRecyclerView?.itemAnimator = DefaultItemAnimator()
            mainRecyclerView?.adapter = homeScreenAdapter
        }

        if (songsList.isNullOrEmpty()) {
            if (actionSortAscending!!.equals("true", ignoreCase = true)) {
                Collections.sort(songsList, Songs.SongObject.nameComparator)
                homeScreenAdapter?.notifyDataSetChanged()
            } else if (actionSortRecent!!.equals("true", ignoreCase = true)) {
                Collections.sort(songsList, Songs.SongObject.dateComparator)
                homeScreenAdapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_ascending -> {
                if (!songsList.isNullOrEmpty()) {
                    val editor =
                        myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
                            ?.edit()
                    editor?.putString("action_sort_ascending", "true")
                    editor?.putString("action_sort_recent", "false")
                    editor?.apply()
                    Collections.sort(songsList, Songs.SongObject.nameComparator)
                }
                homeScreenAdapter?.notifyDataSetChanged()
            }
            R.id.action_sort_recent -> {
                val editor =
                    myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
                editor?.putString("action_sort_recent", "true")
                editor?.putString("action_sort_ascending", "false")
                editor?.apply()
                if (!songsList.isNullOrEmpty()) {
                    Collections.sort(songsList, Songs.SongObject.dateComparator)
                }
                homeScreenAdapter?.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bottomBarSetup() {

        bottomBarClickHandler()

        songTitle?.text = SongPlayingFragment.InitObject.currentSongHelper?.songTitle

        SongPlayingFragment.InitObject.mediaPlayer?.setOnCompletionListener {
            SongPlayingFragment.SongPlayingObject.onSongComplete()
            songTitle?.text = SongPlayingFragment.InitObject.currentSongHelper?.songTitle
        }

        if (SongPlayingFragment.InitObject.mediaPlayer != null && SongPlayingFragment.InitObject.mediaPlayer?.isPlaying as Boolean) {
            nowPlayingBottomBar?.visibility = View.VISIBLE
        } else {
            nowPlayingBottomBar?.visibility = View.INVISIBLE
        }
    }

    private fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener {
            HomeObject.mediaPlayer = SongPlayingFragment.InitObject.mediaPlayer

            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString(
                "songArtist",
                SongPlayingFragment.InitObject.currentSongHelper?.songArtist
            )
            args.putString("songTitle", SongPlayingFragment.InitObject.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.InitObject.currentSongHelper?.songPath)
            args.putInt(
                "songId",
                SongPlayingFragment.InitObject.currentSongHelper?.songId?.toInt() as Int
            )
            args.putInt(
                "songPosition",
                SongPlayingFragment.InitObject.currentSongHelper?.songIdx as Int
            )
            args.putParcelableArrayList("songData", SongPlayingFragment.InitObject.fetchSongs)
            args.putString("MainBottomBar", "success")
            songPlayingFragment.arguments = args
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, songPlayingFragment)
                ?.addToBackStack("SongPlayingFragment")
                ?.commit()
        }

        playPauseButton?.setOnClickListener {
            if (SongPlayingFragment.InitObject.mediaPlayer != null && SongPlayingFragment.InitObject.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.InitObject.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.InitObject.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.InitObject.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.InitObject.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }

    /**
     * - getSongsFromPhone()
     * Function to fetch Songs from Device
     */
    private fun getSongsFromDevice(): ArrayList<Songs> {
        var list = ArrayList<Songs>()
        val contentResolver = myActivity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtists = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)
                list.add(Songs(currentId, currentTitle, currentArtists, currentData, currentDate))
            }
        }
        return list
    }
}