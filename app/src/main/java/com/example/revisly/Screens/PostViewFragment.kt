package com.example.revisly.Screens

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Dao
import com.bumptech.glide.Glide
import com.example.revisly.Adapters.ShowPostsAdapter
import com.example.revisly.Adapters.ViewPagerAdapter
import com.example.revisly.KeyData
import com.example.revisly.Metadata
import com.example.revisly.Platform
import com.example.revisly.Posts
import com.example.revisly.R
import com.example.revisly.Retrofit.RetrofitClient
import com.example.revisly.Retrofit.UrlRequest
import com.example.revisly.RoomDatabase.Data
import com.example.revisly.SavesData
import com.example.revisly.databinding.DialogEnterllinkBinding
import com.example.revisly.databinding.FragmentPostViewBinding
import com.example.revisly.test
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.text.startsWith


class PostViewFragment : Fragment() {
    lateinit var binding: FragmentPostViewBinding
    lateinit var postsAdapter: ShowPostsAdapter
    lateinit var savebinding : DialogEnterllinkBinding
    val postslist = mutableListOf<SavesData>()
    lateinit var viewpgaerAdapter: ViewPagerAdapter
    val imagelist = mutableListOf<String>()

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


    lateinit var db : Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostViewBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db= Data.getInstance(requireContext())

        postsAdapter = ShowPostsAdapter(postslist, object : ShowPostsAdapter.FullView{
            override fun openFullView(postPosition: Int, imagePosition: Int) {

                Log.e("come here", "openFullView: ", )
                // Get the images from the clicked post
                val images = reconstructUrls(imagelist)

                // Create a bundle with the data
                val bundle = Bundle().apply {
                    putStringArrayList("images", ArrayList(images))
                    putInt("postPosition", postPosition)
                    putInt("imagePosition", imagePosition)
                }

                // Navigate to FullscreenFragment
                findNavController().navigate(R.id.action_postViewFragment_to_fullViewFragment,bundle)
            }





        })


        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        layoutManager.isItemPrefetchEnabled = true

        binding.ShowImages.layoutManager = layoutManager
        binding.ShowImages.setHasFixedSize(true)
        binding.ShowImages.adapter = postsAdapter


        GetPost()

        binding.AddSave.setOnClickListener {
            OpenUrlDialog()
        }








        binding.chipGroup3?.setOnCheckedStateChangeListener { group, checkedIds ->
            Log.e("chip item before  ", "onViewCreated:  selected chop is $", )

            if (checkedIds.isNotEmpty()) {
                val selectedId = checkedIds[0] // Get first selected ID
                Log.e("chip item ", "onViewCreated:  selected chop is $selectedId", )

                when (selectedId) {
                    R.id.All -> {
                        Log.e("ChipSelection", "All selected")
                        GetPost()
                    }
                    R.id.Pinterest -> {
                        Log.e("ChipSelection", "Pinterest selected")
                        GetFilterSaves("pinterest") // Make sure this matches your database exactly
                    }
                    R.id.Instagram -> {
                        Log.e("ChipSelection", "Instagram selected")
                        GetFilterSaves("instagram")
                    }
                    R.id.Youtube -> {
                        Log.e("ChipSelection", "Youtube selected")
                        GetFilterSaves("youtube")
                    }
                    R.id.Fav ->{
                        getFavSaves()
                    }
                    R.id.Archived->{
                        getArcSaves()
                    }
                }
            }
        }
    }




private fun GetPost() {
        postslist.clear()
        postslist.addAll(db.inter().GetSavePost())
        postsAdapter.notifyDataSetChanged()
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

    private fun PostViewFragment.createSaveDialog(
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

    private fun PostViewFragment.updateSaveDialog(
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
                        postslist.add(newItem)
                        postsAdapter.notifyItemInserted(postslist.size-1)
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

    private fun PostViewFragment.handleError(dialog: Dialog, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    private fun PostViewFragment.OpenUrlDialog() {
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


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostViewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


private fun PostViewFragment.GetFilterSaves(string: String) {
    postslist.clear()
    postslist.addAll(db.inter().GetSaveFilter(string))

    postsAdapter.notifyDataSetChanged()

    }
fun reconstructUrls(rawItems: List<Any?>?): List<String> {
    if (rawItems == null) return emptyList()

    val rawString = rawItems.joinToString(",") { it.toString().trim() }
    val parts = rawString.split(",")
    val urls = mutableListOf<String>()
    var currentUrl = ""

    for (part in parts) {
        val trimmed = part.trim()
        if (trimmed.startsWith("https://")) {
            if (currentUrl.isNotEmpty()) {
                urls.add(currentUrl)
            }
            currentUrl = trimmed
        } else {
            currentUrl += ",$trimmed"
        }
    }

    if (currentUrl.isNotEmpty()) {
        urls.add(currentUrl)
    }

    return urls
}
}

private fun PostViewFragment.getArcSaves() {
    postslist.clear()
    postslist.addAll(db.inter().GetArcSave())
    postsAdapter.notifyDataSetChanged()
}

private fun PostViewFragment.getFavSaves() {
    postslist.clear()
    postslist.addAll(db.inter().GetFavSave())
    postsAdapter.notifyDataSetChanged()
}
