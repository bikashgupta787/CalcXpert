package com.smartcalc.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcalc.ui.navigation.Screen
import com.smartcalc.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.MonitorWeight



// ── Data model ────────────────────────────────────────────────────────────────
data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val route: String,
    val isAvailable: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val tools = listOf(
        ToolItem(
            "Basic Calculator", "Arithmetic & expressions",
            Icons.Filled.Calculate, Primary,
            Screen.BasicCalculator.route
        ),
        ToolItem(
            "Unit Converter", "Length, weight, volume & more",
            Icons.Filled.Straighten, Color(0xFF30D158),
            Screen.UnitConverter.route
        ),
        ToolItem(
            "Temperature", "°C · °F · K",
            Icons.Filled.Thermostat, Color(0xFFFF6B35),
            Screen.TemperatureConverter.route
        ),
        ToolItem(
            "Date Calculator", "Days between dates",
            Icons.Filled.DateRange, Color(0xFF64D2FF),
            Screen.DateCalculator.route
        ),
        ToolItem(
            "BMI Calculator", "Body mass index",
            Icons.Filled.MonitorWeight, Color(0xFF30D158),
            Screen.BmiCalculator.route
        ),
        ToolItem(
            "Currency Converter", "Live exchange rates",
            Icons.Filled.CurrencyExchange, Color(0xFFFF9F0A),
            Screen.CurrencyConverter.route, isAvailable = false
        ),
        ToolItem(
            "Discount Calculator", "Price & savings",
            Icons.Filled.LocalOffer, Color(0xFFFF375F),
            Screen.DiscountCalc.route
        ),
        ToolItem(
            "Tip Calculator", "Split bills easily",
            Icons.Filled.Restaurant, Color(0xFFAC8FFF),
            Screen.TipCalculator.route, isAvailable = false
        ),
        ToolItem(
            "Loan Calculator", "EMI & interest",
            Icons.Filled.AccountBalance, Color(0xFFFFD60A),
            Screen.LoanCalculator.route
        ),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ──────────────────────────────────────────────────────
            HomeHeader()

            // ── Quick access — available tools ──────────────────────────────
            val available = tools.filter { it.isAvailable }
            val coming    = tools.filter { !it.isAvailable }

            if (available.isNotEmpty()) {
                SectionLabel("Ready to use")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 1200.dp)   // constrain inside scroll
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false        // outer scroll handles it
                ) {
                    itemsIndexed(available) { idx, tool ->
                        AnimatedToolCard(tool = tool, index = idx) {
                            navController.navigate(tool.route)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            if (coming.isNotEmpty()) {
                SectionLabel("Coming soon")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 1200.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(coming) { idx, tool ->
                        AnimatedToolCard(tool = tool, index = idx, dimmed = true) {}
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(top = 16.dp)
    ) {
        Column {
            Text(
                text = "SmartCalc",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "All-in-one calculator toolkit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Decorative gradient pill
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(Primary, Color(0xFF30D158)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Calculate,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }

    Spacer(Modifier.height(8.dp))
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )
    Spacer(Modifier.height(16.dp))
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.4.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

// ── Tool card with entrance animation ────────────────────────────────────────
@Composable
private fun AnimatedToolCard(
    tool: ToolItem,
    index: Int,
    dimmed: Boolean = false,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 60L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(300, easing = EaseOutCubic)
        )
    ) {
        ToolCard(tool = tool, dimmed = dimmed, onClick = onClick)
    }
}

// ── Individual card ───────────────────────────────────────────────────────────
@Composable
private fun ToolCard(
    tool: ToolItem,
    dimmed: Boolean = false,
    onClick: () -> Unit
) {
    val alpha = if (dimmed) 0.45f else 1f
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = onClick,
        enabled = !dimmed,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon in accent-coloured circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tool.accentColor.copy(alpha = if (dimmed) 0.25f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.accentColor.copy(alpha = alpha),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tool.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                    maxLines = 2
                )
            }
        }
    }
}
