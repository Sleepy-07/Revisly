package com.example.revisly.Screens

import android.app.Dialog
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.revisly.Adapters.SavesAdapter
import com.example.revisly.Adapters.ViewPagerAdapter
import com.example.revisly.Platform
import com.example.revisly.Metadata
import com.example.revisly.R
import com.example.revisly.KeyData
import com.example.revisly.Retrofit.RetrofitClient
import com.example.revisly.Retrofit.ScrapeResponse
import com.example.revisly.Retrofit.UrlRequest
import com.example.revisly.RoomDatabase.Data
import com.example.revisly.SavesData
import com.example.revisly.ShareActivity
import com.example.revisly.databinding.DialogEnterllinkBinding
import com.example.revisly.databinding.FragmentHomeBinding
import com.example.revisly.test
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.math.log
import kotlin.text.startsWith

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var savebinding : DialogEnterllinkBinding
    lateinit var savesAdapter: SavesAdapter
    lateinit var db : Data
    lateinit var viewpgaerAdapter: ViewPagerAdapter
    val imagelist = mutableListOf<String>()
//    val postslist = mutableListOf<SavesData>()

   val savelist = mutableListOf<SavesData>()

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

    private var platformname: String = ""
    private var data = Metadata("", null, "")

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



        db= Data.getInstance(requireContext())

        savesAdapter = SavesAdapter(savelist, click = object : SavesAdapter.OnClik{
            override fun toggelfav(position: Int,img : ImageView) {
                Log.e("The position is ", "toggelfav: $position", )
                val item = savelist[position]
                // Step 1: Animate out (shrink + fade)
                img.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction {
                        // Step 2: Change the image
                        item.favoraite = !item.favoraite
                        img.setImageResource(
                            if (item.favoraite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
                        )

                        // Step 3: Animate in (grow + fade)
                        img.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(150)
                            .start()
                        db.inter().UpdateSave(item)
                        savesAdapter.notifyItemChanged(position)
                    }
                    .start()

            }

            override fun toggelmore(postion: Int) {
                val dailog = BottomSheetDialog(requireContext())
                dailog.setContentView(R.layout.bottom_extra_function)
                dailog.show()
                dailog.apply {
                    val item = savelist[postion]
                    val img = findViewById<ImageView>(R.id.SaveThumbnailsExtra)
                    val title = findViewById<TextView>(R.id.SaveTextExtra)

                    Glide.with(requireContext())
                        .load(item.images?.get(0))
                        .centerCrop()
                        .into(img!!)

                    title?.text = item.title

                }
            }

        })




        binding.MyPins?.setOnClickListener {
            binding.TestPin.visibility = View.VISIBLE
            binding.MyPins?.setBackgroundResource(R.drawable.selecetd_layout)
            binding.MyArchived?.setBackgroundResource(R.drawable.unselecetd_layout)



        }
        binding.MyArchived?.setOnClickListener {
            binding.TestPin.visibility = View.GONE
            binding.MyArchived?.setBackgroundResource(R.drawable.selecetd_layout)
            binding.MyPins?.setBackgroundResource(R.drawable.unselecetd_layout)
        }


        binding.AddSave.setOnClickListener {
            OpenUrlDialog()
        }




        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        binding.TestPin.layoutManager = if (isLandscape) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.TestPin.adapter = savesAdapter

        GetSaves()



    }

    private fun GetSaves() {
        savelist.clear()
        savelist.addAll(db.inter().GetSave())
        Log.e("The data is added ", "GetSaves: size = ${savelist.size} and data is\n$savelist  ", )

        savesAdapter.notifyDataSetChanged()

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


    fun GetItems(url: String, dialog: BottomSheetDialog) {
        val request = UrlRequest(url = url)

        // Show progress immediately when starting the request
        val saveDialog = createSaveDialog(url, dialog, showProgress = true)
        saveDialog.show()

        RetrofitClient.api.sendUrl(request).enqueue(object : Callback<test> {
            override fun onResponse(
                call: Call<test>,
                response: Response<test>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("Scraper", "Data is \n$data")
                    val item = KeyData(true, data)
                    updateSaveDialog(saveDialog, item, url)
                } else {
                    Log.e("Scraper", "Failed: ${response.errorBody()?.string()}")
                    val item = KeyData(false, null)
                    handleError(saveDialog, "Error: Please try again later")
                }
            }

            override fun onFailure(call: Call<test>, t: Throwable) {
                Log.e("Scraper", "Error: ${t.message}")
                val item = KeyData(false, null)
                handleError(saveDialog, "Error: Please try again later")
            }
        })
    }

    private fun HomeFragment.createSaveDialog(
        url: String,
        btmdialog: BottomSheetDialog,
        showProgress: Boolean = true
    ): Dialog {
        btmdialog.dismiss()

        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(savebinding.root)
            window?.setBackgroundDrawableResource(R.drawable.round_corner)

            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val dialogWidth = (screenWidth * 0.9).toInt()

            window?.setLayout(dialogWidth, ActionBar.LayoutParams.WRAP_CONTENT)
            window?.setDimAmount(.5f)
            setCancelable(false)
        }

        savebinding.apply {
            progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
            DataCard.visibility = if (showProgress) View.GONE else View.VISIBLE
        }

        return dialog
    }

    private fun HomeFragment.updateSaveDialog(
        dialog: Dialog,
        item: KeyData,
        url: String
    ) {
        savebinding.apply {
            if (item.isget) {
                val data = item.data
                Log.e("The data is ", "OpenSaveLink: $data")

                progressBar.visibility = View.GONE
                DataCard.visibility = View.VISIBLE

                SaveTitle.setText(data?.title)
                platformname = data?.platform ?: ""
                SaveSource.setText(url)
                imagelist.addAll((data?.images ?: emptyList()).reversed())

                Log.e("Images Size", "updateSaveDialog: ${imagelist.size}", )

                Log.e("Indiactor Images", "updateSaveDialog: is empty ${IndiactorImage.isEmpty()} ", )
                viewpgaerAdapter = ViewPagerAdapter(imagelist)
                SaveThumbnails.adapter = viewpgaerAdapter
                IndiactorImage.setViewPager(SaveThumbnails)



                if(imagelist.size == 1) {
                    Log.e("Images Size is less 1 ", "updateSaveDialog: ${imagelist.size}", )

                    IndiactorImage.visibility = View.GONE
                }



//                Glide.with(requireContext())
//                    .load(data?.images?.get(0))
//                    .centerCrop()
//                    .into()

                btnsave.setOnClickListener {

                   val tagss = mutableListOf(platformname)
                    tagss.addAll(splitByComma(SaveTags.text.toString()))

                    // Save logic here

                    val newItem = SavesData(
                        url = url,
                        title = data?.title,
                        thumbnail = data?.thumbnail,
                        timestamp = System.currentTimeMillis(),
                        tags = tagss,
                        category = "",
                        platform = platformname,
                        viewed = false,
                        favoraite = false,
                        notes = SaveNote.text.toString(),
                        archived = false,
                        account_name = data?.account_name,
                        images = data?.images,
                        type = data?.type
                    )

                    val is_valid = db.inter().InsertSave(newItem)
                    if(is_valid){
                        savelist.add(newItem)
                        savesAdapter.notifyItemInserted(savelist.size-1)
                        Toast.makeText(requireContext(), "Data is added", Toast.LENGTH_SHORT).show()
                        Log.e("Data is added ", "OpenSaveLink:  $data", )
                        dialog.dismiss()
                    }
                    else{
                        Toast.makeText(requireContext(), "This Pin is Already Exist", Toast.LENGTH_SHORT).show()
                    }





                }
            } else {
                handleError(dialog, "Request submission error")
            }
        }
    }

    private fun HomeFragment.handleError(dialog: Dialog, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    private fun HomeFragment.OpenUrlDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.bottom_dialog_url)
        dialog.show()

        dialog.apply {
            val url = findViewById<TextInputEditText>(R.id.EnterUrl)
            val findurl = findViewById<MaterialButton>(R.id.btnurlenter)

            findurl?.setOnClickListener {
                if (url?.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Enter the URL to get the pin",
                        Toast.LENGTH_SHORT).show()
                } else {
                    GetItems(url?.text.toString(), dialog)
                }
            }
        }
    }


    fun splitByComma(input: String): List<String> {
        return input.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }





    private fun SetLogo(text : TextView,string: String) {
        val match = platformlist.find {
            it.platformname.equals(string, ignoreCase = true) ||
                    string.contains(it.platformname, ignoreCase = true)
        }

        val drawable = match?.let {
            ContextCompat.getDrawable(requireContext(), it.platfomricon)
        } ?: null  // fallback

        drawable.let {
            text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            text.compoundDrawablePadding = 4 // optional: space between icon and text

        }

    }


    suspend fun fetchMetadata(url: String): Metadata {
        Log.e("Documents is here ", "fetchMetadata: ", )

        try {
            // Fetch the webpage HTML
            val doc: Document = Jsoup.connect(url).get()

            Log.e("Documents is ", "fetchMetadata: $doc", )

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


    fun getAppNameFromUrl(url: String): String {
        val uri = Uri.parse(url)
        val host = uri.host ?: return "Unknown"

        return when {
            "youtube.com" in host || "youtu.be" in host -> "YouTube"
            "instagram.com" in host -> "Instagram"
            "twitter.com" in host || "x.com" in host -> "Twitter"
            "facebook.com" in host -> "Facebook"
            "linkedin.com" in host -> "LinkedIn"
            "pinterest.com" in host || "pin.it" in host -> "Pinterest"
            "reddit.com" in host -> "Reddit"
            "spotify.com" in host -> "Spotify"
            "github.com" in host -> "GitHub"
            "amazon.com" in host  || "amzn.in" in host-> "Amazon"
            "flipkart.com" in host -> "Flipkart"
            "play.google.com" in host -> "Play Store"
            "steam.com" in host -> "Steam"
            "t.me" in host || "telegram.org" in host -> "Telegram"
            "tiktok.com" in host -> "TikTok"
            "snapchat.com" in host -> "Snapchat"
            else -> host.replace("www.", "").split(".")[0].capitalize()
        }
    }

    fun getSourceLink(url: String): String {
        return try {
            val uri = Uri.parse(url)
            val host = uri.host
            if (host != null) {
                if (!host.startsWith("www.")) {
                    "www.$host"
                } else {
                    host
                }
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Invalid URL"
        }
    }


    fun extractFirstUrl(text: String): String? {
        val regex = "(https?://\\S+)".toRegex() // matches until first space
        return regex.find(text)?.value
    }

}

