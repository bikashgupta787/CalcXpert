package com.smartcalc.ui.datecalc

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.ui.theme.Primary
import com.smartcalc.viewmodel.DateCalcMode
import com.smartcalc.viewmodel.DateCalculatorViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
private val FMT_RESULT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen(
    onBack: () -> Unit,
    vm: DateCalculatorViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Date Calculator", style = MaterialTheme.typography.titleMedium) },
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
                .padding(horizontal = 16.dp)
        ) {

            // ── Mode toggle ──────────────────────────────────────────────────
            ModeSegmentedButton(
                selected = state.mode,
                onSelect = { vm.setMode(it) }
            )

            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = state.mode,
                transitionSpec = {
                    fadeIn(tween(250)) + slideInHorizontally { if (targetState == DateCalcMode.DIFFERENCE) -100 else 100 } togetherWith
                    fadeOut(tween(150))
                },
                label = "mode_content"
            ) { mode ->
                when (mode) {
                    DateCalcMode.DIFFERENCE -> DifferenceTab(
                        startDate = state.startDate,
                        endDate   = state.endDate,
                        onStartDate = { vm.setStartDate(it) },
                        onEndDate   = { vm.setEndDate(it) },
                        diffDays    = state.diffDays,
                        diffWeeks   = state.diffWeeks,
                        diffMonths  = state.diffMonths,
                        diffYears   = state.diffYears,
                        diffPeriod  = state.diffPeriod
                    )
                    DateCalcMode.ADD_SUBTRACT -> AddSubtractTab(
                        baseDate    = state.baseDate,
                        addDays     = state.addDays,
                        addMonths   = state.addMonths,
                        addYears    = state.addYears,
                        subtract    = state.subtract,
                        resultDate  = state.resultDate,
                        resultWeekday = state.resultWeekday,
                        onBaseDate  = { vm.setBaseDate(it) },
                        onDays      = { vm.setAddDays(it) },
                        onMonths    = { vm.setAddMonths(it) },
                        onYears     = { vm.setAddYears(it) },
                        onSubtract  = { vm.toggleSubtract(it) }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Mode toggle ───────────────────────────────────────────────────────────────
@Composable
private fun ModeSegmentedButton(
    selected: DateCalcMode,
    onSelect: (DateCalcMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DateCalcMode.values().forEach { mode ->
            val isSelected = selected == mode
            val label = if (mode == DateCalcMode.DIFFERENCE) "Difference" else "Add / Subtract"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Primary else Color.Transparent)
                    .clickable { onSelect(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Difference tab ────────────────────────────────────────────────────────────
@Composable
private fun DifferenceTab(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDate: (LocalDate) -> Unit,
    onEndDate: (LocalDate) -> Unit,
    diffDays: Long,
    diffWeeks: Long,
    diffMonths: Long,
    diffYears: Long,
    diffPeriod: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DatePickerCard("From", startDate, onStartDate)
        DatePickerCard("To",   endDate,   onEndDate)

        Spacer(Modifier.height(4.dp))

        // Result card
        Card(
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Result",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    diffPeriod,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DiffChip("$diffDays", "Days",   Modifier.weight(1f))
                    DiffChip("$diffWeeks", "Weeks",  Modifier.weight(1f))
                    DiffChip("$diffMonths", "Months", Modifier.weight(1f))
                    DiffChip("$diffYears", "Years",  Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DiffChip(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
             color = MaterialTheme.colorScheme.onBackground)
        Text(label, style = MaterialTheme.typography.labelSmall,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Add/Subtract tab ──────────────────────────────────────────────────────────
@Composable
private fun AddSubtractTab(
    baseDate: LocalDate,
    addDays: String,
    addMonths: String,
    addYears: String,
    subtract: Boolean,
    resultDate: LocalDate?,
    resultWeekday: String,
    onBaseDate: (LocalDate) -> Unit,
    onDays: (String) -> Unit,
    onMonths: (String) -> Unit,
    onYears: (String) -> Unit,
    onSubtract: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DatePickerCard("Base Date", baseDate, onBaseDate)

        // Add/Subtract toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            listOf(false to "Add", true to "Subtract").forEach { (isSubtract, label) ->
                val isSelected = subtract == isSubtract
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected)
                                if (isSubtract) MaterialTheme.colorScheme.error
                                else Color(0xFF30D158)
                            else Color.Transparent
                        )
                        .clickable { onSubtract(isSubtract) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // Duration inputs
        Card(
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Duration", style = MaterialTheme.typography.labelMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DurationInput("Years",  addYears,  onYears,  Modifier.weight(1f))
                    DurationInput("Months", addMonths, onMonths, Modifier.weight(1f))
                    DurationInput("Days",   addDays,   onDays,   Modifier.weight(1f))
                }
            }
        }

        // Result
        if (resultDate != null) {
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Primary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Result Date", style = MaterialTheme.typography.labelMedium,
                         color = Primary.copy(alpha = 0.8f))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        resultWeekday,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        resultDate.format(FMT_RESULT),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun DurationInput(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
        ),
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor  = Primary
        )
    )
}

// ── Date picker card ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerCard(
    label: String,
    date: LocalDate,
    onDate: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Card(
        onClick = { showPicker = true },
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    date.format(FMT),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = Primary
            )
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date
                .toEpochDay() * 86_400_000L
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val selected = LocalDate.ofEpochDay(millis / 86_400_000L)
                        onDate(selected)
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(
                state = pickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Primary,
                    todayDateBorderColor      = Primary
                )
            )
        }
    }
}
