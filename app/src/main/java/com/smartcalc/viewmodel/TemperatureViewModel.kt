package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.roundToLong

enum class TempUnit(val label: String, val symbol: String) {
    CELSIUS("Celsius", "°C"),
    FAHRENHEIT("Fahrenheit", "°F"),
    KELVIN("Kelvin", "K")
}

data class TempState(
    val inputUnit: TempUnit = TempUnit.CELSIUS,
    val inputValue: String  = "",
    val celsius: String     = "",
    val fahrenheit: String  = "",
    val kelvin: String      = "",
)

@HiltViewModel
class TemperatureViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TempState())
    val state: StateFlow<TempState> = _state.asStateFlow()

    fun setInputUnit(unit: TempUnit) {
        _state.value = _state.value.copy(inputUnit = unit, inputValue = "")
        clearResults()
    }

    fun onInput(raw: String) {
        // Allow minus only at start, one decimal point
        val sanitized = raw
            .filter { it.isDigit() || it == '.' || it == '-' }
            .let { s ->
                buildString {
                    s.forEachIndexed { i, c ->
                        if (c == '-' && i == 0) append(c)
                        else if (c == '.' && !contains('.')) append(c)
                        else if (c.isDigit()) append(c)
                    }
                }
            }
        _state.value = _state.value.copy(inputValue = sanitized)
        convert(sanitized)
    }

    private fun convert(raw: String) {
        val value = raw.toDoubleOrNull()
        if (value == null) { clearResults(); return }

        val c = when (_state.value.inputUnit) {
            TempUnit.CELSIUS    -> value
            TempUnit.FAHRENHEIT -> (value - 32) * 5.0 / 9.0
            TempUnit.KELVIN     -> value - 273.15
        }

        _state.value = _state.value.copy(
            celsius    = format(c),
            fahrenheit = format(c * 9.0 / 5.0 + 32),
            kelvin     = format(c + 273.15)
        )
    }

    private fun clearResults() {
        _state.value = _state.value.copy(celsius = "", fahrenheit = "", kelvin = "")
    }

    private fun format(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "—"
        // Up to 4 decimal places, strip trailing zeros
        val rounded = (v * 10000).roundToLong() / 10000.0
        return if (rounded == kotlin.math.floor(rounded))
            rounded.toLong().toString()
        else
            rounded.toBigDecimal().stripTrailingZeros().toPlainString()
    }
}
