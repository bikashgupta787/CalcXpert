package com.smartcalc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartcalc.ui.calculator.BasicCalculatorScreen
import com.smartcalc.ui.temperature.TemperatureScreen
import com.smartcalc.ui.datecalc.DateCalculatorScreen
import com.smartcalc.ui.home.HomeScreen
import com.smartcalc.ui.unitconverter.UnitConverterScreen
import com.smartcalc.ui.bmi.BmiScreen
import com.smartcalc.ui.discount.DiscountScreen
import com.smartcalc.ui.loan.LoanScreen



// ── Route constants ───────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Home            : Screen("home")
    object BasicCalculator : Screen("basic_calculator")
    object UnitConverter   : Screen("unit_converter")
    object CurrencyConverter: Screen("currency_converter")
    object DiscountCalc    : Screen("discount_calculator")
    object TipCalculator   : Screen("tip_calculator")
    object DateCalculator  : Screen("date_calculator")
    object LoanCalculator  : Screen("loan_calculator")
    object TemperatureConverter : Screen("temperature_converter")

    object BmiCalculator : Screen("bmi_calculator")

}

// ── Nav graph ─────────────────────────────────────────────────────────────────
@Composable
fun SmartCalcNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.BasicCalculator.route) {
            BasicCalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.DateCalculator.route) {
            DateCalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.TemperatureConverter.route) {
            TemperatureScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.UnitConverter.route) {
            UnitConverterScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.BmiCalculator.route) {
            BmiScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.DiscountCalc.route) {
            DiscountScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.LoanCalculator.route) {
            LoanScreen(onBack = { navController.popBackStack() })
        }

        // Placeholder composables for future screens
        composable(Screen.CurrencyConverter.route) {
            PlaceholderScreen(title = "Currency Converter", onBack = { navController.popBackStack() })
        }
        composable(Screen.TipCalculator.route) {
            PlaceholderScreen(title = "Tip Calculator", onBack = { navController.popBackStack() })
        }
    }
}
