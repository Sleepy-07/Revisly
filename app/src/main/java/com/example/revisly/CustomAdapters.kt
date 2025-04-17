package com.example.revisly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class PlatFormAdapter(context: Context, val list : List<Platform>,private val onItemClick: (Platform) -> Unit) : ArrayAdapter<Platform>(context,R.layout.adapter_platfomr_list_view, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView?: LayoutInflater.from(context).inflate(R.layout.adapter_platfomr_list_view,parent,false)

        val platformname = view.findViewById<TextView>(R.id.platformname)
        val platformicon = view.findViewById<ImageView>(R.id.Platformicon)


        platformname.text = list[position].platformname
        platformicon.setImageResource(list[position].platfomricon)

      view.setOnClickListener {
          onItemClick(list[position])
      }
        return view
    }


}