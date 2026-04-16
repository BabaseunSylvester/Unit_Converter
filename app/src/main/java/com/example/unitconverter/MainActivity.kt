package com.example.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unitconverter.ui.screens.ConversionScreen
import com.example.unitconverter.ui.screens.TasksScreen
import com.example.unitconverter.ui.theme.UnitConverterTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitConverterTheme {
                var selectedItem by rememberSaveable { mutableIntStateOf(0) }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = if (selectedItem == 0) "Unit Converter" else "Task Manager",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            expandedHeight = 40.dp,
                            modifier = Modifier.shadow(elevation = 8.dp)
                        )
                    },
                    bottomBar = {
                        BottomNavComposable(
                            onClick = { selectedItem = it }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (selectedItem == 0)
                        ConversionScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    else
                        TasksScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                }
            }
        }
    }
}




@Composable
fun BottomNavComposable(
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Converter", "Tasks")
    val icons = listOf(Icons.Default.Refresh, Icons.Default.List)
    //val unselectedIcons = listOf(R.drawable.home_svgrepo_com, R.drawable.list_svgrepo_com)

    NavigationBar(
        modifier = modifier.graphicsLayer(shadowElevation = 100f)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onClick(selectedItem)
                }
            )
        }
    }
}

