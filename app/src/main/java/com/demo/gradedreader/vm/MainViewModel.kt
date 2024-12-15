package com.demo.gradedreader.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demo.gradedreader.lessonroom.LessonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val lessonDao: LessonDao) : ViewModel() {
    private val _unitSize = MutableLiveData<Int>()
    val unitSize: LiveData<Int> get() = _unitSize

    init {
        fetchUnitSize()
    }

    private fun fetchUnitSize() {
        viewModelScope.launch {
            val size = withContext(Dispatchers.IO) {
                lessonDao.getUnitSize()
            }
            _unitSize.postValue(size)
        }
    }
}

class MainViewModelFactory(private val lessonDao: LessonDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(lessonDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
