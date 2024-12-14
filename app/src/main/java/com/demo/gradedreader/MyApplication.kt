package com.demo.gradedreader

import android.app.Application
import android.content.Context
import android.util.Log
import com.demo.gradedreader.lessonroom.Lesson
import com.demo.gradedreader.lessonroom.LessonDatabase
import com.demo.gradedreader.wordroom.Word
import com.demo.gradedreader.wordroom.WordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MyApplication : Application() {

    private lateinit var mApplicationContext: Context


    override fun onCreate() {
        super.onCreate()
        mApplicationContext = applicationContext
        initWordAndLessonRoom()

    }

    private fun initWordAndLessonRoom() {
        val sharedPreferences =
            mApplicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val isWordInitialized = sharedPreferences.getBoolean("is_word_db_initialized", false)
        val isLessonInitialized = sharedPreferences.getBoolean("is_lesson_db_initialized", false)

        // 初始化数据库
        val wordDatabase = WordDatabase.getDatabase(mApplicationContext)
        val lessonDatabase = LessonDatabase.getDatabase(mApplicationContext)

        // 检查并初始化未完成的数据库
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!isWordInitialized) {
                    //初始化 nce4_words 数据库
                    populateWordDatabase(mApplicationContext, wordDatabase, "nce4_words")
                    sharedPreferences.edit().putBoolean("is_word_db_initialized", true).apply()
                    Log.d("InitRoom", "Word database initialized.")
                } else {
                    Log.d("InitRoom", "Word database is already initialized.")
                }

                if (!isLessonInitialized) {
                    //初始化 Lesson 数据库

                    populateLessonDatabase(
                        mApplicationContext,
                        lessonDatabase,
                        "新概念英语第4册.txt"
                    )
                    sharedPreferences.edit().putBoolean("is_lesson_db_initialized", true).apply()
                    Log.d("InitRoom", "Lesson database initialized.")
                } else {
                    Log.d("InitRoom", "Lesson database is already initialized.")
                }
            } catch (e: Exception) {
                Log.e("InitRoom", "Error initializing databases: ${e.message}", e)
            }
        }

    }


    private suspend fun populateWordDatabase(
        context: Context,
        database: WordDatabase,
        fileName: String
    ) {
        val wordList = context.assets.open(fileName).bufferedReader().useLines { lines ->
            lines.mapNotNull { line ->
                val parts = line.split("\t")
                if (parts.size == 2) {
                    val word = parts[0].trim()
                    val level = parts[1].trim().toIntOrNull() ?: 0
                    Word(word = word, level = level)
                } else null
            }.toList()
        }
        wordList.forEach { database.wordDao().insert(it) }
    }

    private suspend fun populateLessonDatabase(
        context: Context,
        database: LessonDatabase,
        fileName: String
    ) {
        val lessonDao = database.lessonDao()
        val lines = context.assets.open(fileName).bufferedReader().readLines()

        var unit = 0
        var lesson = 0
        var englishTitle = ""
        var chineseTitle = ""
        var question = ""
        var titleContent = ""
        val content = StringBuilder()
        val translation = StringBuilder()
        var signature = ""
        val newWordsMap = mutableMapOf<String, MutableList<String>>()
        var inNewWords = false
        var inTranslation = false
        var currentWord: String? = null

        suspend fun saveLesson() {
            if (lesson != 0) {  //保存数据
                lessonDao.insertLesson(
                    Lesson(
                        unit = unit,
                        lesson = lesson,
                        englishTitle = englishTitle,
                        chineseTitle = chineseTitle,
                        question = question,
                        titleContent = titleContent,
                        content = content.toString().trim(),
                        translation = translation.toString().trim(),
                        signature = signature,
                        newWords = JSONObject(newWordsMap.mapValues { it.value.joinToString(". ") }).toString(),
                    )
                )
            }
        }

        for (line in lines.map { it.trim() }) {
            if (line.isEmpty()) continue // 跳过空行

            when {
                line.startsWith("Unit") -> {
                    unit = line.split(" ")[1].toInt()
                }

                line.startsWith("Lesson") -> { //匹配Lesson 字段
                    saveLesson()
                    lesson = line.split(" ")[1].toInt()
                    englishTitle = ""
                    chineseTitle = ""
                    question = ""
                    titleContent = ""
                    content.clear()
                    translation.clear()
                    signature = ""
                    newWordsMap.clear()
                    inNewWords = false
                    inTranslation = false
                    currentWord = null
                }
                //匹配 englishQuestion  chineseQuestion
                line.startsWith("First listen and then answer the following question.") -> {
                    val englishQuestion = line.substringBefore("question.").trim()
                    val chineseQuestion = lines.getOrNull(lines.indexOf(line) + 1)?.trim().orEmpty()
                    question = listOf(englishQuestion, chineseQuestion).filter { it.isNotEmpty() }
                        .joinToString("\n")
                }

                line.startsWith("New words and expressions") -> {
                    inNewWords = true
                    inTranslation = false
                }

                line.startsWith("参考译文") -> {
                    inNewWords = false
                    inTranslation = true
                }
                //匹配词典 newWordsMap
                inNewWords -> {
                    if (line.matches(Regex("[a-zA-Z ()]+"))) {
                        currentWord = line
                    } else if (currentWord != null) {
                        newWordsMap.computeIfAbsent(currentWord) { mutableListOf() }.add(line)
                        currentWord = null
                    }
                }

                inTranslation -> {
                    translation.appendLine(line)
                }

                else -> {
                    when {
                        //匹配englishTitle  chineseTitle titleContent
                        englishTitle.isEmpty() && line.contains(Regex("[a-zA-Z]")) -> englishTitle =
                            line

                        chineseTitle.isEmpty() && line.contains(Regex("[\\u4e00-\\u9fa5]")) -> chineseTitle =
                            line

                        titleContent.isEmpty() && line.contains("?") -> titleContent = line
                        else -> {
                            if (!line.startsWith("听录音，然后回答以下问题。")) {
                                // 匹配 content

                                content.appendLine(line)
                                // 匹配signature
                                signature =
                                    content.lines().lastOrNull { it.contains(Regex("[a-zA-Z]")) }
                                        ?.trim() ?: ""
                            }
                        }
                    }
                }
            }
        }

        saveLesson() // 保存最后一节课数据
    }

}