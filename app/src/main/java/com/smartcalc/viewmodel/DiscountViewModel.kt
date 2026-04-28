package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.roundToLong

enum class DiscountMode {
    PERCENT_OFF,       // original price + discount % → final price
    FIND_PERCENT,      // original price + final price → what % off
    REVERSE_CALC,      // final price + discount % → what was original
}

data class DiscountState(
    val mode: DiscountMode   = DiscountMode.PERCENT_OFF,

    // Inputs
    val originalPrice: String = "",
    val discountPct: String   = "",
    val finalPrice: String    = "",

    // Results
    val resultFinalPrice: String    = "",
    val resultSavings: String       = "",
    val resultDiscountPct: String   = "",
    val resultOriginalPrice: String = "",
    val resultSavingsPct: String    = "",  // for reverse
)

@HiltViewModel
class DiscountViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DiscountState())
    val state: StateFlow<DiscountState> = _state.asStateFlow()

    fun setMode(mode: DiscountMode) {
        _state.value = DiscountState(mode = mode)
    }

    fun setOriginalPrice(v: String) {
        _state.value = _state.value.copy(originalPrice = sanitize(v))
        calculate()
    }

    fun setDiscountPct(v: String) {
        // Clamp percentage to 0-100
        val s = sanitize(v)
        val clamped = if (s.toDoubleOrNull() ?: 0.0 > 100.0) "100" else s
        _state.value = _state.value.copy(discountPct = clamped)
        calculate()
    }

    fun setFinalPrice(v: String) {
        _state.value = _state.value.copy(finalPrice = sanitize(v))
        calculate()
    }

    private fun calculate() {
        val s = _state.value
        when (s.mode) {
            DiscountMode.PERCENT_OFF -> calcPercentOff(s)
            DiscountMode.FIND_PERCENT -> calcFindPercent(s)
            DiscountMode.REVERSE_CALC -> calcReverse(s)
        }
    }

    // Mode 1: original + discount% → final price & savings
    private fun calcPercentOff(s: DiscountState) {
        val original = s.originalPrice.toDoubleOrNull()
        val pct      = s.discountPct.toDoubleOrNull()
        if (original == null || pct == null) {
            _state.value = s.copy(resultFinalPrice = "", resultSavings = "")
            return
        }
        val savings   = original * pct / 100.0
        val finalP    = original - savings
        _state.value = s.copy(
            resultFinalPrice = format(finalP),
            resultSavings    = format(savings)
        )
    }

    // Mode 2: original + final → what % off
    private fun calcFindPercent(s: DiscountState) {
        val original = s.originalPrice.toDoubleOrNull()
        val final    = s.finalPrice.toDoubleOrNull()
        if (original == null || final == null || original <= 0) {
            _state.value = s.copy(resultDiscountPct = "", resultSavings = "")
            return
        }
        val savings = original - final
        val pct     = (savings / original) * 100.0
        _state.value = s.copy(
            resultDiscountPct = format(pct),
            resultSavings     = format(savings)
        )
    }

    // Mode 3: final price + discount% → original price
    private fun calcReverse(s: DiscountState) {
        val final = s.finalPrice.toDoubleOrNull()
        val pct   = s.discountPct.toDoubleOrNull()
        if (final == null || pct == null || pct >= 100) {
            _state.value = s.copy(resultOriginalPrice = "", resultSavings = "")
            return
        }
        val original = final / (1.0 - pct / 100.0)
        val savings  = original - final
        _state.value = s.copy(
            resultOriginalPrice = format(original),
            resultSavings       = format(savings)
        )
    }

    private fun sanitize(raw: String): String {
        var hasDot = false
        return buildString {
            raw.forEach { c ->
                when {
                    c.isDigit()          -> append(c)
                    c == '.' && !hasDot  -> { hasDot = true; append(c) }
                }
            }
        }
    }

    private fun format(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "—"
        val rounded = (v * 100).roundToLong() / 100.0
        return if (rounded == kotlin.math.floor(rounded))
            rounded.toLong().toString()
        else
            "%.2f".format(rounded)
    }
}
