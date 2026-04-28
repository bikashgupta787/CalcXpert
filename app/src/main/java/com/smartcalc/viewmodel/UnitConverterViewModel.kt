package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.roundToLong

// ── Categories ────────────────────────────────────────────────────────────────
enum class UnitCategory(val label: String, val icon: String) {
    LENGTH("Length", "📏"),
    WEIGHT("Weight", "⚖️"),
    VOLUME("Volume", "🧪"),
    AREA("Area", "⬛"),
    SPEED("Speed", "💨"),
    DATA("Data Storage", "💾"),
    PRESSURE("Pressure", "🌡️"),
    ENERGY("Energy", "⚡"),
    TIME("Time", "⏱️"),
}

// ── Unit definition ───────────────────────────────────────────────────────────
data class UnitDef(
    val name: String,
    val symbol: String,
    val toBase: Double,      // multiply value by this to get base unit
    val fromBase: Double = 1.0 / toBase  // override for non-linear if needed
)

// ── Unit registry ─────────────────────────────────────────────────────────────
object UnitRegistry {
    val units: Map<UnitCategory, List<UnitDef>> = mapOf(

        UnitCategory.LENGTH to listOf(
            UnitDef("Kilometre",   "km",   1000.0),
            UnitDef("Metre",       "m",    1.0),
            UnitDef("Centimetre",  "cm",   0.01),
            UnitDef("Millimetre",  "mm",   0.001),
            UnitDef("Micrometre",  "µm",   1e-6),
            UnitDef("Mile",        "mi",   1609.344),
            UnitDef("Yard",        "yd",   0.9144),
            UnitDef("Foot",        "ft",   0.3048),
            UnitDef("Inch",        "in",   0.0254),
            UnitDef("Nautical Mi", "nmi",  1852.0),
        ),

        UnitCategory.WEIGHT to listOf(
            UnitDef("Tonne",       "t",    1_000_000.0),
            UnitDef("Kilogram",    "kg",   1000.0),
            UnitDef("Gram",        "g",    1.0),
            UnitDef("Milligram",   "mg",   0.001),
            UnitDef("Microgram",   "µg",   1e-6),
            UnitDef("Pound",       "lb",   453.592),
            UnitDef("Ounce",       "oz",   28.3495),
            UnitDef("Stone",       "st",   6350.29),
        ),

        UnitCategory.VOLUME to listOf(
            UnitDef("Litre",       "L",    1.0),
            UnitDef("Millilitre",  "mL",   0.001),
            UnitDef("Cubic metre", "m³",   1000.0),
            UnitDef("Cubic cm",    "cm³",  0.001),
            UnitDef("US Gallon",   "gal",  3.78541),
            UnitDef("UK Gallon",   "ukgal",4.54609),
            UnitDef("US Quart",    "qt",   0.946353),
            UnitDef("US Pint",     "pt",   0.473176),
            UnitDef("US Cup",      "cup",  0.236588),
            UnitDef("Fluid Oz",    "fl oz",0.0295735),
            UnitDef("Tablespoon",  "tbsp", 0.0147868),
            UnitDef("Teaspoon",    "tsp",  0.00492892),
        ),

        UnitCategory.AREA to listOf(
            UnitDef("Sq Kilometre","km²",  1e6),
            UnitDef("Sq Metre",    "m²",   1.0),
            UnitDef("Sq Centimetre","cm²", 1e-4),
            UnitDef("Sq Millimetre","mm²", 1e-6),
            UnitDef("Sq Mile",     "mi²",  2.58999e6),
            UnitDef("Sq Yard",     "yd²",  0.836127),
            UnitDef("Sq Foot",     "ft²",  0.092903),
            UnitDef("Sq Inch",     "in²",  6.4516e-4),
            UnitDef("Hectare",     "ha",   10_000.0),
            UnitDef("Acre",        "ac",   4046.86),
        ),

        UnitCategory.SPEED to listOf(
            UnitDef("m/s",         "m/s",  1.0),
            UnitDef("km/h",        "km/h", 1.0 / 3.6),
            UnitDef("Miles/h",     "mph",  0.44704),
            UnitDef("Knot",        "kn",   0.514444),
            UnitDef("Foot/s",      "ft/s", 0.3048),
            UnitDef("Mach",        "M",    343.0),
        ),

        UnitCategory.DATA to listOf(
            UnitDef("Bit",         "b",    1.0),
            UnitDef("Byte",        "B",    8.0),
            UnitDef("Kilobyte",    "KB",   8_000.0),
            UnitDef("Megabyte",    "MB",   8_000_000.0),
            UnitDef("Gigabyte",    "GB",   8e9),
            UnitDef("Terabyte",    "TB",   8e12),
            UnitDef("Petabyte",    "PB",   8e15),
            UnitDef("Kibibyte",    "KiB",  8_192.0),
            UnitDef("Mebibyte",    "MiB",  8_388_608.0),
            UnitDef("Gibibyte",    "GiB",  8_589_934_592.0),
        ),

        UnitCategory.PRESSURE to listOf(
            UnitDef("Pascal",      "Pa",   1.0),
            UnitDef("Kilopascal",  "kPa",  1_000.0),
            UnitDef("Megapascal",  "MPa",  1_000_000.0),
            UnitDef("Bar",         "bar",  100_000.0),
            UnitDef("Millibar",    "mbar", 100.0),
            UnitDef("Atm",         "atm",  101_325.0),
            UnitDef("PSI",         "psi",  6_894.76),
            UnitDef("mmHg / Torr", "mmHg", 133.322),
        ),

        UnitCategory.ENERGY to listOf(
            UnitDef("Joule",       "J",    1.0),
            UnitDef("Kilojoule",   "kJ",   1_000.0),
            UnitDef("Calorie",     "cal",  4.184),
            UnitDef("Kilocalorie", "kcal", 4_184.0),
            UnitDef("Watt-hour",   "Wh",   3_600.0),
            UnitDef("kWh",         "kWh",  3_600_000.0),
            UnitDef("BTU",         "BTU",  1_055.06),
            UnitDef("Foot-pound",  "ft·lb",1.35582),
            UnitDef("Electronvolt","eV",   1.60218e-19),
        ),

        UnitCategory.TIME to listOf(
            UnitDef("Nanosecond",  "ns",   1e-9),
            UnitDef("Microsecond", "µs",   1e-6),
            UnitDef("Millisecond", "ms",   0.001),
            UnitDef("Second",      "s",    1.0),
            UnitDef("Minute",      "min",  60.0),
            UnitDef("Hour",        "hr",   3_600.0),
            UnitDef("Day",         "day",  86_400.0),
            UnitDef("Week",        "wk",   604_800.0),
            UnitDef("Month",       "mo",   2_629_800.0),   // avg 30.4375 days
            UnitDef("Year",        "yr",   31_557_600.0),  // avg 365.25 days
            UnitDef("Decade",      "dec",  315_576_000.0),
            UnitDef("Century",     "cent", 3_155_760_000.0),
        ),
    )
}

