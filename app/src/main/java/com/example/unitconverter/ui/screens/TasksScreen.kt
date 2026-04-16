package com.example.unitconverter.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unitconverter.data.task_manager.TaskEntity
import com.example.unitconverter.ui.theme.TextMuted
import com.example.unitconverter.ui.view_models.TaskViewModel


@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel = viewModel(factory = TaskViewModel.Factory)
) {
    val tasksState by taskViewModel.tasksState.collectAsState()
    val currentEditText by taskViewModel.editUiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddTaskForm(
            text = tasksState.newTaskText,
            onTextChange = { taskViewModel.setNewTaskText(it) },
            onAdd = { taskViewModel.addTask() }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (tasksState.tasks.isEmpty()) {
                item { EmptyTasksPlaceholder() }
            } else {
                items(items = tasksState.tasks) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { taskViewModel.toggleTask(task.id, !task.isDone) },
                        onDelete = { taskViewModel.deleteTask(task.id) },
                        editText = currentEditText.editText,
                        onEditTextChange = { taskViewModel.setNewEditText(it) },
                        onEdit = { taskViewModel.editTask(task.id) },
                        viewModel = taskViewModel
                    )
                }
            }
        }
    }
}


@Composable
private fun AddTaskForm(
    text: String,
    onTextChange: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Add a task...", color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                onAdd()
            }),
            shape = RoundedCornerShape(8.dp)
        )

        Button(
            onClick = {
                keyboardController?.hide()
                onAdd()
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    editText: String,
    onEditTextChange: (String) -> Unit,
    onEdit: () -> Unit,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    var editing by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (task.isDone) 0.5f else 1f)
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() }
            )

            Text(
                text = task.text,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            )

            ExposedDropdownMenuBox(
                expanded = expanded, onExpandedChange = { expanded = it }
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .width(120.dp)
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        text = { Text("Edit") },
                        onClick = {
                            if (task.isDone) {
                                expanded = false
                                Toast.makeText(context, "Can't edit a completed task!", Toast.LENGTH_SHORT).show()
                            }else {
                                viewModel.updateCurrentEditTask(task)
                                expanded = false
                                editing = true
                            }
                        }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }

    if (editing) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                value = editText,
                onValueChange = onEditTextChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    editing = false
                    onEdit()
                }),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = {
                    editing = false
                    onEdit()
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Icon(imageVector = Icons.Default.Done, contentDescription = null) }
        }
    }
}


@Composable
private fun EmptyTasksPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No tasks yet — add one above!", color = TextMuted, fontSize = 14.sp)
    }
}