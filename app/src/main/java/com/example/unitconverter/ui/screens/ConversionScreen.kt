package com.example.unitconverter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unitconverter.data.unit_converter.Unit
import com.example.unitconverter.data.unit_converter.areaUnits
import com.example.unitconverter.data.unit_converter.lengthUnits
import com.example.unitconverter.data.unit_converter.massUnits
import com.example.unitconverter.data.unit_converter.volumeUnits
import com.example.unitconverter.ui.view_models.ConversionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen(
    modifier: Modifier = Modifier,
    appViewModel: ConversionViewModel = viewModel()
) {
    val selectedQuantityState by appViewModel.selectedQuantityState.collectAsState()
    val options = listOf(massUnits, lengthUnits, areaUnits, volumeUnits)
    val labels = listOf("Mass", "Length", "Area", "Volume")

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            options.forEachIndexed { index, units ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { appViewModel.updateSelectedQuantityState(units) },
                    selected = selectedQuantityState.selected == units
                ) {
                    Text(labels[index])
                }
            }
        }

        ConversionBox(
            units = selectedQuantityState.selected,
            appViewModel = appViewModel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun ConversionBox(
    units: List<Unit>,
    modifier: Modifier = Modifier,
    appViewModel: ConversionViewModel
) {
    val fromValueState by appViewModel.conversionFromState.collectAsState()
    val toValueState by appViewModel.conversionToState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("from")

            UnitsDropDownMenu(
                units = units,
                onUnitSelected = { appViewModel.onFromUnitSelected(it) },
                currentUnit = fromValueState.unit
            )
        }

        OutlinedTextField(
            value = fromValueState.value,
            onValueChange = {
                appViewModel.onFromValueChange(it)
            },
            suffix = { Text(fromValueState.unit.symbol) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("to")

            UnitsDropDownMenu(
                units = units,
                onUnitSelected = { appViewModel.onToUnitSelected(it) },
                currentUnit = toValueState.unit
            )
        }

        OutlinedTextField(
            value = toValueState.value,
            onValueChange = { appViewModel.onToValueChange(it) },
            suffix = { Text(toValueState.unit.symbol) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitsDropDownMenu(
    units: List<Unit>,
    onUnitSelected: (Unit) -> kotlin.Unit,
    currentUnit: Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .width(200.dp)
        ) {
            Text(currentUnit.name)

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEachIndexed { index, unit ->
                DropdownMenuItem(
                    text = { Text(unit.name) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }

}
