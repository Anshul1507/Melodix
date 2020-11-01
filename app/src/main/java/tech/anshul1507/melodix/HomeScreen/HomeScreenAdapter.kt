package tech.anshul1507.melodix.HomeScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import tech.anshul1507.melodix.R
import tech.anshul1507.melodix.Songs

class HomeScreenAdapter(var songDetailsList: ArrayList<Songs>, var ctx: Context) :
    RecyclerView.Adapter<HomeScreenAdapter.MyViewHolder>() {


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
            R.layout.item_home_screen, parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songDetailsList.get(position)
        holder.trackTitle?.text = song.songTitle
        holder.trackArtist?.text = song.artist

        holder.itemLayout?.setOnClickListener {
            //TODO :: play song on click and intent to playing screen
        }
    }

    override fun getItemCount(): Int = songDetailsList.size
}