// ── UI State ──────────────────────────────────────────────────────────────────
data class UnitConverterState(
    val category: UnitCategory      = UnitCategory.LENGTH,
    val fromUnit: UnitDef           = UnitRegistry.units[UnitCategory.LENGTH]!!.first(),
    val toUnit: UnitDef             = UnitRegistry.units[UnitCategory.LENGTH]!![1],
    val inputValue: String          = "",
    val resultValue: String         = "",
    val showCategorySheet: Boolean  = false,
    val showFromSheet: Boolean      = false,
    val showToSheet: Boolean        = false,
)

@HiltViewModel
class UnitConverterViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(UnitConverterState())
    val state: StateFlow<UnitConverterState> = _state.asStateFlow()

    fun setCategory(cat: UnitCategory) {
        val units = UnitRegistry.units[cat]!!
        _state.value = _state.value.copy(
            category           = cat,
            fromUnit           = units[0],
            toUnit             = units[1],
            inputValue         = "",
            resultValue        = "",
            showCategorySheet  = false
        )
    }

    fun setFromUnit(unit: UnitDef) {
        _state.value = _state.value.copy(fromUnit = unit, showFromSheet = false)
        recalculate()
    }

    fun setToUnit(unit: UnitDef) {
        _state.value = _state.value.copy(toUnit = unit, showToSheet = false)
        recalculate()
    }

    fun onInput(raw: String) {
        val sanitized = sanitize(raw)
        _state.value = _state.value.copy(inputValue = sanitized)
        recalculate()
    }

    fun swapUnits() {
        val s = _state.value
        _state.value = s.copy(
            fromUnit    = s.toUnit,
            toUnit      = s.fromUnit,
            inputValue  = if (s.resultValue.isNotEmpty()) s.resultValue else s.inputValue,
            resultValue = ""
        )
        recalculate()
    }

    fun showCategorySheet(show: Boolean) {
        _state.value = _state.value.copy(showCategorySheet = show)
    }

    fun showFromSheet(show: Boolean) {
        _state.value = _state.value.copy(showFromSheet = show)
    }

    fun showToSheet(show: Boolean) {
        _state.value = _state.value.copy(showToSheet = show)
    }

    // ── Core conversion ───────────────────────────────────────────────────────
    private fun recalculate() {
        val s     = _state.value
        val value = s.inputValue.toDoubleOrNull()
        if (value == null) { _state.value = s.copy(resultValue = ""); return }

        val inBase = value * s.fromUnit.toBase
        val result = inBase * s.toUnit.fromBase

        _state.value = _state.value.copy(resultValue = format(result))
    }

    private fun sanitize(raw: String): String {
        var hasDot = false
        return buildString {
            raw.forEachIndexed { i, c ->
                when {
                    c == '-' && i == 0  -> append(c)
                    c == '.' && !hasDot -> { hasDot = true; append(c) }
                    c.isDigit()         -> append(c)
                }
            }
        }
    }

    private fun format(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "—"
        if (v == 0.0) return "0"
        // Scientific notation for very large or very small
        return when {
            v >= 1e12 || (v < 1e-6 && v > 0) -> String.format("%.6e", v)
            else -> {
                val rounded = (v * 1_000_000).roundToLong() / 1_000_000.0
                if (rounded == kotlin.math.floor(rounded))
                    rounded.toLong().toString()
                else
                    rounded.toBigDecimal().stripTrailingZeros().toPlainString()
            }
        }
    }
}