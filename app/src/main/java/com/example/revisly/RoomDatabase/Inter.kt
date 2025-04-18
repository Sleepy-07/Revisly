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
    fun InsertSave(data : SavesData)

    @Delete
    fun DeleteSave(data : SavesData)

    @Update
    fun UpdateSave(data : SavesData)

    @Query("Select * from SavesData ")
    fun GetSave() : List<SavesData>

    @Insert
    fun InsertPost(data : Posts)

    @Delete
    fun DeletePost(data : Posts)

    @Update
    fun UpdatePost(data : Posts)

    @Query("Select * from Posts ")
    fun GetPost() : List<Posts>


}