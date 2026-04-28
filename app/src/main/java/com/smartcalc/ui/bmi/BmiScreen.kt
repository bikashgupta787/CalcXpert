package com.smartcalc.ui.bmi

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcalc.viewmodel.BmiCategory
import com.smartcalc.viewmodel.BmiUnit
import com.smartcalc.viewmodel.BmiViewModel

// ── Segment colours (left → right on scale) ───────────────────────────────────
private val scaleSegments = listOf(
    Color(0xFFEF5350),  // Severely Underweight
    Color(0xFFFF7043),  // Moderately Underweight
    Color(0xFFFFCA28),  // Mildly Underweight
    Color(0xFF66BB6A),  // Normal
    Color(0xFFFFCA28),  // Overweight
    Color(0xFFFF7043),  // Obese I
    Color(0xFFEF5350),  // Obese II
    Color(0xFFB71C1C),  // Obese III
)

private fun bmiCategoryColor(cat: BmiCategory) = Color(cat.colorHex)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(
    onBack: () -> Unit,
    vm: BmiViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("BMI Calculator", style = MaterialTheme.typography.titleMedium) },
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

            // ── Unit toggle ──────────────────────────────────────────────────
            UnitToggle(
                selected = state.unit,
                onSelect = { vm.setUnit(it) }
            )

            // ── Input fields ─────────────────────────────────────────────────
            if (state.unit == BmiUnit.METRIC) {
                MetricInputs(
                    heightCm = state.heightCm,
                    weightKg = state.weightKg,
                    onHeight = { vm.setHeightCm(it) },
                    onWeight = { vm.setWeightKg(it) }
                )
            } else {
                ImperialInputs(
                    heightFt = state.heightFt,
                    heightIn = state.heightIn,
                    weightLb = state.weightLb,
                    onFt     = { vm.setHeightFt(it) },
                    onIn     = { vm.setHeightIn(it) },
                    onWeight = { vm.setWeightLb(it) }
                )
            }

            // ── BMI Result ───────────────────────────────────────────────────
            if (state.bmi != null && state.category != null) {
                BmiResultCard(
                    bmi      = state.bmi!!,
                    category = state.category!!,
                    scalePos = state.scalePosition
                )
                BmiScaleBar(scalePos = state.scalePosition, category = state.category!!)
                BmiCategoryTable(current = state.category!!)
            } else {
                BmiPlaceholder()
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Unit toggle ───────────────────────────────────────────────────────────────
@Composable
private fun UnitToggle(selected: BmiUnit, onSelect: (BmiUnit) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        listOf(BmiUnit.METRIC to "Metric  (kg / cm)", BmiUnit.IMPERIAL to "Imperial  (lb / ft)").forEach { (unit, label) ->
            val isSelected = selected == unit
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF0A84FF) else Color.Transparent)
                    .then(Modifier.clickableNoRipple { onSelect(unit) })
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Metric inputs ─────────────────────────────────────────────────────────────
@Composable
private fun MetricInputs(
    heightCm: String, weightKg: String,
    onHeight: (String) -> Unit, onWeight: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BmiTextField(
            value = heightCm, onValue = onHeight,
            label = "Height", suffix = "cm",
            modifier = Modifier.weight(1f)
        )
        BmiTextField(
            value = weightKg, onValue = onWeight,
            label = "Weight", suffix = "kg",
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Imperial inputs ───────────────────────────────────────────────────────────
@Composable
private fun ImperialInputs(
    heightFt: String, heightIn: String, weightLb: String,
    onFt: (String) -> Unit, onIn: (String) -> Unit, onWeight: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BmiTextField(
            value = heightFt, onValue = onFt,
            label = "Feet", suffix = "ft",
            modifier = Modifier.weight(1f)
        )
        BmiTextField(
            value = heightIn, onValue = onIn,
            label = "Inches", suffix = "in",
            modifier = Modifier.weight(1f)
        )
        BmiTextField(
            value = weightLb, onValue = onWeight,
            label = "Weight", suffix = "lb",
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Reusable text field ───────────────────────────────────────────────────────
@Composable
private fun BmiTextField(
    value: String, onValue: (String) -> Unit,
    label: String, suffix: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        suffix = {
            Text(
                suffix,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Bold
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Color(0xFF0A84FF),
            focusedLabelColor    = Color(0xFF0A84FF),
            cursorColor          = Color(0xFF0A84FF)
        ),
        modifier = modifier
    )
}

// ── BMI result card ───────────────────────────────────────────────────────────
@Composable
private fun BmiResultCard(bmi: Double, category: BmiCategory, scalePos: Float) {
    val color = bmiCategoryColor(category)

    // Animate BMI value counting up
    val animatedBmi by animateFloatAsState(
        targetValue = bmi.toFloat(),
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "bmi_anim"
    )

    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.10f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left – big BMI number
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Your BMI",
                    style = MaterialTheme.typography.labelMedium,
                    color = color.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "%.1f".format(animatedBmi),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    lineHeight = 56.sp
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        category.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            // Right – radial gauge
            BmiGauge(bmi = bmi.toFloat(), color = color)
        }
    }
}

// ── Circular gauge ─────────────────────────────────────────────────────────────
@Composable
private fun BmiGauge(bmi: Float, color: Color) {
    val animatedSweep by animateFloatAsState(
        targetValue = ((bmi.coerceIn(10f, 45f) - 10f) / 35f) * 240f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "gauge"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .size(100.dp)
            .drawBehind {
                val stroke = 10.dp.toPx()
                val startAngle = 150f
                // Track
                drawArc(
                    color = trackColor,
                    startAngle = startAngle,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(stroke, cap = StrokeCap.Round),
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke)
                )
                // Fill
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    style = Stroke(stroke, cap = StrokeCap.Round),
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "%.1f".format(bmi),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                "BMI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Horizontal gradient scale bar ─────────────────────────────────────────────
@Composable
private fun BmiScaleBar(scalePos: Float, category: BmiCategory) {
    val animatedPos by animateFloatAsState(
        targetValue = scalePos,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "scale_pos"
    )
    val color = bmiCategoryColor(category)

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "BMI Scale",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))

            // Gradient bar + indicator
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val barWidth = maxWidth

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(scaleSegments)
                        )
                )

                // Indicator dot
                val dotSize = 20.dp
                val offsetX = (barWidth - dotSize) * animatedPos

                Box(
                    modifier = Modifier
                        .offset(x = offsetX)
                        .size(dotSize)
                        .offset(y = (-3).dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Scale labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("10", "16", "18.5", "25", "30", "35", "40", "45+").forEach { label ->
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Category table ─────────────────────────────────────────────────────────────
@Composable
private fun BmiCategoryTable(current: BmiCategory) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "BMI Categories",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            BmiCategory.values().forEach { cat ->
                val isActive = cat == current
                val color    = bmiCategoryColor(cat)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isActive) color.copy(alpha = 0.12f) else Color.Transparent
                        )
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            cat.label,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) color
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        cat.range,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) color
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Advice row only for active category
                if (isActive) {
                    Text(
                        cat.advice,
                        style = MaterialTheme.typography.bodySmall,
                        color = color.copy(alpha = 0.85f),
                        modifier = Modifier.padding(start = 28.dp, bottom = 4.dp)
                    )
                }

                if (cat != BmiCategory.values().last()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

// ── Placeholder when no input yet ─────────────────────────────────────────────
@Composable
private fun BmiPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("⚖️", fontSize = 40.sp)
            Text(
                "Enter your height & weight\nto see your BMI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Utility ───────────────────────────────────────────────────────────────────
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit) = this.then(
    Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
)