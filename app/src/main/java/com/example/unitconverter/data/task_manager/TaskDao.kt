package com.example.unitconverter.data.task_manager

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("INSERT INTO tasks (text, isDone) VALUES (:text, 0)")
    suspend fun addTask(text: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Int)

    @Query("UPDATE tasks SET isDone = :isDone WHERE id = :id")
    suspend fun toggleTaskDone(id: Int, isDone: Boolean)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET text = :text WHERE id = :id")
    suspend fun editTask(id: Int, text: String)
}