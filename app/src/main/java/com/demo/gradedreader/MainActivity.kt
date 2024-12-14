package com.demo.gradedreader

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demo.gradedreader.lessonroom.LessonDatabase
import com.demo.gradedreader.wordroom.WordDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val wordDataBase = WordDatabase.getDatabase(this)
        val allWords = wordDataBase.wordDao().getAllWords()
        val lessonDataBase = LessonDatabase.getDatabase(this)
        val allLessons = lessonDataBase.lessonDao().getAllLessons()
        lifecycleScope.launch {
            allWords.collect { wordList ->
                wordList.forEach { word ->
                    Log.d("DHL", "Word: ${word.word}, Level: ${word.level}")

                }
            }

            allLessons.collect { lessonList ->
                lessonList.forEach { lesson ->
                    Log.d(
                        "DHL", """
                Lesson ID: ${lesson.id}
                Unit: ${lesson.unit}
                Lesson: ${lesson.lesson}
                englishTitle: ${lesson.englishTitle}
                chineseTitle: ${lesson.chineseTitle}
                Question: ${lesson.question}
                Content: ${lesson.content}
                Translation: ${lesson.translation}
                New Words: ${lesson.newWords}
            """.trimIndent()
                    )
                }
            }
        }
    }
}