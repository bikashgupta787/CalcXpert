package com.smartcalc.ui.discount

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.viewmodel.DiscountMode
import com.smartcalc.viewmodel.DiscountState
import com.smartcalc.viewmodel.DiscountViewModel

private val Accent = Color(0xFFFF375F)

// Mode metadata
private data class ModeInfo(
    val mode: DiscountMode,
    val label: String,
    val icon: ImageVector,
    val description: String
)

private val modes = listOf(
    ModeInfo(
        DiscountMode.PERCENT_OFF,
        "Discount %",
        Icons.Filled.LocalOffer,
        "Enter price & discount % to get final price"
    ),
    ModeInfo(
        DiscountMode.FIND_PERCENT,
        "Find %",
        Icons.Filled.Search,
        "Enter original & final price to find discount %"
    ),
    ModeInfo(
        DiscountMode.REVERSE_CALC,
        "Original Price",
        Icons.Filled.Undo,
        "Enter final price & discount % to find original"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountScreen(
    onBack: () -> Unit,
    vm: DiscountViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Discount Calculator", style = MaterialTheme.typography.titleMedium) },
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

            // ── Mode selector cards ──────────────────────────────────────────
            ModeSelector(selected = state.mode, onSelect = { vm.setMode(it) })

            // ── Inputs + Results animated per mode ───────────────────────────
            AnimatedContent(
                targetState = state.mode,
                transitionSpec = {
                    fadeIn(tween(220)) + slideInVertically { it / 8 } togetherWith
                            fadeOut(tween(150))
                },
                label = "mode_content"
            ) { mode ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (mode) {
                        DiscountMode.PERCENT_OFF  -> PercentOffSection(state, vm)
                        DiscountMode.FIND_PERCENT -> FindPercentSection(state, vm)
                        DiscountMode.REVERSE_CALC -> ReverseSection(state, vm)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Mode selector ─────────────────────────────────────────────────────────────
@Composable
private fun ModeSelector(selected: DiscountMode, onSelect: (DiscountMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        modes.forEach { info ->
            val isSelected = selected == info.mode
            Card(
                onClick = { onSelect(info.mode) },
                shape  = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Accent.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (isSelected)
                    androidx.compose.foundation.BorderStroke(1.5.dp, Accent)
                else null,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        info.icon,
                        contentDescription = null,
                        tint = if (isSelected) Accent
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        info.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Accent
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Description line
    val desc = modes.first { it.mode == selected }.description
    Text(
        desc,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

// ── MODE 1: Percent Off ───────────────────────────────────────────────────────
@Composable
private fun PercentOffSection(state: DiscountState, vm: DiscountViewModel) {
    InputCard {
        DiscountField(
            value    = state.originalPrice,
            onValue  = { vm.setOriginalPrice(it) },
            label    = "Original Price",
            prefix   = "₹"
        )
        DiscountField(
            value    = state.discountPct,
            onValue  = { vm.setDiscountPct(it) },
            label    = "Discount",
            suffix   = "%"
        )
    }

    val hasResult = state.resultFinalPrice.isNotEmpty()
    if (hasResult) {
        // Big savings banner
        SavingsBanner(savings = state.resultSavings, pct = state.discountPct)

        ResultCard {
            ResultRow(label = "Final Price",  value = "₹ ${state.resultFinalPrice}", highlight = true)
            ResultDivider()
            ResultRow(label = "You Save",     value = "₹ ${state.resultSavings}")
            ResultDivider()
            ResultRow(label = "Original",     value = "₹ ${state.originalPrice}")
        }
    } else {
        InputPlaceholder()
    }
}

// ── MODE 2: Find Percent ──────────────────────────────────────────────────────
@Composable
private fun FindPercentSection(state: DiscountState, vm: DiscountViewModel) {
    InputCard {
        DiscountField(
            value   = state.originalPrice,
            onValue = { vm.setOriginalPrice(it) },
            label   = "Original Price",
            prefix  = "₹"
        )
        DiscountField(
            value   = state.finalPrice,
            onValue = { vm.setFinalPrice(it) },
            label   = "Final / Sale Price",
            prefix  = "₹"
        )
    }

    val hasResult = state.resultDiscountPct.isNotEmpty()
    if (hasResult) {
        SavingsBanner(savings = state.resultSavings, pct = state.resultDiscountPct)

        ResultCard {
            ResultRow(label = "Discount %",  value = "${state.resultDiscountPct}% OFF", highlight = true)
            ResultDivider()
            ResultRow(label = "You Save",    value = "₹ ${state.resultSavings}")
            ResultDivider()
            ResultRow(label = "Original",    value = "₹ ${state.originalPrice}")
        }
    } else {
        InputPlaceholder()
    }
}

// ── MODE 3: Reverse (find original) ──────────────────────────────────────────
@Composable
private fun ReverseSection(state: DiscountState, vm: DiscountViewModel) {
    InputCard {
        DiscountField(
            value   = state.finalPrice,
            onValue = { vm.setFinalPrice(it) },
            label   = "Sale / Final Price",
            prefix  = "₹"
        )
        DiscountField(
            value   = state.discountPct,
            onValue = { vm.setDiscountPct(it) },
            label   = "Discount Applied",
            suffix  = "%"
        )
    }

    val hasResult = state.resultOriginalPrice.isNotEmpty()
    if (hasResult) {
        SavingsBanner(savings = state.resultSavings, pct = state.discountPct)

        ResultCard {
            ResultRow(label = "Original Price", value = "₹ ${state.resultOriginalPrice}", highlight = true)
            ResultDivider()
            ResultRow(label = "You Saved",      value = "₹ ${state.resultSavings}")
            ResultDivider()
            ResultRow(label = "Sale Price",     value = "₹ ${state.finalPrice}")
        }
    } else {
        InputPlaceholder()
    }
}

// ── Savings banner ────────────────────────────────────────────────────────────
@Composable
private fun SavingsBanner(savings: String, pct: String) {
    val animScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "banner_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Accent.copy(alpha = 0.10f))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Filled.LocalOffer,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "$pct% OFF",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Accent
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "You save  ₹ $savings",
                style = MaterialTheme.typography.bodyMedium,
                color = Accent.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Input card wrapper ────────────────────────────────────────────────────────
@Composable
private fun InputCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

// ── Result card wrapper ───────────────────────────────────────────────────────
@Composable
private fun ResultCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// ── Single result row ─────────────────────────────────────────────────────────
@Composable
private fun ResultRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = if (highlight) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.bodyLarge,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (highlight) Accent else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResultDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

// ── Text field ────────────────────────────────────────────────────────────────
@Composable
private fun DiscountField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    prefix: String? = null,
    suffix: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        prefix = prefix?.let { { Text(it, color = Accent, fontWeight = FontWeight.Bold) } },
        suffix = suffix?.let { { Text(it, color = Accent, fontWeight = FontWeight.Bold) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        textStyle = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            focusedLabelColor  = Accent,
            cursorColor        = Accent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// ── Placeholder ───────────────────────────────────────────────────────────────
@Composable
private fun InputPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🏷️", fontSize = 36.sp)
            Text(
                "Fill in the fields above\nto calculate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
