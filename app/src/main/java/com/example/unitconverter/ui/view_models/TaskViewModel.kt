package com.example.unitconverter.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.unitconverter.ThisApplication
import com.example.unitconverter.data.task_manager.TaskEntity
import com.example.unitconverter.data.task_manager.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _tasksLocalState = MutableStateFlow(TasksUiState())
    val tasksState = combine(
        flow = _tasksLocalState,
        flow2 = taskRepository.getAllTasks().distinctUntilChanged()
    ) { local, dbTasks ->
        local.copy(tasks = dbTasks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TasksUiState()
    )

    private val _editUiState = MutableStateFlow(CurrentEditUiState())
    val editUiState = _editUiState.asStateFlow()

    fun toggleTask(taskId: Int, isDone: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskDone(taskId, isDone)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }


    fun updateCurrentEditTask(task: TaskEntity) {
        _editUiState.update {
            it.copy(task = task, editText = task.text)
        }
    }
    fun setNewEditText(text: String) {
        _editUiState.update {
            it.copy(editText = text)
        }
    }
    fun editTask(taskId: Int) {
        val trimmedText = _editUiState.value.editText.trim()
        if (trimmedText.isBlank()) return

        viewModelScope.launch {
            taskRepository.editTask(taskId, trimmedText)
        }

        _editUiState.update { it.copy(editText = "") }
    }

    fun addTask() {
        val trimmedText = _tasksLocalState.value.newTaskText.trim()
        if (trimmedText.isBlank()) return

        viewModelScope.launch {
            taskRepository.addTask(trimmedText)
        }

        _tasksLocalState.update { it.copy(newTaskText = "") }
    }

    fun setNewTaskText(text: String) {
        _tasksLocalState.update {
            it.copy(newTaskText = text)
        }
    }





    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as ThisApplication
                TaskViewModel(application.container.repository)
            }
        }
    }
}



data class TasksUiState(
    val tasks: List<TaskEntity> = listOf(),
    val newTaskText: String = ""
)

data class CurrentEditUiState(
    val task: TaskEntity = TaskEntity(0, "", false),
    val editText: String = ""
)