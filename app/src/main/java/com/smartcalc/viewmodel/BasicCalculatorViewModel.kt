package com.smartcalc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.sqrt

// ── UI State ──────────────────────────────────────────────────────────────────
data class CalculatorState(
    val display: String      = "0",
    val expression: String   = "",
    val hasError: Boolean    = false,
    val justEvaluated: Boolean = false
)

// ── Button types ──────────────────────────────────────────────────────────────
sealed class CalcButton {
    data class Number(val value: String)   : CalcButton()
    data class Operator(val symbol: String): CalcButton()
    object Clear    : CalcButton()
    object Delete   : CalcButton()
    object Equals   : CalcButton()
    object Decimal  : CalcButton()
    object ToggleSign : CalcButton()
    object Percent  : CalcButton()
    object Sqrt     : CalcButton()
}

@HiltViewModel
class BasicCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    // internal mutable operand tracking
    private var currentInput  = ""
    private var storedOperand = ""
    private var pendingOp     = ""
    private var waitingForSecond = false

    fun onButton(button: CalcButton) {
        val s = _state.value
        when (button) {
            is CalcButton.Number   -> onDigit(button.value)
            is CalcButton.Operator -> onOperator(button.symbol)
            CalcButton.Clear       -> onClear()
            CalcButton.Delete      -> onDelete()
            CalcButton.Equals      -> onEquals()
            CalcButton.Decimal     -> onDecimal()
            CalcButton.ToggleSign  -> onToggleSign()
            CalcButton.Percent     -> onPercent()
            CalcButton.Sqrt        -> onSqrt()
        }
    }

    private fun onDigit(d: String) {
        val s = _state.value
        if (s.hasError) { onClear(); return }

        if (waitingForSecond || s.justEvaluated) {
            currentInput = if (d == "0" && currentInput.isEmpty()) "" else d
            waitingForSecond = false
            _state.value = s.copy(display = currentInput.ifEmpty { "0" }, justEvaluated = false)
            return
        }

        // Limit display length
        if (currentInput.replace("-", "").replace(".", "").length >= 12) return

        currentInput = if (currentInput == "0" && d != ".") d else currentInput + d
        _state.value = s.copy(display = currentInput)
    }

    private fun onDecimal() {
        if (_state.value.hasError) return
        if (waitingForSecond) {
            currentInput = "0."
            waitingForSecond = false
            _state.value = _state.value.copy(display = "0.")
            return
        }
        if ("." in currentInput) return
        currentInput = if (currentInput.isEmpty()) "0." else "$currentInput."
        _state.value = _state.value.copy(display = currentInput)
    }

    private fun onOperator(op: String) {
        if (_state.value.hasError) return

        val inputVal = currentInput.toDoubleOrNull() ?: storedOperand.toDoubleOrNull() ?: 0.0

        // Chain operations
        if (!waitingForSecond && pendingOp.isNotEmpty() && currentInput.isNotEmpty()) {
            val result = compute(storedOperand.toDoubleOrNull() ?: 0.0, inputVal, pendingOp)
            val fmt = format(result)
            storedOperand = fmt
            currentInput = ""
            _state.value = _state.value.copy(
                display    = fmt,
                expression = "$fmt $op",
                justEvaluated = false
            )
        } else {
            storedOperand = currentInput.ifEmpty { storedOperand }
            currentInput = ""
            _state.value = _state.value.copy(
                display    = storedOperand,
                expression = "$storedOperand $op",
                justEvaluated = false
            )
        }

        pendingOp = op
        waitingForSecond = true
    }

    private fun onEquals() {
        if (_state.value.hasError || pendingOp.isEmpty()) return
        val a = storedOperand.toDoubleOrNull() ?: return
        val b = currentInput.toDoubleOrNull() ?: storedOperand.toDoubleOrNull() ?: return

        val expr = "${_state.value.expression} ${currentInput.ifEmpty { storedOperand }}"
        val result = compute(a, b, pendingOp)

        if (result.isNaN() || result.isInfinite()) {
            _state.value = _state.value.copy(
                display    = if (result.isInfinite()) "∞" else "Error",
                expression = expr,
                hasError   = true
            )
            reset()
            return
        }

        val fmt = format(result)
        currentInput  = fmt
        storedOperand = fmt
        pendingOp     = ""
        waitingForSecond = false
        _state.value = _state.value.copy(
            display    = fmt,
            expression = "$expr =",
            hasError   = false,
            justEvaluated = true
        )
    }

    private fun onClear() {
        reset()
        _state.value = CalculatorState()
    }

    private fun onDelete() {
        if (_state.value.hasError || _state.value.justEvaluated) { onClear(); return }
        if (currentInput.isEmpty() || currentInput == "0") return
        currentInput = if (currentInput.length == 1) "" else currentInput.dropLast(1)
        _state.value = _state.value.copy(display = currentInput.ifEmpty { "0" })
    }

    private fun onToggleSign() {
        val d = currentInput.toDoubleOrNull() ?: return
        currentInput = format(-d)
        _state.value = _state.value.copy(display = currentInput)
    }

    private fun onPercent() {
        val d = currentInput.toDoubleOrNull() ?: return
        currentInput = format(d / 100.0)
        _state.value = _state.value.copy(display = currentInput)
    }

    private fun onSqrt() {
        val d = currentInput.toDoubleOrNull() ?: return
        if (d < 0) {
            _state.value = _state.value.copy(display = "Error", hasError = true)
            reset()
            return
        }
        val result = sqrt(d)
        currentInput = format(result)
        _state.value = _state.value.copy(
            display    = currentInput,
            expression = "√($d)",
            justEvaluated = true
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun compute(a: Double, b: Double, op: String): Double = when (op) {
        "+"  -> a + b
        "−"  -> a - b
        "×"  -> a * b
        "÷"  -> if (b == 0.0) Double.POSITIVE_INFINITY else a / b
        else -> b
    }

    private fun format(value: Double): String {
        return if (value == kotlin.math.floor(value) && !value.isInfinite())
            value.toLong().toString()
        else
            value.toBigDecimal().stripTrailingZeros().toPlainString()
    }

    private fun reset() {
        currentInput     = ""
        storedOperand    = ""
        pendingOp        = ""
        waitingForSecond = false
    }
}
