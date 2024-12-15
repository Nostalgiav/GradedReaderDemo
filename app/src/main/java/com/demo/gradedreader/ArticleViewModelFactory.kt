package com.demo.gradedreader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.gradedreader.lessonroom.LessonDao
import com.demo.gradedreader.vm.ArticleViewModel

class ArticleViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}