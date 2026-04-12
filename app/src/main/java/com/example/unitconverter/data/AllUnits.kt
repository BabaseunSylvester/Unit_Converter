package com.example.unitconverter.data

data class Unit(
    val name: String,
    val symbol: String
)

val massUnits: List<Unit> = listOf(
    Unit("Tons", "t"),
    Unit("UK Tons", "t"),
    Unit("US Tons", "t"),
    Unit("Pounds", "lb"),
    Unit("Ounces", "oz"),
    Unit("Kilograms", "kg"),
    Unit("Grams", "g")
)

val lengthUnits = listOf(
    Unit("Millimetres", "mm"),
    Unit("Centimetres", "cm"),
    Unit("Metres", "m"),
    Unit("Kilometres", "km"),
    Unit("Inches", "in"),
    Unit("Feet", "ft"),
    Unit("Yards", "yd"),
    Unit("Miles", "mi"),
    Unit("Nautical Miles", "NM"),
    Unit("Mils", "mil"),
)

val areaUnits = listOf(
    Unit("Acres", "ac"),
    Unit("Ares", "a"),
    Unit("Hectares", "ha"),
    Unit("Square Centimetres", "cm²"),
    Unit("Square Feet", "ft²"),
    Unit("Square Inches", "in²"),
    Unit("Square Metres", "m²")
)

val volumeUnits = listOf(
    Unit("UK Gallons", "gal"),
    Unit("US Gallons", "gal"),
    Unit("Litres", "L"),
    Unit("Millilitres", "mL"),
    Unit("Cubic Centimetres", "cm³"),
    Unit("Cubic Metres", "m³"),
    Unit("Cubic Inches", "in³"),
    Unit("Cubic Feet", "ft³")
)
