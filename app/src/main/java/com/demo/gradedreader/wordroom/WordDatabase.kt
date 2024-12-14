package com.demo.gradedreader.wordroom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile private var instance: WordDatabase? = null

        fun getDatabase(context: Context): WordDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "words_database"
                ).build().also { instance = it }
            }
        }
    }
}