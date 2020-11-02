package tech.anshul1507.melodix.FavoriteScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.melodix.R
import tech.anshul1507.melodix.Models.Songs
import tech.anshul1507.melodix.SongPlayingScreen.SongPlayingFragment

class FavScreenAdapter(var songDetailsList: ArrayList<Songs>, var ctx: Context) :
    RecyclerView.Adapter<FavScreenAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var itemLayout: RelativeLayout? = null

        init {
            trackTitle = view.findViewById(R.id.track_title) as TextView
            trackArtist = view.findViewById(R.id.track_artist) as TextView
            itemLayout = view.findViewById(R.id.item_home_layout) as RelativeLayout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_song, parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songDetailsList[position]
        holder.trackTitle?.text = song.songTitle
        holder.trackArtist?.text = song.artist

        holder.itemLayout?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist", song.artist)
            args.putString("songTitle", song.songTitle)
            args.putString("path", song.songData)
            args.putInt("songId", song.songID.toInt())
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetailsList)
            songPlayingFragment.arguments = args
            (ctx as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, songPlayingFragment)
                .addToBackStack("SongPlayingFragmentFavorite")
                .commit()
        }
    }

    override fun getItemCount(): Int = songDetailsList.size
}
