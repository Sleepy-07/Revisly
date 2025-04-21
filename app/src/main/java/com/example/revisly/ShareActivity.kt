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


        GetItems(url)





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