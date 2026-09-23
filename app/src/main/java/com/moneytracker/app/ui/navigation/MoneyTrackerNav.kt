package com.moneytracker.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneytracker.app.di.AppContainer
import com.moneytracker.app.ui.budget.BudgetScreen
import com.moneytracker.app.ui.budget.BudgetViewModel
import com.moneytracker.app.ui.categories.CategoriesScreen
import com.moneytracker.app.ui.categories.CategoriesViewModel
import com.moneytracker.app.ui.expense.ExpenseEditorScreen
import com.moneytracker.app.ui.expense.ExpenseEditorViewModel
import com.moneytracker.app.ui.home.HomeScreen
import com.moneytracker.app.ui.home.HomeViewModel
import com.moneytracker.app.ui.more.MoreScreen
import com.moneytracker.app.ui.reports.ReportsScreen
import com.moneytracker.app.ui.reports.ReportsViewModel

private object Routes {
    const val Home = "home"
    const val Reports = "reports"
    const val More = "more"
    const val AddExpense = "expense/new"
    const val EditExpense = "expense/{expenseId}"
    const val Categories = "categories"
    const val Budget = "budget"

    fun editExpense(id: Long) = "expense/$id"
}

@Composable
fun MoneyTrackerNav(container: AppContainer) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = MoneyBottomTabs.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                MoneyBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.Home) {
                val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(container))
                HomeScreen(
                    viewModel = vm,
                    onAddExpense = { navController.navigate(Routes.AddExpense) },
                    onEditExpense = { id -> navController.navigate(Routes.editExpense(id)) },
                )
            }
            composable(Routes.Reports) {
                val vm: ReportsViewModel = viewModel(factory = ReportsViewModel.Factory(container))
                ReportsScreen(viewModel = vm)
            }
            composable(Routes.More) {
                MoreScreen(
                    container = container,
                    onCategories = { navController.navigate(Routes.Categories) },
                    onBudget = { navController.navigate(Routes.Budget) },
                )
            }
            composable(Routes.AddExpense) {
                val vm: ExpenseEditorViewModel = viewModel(
                    factory = ExpenseEditorViewModel.Factory(container, expenseId = null),
                )
                ExpenseEditorScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.EditExpense,
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("expenseId")
                val vm: ExpenseEditorViewModel = viewModel(
                    factory = ExpenseEditorViewModel.Factory(container, expenseId = id),
                )
                ExpenseEditorScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() },
                )
            }
            composable(Routes.Categories) {
                val vm: CategoriesViewModel = viewModel(factory = CategoriesViewModel.Factory(container))
                CategoriesScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.Budget) {
                val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(container))
                BudgetScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
