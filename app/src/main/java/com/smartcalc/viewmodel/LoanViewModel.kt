package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToLong

// ── Amortization row ──────────────────────────────────────────────────────────
data class AmortizationRow(
    val month: Int,
    val emi: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double,
)

// ── UI State ──────────────────────────────────────────────────────────────────
data class LoanState(
    val loanAmount: String      = "",
    val interestRate: String    = "",   // annual %
    val tenureMonths: String    = "",
    val tenureYears: String     = "",   // convenience — synced with months
    val useTenureYears: Boolean = true, // which field user is editing

    // Results
    val emi: Double?            = null,
    val totalPayment: Double?   = null,
    val totalInterest: Double?  = null,
    val principalPct: Float     = 0f,
    val interestPct: Float      = 0f,

    // Amortization
    val schedule: List<AmortizationRow> = emptyList(),
    val showFullSchedule: Boolean       = false,
)

@HiltViewModel
class LoanViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoanState())
    val state: StateFlow<LoanState> = _state.asStateFlow()

    fun setLoanAmount(v: String) {
        _state.value = _state.value.copy(loanAmount = sanitize(v))
        calculate()
    }

    fun setInterestRate(v: String) {
        val s = sanitize(v)
        val clamped = if (s.toDoubleOrNull() ?: 0.0 > 100.0) "100" else s
        _state.value = _state.value.copy(interestRate = clamped)
        calculate()
    }

    fun setTenureMonths(v: String) {
        val s = sanitizeInt(v, max = 360)
        val years = (s.toIntOrNull() ?: 0) / 12
        _state.value = _state.value.copy(
            tenureMonths = s,
            tenureYears  = if (years > 0) years.toString() else "",
            useTenureYears = false
        )
        calculate()
    }

    fun setTenureYears(v: String) {
        val s = sanitizeInt(v, max = 30)
        val months = (s.toIntOrNull() ?: 0) * 12
        _state.value = _state.value.copy(
            tenureYears  = s,
            tenureMonths = if (months > 0) months.toString() else "",
            useTenureYears = true
        )
        calculate()
    }

    fun toggleFullSchedule() {
        _state.value = _state.value.copy(showFullSchedule = !_state.value.showFullSchedule)
    }

    // ── Core EMI formula: EMI = P * r * (1+r)^n / ((1+r)^n - 1) ─────────────
    private fun calculate() {
        val s       = _state.value
        val p       = s.loanAmount.toDoubleOrNull()
        val annRate = s.interestRate.toDoubleOrNull()
        val n       = s.tenureMonths.toIntOrNull()

        if (p == null || annRate == null || n == null || p <= 0 || annRate <= 0 || n <= 0) {
            _state.value = s.copy(
                emi = null, totalPayment = null, totalInterest = null,
                principalPct = 0f, interestPct = 0f, schedule = emptyList()
            )
            return
        }

        val r   = annRate / 12.0 / 100.0          // monthly rate
        val emi = p * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
        val totalPayment  = emi * n
        val totalInterest = totalPayment - p
        val principalPct  = (p / totalPayment).toFloat()
        val interestPct   = (totalInterest / totalPayment).toFloat()

        // Build amortization schedule
        val schedule = buildList {
            var balance = p
            for (month in 1..n) {
                val interestPart   = balance * r
                val principalPart  = emi - interestPart
                balance -= principalPart
                add(AmortizationRow(
                    month     = month,
                    emi       = emi,
                    principal = principalPart,
                    interest  = interestPart,
                    balance   = maxOf(0.0, balance)
                ))
            }
        }

        _state.value = s.copy(
            emi           = emi,
            totalPayment  = totalPayment,
            totalInterest = totalInterest,
            principalPct  = principalPct,
            interestPct   = interestPct,
            schedule      = schedule
        )
    }

    private fun sanitize(raw: String): String {
        var hasDot = false
        return buildString {
            raw.forEach { c ->
                when {
                    c.isDigit()         -> append(c)
                    c == '.' && !hasDot -> { hasDot = true; append(c) }
                }
            }
        }
    }

    private fun sanitizeInt(raw: String, max: Int): String {
        val digits = raw.filter { it.isDigit() }
        val num = digits.toIntOrNull() ?: return digits
        return if (num > max) max.toString() else digits
    }
}

// ── Formatting helpers (used by UI) ──────────────────────────────────────────
fun formatCurrency(v: Double): String {
    if (v.isNaN() || v.isInfinite()) return "—"
    return when {
        v >= 1_00_00_000 -> "₹ ${"%.2f".format(v / 1_00_00_000)} Cr"
        v >= 1_00_000    -> "₹ ${"%.2f".format(v / 1_00_000)} L"
        v >= 1_000       -> "₹ ${"%.2f".format(v / 1_000)} K"
        else             -> "₹ ${"%.0f".format(v)}"
    }
}

fun formatCurrencyFull(v: Double): String {
    if (v.isNaN() || v.isInfinite()) return "—"
    val rounded = (v * 100).roundToLong() / 100.0
    val parts   = "%.2f".format(rounded).split(".")
    val intPart = parts[0].toLongOrNull() ?: 0L
    // Indian number formatting: 1,00,000
    val formatted = buildString {
        val s = intPart.toString()
        val len = s.length
        if (len <= 3) {
            append(s)
        } else {
            append(s.substring(0, len - 3))
            var pos = len - 3
            append(",")
            append(s.substring(pos))
            // already handled
        }
    }
    return "₹ ${formatted}.${parts.getOrElse(1) { "00" }}"
}