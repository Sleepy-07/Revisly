package com.example.revisly

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.revisly.Retrofit.ScrapeResponse
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

data class Platform(
    val platformname  : String,
    val platfomricon : Int
)
data class Metadata(
    val title: String?,
    val thumbnail: String?,
    val type: String?
)

@Parcelize
@Entity
data class SavesData(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val url : String,
    val title : String? = "",
    val thumbnail: String?,
    val timestamp: Long,
    val tags : MutableList<String>?,
    val category : String? = "",
    val platform : String,
    val account_name: String?,
    val images : MutableList<String>?,
    val viewed : Boolean = false,
    var favoraite : Boolean = false,
    val notes : String? = "",
    var archived : Boolean = false,
    val type : String? = "",
    ) : Parcelable

@Entity
data class Posts(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val img : String
)


data class test(
    val platform : String?,
    val account_name : String?,
    val thumbnail: String?,
    val title : String?,
    val type : String?,
    val images : MutableList<String>
    )

data class KeyData(
    val isget : Boolean,
    val data : test?
)


@Parcelize
data class FlattenedImage(
    val imageUrl: String,
    val parentItem: SavesData,
    val imageIndex: Int,
    val itemIndex: Int
) : Parcelable
