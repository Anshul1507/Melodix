package tech.anshul1507.melodix

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.melodix.HomeScreen.HomeFragment

class NavDrawerAdapter(
    private var itemList: Array<String>,
    private var imageList: IntArray,
    private var context: Context
) :
    RecyclerView.Adapter<NavDrawerAdapter.NavViewHolder>() {

//    private var itemList: ArrayList<String> = itemList
//    var imageList: IntArray = imageList
//    var mContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder =
        NavViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nav_drawer, parent, false)
        )

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {

        holder.iconNavDrawer?.setBackgroundResource(imageList[position])
        holder.titleNavDrawer?.text = itemList[position]

        holder.itemNavDrawerLayout?.setOnClickListener {
            when (position) {
                0 -> {
                    val homeFragment = HomeFragment()
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment).commit()
                }
                1 -> {
                    //TODO:: Favorite Fragment
                }
                2 -> {
                    //TODO:: Settings Fragment
                }
            }
            MainActivity.BaseObject.drawerLayout?.closeDrawers()
        }
    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconNavDrawer: ImageView? = null
        var titleNavDrawer: TextView? = null
        var itemNavDrawerLayout: RelativeLayout? = null

        init {
            iconNavDrawer = itemView.findViewById(R.id.icon_nav_drawer)
            titleNavDrawer = itemView.findViewById(R.id.title_nav_drawer)
            itemNavDrawerLayout = itemView.findViewById(R.id.item_nav_drawer_layout)
        }
    }
}