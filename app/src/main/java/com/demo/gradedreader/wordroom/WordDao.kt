package com.demo.gradedreader.wordroom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    // 插入单词
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    // 获取所有单词，按等级排序
    @Query("SELECT * FROM word_table ORDER BY level ASC")
    fun getAllWords(): Flow<List<Word>>

    // 根据等级获取单词
    @Query("SELECT * FROM word_table WHERE level = :level")
    fun getWordsByLevel(level: Int): Flow<List<Word>>

    // 删除所有单词
    @Query("DELETE FROM word_table")
    suspend fun deleteAll()
}