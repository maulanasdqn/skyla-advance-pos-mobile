package com.skyla.pos.reports.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaTopBar

@Composable
fun ReportsDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDailySales: () -> Unit,
    onNavigateToTopProducts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCashierPerformance: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
) {
    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Reports",
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Select a Report",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp),
            )

            ReportDashboardCard(
                icon = Icons.Default.BarChart,
                title = "Daily Sales Report",
                description = "View daily sales summary, transactions, and net sales",
                onClick = onNavigateToDailySales,
            )

            ReportDashboardCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Top Products",
                description = "See best-selling products by quantity and revenue",
                onClick = onNavigateToTopProducts,
            )

            ReportDashboardCard(
                icon = Icons.Default.Inventory2,
                title = "Inventory Report",
                description = "Monitor stock levels and identify low-stock items",
                onClick = onNavigateToInventory,
            )

            ReportDashboardCard(
                icon = Icons.Default.People,
                title = "Cashier Performance",
                description = "Compare cashier sales, transactions, and averages",
                onClick = onNavigateToCashierPerformance,
            )

            ReportDashboardCard(
                icon = Icons.Default.Payment,
                title = "Payment Methods",
                description = "Analyze payment method distribution and totals",
                onClick = onNavigateToPaymentMethods,
            )
        }
    }
}

@Composable
private fun ReportDashboardCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    SkylaCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
