package com.example.revisly

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.revisly.Adapters.ViewPagerAdapter
import com.example.revisly.Retrofit.RetrofitClient
import com.example.revisly.Retrofit.UrlRequest
import com.example.revisly.RoomDatabase.Data
import com.example.revisly.Screens.HomeFragment
import com.example.revisly.databinding.ActivityShareBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
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

class ShareActivity : AppCompatActivity() {
    lateinit var binding: ActivityShareBinding
    val imagelist = mutableListOf<String>()

    lateinit var db : Data



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
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        enableEdgeToEdge()
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db= Data.getInstance(this)

        val window = window

        val layoutParams:  WindowManager.LayoutParams = window.attributes
        layoutParams.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        window.attributes = layoutParams


        window.setGravity(Gravity.CENTER)


        window.setBackgroundDrawableResource(R.drawable.round_corner)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)



        var url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        url = extractFirstUrl(url).toString()

        if((url.contains("youtube.com") || url.contains("youtu.be")) && !url.contains("post")){

            // Show progress immediately when starting the request
            lifecycleScope.launch {


                binding.progressBar.visibility = View.VISIBLE
                binding.DataCard.visibility = View.GONE

                val metadata = fetchMetadata(url)
                // Safely update UI here

                Log.e("YotuebData", "OpenUrlDialog:  $metadata",)

                updateSaveDialog(metadata, url)

            }
        }else{
            GetItems(url)

        }
            platformname = "Youtube"







        binding.apply {

        btnsave.setOnClickListener {
                finish()
            }
        }



    }


    fun GetItems(url: String) {
        val request = UrlRequest(url = url)

        // Show progress immediately when starting the request
        binding.progressBar.visibility = View.VISIBLE
        binding.DataCard.visibility = View.GONE

        RetrofitClient.api.sendUrl(request).enqueue(object : Callback<test> {
            override fun onResponse(
                call: Call<test>,
                response: Response<test>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("Scraper", "Data is \n$data")
                    val item = KeyData(true, data)
                    updateSaveDialog( item, url)
                } else {
                    Log.e("Scraper", "Failed: ${response.errorBody()?.string()}")
                    val item = KeyData(false, null)
                    handleError( "Error: Please try again later")
                }
            }

            override fun onFailure(call: Call<test>, t: Throwable) {
                Log.e("Scraper", "Error: ${t.message}")
                val item = KeyData(false, null)
                handleError("Error: Please try again later")
            }
        })
    }

    private fun ShareActivity.updateSaveDialog(
        item: KeyData,
        url: String
    ) {
        binding.apply {
            if (item.isget) {
                val data = item.data
                Log.e("The data is ", "OpenSaveLink: $data")

                progressBar.visibility = View.GONE
                DataCard.visibility = View.VISIBLE

                SaveTitle.setText(data?.title)
                platformname = data?.platform ?: ""
                SaveSource.setText(url)
                imagelist.addAll((data?.images ?: emptyList()).reversed())

                Glide.with(this@ShareActivity)
                    .load(item.data?.images?.get(0))
                    .centerCrop()
                    .into(SaveThumbnails)

                Log.e("Images Size", "updateSaveDialog: ${imagelist.size}", )
                val tagss = mutableListOf(platformname)


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
                Toast.makeText(this@ShareActivity, "Data is added", Toast.LENGTH_SHORT).show()
                Log.e("Data is added ", "OpenSaveLink:  $data", )

                btnsave.setOnClickListener {


                    finish()

                }
            } else {
                handleError( "Request submission error")
            }
        }
    }

    private fun ShareActivity.handleError( message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
       finish()
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
            ContextCompat.getDrawable(this, it.platfomricon)
        } ?: null  // fallback

        drawable.let {
            text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            text.compoundDrawablePadding = 4 // optional: space between icon and text

        }

    }


    suspend fun fetchMetadata(url: String): KeyData = withContext(Dispatchers.IO) {
        try {
            val doc: Document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get()

            // Standard meta tags
            val title = doc.select("meta[property=og:title]").attr("content")
            val thumbnail = doc.select("meta[property=og:image]").attr("content")
            val type = doc.select("meta[property=og:type]").attr("content")

            // Special handling for YouTube
            var accountName = if (url.contains("youtube.com") || url.contains("youtu.be")) {
                // Method 1: Try to get from link element
                val channelLink = doc.select("link[itemprop=name]").attr("content")
                if (channelLink.isNotEmpty()) {
                    channelLink
                } else {
                    // Method 2: Try to get from meta tag
                    val authorMeta = doc.select("meta[itemprop=author]").attr("content")
                    if (authorMeta.isNotEmpty()) {
                        authorMeta
                    } else {
                        // Method 3: Try to get from channel name link
                        doc.select("a.yt-simple-endpoint.style-scope.yt-formatted-string")
                            .first()?.text() ?: "Unknown"
                    }
                }
            } else {
                // For non-YouTube sites, use standard author meta tag
                doc.select("meta[name=author]").attr("content").takeIf { it.isNotEmpty() } ?: "Unknown"
            }

            val fallbackTitle = if (title.isEmpty()) doc.title() else title
            val fallbackType = if (type.isEmpty()) "website" else type
            val item = KeyData(
                isget = true,
                data = test(
                    platform = getAppNameFromUrl(url),
                    account_name = accountName,
                    thumbnail = thumbnail,
                    title = fallbackTitle,
                    type = "video",
                    images = mutableListOf(thumbnail)
                ))


            KeyData(
                item.isget,
                item.data
            )
        } catch (e: IOException) {
            e.printStackTrace()
            KeyData(false, null)
        }
    }

    fun getAppNameFromUrl(url: String): String {
        val uri = Uri.parse(url)
        val host = uri.host ?: return "Unknown"

        return when {
            "youtube.com" in host || "youtu.be" in host -> "youtube"
            "instagram.com" in host -> "instagram"
            "twitter.com" in host || "x.com" in host -> "Twitter"
            "facebook.com" in host -> "facebook"
            "linkedin.com" in host -> "linkedin"
            "pinterest.com" in host || "pin.it" in host -> "pinterest"
            "reddit.com" in host -> "reddit"
            "spotify.com" in host -> "spotify"
            "github.com" in host -> "gitHub"
            "amazon.com" in host  || "amzn.in" in host-> "amazon"
            "flipkart.com" in host -> "flipkart"
            "play.google.com" in host -> "play store"
            "steam.com" in host -> "steam"
            "t.me" in host || "telegram.org" in host -> "telegram"
            "tiktok.com" in host -> "tiktok"
            "snapchat.com" in host -> "snapchat"
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