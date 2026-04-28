package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.pow

enum class BmiUnit { METRIC, IMPERIAL }

enum class BmiCategory(
    val label: String,
    val range: String,
    val advice: String,
    val colorHex: Long
) {
    SEVERE_THIN(  "Severely Underweight", "< 16.0",    "Immediate medical attention recommended.",     0xFFEF5350),
    MODERATE_THIN("Moderately Underweight","16.0 – 16.9","Consult a doctor and nutritionist.",         0xFFFF7043),
    MILD_THIN(    "Mildly Underweight",   "17.0 – 18.4","Increase caloric intake gradually.",          0xFFFFCA28),
    NORMAL(       "Normal Weight",        "18.5 – 24.9","Great! Maintain your healthy lifestyle.",     0xFF66BB6A),
    OVERWEIGHT(   "Overweight",           "25.0 – 29.9","Consider more activity and balanced diet.",   0xFFFFCA28),
    OBESE_1(      "Obese Class I",        "30.0 – 34.9","Consult a healthcare professional.",          0xFFFF7043),
    OBESE_2(      "Obese Class II",       "35.0 – 39.9","Medical intervention may be needed.",         0xFFEF5350),
    OBESE_3(      "Obese Class III",      "≥ 40.0",     "Immediate medical consultation advised.",     0xFFB71C1C),
}

data class BmiState(
    val unit: BmiUnit          = BmiUnit.METRIC,

    // Metric inputs
    val heightCm: String       = "",
    val weightKg: String       = "",

    // Imperial inputs
    val heightFt: String       = "",
    val heightIn: String       = "",
    val weightLb: String       = "",

    // Result
    val bmi: Double?           = null,
    val category: BmiCategory? = null,
    val scalePosition: Float   = 0f,   // 0..1 across the visual scale
)

@HiltViewModel
class BmiViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(BmiState())
    val state: StateFlow<BmiState> = _state.asStateFlow()

    fun setUnit(unit: BmiUnit) {
        _state.value = BmiState(unit = unit)
    }

    // ── Metric setters ────────────────────────────────────────────────────────
    fun setHeightCm(v: String) {
        _state.value = _state.value.copy(heightCm = sanitize(v, max = 300))
        calculate()
    }

    fun setWeightKg(v: String) {
        _state.value = _state.value.copy(weightKg = sanitize(v, max = 500))
        calculate()
    }

    // ── Imperial setters ──────────────────────────────────────────────────────
    fun setHeightFt(v: String) {
        _state.value = _state.value.copy(heightFt = sanitize(v, max = 9, decimals = false))
        calculate()
    }

    fun setHeightIn(v: String) {
        _state.value = _state.value.copy(heightIn = sanitize(v, max = 11, decimals = false))
        calculate()
    }

    fun setWeightLb(v: String) {
        _state.value = _state.value.copy(weightLb = sanitize(v, max = 1000))
        calculate()
    }

    // ── Core calculation ──────────────────────────────────────────────────────
    private fun calculate() {
        val s = _state.value

        val bmi: Double? = when (s.unit) {
            BmiUnit.METRIC -> {
                val h = s.heightCm.toDoubleOrNull()?.div(100.0)
                val w = s.weightKg.toDoubleOrNull()
                if (h != null && w != null && h > 0) w / h.pow(2) else null
            }
            BmiUnit.IMPERIAL -> {
                val ft = s.heightFt.toDoubleOrNull() ?: 0.0
                val inches = s.heightIn.toDoubleOrNull() ?: 0.0
                val totalInches = ft * 12 + inches
                val w = s.weightLb.toDoubleOrNull()
                if (totalInches > 0 && w != null) (w / totalInches.pow(2)) * 703 else null
            }
        }

        val category = bmi?.let { categorise(it) }
        val scalePos = bmi?.let { scalePosition(it) } ?: 0f

        _state.value = s.copy(bmi = bmi, category = category, scalePosition = scalePos)
    }

    private fun categorise(bmi: Double): BmiCategory = when {
        bmi < 16.0  -> BmiCategory.SEVERE_THIN
        bmi < 17.0  -> BmiCategory.MODERATE_THIN
        bmi < 18.5  -> BmiCategory.MILD_THIN
        bmi < 25.0  -> BmiCategory.NORMAL
        bmi < 30.0  -> BmiCategory.OVERWEIGHT
        bmi < 35.0  -> BmiCategory.OBESE_1
        bmi < 40.0  -> BmiCategory.OBESE_2
        else        -> BmiCategory.OBESE_3
    }

    // Maps BMI to 0..1 for the visual scale (clamp between 10 and 45)
    private fun scalePosition(bmi: Double): Float {
        val min = 10.0
        val max = 45.0
        return ((bmi.coerceIn(min, max) - min) / (max - min)).toFloat()
    }

    private fun sanitize(raw: String, max: Int, decimals: Boolean = true): String {
        var hasDot = false
        val filtered = buildString {
            raw.forEach { c ->
                when {
                    c.isDigit()         -> append(c)
                    c == '.' && decimals && !hasDot -> { hasDot = true; append(c) }
                }
            }
        }
        // Clamp to max value
        val num = filtered.toDoubleOrNull()
        return if (num != null && num > max) max.toString() else filtered
    }
}