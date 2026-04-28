package com.smartcalc.ui.calculator

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.ui.theme.*
import com.smartcalc.viewmodel.BasicCalculatorViewModel
import com.smartcalc.viewmodel.CalcButton

// ── Button style enum ─────────────────────────────────────────────────────────
private enum class BtnStyle { Function, Operator, Number, Zero, Equals }

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicCalculatorScreen(
    onBack: () -> Unit,
    vm: BasicCalculatorViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Calculator", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            // ── Display ──────────────────────────────────────────────────────
            DisplayArea(
                expression = state.expression,
                display    = state.display,
                hasError   = state.hasError
            )

            Spacer(Modifier.height(16.dp))

            // ── Button grid ──────────────────────────────────────────────────
            val rows: List<List<Pair<String, CalcButton>>> = listOf(
                listOf(
                    "AC"  to CalcButton.Clear,
                    "+/-" to CalcButton.ToggleSign,
                    "%"   to CalcButton.Percent,
                    "÷"   to CalcButton.Operator("÷")
                ),
                listOf(
                    "7"   to CalcButton.Number("7"),
                    "8"   to CalcButton.Number("8"),
                    "9"   to CalcButton.Number("9"),
                    "×"   to CalcButton.Operator("×")
                ),
                listOf(
                    "4"   to CalcButton.Number("4"),
                    "5"   to CalcButton.Number("5"),
                    "6"   to CalcButton.Number("6"),
                    "−"   to CalcButton.Operator("−")
                ),
                listOf(
                    "1"   to CalcButton.Number("1"),
                    "2"   to CalcButton.Number("2"),
                    "3"   to CalcButton.Number("3"),
                    "+"   to CalcButton.Operator("+")
                ),
                listOf(
                    "√"   to CalcButton.Sqrt,
                    "0"   to CalcButton.Number("0"),
                    "."   to CalcButton.Decimal,
                    "="   to CalcButton.Equals
                )
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { (label, action) ->
                        CalcKey(
                            label  = label,
                            style  = styleFor(label),
                            weight = if (label == "0") 2f else 1f,
                            action = action,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vm.onButton(action)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Display ───────────────────────────────────────────────────────────────────
@Composable
private fun DisplayArea(expression: String, display: String, hasError: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Expression line
        AnimatedVisibility(
            visible = expression.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically()
        ) {
            Text(
                text  = expression,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(4.dp))

        // Main display — scales font size to fit
        val displayColor = when {
            hasError -> MaterialTheme.colorScheme.error
            else     -> MaterialTheme.colorScheme.onBackground
        }
        val fontSize = when {
            display.length > 12 -> 36.sp
            display.length > 9  -> 48.sp
            else                -> 64.sp
        }

        Text(
            text     = display,
            fontSize = fontSize,
            fontWeight = FontWeight.Light,
            color    = displayColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

// ── Calculator key ────────────────────────────────────────────────────────────
@Composable
private fun RowScope.CalcKey(
    label: String,
    style: BtnStyle,
    weight: Float = 1f,
    action: CalcButton,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.red < 0.5f

    val bg: Color = when (style) {
        BtnStyle.Function -> if (isDark) MaterialTheme.colorScheme.surfaceVariant
                             else Color(0xFFD4D4D4)
        BtnStyle.Operator,
        BtnStyle.Equals   -> Primary
        BtnStyle.Number,
        BtnStyle.Zero     -> if (isDark) Color(0xFF2C2C2E)
                             else Color(0xFFFFFFFF)
    }

    val textColor: Color = when (style) {
        BtnStyle.Operator,
        BtnStyle.Equals   -> Color.White
        else              -> MaterialTheme.colorScheme.onSurface
    }

    val shape = if (style == BtnStyle.Zero)
        RoundedCornerShape(50) else CircleShape

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "key_scale"
    )

    Box(
        modifier = Modifier
            .weight(weight)
            .aspectRatio(if (weight == 2f) 2.2f else 1f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(bg)
            .then(
                Modifier.noRippleClickable(
                    onPress = { pressed = true },
                    onRelease = { pressed = false },
                    onClick = onClick
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (action == CalcButton.Delete) {
            Icon(
                Icons.Filled.Backspace, "Delete",
                tint   = textColor,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text  = label,
                color = textColor,
                fontSize = when (label) {
                    "+/-", "AC", "√" -> 18.sp
                    else             -> 22.sp
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Style mapping ─────────────────────────────────────────────────────────────
private fun styleFor(label: String): BtnStyle = when (label) {
    "AC", "+/-", "%", "√" -> BtnStyle.Function
    "÷", "×", "−", "+"   -> BtnStyle.Operator
    "="                   -> BtnStyle.Equals
    "0"                   -> BtnStyle.Zero
    else                  -> BtnStyle.Number
}

// ── Custom press-tracking modifier (no ripple) ────────────────────────────────
@Composable
private fun Modifier.noRippleClickable(
    onPress: () -> Unit,
    onRelease: () -> Unit,
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        onClick()
    }
)
