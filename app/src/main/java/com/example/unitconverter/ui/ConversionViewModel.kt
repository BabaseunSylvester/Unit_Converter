package com.example.unitconverter.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.unitconverter.data.Unit as ConversionUnit
import com.example.unitconverter.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConversionViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _conversionFromState = MutableStateFlow(
        ConversionFromState(
            unit = findUnitByName(savedStateHandle["fromUnitName"] ?: massUnits[0].name),
            value = savedStateHandle["fromValue"] ?: ""
        )
    )
    val conversionFromState = _conversionFromState.asStateFlow()

    private val _conversionToState = MutableStateFlow(
        ConversionToState(
            unit = findUnitByName(savedStateHandle["toUnitName"] ?: massUnits[1].name),
            value = savedStateHandle["toValue"] ?: ""
        )
    )
    val conversionToState = _conversionToState.asStateFlow()

    private val _selectedQuantityState = MutableStateFlow(
        SelectedQuantityState(
            selected = findCategoryForUnit(_conversionFromState.value.unit.name)
        )
    )
    val selectedQuantityState = _selectedQuantityState.asStateFlow()

    private fun findUnitByName(name: String): ConversionUnit {
        val allUnits = massUnits + lengthUnits + areaUnits + volumeUnits
        return allUnits.find { it.name == name } ?: massUnits[0]
    }

    private fun findCategoryForUnit(unitName: String): List<ConversionUnit> {
        return when {
            massUnits.any { it.name == unitName } -> massUnits
            lengthUnits.any { it.name == unitName } -> lengthUnits
            areaUnits.any { it.name == unitName } -> areaUnits
            volumeUnits.any { it.name == unitName } -> volumeUnits
            else -> massUnits
        }
    }

    fun onFromUnitSelected(unit: ConversionUnit) {
        _conversionFromState.update { it.copy(unit = unit) }
        savedStateHandle["fromUnitName"] = unit.name
        performConversion()
    }

    fun onFromValueChange(fromValue: String) {
        _conversionFromState.update { it.copy(value = fromValue) }
        savedStateHandle["fromValue"] = fromValue
        performConversion()
    }

    fun onToUnitSelected(unit: ConversionUnit) {
        _conversionToState.update { it.copy(unit = unit) }
        savedStateHandle["toUnitName"] = unit.name
        performConversion()
    }

    fun onToValueChange(toValue: String) {
        _conversionToState.update { it.copy(value = toValue) }
        savedStateHandle["toValue"] = toValue
    }

    fun updateSelectedQuantityState(selected: List<ConversionUnit>) {
        _selectedQuantityState.update { it.copy(selected = selected) }
        // When switching categories, this resets units to defaults from the new category
        if (selected.isNotEmpty()) {
            onFromUnitSelected(selected[0])
            onToUnitSelected(if (selected.size > 1) selected[1] else selected[0])
        }
    }

    private fun performConversion() {
        val fromValue = _conversionFromState.value.value
        val fromUnit = _conversionFromState.value.unit
        val toUnit = _conversionToState.value.unit

        if (fromValue.isBlank()) {
            updateToValue("")
            return
        }

        val input = fromValue.toDoubleOrNull() ?: return

        val isMass = massUnits.any { it.name == fromUnit.name }
        val isLength = lengthUnits.any { it.name == fromUnit.name }
        val isArea = areaUnits.any { it.name == fromUnit.name }

        val output = when {
            isMass -> massConvert(input, fromUnit.name, toUnit.name)
            isLength -> lengthConvert(input, fromUnit.name, toUnit.name)
            isArea -> areaConvert(input, fromUnit.name, toUnit.name)
            else -> volumeConvert(input, fromUnit.name, toUnit.name)
        }

        updateToValue(output.toString())
    }

    private fun updateToValue(value: String) {
        _conversionToState.update { it.copy(value = value) }
        savedStateHandle["toValue"] = value
    }

    private fun massConvert(input: Double, from: String, to: String): Double {
        val toGramsFactor: Double = when (from) {
            "Tons" -> tonsGramsCF
            "UK Tons" -> UKTonsGramsCF
            "US Tons" -> USTonsGramsCF
            "Pounds" -> poundsGramsCF
            "Ounces" -> ouncesGramsCF
            "Kilograms" -> kilogramsGramsCF
            "Grams" -> gramsGramsCF
            else -> 1.0
        }
        val grams = input * toGramsFactor
        val toTargetFactor: Double = when (to) {
            "Tons" -> 1.0 / tonsGramsCF
            "UK Tons" -> 1.0 / UKTonsGramsCF
            "US Tons" -> 1.0 / USTonsGramsCF
            "Pounds" -> 1.0 / poundsGramsCF
            "Ounces" -> 1.0 / ouncesGramsCF
            "Kilograms" -> 1.0 / kilogramsGramsCF
            "Grams" -> 1.0 / gramsGramsCF
            else -> 1.0
        }
        return grams * toTargetFactor
    }

    private fun lengthConvert(input: Double, from: String, to: String): Double {
        val toMetresFactor: Double = when (from) {
            "Millimetres" -> millimetresMetresCF
            "Centimetres" -> centimetresMetresCF
            "Metres" -> metresMetresCF
            "Kilometres" -> kilometresMetresCF
            "Inches" -> inchesMetresCF
            "Feet" -> feetMetresCF
            "Yards" -> yardsMetresCF
            "Miles" -> milesMetresCF
            "Nautical Miles" -> nauticalMilesMetresCF
            "Mils" -> milsMetresCF
            else -> 1.0
        }
        val metres = input * toMetresFactor
        val toTargetFactor: Double = when (to) {
            "Millimetres" -> 1.0 / millimetresMetresCF
            "Centimetres" -> 1.0 / centimetresMetresCF
            "Metres" -> 1.0 / metresMetresCF
            "Kilometres" -> 1.0 / kilometresMetresCF
            "Inches" -> 1.0 / inchesMetresCF
            "Feet" -> 1.0 / feetMetresCF
            "Yards" -> 1.0 / yardsMetresCF
            "Miles" -> 1.0 / milesMetresCF
            "Nautical Miles" -> 1.0 / nauticalMilesMetresCF
            "Mils" -> 1.0 / milsMetresCF
            else -> 1.0
        }
        return metres * toTargetFactor
    }

    private fun areaConvert(input: Double, from: String, to: String): Double {
        val toSquareMetresFactor: Double = when (from) {
            "Acres" -> acresSquareMetresCF
            "Ares" -> aresSquareMetresCF
            "Hectares" -> hectaresSquareMetresCF
            "Square Centimetres" -> squareCentimetresSquareMetresCF
            "Square Feet" -> squareFeetSquareMetresCF
            "Square Inches" -> squareInchesSquareMetresCF
            "Square Metres" -> squareMetresSquareMetresCF
            else -> 1.0
        }
        val squareMetres = input * toSquareMetresFactor
        val toTargetFactor: Double = when (to) {
            "Acres" -> 1.0 / acresSquareMetresCF
            "Ares" -> 1.0 / aresSquareMetresCF
            "Hectares" -> 1.0 / hectaresSquareMetresCF
            "Square Centimetres" -> 1.0 / squareCentimetresSquareMetresCF
            "Square Feet" -> 1.0 / squareFeetSquareMetresCF
            "Square Inches" -> 1.0 / squareInchesSquareMetresCF
            "Square Metres" -> 1.0/ squareMetresSquareMetresCF
            else -> 1.0
        }
        return squareMetres * toTargetFactor
    }

    private fun volumeConvert(input: Double, from: String, to: String): Double {
        val toCubicMetresFactor: Double = when (from) {
            "UK Gallons" -> UKGallonsCubicMetresCF
            "US Gallons" -> USGallonsCubicMetresCF
            "Litres" -> litresCubicMetresCF
            "Millilitres" -> millilitresCubicMetresCF
            "Cubic Centimetres" -> cubicCentimetresCubicMetresCF
            "Cubic Metres" -> cubicMetresCubicMetresCF
            "Cubic Inches" -> cubicInchesCubicMetresCF
            "Cubic Feet" -> cubicFeetCubicMetresCF
            else -> 1.0
        }
        val cubicMetres = input * toCubicMetresFactor
        val toTargetFactor: Double = when (to) {
            "UK Gallons" -> 1.0 / UKGallonsCubicMetresCF
            "US Gallons" -> 1.0 / USGallonsCubicMetresCF
            "Litres" -> 1.0 / litresCubicMetresCF
            "Millilitres" -> 1.0 / millilitresCubicMetresCF
            "Cubic Centimetres" -> 1.0 / cubicCentimetresCubicMetresCF
            "Cubic Metres" -> 1.0 / cubicMetresCubicMetresCF
            "Cubic Inches" -> 1.0 / cubicInchesCubicMetresCF
            "Cubic Feet" -> 1.0/cubicFeetCubicMetresCF
            else -> 1.0
        }
        return cubicMetres * toTargetFactor
    }
}

data class ConversionFromState(val unit: ConversionUnit = massUnits[0], val value: String = "")
data class ConversionToState(val unit: ConversionUnit = massUnits[1], val value: String  = "")
data class SelectedQuantityState(val selected: List<ConversionUnit> = massUnits)
