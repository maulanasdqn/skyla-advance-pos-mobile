package com.skyla.pos.navigation

sealed class Screen(val route: String) {

    // Auth
    data object Login : Screen("login")

    // Sales
    data object SalesList : Screen("sales_list")
    data object Pos : Screen("pos?saleId={saleId}") {
        fun createRoute(saleId: String? = null): String {
            return if (saleId != null) "pos?saleId=$saleId" else "pos"
        }
    }

    // Payments
    data object Payment : Screen("payment/{saleId}") {
        fun createRoute(saleId: String) = "payment/$saleId"
    }
    data object Receipt : Screen("receipt/{saleId}") {
        fun createRoute(saleId: String) = "receipt/$saleId"
    }

    // Products
    data object ProductList : Screen("products")
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    data object ProductForm : Screen("product_form?productId={productId}") {
        fun createRoute(productId: String? = null): String {
            return if (productId != null) "product_form?productId=$productId" else "product_form"
        }
    }

    // Categories
    data object CategoryList : Screen("categories")
    data object CategoryForm : Screen("category_form?categoryId={categoryId}") {
        fun createRoute(categoryId: String? = null): String {
            return if (categoryId != null) "category_form?categoryId=$categoryId" else "category_form"
        }
    }

    // Customers
    data object CustomerList : Screen("customers")
    data object CustomerDetail : Screen("customer/{customerId}") {
        fun createRoute(customerId: String) = "customer/$customerId"
    }
    data object CustomerForm : Screen("customer_form?customerId={customerId}") {
        fun createRoute(customerId: String? = null): String {
            return if (customerId != null) "customer_form?customerId=$customerId" else "customer_form"
        }
    }

    // Dashboard
    data object Dashboard : Screen("dashboard")

    // Inventory
    data object InventoryList : Screen("inventory")
    data object InventoryAdjustmentForm : Screen("inventory_adjustment")
    data object InventoryAdjustmentList : Screen("inventory_adjustments")

    // Users
    data object UserList : Screen("users")
    data object UserDetail : Screen("user/{userId}") {
        fun createRoute(userId: String) = "user/$userId"
    }
    data object UserForm : Screen("user_form?userId={userId}") {
        fun createRoute(userId: String? = null): String {
            return if (userId != null) "user_form?userId=$userId" else "user_form"
        }
    }

    // Reports
    data object ReportsDashboard : Screen("reports")
    data object DailySalesReport : Screen("reports/daily_sales")
    data object TopProductsReport : Screen("reports/top_products")
    data object InventoryReport : Screen("reports/inventory")
    data object CashierPerformance : Screen("reports/cashier_performance")
    data object PaymentMethodsReport : Screen("reports/payment_methods")

    // Settings & Other
    data object ChangePassword : Screen("change_password")
    data object Settings : Screen("settings")
    data object MoreMenu : Screen("more")
}
