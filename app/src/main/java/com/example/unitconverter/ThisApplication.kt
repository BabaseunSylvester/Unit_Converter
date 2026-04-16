package com.example.unitconverter

import android.app.Application
import com.example.unitconverter.data.task_manager.AppTaskContainer
import com.example.unitconverter.data.task_manager.TaskContainer

class ThisApplication() : Application() {
    lateinit var container: TaskContainer

    override fun onCreate() {
        super.onCreate()
        container = AppTaskContainer(this)
    }
}