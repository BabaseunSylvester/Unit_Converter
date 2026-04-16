package com.example.unitconverter.data.task_manager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val text: String,
    val isDone: Boolean
)