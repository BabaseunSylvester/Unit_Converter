package com.example.unitconverter.data.task_manager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: TasksDatabase? = null

        fun getDatabase(context: Context): TasksDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, TasksDatabase::class.java, "tasks-database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}