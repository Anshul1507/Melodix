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

    object Statified {
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
        Statified.toolbar = view?.findViewById(R.id.toolbar)
        return view
    }

    private fun initViews(view: View) {
        nowPlayingBottomBar = view.findViewById(R.id.bottom_play_layout)
        playPauseButton = view.findViewById(R.id.btn_play_pause_main)
        songTitle = view.findViewById(R.id.song_title_text_main)
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
        //TODO:: local db methods

        bottomBarSetup()

        songsList = getSongsFromDevice()
        if (songsList.isNullOrEmpty()) {
            contentMainLayout?.visibility = View.INVISIBLE
            noSongsLayout?.visibility = View.VISIBLE
        } else {
            //TODO:: set adapter for main recycler view
            homeScreenAdapter =
                HomeScreenAdapter(songsList as ArrayList<Songs>, myActivity as Context)
            mainRecyclerView?.layoutManager = LinearLayoutManager(myActivity)
            mainRecyclerView?.itemAnimator = DefaultItemAnimator()
            mainRecyclerView?.adapter = homeScreenAdapter
        }

        //TODO:: sort songs according to prefs

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
                    //TODO:: Save to local db and sort accordingly
                }
            }
            R.id.action_sort_recent -> {
                //TODO:: Save to local db and sort accordingly
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun bottomBarSetup() {
        bottomBarClickHandler()

        //TODO:: set song name to title textview
        //TODO:: song on complete listener
        //TODO:: boolean check for song playing -> visibility of bottom bar
    }

    private fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener {
            //TODO:: start Now Playing screen
        }

        playPauseButton?.setOnClickListener {
            //TODO:: play/pause media player
            //TODO:: change the play/pause icon accordingly
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