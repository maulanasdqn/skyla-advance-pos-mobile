package com.skyla.pos.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.skyla.pos.categories.presentation.form.CategoryFormScreen
import com.skyla.pos.categories.presentation.list.CategoryListScreen
import com.skyla.pos.customers.presentation.detail.CustomerDetailScreen
import com.skyla.pos.customers.presentation.form.CustomerFormScreen
import com.skyla.pos.customers.presentation.list.CustomerListScreen
import com.skyla.pos.dashboard.presentation.DashboardScreen
import com.skyla.pos.inventory.presentation.adjustment.InventoryAdjustmentFormScreen
import com.skyla.pos.inventory.presentation.adjustment.InventoryAdjustmentListScreen
import com.skyla.pos.inventory.presentation.list.InventoryListScreen
import com.skyla.pos.model.SaleStatus
import com.skyla.pos.payments.presentation.PaymentScreen
import com.skyla.pos.products.presentation.detail.ProductDetailScreen
import com.skyla.pos.products.presentation.form.ProductFormScreen
import com.skyla.pos.products.presentation.list.ProductListScreen
import com.skyla.pos.reports.presentation.CashierPerformanceScreen
import com.skyla.pos.reports.presentation.DailySalesReportScreen
import com.skyla.pos.reports.presentation.InventoryReportScreen
import com.skyla.pos.reports.presentation.PaymentMethodsReportScreen
import com.skyla.pos.reports.presentation.ReportsDashboardScreen
import com.skyla.pos.reports.presentation.TopProductsReportScreen
import com.skyla.pos.sales.presentation.list.SalesListScreen
import com.skyla.pos.sales.presentation.pos.PosScreen
import com.skyla.pos.sales.presentation.receipt.ReceiptScreen
import com.skyla.pos.ui.MoreMenuScreen
import com.skyla.pos.ui.SettingsScreen
import com.skyla.pos.users.presentation.detail.UserDetailScreen
import com.skyla.pos.users.presentation.form.UserFormScreen
import com.skyla.pos.users.presentation.list.UserListScreen

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
                ProductListScreen(
                    onNavigateToDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToForm = { productId ->
                        navController.navigate(Screen.ProductForm.createRoute(productId))
                    },
                )
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            ) {
                ProductDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { productId ->
                        navController.navigate(Screen.ProductForm.createRoute(productId))
                    },
                )
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
                ProductFormScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onProductSaved = { navController.popBackStack() },
                )
            }

            // Categories
            composable(Screen.CategoryList.route) {
                CategoryListScreen(
                    onNavigateToForm = { categoryId ->
                        navController.navigate(Screen.CategoryForm.createRoute(categoryId))
                    },
                )
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
                CategoryFormScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCategorySaved = { navController.popBackStack() },
                )
            }

            // Customers
            composable(Screen.CustomerList.route) {
                CustomerListScreen(
                    onNavigateToDetail = { customerId ->
                        navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                    },
                    onNavigateToForm = { customerId ->
                        navController.navigate(Screen.CustomerForm.createRoute(customerId))
                    },
                )
            }

            composable(
                route = Screen.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.StringType }),
            ) {
                CustomerDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { customerId ->
                        navController.navigate(Screen.CustomerForm.createRoute(customerId))
                    },
                )
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
                CustomerFormScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCustomerSaved = { navController.popBackStack() },
                )
            }

            // Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            // Inventory
            composable(Screen.InventoryList.route) {
                InventoryListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdjustmentForm = {
                        navController.navigate(Screen.InventoryAdjustmentForm.route)
                    },
                    onNavigateToAdjustmentHistory = {
                        navController.navigate(Screen.InventoryAdjustmentList.route)
                    },
                )
            }

            composable(Screen.InventoryAdjustmentForm.route) {
                InventoryAdjustmentFormScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAdjustmentCreated = { navController.popBackStack() },
                )
            }

            composable(Screen.InventoryAdjustmentList.route) {
                InventoryAdjustmentListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToForm = {
                        navController.navigate(Screen.InventoryAdjustmentForm.route)
                    },
                )
            }

            // Users
            composable(Screen.UserList.route) {
                UserListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    },
                    onNavigateToForm = { userId ->
                        navController.navigate(Screen.UserForm.createRoute(userId))
                    },
                )
            }

            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            ) {
                UserDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { userId ->
                        navController.navigate(Screen.UserForm.createRoute(userId))
                    },
                )
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
                UserFormScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onUserSaved = { navController.popBackStack() },
                )
            }

            // Reports
            composable(Screen.ReportsDashboard.route) {
                ReportsDashboardScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDailySales = {
                        navController.navigate(Screen.DailySalesReport.route)
                    },
                    onNavigateToTopProducts = {
                        navController.navigate(Screen.TopProductsReport.route)
                    },
                    onNavigateToInventory = {
                        navController.navigate(Screen.InventoryReport.route)
                    },
                    onNavigateToCashierPerformance = {
                        navController.navigate(Screen.CashierPerformance.route)
                    },
                    onNavigateToPaymentMethods = {
                        navController.navigate(Screen.PaymentMethodsReport.route)
                    },
                )
            }

            composable(Screen.DailySalesReport.route) {
                DailySalesReportScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.TopProductsReport.route) {
                TopProductsReportScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.InventoryReport.route) {
                InventoryReportScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.CashierPerformance.route) {
                CashierPerformanceScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.PaymentMethodsReport.route) {
                PaymentMethodsReportScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
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

