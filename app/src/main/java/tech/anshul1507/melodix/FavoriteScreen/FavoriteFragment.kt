package tech.anshul1507.melodix.FavoriteScreen

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.melodix.Models.Songs
import tech.anshul1507.melodix.R

class FavoriteFragment : Fragment() {

    private var myActivity: Activity? = null
    private var noFavorites: TextView? = null
    private var nowPlayingBottomBar: RelativeLayout? = null
    private var playPauseButton: ImageButton? = null
    private var songTitle: TextView? = null
    private var favRecyclerView: RecyclerView? = null
    private var trackPosition: Int = 0
    private var favSongsList: ArrayList<Songs>? = null

    object FavObject {
        var mediaPlayer: MediaPlayer? = null
        var flag: Int = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity?.title = "Favorites"
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        noFavorites = view?.findViewById(R.id.no_favorite_layout)
        nowPlayingBottomBar = view.findViewById(R.id.bottom_play_layout)
        songTitle = view.findViewById(R.id.song_title_text_fav)
        playPauseButton = view.findViewById(R.id.btn_play_pause_fav)
        favRecyclerView = view.findViewById(R.id.favorite_recycler_view)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_sort)
        item?.isVisible = false
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
        val list = ArrayList<Songs>()
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