package com.demo.gradedreader.lessonroom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Insert
    suspend fun insertLesson(lesson: Lesson)

    @Query("SELECT * FROM lesson_table WHERE unit = :unitNumber ORDER BY lesson ASC")
    suspend fun getLessonsByUnit(unitNumber: Int): List<Lesson>

    @Query("SELECT * FROM lesson_table WHERE id = :id")
    suspend fun getLessonById(id: Int): Lesson

    @Query("SELECT * FROM lesson_table")
    fun getAllLessons(): Flow<List<Lesson>>

    @Query("SELECT COUNT(DISTINCT unit) FROM lesson_table")
    fun getUnitSize():Int

    @Query("SELECT DISTINCT unit FROM lesson_table")
    fun getAllUnit():List<Int>
}