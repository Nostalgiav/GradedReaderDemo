package com.demo.gradedreader.lessonroom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_table")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // 主键，自增
    val unit: Int,             // 单元编号 1
    val lesson: Int,           // 课程编号 1
    val englishTitle: String,  // 英语标题
    val chineseTitle: String,  //中文标题
    val question: String,      // 听后问题 1
    val titleContent: String,//
    val content: String,       // 课程正文
    val translation: String,   // 正文翻译
    val signature: String,    //署名
    val newWords: String,      // 生词及解释（JSON 字符串）
)