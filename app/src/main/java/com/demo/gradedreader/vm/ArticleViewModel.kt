package com.demo.gradedreader.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.Utils
import com.demo.gradedreader.lessonroom.Lesson
import com.demo.gradedreader.lessonroom.LessonDao
import com.demo.gradedreader.lessonroom.LessonDatabase
import com.demo.gradedreader.wordroom.Word
import com.demo.gradedreader.wordroom.WordDao
import com.demo.gradedreader.wordroom.WordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleViewModel() : ViewModel() {
    private val lessonDao: LessonDao = LessonDatabase.getDatabase(Utils.getApp()).lessonDao()
    private val wordDao: WordDao =  WordDatabase.getDatabase(Utils.getApp()).wordDao()

    private val _articles = MutableLiveData<List<Lesson>>()
    val articles: LiveData<List<Lesson>> = _articles

    private val _highlightedWords = MutableLiveData<List<Word>>()
    val highlightedWords: LiveData<List<Word>> = _highlightedWords

    fun fetchArticles(unitId: Int) {
        viewModelScope.launch {
            _articles.postValue(lessonDao.getLessonsByUnit(unitId))
        }
    }

    fun fetchWordsByLevel(maxLevel: Int) {
        viewModelScope.launch {
            val words = withContext(Dispatchers.IO) {
                wordDao.getWordsByLevel(maxLevel)
            }
            _highlightedWords.postValue(words)
        }
    }
}