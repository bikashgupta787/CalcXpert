package com.smartcalc.ui.unitconverter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.ui.theme.Primary
import com.smartcalc.viewmodel.UnitCategory
import com.smartcalc.viewmodel.UnitConverterViewModel
import com.smartcalc.viewmodel.UnitDef
import com.smartcalc.viewmodel.UnitRegistry

// Accent colour per category
private val categoryColor = mapOf(
    UnitCategory.LENGTH   to Color(0xFF0A84FF),
    UnitCategory.WEIGHT   to Color(0xFF30D158),
    UnitCategory.VOLUME   to Color(0xFF64D2FF),
    UnitCategory.AREA     to Color(0xFFFF9F0A),
    UnitCategory.SPEED    to Color(0xFFFF375F),
    UnitCategory.DATA     to Color(0xFFAC8FFF),
    UnitCategory.PRESSURE to Color(0xFFFFD60A),
    UnitCategory.ENERGY   to Color(0xFFFF6B35),
    UnitCategory.TIME     to Color(0xFF30D158),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    onBack: () -> Unit,
    vm: UnitConverterViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val accent = categoryColor[state.category] ?: Primary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter", style = MaterialTheme.typography.titleMedium) },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Category picker ──────────────────────────────────────────────
            CategoryChipRow(
                selected = state.category,
                accent   = accent,
                onClick  = { vm.showCategorySheet(true) }
            )

            // ── From / Swap / To ─────────────────────────────────────────────
            ConversionCard(
                fromUnit    = state.fromUnit,
                toUnit      = state.toUnit,
                inputValue  = state.inputValue,
                resultValue = state.resultValue,
                accent      = accent,
                onInput     = { vm.onInput(it) },
                onSwap      = { vm.swapUnits() },
                onFromClick = { vm.showFromSheet(true) },
                onToClick   = { vm.showToSheet(true) }
            )

            // ── Formula hint ─────────────────────────────────────────────────
            if (state.inputValue.isNotEmpty() && state.resultValue.isNotEmpty()) {
                FormulaHint(
                    from   = state.fromUnit,
                    to     = state.toUnit,
                    input  = state.inputValue,
                    result = state.resultValue,
                    accent = accent
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }

    // ── Category bottom sheet ────────────────────────────────────────────────
    if (state.showCategorySheet) {
        CategorySheet(
            current  = state.category,
            onSelect = { vm.setCategory(it) },
            onDismiss= { vm.showCategorySheet(false) }
        )
    }

    // ── From unit bottom sheet ───────────────────────────────────────────────
    if (state.showFromSheet) {
        UnitPickerSheet(
            title    = "From Unit",
            units    = UnitRegistry.units[state.category] ?: emptyList(),
            selected = state.fromUnit,
            accent   = accent,
            onSelect = { vm.setFromUnit(it) },
            onDismiss= { vm.showFromSheet(false) }
        )
    }

    // ── To unit bottom sheet ─────────────────────────────────────────────────
    if (state.showToSheet) {
        UnitPickerSheet(
            title    = "To Unit",
            units    = UnitRegistry.units[state.category] ?: emptyList(),
            selected = state.toUnit,
            accent   = accent,
            onSelect = { vm.setToUnit(it) },
            onDismiss= { vm.showToSheet(false) }
        )
    }
}

// ── Category chip row ─────────────────────────────────────────────────────────
@Composable
private fun CategoryChipRow(
    selected: UnitCategory,
    accent: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selected.icon, fontSize = 18.sp)
                }
                Column {
                    Text(
                        "Category",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        selected.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Change category",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Main conversion card ──────────────────────────────────────────────────────
@Composable
private fun ConversionCard(
    fromUnit: UnitDef,
    toUnit: UnitDef,
    inputValue: String,
    resultValue: String,
    accent: Color,
    onInput: (String) -> Unit,
    onSwap: () -> Unit,
    onFromClick: () -> Unit,
    onToClick: () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            // FROM row
            UnitRow(
                label     = "From",
                unit      = fromUnit,
                value     = inputValue,
                accent    = accent,
                isInput   = true,
                onValue   = onInput,
                onUnitClick = onFromClick
            )

            // Swap button centred
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                FilledIconButton(
                    onClick = onSwap,
                    modifier = Modifier.size(38.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = accent,
                        contentColor   = Color.White
                    )
                ) {
                    Icon(Icons.Filled.SwapVert, "Swap", modifier = Modifier.size(20.dp))
                }
            }

            // TO row
            UnitRow(
                label     = "To",
                unit      = toUnit,
                value     = resultValue,
                accent    = accent,
                isInput   = false,
                onValue   = {},
                onUnitClick = onToClick
            )
        }
    }
}

// ── Single from/to row ────────────────────────────────────────────────────────
@Composable
private fun UnitRow(
    label: String,
    unit: UnitDef,
    value: String,
    accent: Color,
    isInput: Boolean,
    onValue: (String) -> Unit,
    onUnitClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Value field
            if (isInput) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValue,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            "0",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor          = accent
                    )
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = value.ifEmpty { "—" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (value.isEmpty())
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        else accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Unit selector button
            OutlinedCard(
                onClick = onUnitClick,
                shape  = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp, if (isInput) accent else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        unit.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isInput) accent else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        unit.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 72.dp)
                    )
                }
            }
        }
    }
}

// ── Formula hint ──────────────────────────────────────────────────────────────
@Composable
private fun FormulaHint(
    from: UnitDef,
    to: UnitDef,
    input: String,
    result: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("$input ${from.symbol}  =  ", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$result ${to.symbol}", style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold, color = accent)
    }
}

// ── Category picker sheet ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySheet(
    current: UnitCategory,
    onSelect: (UnitCategory) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(Modifier.padding(bottom = 32.dp)) {
            Text(
                "Choose Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp)
            ) {
                items(UnitCategory.values()) { cat ->
                    val isSelected = cat == current
                    val color = categoryColor[cat] ?: Primary
                    Card(
                        onClick = { onSelect(cat) },
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) color.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected)
                            androidx.compose.foundation.BorderStroke(1.5.dp, color)
                        else null
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(cat.icon, fontSize = 20.sp)
                            Text(
                                cat.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Unit picker sheet ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitPickerSheet(
    title: String,
    units: List<UnitDef>,
    selected: UnitDef,
    accent: Color,
    onSelect: (UnitDef) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(Modifier.padding(bottom = 32.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(units) { unit ->
                    val isSelected = unit.name == selected.name
                    Card(
                        onClick = { onSelect(unit) },
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) accent.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected)
                            androidx.compose.foundation.BorderStroke(1.5.dp, accent)
                        else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                unit.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) accent else MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) accent.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.background
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    unit.symbol,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accent
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}