package com.smartcalc.ui.loan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.viewmodel.AmortizationRow
import com.smartcalc.viewmodel.LoanState
import com.smartcalc.viewmodel.LoanViewModel
import com.smartcalc.viewmodel.formatCurrency
import com.smartcalc.viewmodel.formatCurrencyFull

private val AccentBlue   = Color(0xFF0A84FF)
private val AccentOrange = Color(0xFFFF9F0A)
private val AccentGreen  = Color(0xFF30D158)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(
    onBack: () -> Unit,
    vm: LoanViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Loan Calculator", style = MaterialTheme.typography.titleMedium) },
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

            // ── Input card ───────────────────────────────────────────────────
            InputCard(state = state, vm = vm)

            // ── Results ──────────────────────────────────────────────────────
            if (state.emi != null) {
                EmiResultCard(state = state)
                DonutBreakdown(state = state)
                AmortizationSection(state = state, vm = vm)
            } else {
                Placeholder()
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Input card ────────────────────────────────────────────────────────────────
@Composable
private fun InputCard(state: LoanState, vm: LoanViewModel) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Loan amount
            LoanField(
                value   = state.loanAmount,
                onValue = { vm.setLoanAmount(it) },
                label   = "Loan Amount",
                prefix  = "₹",
                kb      = KeyboardType.Decimal
            )

            // Interest rate
            LoanField(
                value   = state.interestRate,
                onValue = { vm.setInterestRate(it) },
                label   = "Annual Interest Rate",
                suffix  = "% p.a.",
                kb      = KeyboardType.Decimal
            )

            // Tenure — years + months side by side
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LoanField(
                    value   = state.tenureYears,
                    onValue = { vm.setTenureYears(it) },
                    label   = "Years",
                    suffix  = "yr",
                    kb      = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                LoanField(
                    value   = state.tenureMonths,
                    onValue = { vm.setTenureMonths(it) },
                    label   = "Months",
                    suffix  = "mo",
                    kb      = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ── EMI result card ───────────────────────────────────────────────────────────
@Composable
private fun EmiResultCard(state: LoanState) {
    val animatedEmi by animateFloatAsState(
        targetValue  = state.emi!!.toFloat(),
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "emi_anim"
    )

    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = AccentBlue.copy(alpha = 0.10f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Monthly EMI",
                style = MaterialTheme.typography.labelMedium,
                color = AccentBlue.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                formatCurrencyFull(animatedEmi.toDouble()),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = AccentBlue
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = AccentBlue.copy(alpha = 0.15f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Principal",
                    value = formatCurrency(state.loanAmount.toDoubleOrNull() ?: 0.0),
                    color = AccentGreen
                )
                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color    = AccentBlue.copy(alpha = 0.15f)
                )
                SummaryItem(
                    label = "Total Interest",
                    value = formatCurrency(state.totalInterest ?: 0.0),
                    color = AccentOrange
                )
                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color    = AccentBlue.copy(alpha = 0.15f)
                )
                SummaryItem(
                    label = "Total Payment",
                    value = formatCurrency(state.totalPayment ?: 0.0),
                    color = AccentBlue
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Donut chart breakdown ─────────────────────────────────────────────────────
@Composable
private fun DonutBreakdown(state: LoanState) {
    val animPrincipal by animateFloatAsState(
        targetValue = state.principalPct,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "principal_anim"
    )

    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Donut
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        val stroke      = 18.dp.toPx()
                        val inset       = stroke / 2
                        val arcSize     = Size(size.width - stroke, size.height - stroke)
                        val topLeft     = Offset(inset, inset)
                        val principalSweep = animPrincipal * 360f
                        val interestSweep  = (1f - animPrincipal) * 360f

                        // Principal (green) — start from top
                        drawArc(
                            color      = AccentGreen,
                            startAngle = -90f,
                            sweepAngle = principalSweep,
                            useCenter  = false,
                            style      = Stroke(stroke, cap = StrokeCap.Butt),
                            topLeft    = topLeft,
                            size       = arcSize
                        )
                        // Interest (orange)
                        drawArc(
                            color      = AccentOrange,
                            startAngle = -90f + principalSweep,
                            sweepAngle = interestSweep,
                            useCenter  = false,
                            style      = Stroke(stroke, cap = StrokeCap.Butt),
                            topLeft    = topLeft,
                            size       = arcSize
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(animPrincipal * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    Text(
                        "principal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LegendRow(
                    color = AccentGreen,
                    label = "Principal",
                    value = formatCurrencyFull(state.loanAmount.toDoubleOrNull() ?: 0.0),
                    pct   = "${(state.principalPct * 100).toInt()}%"
                )
                LegendRow(
                    color = AccentOrange,
                    label = "Interest",
                    value = formatCurrencyFull(state.totalInterest ?: 0.0),
                    pct   = "${(state.interestPct * 100).toInt()}%"
                )
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String, value: String, pct: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    pct,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Amortization schedule ─────────────────────────────────────────────────────
@Composable
private fun AmortizationSection(state: LoanState, vm: LoanViewModel) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Amortization Schedule",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${state.schedule.size} months",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            // Column headers
            AmortizationHeader()
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Show first 6 rows always
            val preview = state.schedule.take(6)
            preview.forEach { row ->
                AmortizationRowItem(row = row)
            }

            // Expand / collapse rest
            if (state.schedule.size > 6) {
                AnimatedVisibility(
                    visible = state.showFullSchedule,
                    enter = fadeIn() + expandVertically(),
                    exit  = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        state.schedule.drop(6).forEach { row ->
                            AmortizationRowItem(row = row)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick  = { vm.toggleFullSchedule() },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = AccentBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue)
                ) {
                    Icon(
                        if (state.showFullSchedule) Icons.Filled.ExpandLess
                        else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (state.showFullSchedule) "Show Less"
                        else "View All ${state.schedule.size} Months",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun AmortizationHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("Mo.",      modifier = Modifier.width(32.dp),  style = headerStyle(), textAlign = TextAlign.Start)
        Text("EMI",      modifier = Modifier.weight(1f),    style = headerStyle(), textAlign = TextAlign.End)
        Text("Principal",modifier = Modifier.weight(1.2f),  style = headerStyle(), textAlign = TextAlign.End)
        Text("Interest", modifier = Modifier.weight(1f),    style = headerStyle(), textAlign = TextAlign.End)
        Text("Balance",  modifier = Modifier.weight(1.2f),  style = headerStyle(), textAlign = TextAlign.End)
    }
}

@Composable
private fun headerStyle() = MaterialTheme.typography.labelSmall.copy(
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontWeight = FontWeight.SemiBold
)

@Composable
private fun AmortizationRowItem(row: AmortizationRow) {
    val isEven = row.month % 2 == 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isEven) MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                else Color.Transparent
            )
            .padding(vertical = 6.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${row.month}",
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            compactAmount(row.emi),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            color = AccentBlue,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            compactAmount(row.principal),
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            color = AccentGreen
        )
        Text(
            compactAmount(row.interest),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            color = AccentOrange
        )
        Text(
            compactAmount(row.balance),
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Compact number format for table: 1,50,000 → 1.5L
private fun compactAmount(v: Double): String {
    return when {
        v >= 1_00_00_000 -> "${"%.1f".format(v / 1_00_00_000)}Cr"
        v >= 1_00_000    -> "${"%.1f".format(v / 1_00_000)}L"
        v >= 1_000       -> "${"%.1f".format(v / 1_000)}K"
        else             -> "%.0f".format(v)
    }
}

// ── Placeholder ───────────────────────────────────────────────────────────────
@Composable
private fun Placeholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🏦", fontSize = 44.sp)
            Text(
                "Enter loan details above\nto calculate your EMI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Reusable field ────────────────────────────────────────────────────────────
@Composable
private fun LoanField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    prefix: String? = null,
    suffix: String? = null,
    kb: KeyboardType = KeyboardType.Decimal,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label  = { Text(label) },
        prefix = prefix?.let { { Text(it, color = AccentBlue, fontWeight = FontWeight.Bold) } },
        suffix = suffix?.let { { Text(it, color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp) } },
        keyboardOptions = KeyboardOptions(keyboardType = kb),
        singleLine = true,
        shape  = RoundedCornerShape(12.dp),
        textStyle = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            focusedLabelColor  = AccentBlue,
            cursorColor        = AccentBlue
        ),
        modifier = modifier
    )
}
