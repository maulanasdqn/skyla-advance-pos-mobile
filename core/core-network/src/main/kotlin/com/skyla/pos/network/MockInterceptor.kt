package com.skyla.pos.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {

    private val jsonMediaType = "application/json".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val (code, body) = route(method, path)

        return Response.Builder()
            .code(code)
            .message("OK")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(body.toResponseBody(jsonMediaType))
            .build()
    }

    private fun route(method: String, path: String): Pair<Int, String> {
        return when {
            // Auth
            method == "POST" && path.endsWith("/auth/login") -> 200 to loginResponse()
            method == "POST" && path.endsWith("/auth/refresh") -> 200 to refreshTokenResponse()
            method == "POST" && path.endsWith("/auth/logout") -> 200 to messageResponse("Logged out successfully")
            method == "POST" && path.endsWith("/auth/logout-all") -> 200 to messageResponse("All sessions logged out")
            method == "POST" && path.endsWith("/auth/password/change") -> 200 to messageResponse("Password changed successfully")

            // Products
            method == "GET" && path.matches(Regex(".*/products/barcode/.*")) -> 200 to singleProduct(0)
            method == "GET" && path.matches(Regex(".*/products/sku/.*")) -> 200 to singleProduct(0)
            method == "GET" && path.matches(Regex(".*/products/[^/]+$")) && !path.endsWith("/products") -> 200 to singleProduct(0)
            method == "GET" && path.endsWith("/products") -> 200 to productListResponse()
            method == "POST" && path.endsWith("/products") -> 201 to singleProduct(0)
            method == "PUT" && path.matches(Regex(".*/products/[^/]+$")) -> 200 to singleProduct(0)
            method == "DELETE" && path.matches(Regex(".*/products/[^/]+$")) -> 200 to messageResponse("Product deleted")
            method == "POST" && path.matches(Regex(".*/products/[^/]+/deactivate")) -> 200 to messageResponse("Product deactivated")

            // Categories
            method == "GET" && path.matches(Regex(".*/categories/[^/]+$")) && !path.endsWith("/categories") -> 200 to singleCategory(0)
            method == "GET" && path.endsWith("/categories") -> 200 to categoryListResponse()
            method == "POST" && path.endsWith("/categories") -> 201 to singleCategory(0)
            method == "PUT" && path.matches(Regex(".*/categories/[^/]+$")) -> 200 to singleCategory(0)
            method == "DELETE" && path.matches(Regex(".*/categories/[^/]+$")) -> 200 to messageResponse("Category deleted")
            method == "POST" && path.matches(Regex(".*/categories/[^/]+/deactivate")) -> 200 to messageResponse("Category deactivated")

            // Customers
            method == "GET" && path.matches(Regex(".*/customers/[^/]+$")) && !path.endsWith("/customers") -> 200 to singleCustomer(0)
            method == "GET" && path.endsWith("/customers") -> 200 to customerListResponse()
            method == "POST" && path.endsWith("/customers") -> 201 to singleCustomer(0)
            method == "PUT" && path.matches(Regex(".*/customers/[^/]+$")) -> 200 to singleCustomer(0)
            method == "DELETE" && path.matches(Regex(".*/customers/[^/]+$")) -> 200 to messageResponse("Customer deleted")

            // Sales - receipt (must be before generic sale detail)
            method == "GET" && path.matches(Regex(".*/sales/[^/]+/receipt")) -> 200 to receiptResponse()

            // Sales - items
            method == "POST" && path.matches(Regex(".*/sales/[^/]+/items")) -> 200 to singleSaleWithItems()
            method == "PUT" && path.matches(Regex(".*/sales/[^/]+/items/[^/]+")) -> 200 to singleSaleWithItems()
            method == "DELETE" && path.matches(Regex(".*/sales/[^/]+/items/[^/]+")) -> 200 to singleSaleWithItems()

            // Sales - discount
            method == "PUT" && path.matches(Regex(".*/sales/[^/]+/discount")) -> 200 to singleSaleWithItems()

            // Sales - complete
            method == "POST" && path.matches(Regex(".*/sales/[^/]+/complete")) -> 200 to completedSaleResponse()

            // Sales - void
            method == "POST" && path.matches(Regex(".*/sales/[^/]+/void")) -> 200 to voidedSaleResponse()

            // Sales - payments
            method == "POST" && path.matches(Regex(".*/sales/[^/]+/payments")) -> 201 to singlePaymentResponse()
            method == "GET" && path.matches(Regex(".*/sales/[^/]+/payments/summary")) -> 200 to paymentSummaryResponse()
            method == "GET" && path.matches(Regex(".*/sales/[^/]+/payments")) -> 200 to paymentListResponse()

            // Sales - CRUD
            method == "GET" && path.matches(Regex(".*/sales/[^/]+$")) && !path.endsWith("/sales") -> 200 to singleSaleWithItems()
            method == "GET" && path.endsWith("/sales") -> 200 to saleListResponse()
            method == "POST" && path.endsWith("/sales") -> 201 to newDraftSaleResponse()

            // Inventory
            method == "GET" && path.endsWith("/inventory/low-stock") -> 200 to lowStockResponse()
            method == "GET" && path.matches(Regex(".*/inventory/stock/[^/]+")) -> 200 to stockLevelResponse()
            method == "GET" && path.matches(Regex(".*/inventory/adjustments/[^/]+$")) && !path.endsWith("/adjustments") -> 200 to singleAdjustmentResponse()
            method == "GET" && path.endsWith("/inventory/adjustments") -> 200 to adjustmentListResponse()
            method == "POST" && path.endsWith("/inventory/adjustments") -> 201 to singleAdjustmentResponse()

            // Dashboard
            method == "GET" && path.endsWith("/dashboard/summary") -> 200 to dashboardSummaryResponse()

            // Users
            method == "GET" && path.matches(Regex(".*/users/[^/]+$")) && !path.endsWith("/users") -> 200 to singleUserResponse(0)
            method == "GET" && path.endsWith("/users") -> 200 to userListResponse()
            method == "POST" && path.endsWith("/users") -> 201 to singleUserResponse(0)
            method == "PUT" && path.matches(Regex(".*/users/[^/]+$")) -> 200 to singleUserResponse(0)
            method == "DELETE" && path.matches(Regex(".*/users/[^/]+$")) -> 200 to messageResponse("User deleted")
            method == "POST" && path.matches(Regex(".*/users/[^/]+/deactivate")) -> 200 to messageResponse("User deactivated")

            // Reports
            method == "GET" && path.endsWith("/reports/daily-sales") -> 200 to dailySalesReportResponse()
            method == "GET" && path.endsWith("/reports/top-products") -> 200 to topProductsReportResponse()
            method == "GET" && path.endsWith("/reports/inventory") -> 200 to inventoryReportResponse()
            method == "GET" && path.endsWith("/reports/cashier-performance") -> 200 to cashierPerformanceReportResponse()
            method == "GET" && path.endsWith("/reports/payment-methods") -> 200 to paymentMethodsReportResponse()

            // Fallback
            else -> 404 to """{"message":"Mock: No route matched for $method $path"}"""
        }
    }

    // ── Auth ─────────────────────────────────────────────────────────

    private fun loginResponse() = """
    {
      "data": {
        "user": {
          "id": "usr-001",
          "email": "admin@skyla.com",
          "first_name": "Admin",
          "last_name": "Skyla",
          "role": "admin"
        },
        "tokens": {
          "access_token": "mock-access-token-xyz",
          "refresh_token": "mock-refresh-token-xyz",
          "token_type": "Bearer"
        }
      }
    }
    """.trimIndent()

    private fun refreshTokenResponse() = """
    {
      "data": {
        "access_token": "mock-refreshed-access-token",
        "token_type": "Bearer"
      }
    }
    """.trimIndent()

    private fun messageResponse(msg: String) = """{"message":"$msg"}"""

    // ── Products ─────────────────────────────────────────────────────

    private val products = listOf(
        MockProduct("prd-001", "SKU-001", "8901234001", "Americano Coffee", "Classic black coffee", "cat-001", 2500, 1000, 150, 20, true),
        MockProduct("prd-002", "SKU-002", "8901234002", "Cafe Latte", "Espresso with steamed milk", "cat-001", 3500, 1500, 120, 15, true),
        MockProduct("prd-003", "SKU-003", "8901234003", "Cappuccino", "Espresso with foamed milk", "cat-001", 3500, 1500, 100, 15, true),
        MockProduct("prd-004", "SKU-004", "8901234004", "Green Tea Latte", "Matcha green tea with milk", "cat-001", 4000, 1800, 80, 10, true),
        MockProduct("prd-005", "SKU-005", "8901234005", "Chocolate Croissant", "Buttery croissant with chocolate", "cat-002", 3000, 1200, 50, 10, true),
        MockProduct("prd-006", "SKU-006", "8901234006", "Blueberry Muffin", "Freshly baked blueberry muffin", "cat-002", 2800, 1100, 40, 8, true),
        MockProduct("prd-007", "SKU-007", "8901234007", "Caesar Salad", "Fresh salad with caesar dressing", "cat-002", 5500, 2500, 30, 5, true),
        MockProduct("prd-008", "SKU-008", "8901234008", "Potato Chips", "Crispy salted potato chips", "cat-003", 1500, 700, 200, 30, true),
        MockProduct("prd-009", "SKU-009", "8901234009", "Mineral Water 500ml", "Bottled mineral water", "cat-001", 1000, 400, 300, 50, true),
        MockProduct("prd-010", "SKU-010", "8901234010", "Skyla Tote Bag", "Branded canvas tote bag", "cat-004", 15000, 7000, 25, 5, true),
    )

    private fun productJson(p: MockProduct) = """
    {
      "id": "${p.id}",
      "sku": "${p.sku}",
      "barcode": "${p.barcode}",
      "name": "${p.name}",
      "description": "${p.description}",
      "category_id": "${p.categoryId}",
      "price": ${p.price},
      "cost_price": ${p.costPrice},
      "current_stock": ${p.currentStock},
      "reorder_level": ${p.reorderLevel},
      "image_url": null,
      "is_active": ${p.isActive},
      "created_at": "2025-01-15T08:00:00Z",
      "updated_at": "2025-01-20T10:30:00Z"
    }
    """.trimIndent()

    private fun singleProduct(index: Int) = """{"data":${productJson(products[index])}}"""

    private fun productListResponse(): String {
        val items = products.joinToString(",") { productJson(it) }
        return """
        {
          "data": [$items],
          "meta": {
            "current_page": 1,
            "per_page": 20,
            "total_items": ${products.size},
            "total_pages": 1
          }
        }
        """.trimIndent()
    }

    // ── Categories ───────────────────────────────────────────────────

    private val categories = listOf(
        Triple("cat-001", "Beverages", "Coffee, tea, juice, and other drinks"),
        Triple("cat-002", "Food", "Pastries, salads, and meals"),
        Triple("cat-003", "Snacks", "Chips, cookies, and light bites"),
        Triple("cat-004", "Merchandise", "Branded merchandise and accessories"),
    )

    private fun categoryJson(index: Int): String {
        val (id, name, desc) = categories[index]
        return """
        {
          "id": "$id",
          "name": "$name",
          "description": "$desc",
          "parent_id": null,
          "sort_order": $index,
          "is_active": true,
          "created_at": "2025-01-10T08:00:00Z",
          "updated_at": "2025-01-10T08:00:00Z"
        }
        """.trimIndent()
    }

    private fun singleCategory(index: Int) = """{"data":${categoryJson(index)}}"""

    private fun categoryListResponse(): String {
        val items = categories.indices.joinToString(",") { categoryJson(it) }
        return """
        {
          "data": [$items],
          "meta": {
            "current_page": 1,
            "per_page": 20,
            "total_items": ${categories.size},
            "total_pages": 1
          }
        }
        """.trimIndent()
    }

    // ── Customers ────────────────────────────────────────────────────

    private data class MockCustomer(
        val id: String, val name: String, val phone: String?,
        val email: String?, val loyaltyPoints: Int, val totalSpent: Long, val notes: String?,
    )

    private val customers = listOf(
        MockCustomer("cst-001", "John Doe", "+1234567890", "john@email.com", 250, 350000, "Regular customer"),
        MockCustomer("cst-002", "Jane Smith", "+1234567891", "jane@email.com", 180, 270000, "Prefers soy milk"),
        MockCustomer("cst-003", "Bob Wilson", "+1234567892", "bob@email.com", 50, 85000, null),
        MockCustomer("cst-004", "Alice Brown", "+1234567893", null, 420, 620000, "VIP member"),
        MockCustomer("cst-005", "Charlie Lee", null, "charlie@email.com", 90, 120000, null),
    )

    private fun customerJson(c: MockCustomer) = """
    {
      "id": "${c.id}",
      "name": "${c.name}",
      "phone": ${c.phone?.let { "\"$it\"" } ?: "null"},
      "email": ${c.email?.let { "\"$it\"" } ?: "null"},
      "loyalty_points": ${c.loyaltyPoints},
      "total_spent": ${c.totalSpent},
      "notes": ${c.notes?.let { "\"$it\"" } ?: "null"},
      "is_active": true,
      "created_at": "2025-01-05T08:00:00Z",
      "updated_at": "2025-01-20T10:00:00Z"
    }
    """.trimIndent()

    private fun singleCustomer(index: Int) = """{"data":${customerJson(customers[index])}}"""

    private fun customerListResponse(): String {
        val items = customers.joinToString(",") { customerJson(it) }
        return """
        {
          "data": [$items],
          "meta": {
            "current_page": 1,
            "per_page": 20,
            "total_items": ${customers.size},
            "total_pages": 1
          }
        }
        """.trimIndent()
    }

    // ── Sales ────────────────────────────────────────────────────────

    private val saleItems = """
    [
      {
        "id": "si-001",
        "sale_id": "sale-001",
        "product_id": "prd-001",
        "product_name": "Americano Coffee",
        "product_sku": "SKU-001",
        "quantity": 2,
        "unit_price": 2500,
        "discount_amount": 0,
        "line_total": 5000,
        "created_at": "2025-01-20T09:15:00Z"
      },
      {
        "id": "si-002",
        "sale_id": "sale-001",
        "product_id": "prd-002",
        "product_name": "Cafe Latte",
        "product_sku": "SKU-002",
        "quantity": 1,
        "unit_price": 3500,
        "discount_amount": 0,
        "line_total": 3500,
        "created_at": "2025-01-20T09:15:30Z"
      },
      {
        "id": "si-003",
        "sale_id": "sale-001",
        "product_id": "prd-005",
        "product_name": "Chocolate Croissant",
        "product_sku": "SKU-005",
        "quantity": 1,
        "unit_price": 3000,
        "discount_amount": 0,
        "line_total": 3000,
        "created_at": "2025-01-20T09:16:00Z"
      }
    ]
    """.trimIndent()

    private fun saleJson(
        id: String, number: String, status: String,
        subtotal: Long, discount: Long, tax: Long, total: Long,
        items: String = "[]",
        voidReason: String? = null, completedAt: String? = null, voidedAt: String? = null,
    ): String {
        return """
        {
          "id": "$id",
          "sale_number": "$number",
          "cashier_id": "usr-001",
          "customer_id": "cst-001",
          "status": "$status",
          "subtotal": $subtotal,
          "discount_amount": $discount,
          "tax_amount": $tax,
          "total_amount": $total,
          "notes": null,
          "voided_at": ${voidedAt?.let { "\"$it\"" } ?: "null"},
          "voided_by": ${if (voidedAt != null) "\"usr-001\"" else "null"},
          "void_reason": ${voidReason?.let { "\"$it\"" } ?: "null"},
          "completed_at": ${completedAt?.let { "\"$it\"" } ?: "null"},
          "created_at": "2025-01-20T09:15:00Z",
          "updated_at": "2025-01-20T09:30:00Z",
          "items": $items
        }
        """.trimIndent()
    }

    private fun singleSaleWithItems() = """{"data":${saleJson(
        "sale-001", "SL-20250120-001", "draft",
        11500, 0, 1150, 12650,
        items = saleItems,
    )}}"""

    private fun newDraftSaleResponse() = """{"data":${saleJson(
        "sale-new", "SL-20250120-004", "draft",
        0, 0, 0, 0,
        items = "[]",
    )}}"""

    private fun completedSaleResponse() = """{"data":${saleJson(
        "sale-001", "SL-20250120-001", "completed",
        11500, 0, 1150, 12650,
        items = saleItems,
        completedAt = "2025-01-20T09:30:00Z",
    )}}"""

    private fun voidedSaleResponse() = """{"data":${saleJson(
        "sale-001", "SL-20250120-001", "voided",
        11500, 0, 1150, 12650,
        items = saleItems,
        voidReason = "Customer request",
        voidedAt = "2025-01-20T10:00:00Z",
    )}}"""

    private fun saleListResponse(): String {
        val sale1 = saleJson("sale-001", "SL-20250120-001", "completed", 11500, 0, 1150, 12650, completedAt = "2025-01-20T09:30:00Z")
        val sale2 = saleJson("sale-002", "SL-20250120-002", "completed", 7000, 500, 650, 7150, completedAt = "2025-01-20T11:00:00Z")
        val sale3 = saleJson("sale-003", "SL-20250120-003", "voided", 4000, 0, 400, 4400, voidReason = "Wrong order", voidedAt = "2025-01-20T12:00:00Z")
        return """
        {
          "data": [$sale1,$sale2,$sale3],
          "meta": {
            "current_page": 1,
            "per_page": 20,
            "total_items": 3,
            "total_pages": 1
          }
        }
        """.trimIndent()
    }

    // ── Payments ─────────────────────────────────────────────────────

    private fun singlePaymentResponse() = """
    {
      "data": {
        "id": "pay-001",
        "sale_id": "sale-001",
        "payment_method": "cash",
        "amount": 15000,
        "reference_number": null,
        "change_amount": 2350,
        "created_at": "2025-01-20T09:28:00Z"
      }
    }
    """.trimIndent()

    private fun paymentListResponse() = """
    {
      "data": [
        {
          "id": "pay-001",
          "sale_id": "sale-001",
          "payment_method": "cash",
          "amount": 15000,
          "reference_number": null,
          "change_amount": 2350,
          "created_at": "2025-01-20T09:28:00Z"
        }
      ],
      "meta": {
        "current_page": 1,
        "per_page": 20,
        "total_items": 1,
        "total_pages": 1
      }
    }
    """.trimIndent()

    private fun paymentSummaryResponse() = """
    {
      "data": {
        "sale_id": "sale-001",
        "total_amount": 12650,
        "total_paid": 15000,
        "remaining_balance": 0,
        "payments": [
          {
            "id": "pay-001",
            "sale_id": "sale-001",
            "payment_method": "cash",
            "amount": 15000,
            "reference_number": null,
            "change_amount": 2350,
            "created_at": "2025-01-20T09:28:00Z"
          }
        ]
      }
    }
    """.trimIndent()

    // ── Receipt ──────────────────────────────────────────────────────

    private fun receiptResponse() = """
    {
      "data": {
        "sale": ${saleJson(
            "sale-001", "SL-20250120-001", "completed",
            11500, 0, 1150, 12650,
            items = saleItems,
            completedAt = "2025-01-20T09:30:00Z",
        )},
        "payments": [
          {
            "id": "pay-001",
            "sale_id": "sale-001",
            "payment_method": "cash",
            "amount": 15000,
            "reference_number": null,
            "change_amount": 2350,
            "created_at": "2025-01-20T09:28:00Z"
          }
        ],
        "cashier_name": "Admin Skyla",
        "customer_name": "John Doe"
      }
    }
    """.trimIndent()

    // ── Inventory ────────────────────────────────────────────────────

    private fun stockLevelResponse() = """
    {
      "data": {
        "product_id": "prd-001",
        "product_name": "Americano Coffee",
        "product_sku": "SKU-001",
        "current_stock": 150,
        "reorder_level": 20,
        "is_low_stock": false
      }
    }
    """.trimIndent()

    private fun lowStockResponse(): String {
        return """
        {
          "data": [
            {
              "product_id": "prd-010",
              "product_name": "Skyla Tote Bag",
              "product_sku": "SKU-010",
              "current_stock": 5,
              "reorder_level": 5,
              "is_low_stock": true
            },
            {
              "product_id": "prd-007",
              "product_name": "Caesar Salad",
              "product_sku": "SKU-007",
              "current_stock": 3,
              "reorder_level": 5,
              "is_low_stock": true
            }
          ],
          "meta": {
            "current_page": 1,
            "per_page": 20,
            "total_items": 2,
            "total_pages": 1
          }
        }
        """.trimIndent()
    }

    private fun singleAdjustmentResponse() = """
    {
      "data": {
        "id": "adj-001",
        "product_id": "prd-001",
        "quantity_change": 50,
        "reason": "purchase",
        "reference_id": null,
        "notes": "Restocked from supplier",
        "adjusted_by": "usr-001",
        "created_at": "2025-01-18T14:00:00Z"
      }
    }
    """.trimIndent()

    private fun adjustmentListResponse() = """
    {
      "data": [
        {
          "id": "adj-001",
          "product_id": "prd-001",
          "quantity_change": 50,
          "reason": "purchase",
          "reference_id": null,
          "notes": "Restocked from supplier",
          "adjusted_by": "usr-001",
          "created_at": "2025-01-18T14:00:00Z"
        },
        {
          "id": "adj-002",
          "product_id": "prd-005",
          "quantity_change": -3,
          "reason": "damage",
          "reference_id": null,
          "notes": "Damaged during delivery",
          "adjusted_by": "usr-001",
          "created_at": "2025-01-19T10:30:00Z"
        },
        {
          "id": "adj-003",
          "product_id": "prd-008",
          "quantity_change": 100,
          "reason": "initial",
          "reference_id": null,
          "notes": "Initial stock count",
          "adjusted_by": "usr-001",
          "created_at": "2025-01-15T08:00:00Z"
        }
      ],
      "meta": {
        "current_page": 1,
        "per_page": 20,
        "total_items": 3,
        "total_pages": 1
      }
    }
    """.trimIndent()

    // ── Dashboard ────────────────────────────────────────────────────

    private fun dashboardSummaryResponse() = """
    {
      "data": {
        "today_sales": 245000,
        "today_transactions": 18,
        "today_revenue": 220500,
        "low_stock_count": 2,
        "recent_sales": [
          ${saleJson("sale-001", "SL-20250120-001", "completed", 11500, 0, 1150, 12650, completedAt = "2025-01-20T09:30:00Z")},
          ${saleJson("sale-002", "SL-20250120-002", "completed", 7000, 500, 650, 7150, completedAt = "2025-01-20T11:00:00Z")},
          ${saleJson("sale-003", "SL-20250120-003", "completed", 15000, 0, 1500, 16500, completedAt = "2025-01-20T13:45:00Z")}
        ]
      }
    }
    """.trimIndent()

    // ── Users ────────────────────────────────────────────────────────

    private data class MockUser(
        val id: String, val email: String, val firstName: String, val lastName: String,
        val role: String, val isActive: Boolean, val lastLoginAt: String?,
    )

    private val users = listOf(
        MockUser("usr-001", "admin@skyla.com", "Admin", "Skyla", "admin", true, "2025-01-20T08:00:00Z"),
        MockUser("usr-002", "manager@skyla.com", "Sarah", "Johnson", "manager", true, "2025-01-20T07:30:00Z"),
        MockUser("usr-003", "cashier1@skyla.com", "Mike", "Davis", "cashier", true, "2025-01-20T09:00:00Z"),
        MockUser("usr-004", "cashier2@skyla.com", "Lisa", "Chen", "cashier", true, "2025-01-19T17:00:00Z"),
        MockUser("usr-005", "staff@skyla.com", "Tom", "Wilson", "cashier", false, null),
    )

    private fun userJson(u: MockUser) = """
    {
      "id": "${u.id}",
      "email": "${u.email}",
      "first_name": "${u.firstName}",
      "last_name": "${u.lastName}",
      "role": "${u.role}",
      "is_active": ${u.isActive},
      "last_login_at": ${u.lastLoginAt?.let { "\"$it\"" } ?: "null"},
      "created_at": "2025-01-01T08:00:00Z",
      "updated_at": "2025-01-20T10:00:00Z"
    }
    """.trimIndent()

    private fun singleUserResponse(index: Int) = """{"data":${userJson(users[index])}}"""

    private fun userListResponse(): String {
        val items = users.joinToString(",") { userJson(it) }
        return """
        {
          "data": [$items],
          "meta": {
            "current_page": 1,
            "per_page": 20,
            "total_items": ${users.size},
            "total_pages": 1
          }
        }
        """.trimIndent()
    }

    // ── Reports ──────────────────────────────────────────────────────

    private fun dailySalesReportResponse() = """
    {
      "data": [
        {"date": "2025-01-20", "total_sales": 245000, "total_transactions": 18, "total_discount": 5000, "total_tax": 24000, "net_sales": 220500},
        {"date": "2025-01-19", "total_sales": 312000, "total_transactions": 24, "total_discount": 8000, "total_tax": 30400, "net_sales": 281600},
        {"date": "2025-01-18", "total_sales": 198000, "total_transactions": 15, "total_discount": 3000, "total_tax": 19500, "net_sales": 178500},
        {"date": "2025-01-17", "total_sales": 276000, "total_transactions": 21, "total_discount": 6000, "total_tax": 27000, "net_sales": 249000},
        {"date": "2025-01-16", "total_sales": 158000, "total_transactions": 12, "total_discount": 2000, "total_tax": 15600, "net_sales": 142400},
        {"date": "2025-01-15", "total_sales": 334000, "total_transactions": 26, "total_discount": 10000, "total_tax": 32400, "net_sales": 301600},
        {"date": "2025-01-14", "total_sales": 289000, "total_transactions": 22, "total_discount": 7000, "total_tax": 28200, "net_sales": 260800}
      ],
      "meta": {"current_page": 1, "per_page": 20, "total_items": 7, "total_pages": 1}
    }
    """.trimIndent()

    private fun topProductsReportResponse() = """
    {
      "data": [
        {"product_id": "prd-001", "product_name": "Americano Coffee", "product_sku": "SKU-001", "total_quantity": 85, "total_revenue": 212500},
        {"product_id": "prd-002", "product_name": "Cafe Latte", "product_sku": "SKU-002", "total_quantity": 62, "total_revenue": 217000},
        {"product_id": "prd-009", "product_name": "Mineral Water 500ml", "product_sku": "SKU-009", "total_quantity": 55, "total_revenue": 55000},
        {"product_id": "prd-005", "product_name": "Chocolate Croissant", "product_sku": "SKU-005", "total_quantity": 40, "total_revenue": 120000},
        {"product_id": "prd-003", "product_name": "Cappuccino", "product_sku": "SKU-003", "total_quantity": 38, "total_revenue": 133000},
        {"product_id": "prd-008", "product_name": "Potato Chips", "product_sku": "SKU-008", "total_quantity": 35, "total_revenue": 52500},
        {"product_id": "prd-006", "product_name": "Blueberry Muffin", "product_sku": "SKU-006", "total_quantity": 28, "total_revenue": 78400},
        {"product_id": "prd-004", "product_name": "Green Tea Latte", "product_sku": "SKU-004", "total_quantity": 22, "total_revenue": 88000},
        {"product_id": "prd-007", "product_name": "Caesar Salad", "product_sku": "SKU-007", "total_quantity": 15, "total_revenue": 82500},
        {"product_id": "prd-010", "product_name": "Skyla Tote Bag", "product_sku": "SKU-010", "total_quantity": 5, "total_revenue": 75000}
      ],
      "meta": {"current_page": 1, "per_page": 20, "total_items": 10, "total_pages": 1}
    }
    """.trimIndent()

    private fun inventoryReportResponse() = """
    {
      "data": [
        {"product_id": "prd-009", "product_name": "Mineral Water 500ml", "product_sku": "SKU-009", "current_stock": 300, "reorder_level": 50, "stock_value": 120000, "is_low_stock": false},
        {"product_id": "prd-008", "product_name": "Potato Chips", "product_sku": "SKU-008", "current_stock": 200, "reorder_level": 30, "stock_value": 140000, "is_low_stock": false},
        {"product_id": "prd-001", "product_name": "Americano Coffee", "product_sku": "SKU-001", "current_stock": 150, "reorder_level": 20, "stock_value": 150000, "is_low_stock": false},
        {"product_id": "prd-002", "product_name": "Cafe Latte", "product_sku": "SKU-002", "current_stock": 120, "reorder_level": 15, "stock_value": 180000, "is_low_stock": false},
        {"product_id": "prd-003", "product_name": "Cappuccino", "product_sku": "SKU-003", "current_stock": 100, "reorder_level": 15, "stock_value": 150000, "is_low_stock": false},
        {"product_id": "prd-004", "product_name": "Green Tea Latte", "product_sku": "SKU-004", "current_stock": 80, "reorder_level": 10, "stock_value": 144000, "is_low_stock": false},
        {"product_id": "prd-005", "product_name": "Chocolate Croissant", "product_sku": "SKU-005", "current_stock": 50, "reorder_level": 10, "stock_value": 60000, "is_low_stock": false},
        {"product_id": "prd-006", "product_name": "Blueberry Muffin", "product_sku": "SKU-006", "current_stock": 40, "reorder_level": 8, "stock_value": 44000, "is_low_stock": false},
        {"product_id": "prd-010", "product_name": "Skyla Tote Bag", "product_sku": "SKU-010", "current_stock": 5, "reorder_level": 5, "stock_value": 35000, "is_low_stock": true},
        {"product_id": "prd-007", "product_name": "Caesar Salad", "product_sku": "SKU-007", "current_stock": 3, "reorder_level": 5, "stock_value": 7500, "is_low_stock": true}
      ],
      "meta": {"current_page": 1, "per_page": 20, "total_items": 10, "total_pages": 1}
    }
    """.trimIndent()

    private fun cashierPerformanceReportResponse() = """
    {
      "data": [
        {"cashier_id": "usr-001", "cashier_name": "Admin Skyla", "total_sales": 520000, "total_transactions": 38, "average_sale": 13684},
        {"cashier_id": "usr-003", "cashier_name": "Mike Davis", "total_sales": 445000, "total_transactions": 32, "average_sale": 13906},
        {"cashier_id": "usr-004", "cashier_name": "Lisa Chen", "total_sales": 387000, "total_transactions": 28, "average_sale": 13821},
        {"cashier_id": "usr-002", "cashier_name": "Sarah Johnson", "total_sales": 260000, "total_transactions": 20, "average_sale": 13000}
      ],
      "meta": {"current_page": 1, "per_page": 20, "total_items": 4, "total_pages": 1}
    }
    """.trimIndent()

    private fun paymentMethodsReportResponse() = """
    {
      "data": [
        {"payment_method": "cash", "total_amount": 892000, "transaction_count": 68, "percentage": 55.3},
        {"payment_method": "card", "total_amount": 478000, "transaction_count": 35, "percentage": 29.6},
        {"payment_method": "e_wallet", "total_amount": 242000, "transaction_count": 15, "percentage": 15.1}
      ],
      "meta": {"current_page": 1, "per_page": 20, "total_items": 3, "total_pages": 1}
    }
    """.trimIndent()

    // ── Helpers ──────────────────────────────────────────────────────

    private data class MockProduct(
        val id: String, val sku: String, val barcode: String,
        val name: String, val description: String, val categoryId: String,
        val price: Long, val costPrice: Long,
        val currentStock: Int, val reorderLevel: Int, val isActive: Boolean,
    )
}
