package com.skyla.pos.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skyla.pos.auth.presentation.login.LoginScreen
import com.skyla.pos.auth.presentation.password.ChangePasswordScreen
import com.skyla.pos.model.SaleStatus
import com.skyla.pos.payments.presentation.PaymentScreen
import com.skyla.pos.sales.presentation.list.SalesListScreen
import com.skyla.pos.sales.presentation.pos.PosScreen
import com.skyla.pos.sales.presentation.receipt.ReceiptScreen
import com.skyla.pos.ui.MoreMenuScreen
import com.skyla.pos.ui.SettingsScreen

@Composable
fun SkylaApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine which routes show the bottom bar
    val bottomBarRoutes = bottomNavItems.map { it.route }
    val showBottomBar = currentRoute in bottomBarRoutes

    val startDestination = Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
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
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
        ) {
            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.SalesList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                )
            }

            // Sales
            composable(Screen.SalesList.route) {
                SalesListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaleClick = { saleId, status ->
                        if (status == SaleStatus.DRAFT) {
                            navController.navigate(Screen.Pos.createRoute(saleId))
                        } else {
                            navController.navigate(Screen.Receipt.createRoute(saleId))
                        }
                    },
                    onCreateSale = {
                        navController.navigate(Screen.Pos.createRoute())
                    },
                )
            }

            composable(
                route = Screen.Pos.route,
                arguments = listOf(
                    navArgument("saleId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                PosScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPayment = { saleId ->
                        navController.navigate(Screen.Payment.createRoute(saleId))
                    },
                )
            }

            // Payments
            composable(
                route = Screen.Payment.route,
                arguments = listOf(navArgument("saleId") { type = NavType.StringType }),
            ) {
                PaymentScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaleCompleted = { saleId ->
                        navController.navigate(Screen.Receipt.createRoute(saleId)) {
                            popUpTo(Screen.SalesList.route) { inclusive = false }
                        }
                    },
                )
            }

            composable(
                route = Screen.Receipt.route,
                arguments = listOf(navArgument("saleId") { type = NavType.StringType }),
            ) {
                ReceiptScreen(
                    onNavigateBack = {
                        navController.popBackStack(Screen.SalesList.route, inclusive = false)
                    },
                )
            }

            // Products
            composable(Screen.ProductList.route) {
                PlaceholderScreen(title = "Products")
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            ) {
                PlaceholderScreen(title = "Product Detail")
            }

            composable(
                route = Screen.ProductForm.route,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                PlaceholderScreen(title = "Product Form")
            }

            // Categories
            composable(Screen.CategoryList.route) {
                PlaceholderScreen(title = "Categories")
            }

            composable(
                route = Screen.CategoryForm.route,
                arguments = listOf(
                    navArgument("categoryId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                PlaceholderScreen(title = "Category Form")
            }

            // Customers
            composable(Screen.CustomerList.route) {
                PlaceholderScreen(title = "Customers")
            }

            composable(
                route = Screen.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.StringType }),
            ) {
                PlaceholderScreen(title = "Customer Detail")
            }

            composable(
                route = Screen.CustomerForm.route,
                arguments = listOf(
                    navArgument("customerId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                PlaceholderScreen(title = "Customer Form")
            }

            // Dashboard
            composable(Screen.Dashboard.route) {
                PlaceholderScreen(title = "Dashboard")
            }

            // Inventory
            composable(Screen.InventoryList.route) {
                PlaceholderScreen(title = "Inventory")
            }

            composable(Screen.InventoryAdjustmentForm.route) {
                PlaceholderScreen(title = "Inventory Adjustment")
            }

            composable(Screen.InventoryAdjustmentList.route) {
                PlaceholderScreen(title = "Inventory Adjustments")
            }

            // Users
            composable(Screen.UserList.route) {
                PlaceholderScreen(title = "Users")
            }

            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            ) {
                PlaceholderScreen(title = "User Detail")
            }

            composable(
                route = Screen.UserForm.route,
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                PlaceholderScreen(title = "User Form")
            }

            // Reports
            composable(Screen.ReportsDashboard.route) {
                PlaceholderScreen(title = "Reports")
            }

            composable(Screen.DailySalesReport.route) {
                PlaceholderScreen(title = "Daily Sales Report")
            }

            composable(Screen.TopProductsReport.route) {
                PlaceholderScreen(title = "Top Products Report")
            }

            composable(Screen.InventoryReport.route) {
                PlaceholderScreen(title = "Inventory Report")
            }

            composable(Screen.CashierPerformance.route) {
                PlaceholderScreen(title = "Cashier Performance")
            }

            composable(Screen.PaymentMethodsReport.route) {
                PlaceholderScreen(title = "Payment Methods Report")
            }

            // Change Password
            composable(Screen.ChangePassword.route) {
                ChangePasswordScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onPasswordChanged = { navController.popBackStack() },
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }

            // More Menu
            composable(Screen.MoreMenu.route) {
                MoreMenuScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route)
                    },
                    onNavigateToCategories = {
                        navController.navigate(Screen.CategoryList.route)
                    },
                    onNavigateToInventory = {
                        navController.navigate(Screen.InventoryList.route)
                    },
                    onNavigateToUsers = {
                        navController.navigate(Screen.UserList.route)
                    },
                    onNavigateToReports = {
                        navController.navigate(Screen.ReportsDashboard.route)
                    },
                    onNavigateToChangePassword = {
                        navController.navigate(Screen.ChangePassword.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
