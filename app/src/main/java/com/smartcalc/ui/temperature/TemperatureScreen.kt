package com.smartcalc.ui.temperature

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.ui.theme.Primary
import com.smartcalc.viewmodel.TempUnit
import com.smartcalc.viewmodel.TemperatureViewModel

// Colour per unit — gives each card its own personality
private val unitColor = mapOf(
    TempUnit.CELSIUS    to Color(0xFF0A84FF),
    TempUnit.FAHRENHEIT to Color(0xFFFF6B35),
    TempUnit.KELVIN     to Color(0xFFAC8FFF),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureScreen(
    onBack: () -> Unit,
    vm: TemperatureViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Temperature", style = MaterialTheme.typography.titleMedium) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            // ── Header illustration ──────────────────────────────────────────
            TempHeader()

            // ── Unit selector ────────────────────────────────────────────────
            UnitSelector(
                selected = state.inputUnit,
                onSelect = { vm.setInputUnit(it) }
            )

            // ── Input field ──────────────────────────────────────────────────
            InputField(
                value    = state.inputValue,
                unit     = state.inputUnit,
                onValue  = { vm.onInput(it) }
            )

            // ── Result cards ─────────────────────────────────────────────────
            Text(
                "Converted Values",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            TempUnit.values().forEach { unit ->
                val result = when (unit) {
                    TempUnit.CELSIUS    -> state.celsius
                    TempUnit.FAHRENHEIT -> state.fahrenheit
                    TempUnit.KELVIN     -> state.kelvin
                }
                val isSource = unit == state.inputUnit
                ResultCard(
                    unit     = unit,
                    value    = if (isSource) state.inputValue else result,
                    isSource = isSource
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun TempHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0A84FF).copy(alpha = 0.15f), Color(0xFFAC8FFF).copy(alpha = 0.15f))
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.Thermostat,
                contentDescription = null,
                tint   = Primary,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(
                    "Temperature Converter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Celsius · Fahrenheit · Kelvin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Unit selector tabs ────────────────────────────────────────────────────────
@Composable
private fun UnitSelector(
    selected: TempUnit,
    onSelect: (TempUnit) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TempUnit.values().forEach { unit ->
            val isSelected = selected == unit
            val color = unitColor[unit] ?: Primary
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) color else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(unit) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        unit.symbol,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        unit.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White.copy(alpha = 0.85f)
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

// ── Input field ───────────────────────────────────────────────────────────────
@Composable
private fun InputField(
    value: String,
    unit: TempUnit,
    onValue: (String) -> Unit
) {
    val color = unitColor[unit] ?: Primary

    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        placeholder = {
            Text(
                "Enter temperature in ${unit.label}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        trailingIcon = {
            Text(
                unit.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color,
                modifier = Modifier.padding(end = 12.dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = color,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor    = color,
            cursorColor          = color
        )
    )
}

// ── Result card ───────────────────────────────────────────────────────────────
@Composable
private fun ResultCard(
    unit: TempUnit,
    value: String,
    isSource: Boolean
) {
    val color = unitColor[unit] ?: Primary
    val isEmpty = value.isEmpty()

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSource)
                color.copy(alpha = 0.12f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSource) Modifier.border(
                    width = 1.5.dp,
                    color = color.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left — unit name + badge
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        unit.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSource) color else MaterialTheme.colorScheme.onSurface
                    )
                    if (isSource) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(color.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "INPUT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                Text(
                    unit.symbol,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right — value
            AnimatedContent(
                targetState = value,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                label = "value_${unit.name}"
            ) { v ->
                Text(
                    text = if (v.isEmpty()) "—" else v,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isEmpty) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    else if (isSource) color
                    else MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}