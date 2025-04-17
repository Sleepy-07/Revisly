package com.example.revisly

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TestingAdpter(val list : List<Int>) : RecyclerView.Adapter<TestingAdpter.ViewHolder>() {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val img = view.findViewById<ImageView>(R.id.ImageSet)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TestingAdpter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.testing_pin,parent,false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: TestingAdpter.ViewHolder, position: Int) {
      holder.apply {

          Log.e("Image is seted $position ", "onBindViewHolder:  ", )
          val item = list[position]
          Glide.with(holder.itemView.context)
              .load(item)
              .centerCrop()
              .into(img)

      }

    }

    override fun getItemCount(): Int = list.size

}