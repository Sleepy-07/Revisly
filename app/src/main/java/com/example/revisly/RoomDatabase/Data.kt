package com.example.revisly.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.revisly.Posts
import com.example.revisly.SavesData

@Database(entities = [SavesData::class,
                        Posts::class], version=1 , exportSchema = true)
@TypeConverters(Converters::class)
abstract class Data() : RoomDatabase() {
    abstract fun inter() : Inter

    companion object{
        private var data : Data? = null

        fun getInstance(context: Context):Data {
            if(data == null){
                data = Room.databaseBuilder(context,
                    Data::class.java,
                    "Data_BAse")
                    .allowMainThreadQueries()
                    .build()

            }
            return data!!
        }
    }

}