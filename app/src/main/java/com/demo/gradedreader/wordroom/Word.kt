package com.demo.gradedreader.wordroom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 自动生成主键
    val word: String,
    val level: Int
)
