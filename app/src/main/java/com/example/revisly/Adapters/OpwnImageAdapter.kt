package com.example.revisly.Adapters

import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.revisly.Extras.DynamicHeightViewPager2
import com.example.revisly.FlattenedImage
import com.example.revisly.R
import com.example.revisly.SavesData
import com.example.revisly.utility.canScrollHorizontallyBackward
import com.example.revisly.utility.canScrollHorizontallyForward
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.relex.circleindicator.CircleIndicator3

class OpenImageAdapter(val list : MutableList<SavesData> ) : RecyclerView.Adapter<OpenImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(R.layout.openimage_viewpagger_layout,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.apply {
            val item = list[position]
            accountname.text = item.account_name
            title.text = item.title
            timesavestamp.text = item.timestamp.toString()


            back.setOnClickListener {
                // Handle back button click
            }

            direct.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                holder.itemView.context.startActivity(intent)
            }

//            viewpager.setAdapter(ShowImages(item.images))
            Log.d("OpenImageAdapter", "Images received: ${item.images}")

//            indicator.setViewPager(viewpager)

            val imageAdapter = ShowImages(item.images ?: mutableListOf())
            holder.viewpager.adapter = imageAdapter


            holder.viewpager.setOnTouchListener { _, _ ->
                val canScroll = holder.viewpager.canScrollHorizontallyForward() || holder.viewpager.canScrollHorizontallyBackward()
                if (canScroll) {
                    holder.viewpager.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }


            holder.indicator.setViewPager(holder.viewpager)

            item.images?.size?.let {
                if(it <= 1) {
                    holder.indicator.visibility = View.GONE
                } else {
                    holder.indicator.visibility = View.VISIBLE
                }
            }



        }


    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view)  {

        val back : ImageView = view.findViewById(R.id.backBtn)
        val direct : FloatingActionButton = view.findViewById(R.id.visitSourceButton)
        val viewpager : ViewPager2 = view.findViewById(R.id.ImageSet)
        val indicator : CircleIndicator3 = view.findViewById(R.id.Indicator)
        val timesavestamp : TextView = view.findViewById(R.id.imageSaveTime)
        val title : TextView = view.findViewById(R.id.imageTitle)
        val accountname : TextView = view.findViewById(R.id.imageaccount)
        val source : TextView = view.findViewById(R.id.imageSource)









    }


    inner class ShowImages(val imagelist : MutableList<String>? ) : RecyclerView.Adapter<ShowImages.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ShowImages.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_full_image,parent,false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ShowImages.ViewHolder,
            position: Int
        ) {
            val imageUrl = imagelist?.get(position)

            Log.d("ShowImages", "Binding image at position $position: ${imageUrl}")


            // Load the image using your preferred image loading library (e.g., Glide, Picasso)
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(object : CustomTarget<Drawable>() {


                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {
                        holder.imageView.setImageDrawable(resource)

                        // Notify height container
                        holder.imageView.post {
                            val dynamicContainer = (holder.itemView.parent?.parent as? DynamicHeightViewPager2)
                            dynamicContainer?.measureCurrentView(holder.itemView)
                        }





                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })

        }

        override fun getItemCount(): Int = imagelist?.size ?: 0

        inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view)  {
            val imageView : ImageView = view.findViewById(R.id.fullImageView)
            init {
                imageView.setOnClickListener {
                    // Handle image click
                }
            }

        }
    }
}