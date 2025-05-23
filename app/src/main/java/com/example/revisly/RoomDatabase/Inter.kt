package com.example.revisly.RoomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.revisly.Posts
import com.example.revisly.SavesData

@Dao
interface Inter {

    @Insert
    fun InsertSa(data : SavesData)

    fun InsertSave(data: SavesData) : Boolean{
        val valid = is_ValidUrl(data.images.toString())
        if(!valid){
            InsertSa(data)
            return true
        }
        return false
    }


    @Delete
    fun DeleteSave(data : SavesData)

    @Update
    fun UpdateSave(data : SavesData)

    @Query("Select * from SavesData ")
    fun GetSave() : List<SavesData>

    @Query("Select * from SavesData where url == :id ")
    fun is_Valid(id: String) : Boolean

    @Query("Select * from SavesData where images == :id ")
    fun is_ValidUrl(id: String) : Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM SavesData WHERE images IN (:ids))")
    fun isValidUrl(ids: MutableList<String>): Boolean

    @Query("Select * from SavesData where type in ('Post','Pin','carousel')" )
    fun GetSavePost() : List<SavesData>

    @Query("Select * from SavesData where platform = :filter and type in ('Post','Pin','carousel','image')" )
    fun GetSaveFilter(filter : String) : List<SavesData>

    @Query("Select * from SavesData where platform = :filter" )
    fun GetSaveFilterInsta(filter : String) : List<SavesData>
    @Query("Select * from SavesData where favoraite == true " )
    fun GetFavSave() : List<SavesData>

    @Query("""
    SELECT * FROM SavesData
    WHERE (:isfav = 0 OR favoraite = 1)
    AND (
        :contentType = 'all' 
        OR (
            :contentType = 'image' AND type IN ('Post', 'Pin', 'carousel', 'image')
        ) 
        OR (
            :contentType = 'video' AND type IN ('Reel', 'video', 'short', "video.other")
        )
    )
    AND (:source = 'all' OR platform = :source)
""")
    fun getFilteredSaves(
        contentType: String,
        source: String,
        isfav: Boolean
    ): List<SavesData>








    @Query("Select * from SavesData where archived == true " )
    fun GetArcSave() : List<SavesData>

    @Insert
    fun InsertPost(data : Posts)

    @Delete
    fun DeletePost(data : Posts)

    @Update
    fun UpdatePost(data : Posts)

    @Query("Select * from Posts ")
    fun GetPost() : List<Posts>


}