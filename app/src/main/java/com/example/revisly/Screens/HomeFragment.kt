package com.example.revisly.Screens

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.revisly.PlatFormAdapter
import com.example.revisly.Platform
import com.example.revisly.Metadata
import com.example.revisly.R
import com.example.revisly.TestingAdpter
import com.example.revisly.databinding.DialogEnterllinkBinding
import com.example.revisly.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var savebinding : DialogEnterllinkBinding
    lateinit var testingAdpter: TestingAdpter

    val imglist = listOf(
        R.drawable.test1,
        R.drawable.test2,
        R.drawable.test3,
        R.drawable.test4,
        R.drawable.test5,
        R.drawable.test6,
        R.drawable.test7,
        R.drawable.test8,
        R.drawable.test9,
        R.drawable.test10,
        R.drawable.test11,
        R.drawable.test12,
        R.drawable.test13,
        R.drawable.test14,
        R.drawable.test15,
        R.drawable.test16,
        R.drawable.test17,
        R.drawable.test18,
        R.drawable.test19,
        R.drawable.test20,



    )

    val platformlist = listOf<Platform>(
        Platform("Youtube",R.drawable.youtube),
        Platform("Facebook",R.drawable.facebook),
        Platform("Instagram",R.drawable.instagram),
        Platform("Pintrest",R.drawable.pinterest),
        Platform("Spotify",R.drawable.spotify),
        Platform("Telegram",R.drawable.telegram),
        Platform("Twitter or X",R.drawable.twitter),
        Platform("Amazon",R.drawable.amazon),
        Platform("Flipkart",R.drawable.flipkart),
        Platform("Playstore",R.drawable.playstore),
        Platform("Steam",R.drawable.steam),



        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        savebinding = DialogEnterllinkBinding.inflate(layoutInflater)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        testingAdpter = TestingAdpter(imglist)


        binding.AddSave.setOnClickListener {
            OpenSaveLink()
        }

        SetupPlatformAdapter()



        binding.TestPin.layoutManager = StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager.VERTICAL
        )
        binding.TestPin.adapter = testingAdpter
        binding.TestPin.itemAnimator = DefaultItemAnimator()



    }

    private fun SetupPlatformAdapter() {
        val adapter = PlatFormAdapter(requireContext(),platformlist){selectedplatform ->

            savebinding.platformselect.setText(selectedplatform.platformname, false)

            val drawable = ContextCompat.getDrawable(requireContext(), selectedplatform.platfomricon)
            drawable?.setBounds(0, 0, 60, 60) // Resize if needed

            savebinding.platformselect.setCompoundDrawables(drawable, null, null, null)

            savebinding.platformselect.dismissDropDown()
        }
        savebinding.platformselect.setAdapter(adapter)


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}

private fun HomeFragment.OpenSaveLink() {
    val dailog = Dialog(requireContext())
    dailog.apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(savebinding.root)
        window?.setBackgroundDrawableResource(R.drawable.round_corner)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val dialogWidth = (screenWidth * 0.9).toInt()
        val dialogheight = (screenHeight * 0.8) .toInt()

        window?.setLayout(dialogWidth,dialogheight)

        window?.setDimAmount(.5f)
        setCancelable(false)

    }
    dailog.show()

    savebinding.apply {
        BackDialogsave.setOnClickListener {
            dailog.dismiss()
        }
        val list = listOf(
            SavesUrl,
            SaveTitle,
            SaveTags
        )




        SaveBtn.setOnClickListener {
            if(SavesUrl.text.toString().isEmpty() ||
                SaveTitle.text.toString().isEmpty() ||
                SaveTags.text.toString().isEmpty()){

                Toast.makeText(requireContext(), "Please enter the minimum information", Toast.LENGTH_SHORT).show()
                for (i in list){
                    if(i.text.toString().isEmpty()){
                        i.error =  "Please enter this"
                    }
                }
            }
            else{
                val data = mapOf(
                    "url" to SavesUrl.text.toString(),
                    "title" to SaveTitle.text.toString(),
                    "note" to SaveNote.text.toString(),
                    "tags" to SaveTags.text.toString(),
                    "platform" to platformselect.text.toString(),
                    "timecreated" to System.currentTimeMillis()

                )

                Toast.makeText(requireContext(), "Data is added", Toast.LENGTH_SHORT).show()
                Log.e("Data is added ", "OpenSaveLink:  $data", )
                dailog.dismiss()

            }
        }

    }






    fun fetchMetadata(url: String): Metadata {
        try {
            // Fetch the webpage HTML
            val doc: Document = Jsoup.connect(url).get()

            // Extract Open Graph metadata (og:title, og:image, og:type)
            val title = doc.select("meta[property=og:title]").attr("content")
            val thumbnail = doc.select("meta[property=og:image]").attr("content")
            val type = doc.select("meta[property=og:type]").attr("content")

            // If no Open Graph data is found, use the fallback <title> tag
            val fallbackTitle = if (title.isEmpty()) doc.title() else title
            val fallbackType = if (type.isEmpty()) "website" else type

            return Metadata(fallbackTitle, thumbnail.takeIf { it.isNotEmpty() }, fallbackType)
        } catch (e: IOException) {
            e.printStackTrace()
            return Metadata("Error", null, "website")
        }
    }
}
