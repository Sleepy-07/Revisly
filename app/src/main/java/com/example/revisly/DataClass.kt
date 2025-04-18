package com.example.revisly

import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity
data class SavesData(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val url : String,
    val title : String? = "",
    val thumbnail: String?,
    val timestamp: Long,
    val tags : List<String> ? = emptyList(),
    val category : String? = "",
    val platform : String,
    val viewed : Boolean = false,
    var favoraite : Boolean = false,
    val notes : String? = "",
    val archived : Boolean = false,

    )

@Entity
data class Posts(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val img : String
)