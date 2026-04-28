package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

// ── Tab modes ─────────────────────────────────────────────────────────────────
enum class DateCalcMode { DIFFERENCE, ADD_SUBTRACT }

// ── UI State ──────────────────────────────────────────────────────────────────
data class DateCalcState(
    val mode: DateCalcMode = DateCalcMode.DIFFERENCE,

    // --- Difference mode ---
    val startDate: LocalDate = LocalDate.now().minusDays(30),
    val endDate: LocalDate   = LocalDate.now(),

    // --- Add/Subtract mode ---
    val baseDate: LocalDate  = LocalDate.now(),
    val addDays: String      = "0",
    val addMonths: String    = "0",
    val addYears: String     = "0",
    val subtract: Boolean    = false,

    // --- Results ---
    val diffDays: Long       = 0L,
    val diffWeeks: Long      = 0L,
    val diffMonths: Long     = 0L,
    val diffYears: Long      = 0L,
    val diffPeriod: String   = "",
    val resultDate: LocalDate? = null,
    val resultWeekday: String  = "",
)

@HiltViewModel
class DateCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DateCalcState())
    val state: StateFlow<DateCalcState> = _state.asStateFlow()

    init { calculateDifference() }

    fun setMode(mode: DateCalcMode) {
        _state.value = _state.value.copy(mode = mode)
        if (mode == DateCalcMode.DIFFERENCE) calculateDifference()
        else calculateResult()
    }

    // ── Difference mode ───────────────────────────────────────────────────────
    fun setStartDate(date: LocalDate) {
        _state.value = _state.value.copy(startDate = date)
        calculateDifference()
    }

    fun setEndDate(date: LocalDate) {
        _state.value = _state.value.copy(endDate = date)
        calculateDifference()
    }

    private fun calculateDifference() {
        val s    = _state.value
        val from = if (s.startDate <= s.endDate) s.startDate else s.endDate
        val to   = if (s.startDate <= s.endDate) s.endDate   else s.startDate
        val period = Period.between(from, to)
        val days   = ChronoUnit.DAYS.between(from, to)
        val weeks  = ChronoUnit.WEEKS.between(from, to)
        val months = ChronoUnit.MONTHS.between(from, to)
        val years  = ChronoUnit.YEARS.between(from, to)

        val periodStr = buildString {
            if (period.years  > 0) append("${period.years}y ")
            if (period.months > 0) append("${period.months}mo ")
            if (period.days   > 0) append("${period.days}d")
        }.trim().ifEmpty { "Same day" }

        _state.value = s.copy(
            diffDays   = days,
            diffWeeks  = weeks,
            diffMonths = months,
            diffYears  = years,
            diffPeriod = periodStr
        )
    }

    // ── Add/Subtract mode ─────────────────────────────────────────────────────
    fun setBaseDate(date: LocalDate) {
        _state.value = _state.value.copy(baseDate = date)
        calculateResult()
    }

    fun setAddDays(v: String) {
        _state.value = _state.value.copy(addDays = v.filter { it.isDigit() }.take(4))
        calculateResult()
    }

    fun setAddMonths(v: String) {
        _state.value = _state.value.copy(addMonths = v.filter { it.isDigit() }.take(3))
        calculateResult()
    }

    fun setAddYears(v: String) {
        _state.value = _state.value.copy(addYears = v.filter { it.isDigit() }.take(3))
        calculateResult()
    }

    fun toggleSubtract(subtract: Boolean) {
        _state.value = _state.value.copy(subtract = subtract)
        calculateResult()
    }

    private fun calculateResult() {
        val s   = _state.value
        val sign = if (s.subtract) -1L else 1L
        val d   = (s.addDays.toLongOrNull() ?: 0L) * sign
        val m   = (s.addMonths.toLongOrNull() ?: 0L) * sign
        val y   = (s.addYears.toLongOrNull() ?: 0L) * sign
        val result = s.baseDate
            .plusDays(d).plusMonths(m).plusYears(y)
        val weekday = result.dayOfWeek.name
            .lowercase().replaceFirstChar { it.uppercase() }
        _state.value = s.copy(
            resultDate    = result,
            resultWeekday = weekday
        )
    }
}
