package tech.anshul1507.melodix.FavoriteScreen

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.melodix.DB.LocalDB
import tech.anshul1507.melodix.Models.Songs
import tech.anshul1507.melodix.R
import tech.anshul1507.melodix.SongPlayingScreen.SongPlayingFragment

class FavoriteFragment : Fragment() {

    private var myActivity: Activity? = null
    private var noFavorites: TextView? = null
    private var nowPlayingBottomBar: RelativeLayout? = null
    private var playPauseButton: ImageButton? = null
    private var songTitle: TextView? = null
    private var favRecyclerView: RecyclerView? = null
    private var trackPosition: Int = 0
    private var favSongsList: ArrayList<Songs>? = null
    private var dbInstance: LocalDB? = null

    object FavObject {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        myActivity?.title = "Favorites"
        setHasOptionsMenu(true)
        dbInstance = LocalDB(myActivity)
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
        fetchFavoriteSongs()
        bottomBarSetup()
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
            FavObject.mediaPlayer = SongPlayingFragment.InitObject.mediaPlayer

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
            args.putString("FavBottomBar", "success")
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

    private fun fetchFavoriteSongs() {
        if (dbInstance?.checkSize() as Int > 0) {
            favSongsList = ArrayList() // Make a new instance
            val listSongsFromDevice = getSongsFromDevice()
            for (item in listSongsFromDevice) {
                if (dbInstance?.queryDBList(item.songID) == 1) {
                    favSongsList!!.add(item)
                }
            }
            if (favSongsList!!.isEmpty()) {
                favRecyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
                val favAdapter =
                    FavScreenAdapter(favSongsList as ArrayList<Songs>, myActivity as Context)
                favRecyclerView?.layoutManager = LinearLayoutManager(activity)
                favRecyclerView?.itemAnimator = DefaultItemAnimator()
                favRecyclerView?.adapter = favAdapter
                favRecyclerView?.setHasFixedSize(true)
            }
        } else {
            favRecyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
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