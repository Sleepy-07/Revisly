package com.example.revisly.Adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.revisly.Adapters.ShowPostsAdapter.ViewHolder
import com.example.revisly.R
import com.example.revisly.SavesData
import kotlin.contracts.contract

class SavesAdapter(val list : MutableList<SavesData>, val click : OnClik) : RecyclerView.Adapter<SavesAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.SaveTitle)
        val time = view.findViewById<TextView>(R.id.SaveTime)
        val note = view.findViewById<TextView>(R.id.SaveNote)
        val source = view.findViewById<TextView>(R.id.SaveSource)
        val thumbnail = view.findViewById<ImageView>(R.id.SaveThumbnails)

        val fav = view.findViewById<ImageView>(R.id.SaveFav)





    }

    interface OnClik {
            fun toggelfav(postion : Int,img : ImageView)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_setsaves,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.apply {
            val item = list[position]
            if(item.favoraite){
                fav.setImageResource(R.drawable.ic_favorite_filled)
            }
            else{
                fav.setImageResource(R.drawable.ic_favorite)
            }

            title.text = item.title
            note.text = item.notes
            source.text = item.platform
            time.text = "save " +getTimeAgo(item.timestamp)
            Glide.with(itemView.context)
                .load(item.images?.get(0))
                .centerCrop()
                .into(thumbnail)


            fav.setOnClickListener {
                click.toggelfav(position,fav)
            }

            holder.itemView.setOnClickListener {
                val context = itemView.context
                val url = item.url
                if (url.isNotEmpty()) {
                    openUrl(context, url)
                } else {
                    Toast.makeText(context, "URL is empty", Toast.LENGTH_SHORT).show()
                }
            }


            }



    }

    override fun getItemCount(): Int = list.size

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No application can handle this request", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    fun getTimeAgo(timestampMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestampMillis

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
            else -> {
                // For older entries, return formatted date like "Apr 15, 2025"
                val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                sdf.format(java.util.Date(timestampMillis))
            }
        }
    }




}







