package com.example.unitconverter.data.task_manager

import android.content.Context

interface TaskContainer {
    val repository: TaskRepository
}



class AppTaskContainer(context: Context) : TaskContainer {
    override val repository: TaskRepository by lazy {
        AppTaskRepository(TasksDatabase.getDatabase(context).taskDao())
    }
}