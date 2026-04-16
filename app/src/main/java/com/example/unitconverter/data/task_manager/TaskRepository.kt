package com.example.unitconverter.data.task_manager

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(text: String)
    suspend fun deleteTask(id: Int)
    suspend fun toggleTaskDone(id: Int, isDone: Boolean)
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun editTask(id: Int, text: String)
}


class AppTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun addTask(text: String) {
        taskDao.addTask(text)
    }

    override suspend fun deleteTask(id: Int) {
        taskDao.deleteTask(id)
    }

    override suspend fun toggleTaskDone(id: Int, isDone: Boolean) {
        taskDao.toggleTaskDone(id, isDone)
    }

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    override suspend fun editTask(id: Int, text: String) {
        taskDao.editTask(id, text)
    }
}