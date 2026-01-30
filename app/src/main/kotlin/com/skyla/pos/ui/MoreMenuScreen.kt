package com.skyla.pos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skyla.pos.ui.components.SkylaTopBar

private data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreMenuScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val menuItems = listOf(
        MenuItem(
            label = "Dashboard",
            icon = Icons.Default.Dashboard,
            onClick = onNavigateToDashboard,
        ),
        MenuItem(
            label = "Categories",
            icon = Icons.Default.Category,
            onClick = onNavigateToCategories,
        ),
        MenuItem(
            label = "Inventory",
            icon = Icons.Default.Inventory,
            onClick = onNavigateToInventory,
        ),
        MenuItem(
            label = "Users",
            icon = Icons.Default.People,
            onClick = onNavigateToUsers,
        ),
        MenuItem(
            label = "Reports",
            icon = Icons.Default.Assessment,
            onClick = onNavigateToReports,
        ),
        MenuItem(
            label = "Change Password",
            icon = Icons.Default.Lock,
            onClick = onNavigateToChangePassword,
        ),
        MenuItem(
            label = "Settings",
            icon = Icons.Default.Settings,
            onClick = onNavigateToSettings,
        ),
        MenuItem(
            label = "Logout",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = onLogout,
        ),
    )

    Scaffold(
        topBar = {
            SkylaTopBar(title = "More")
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(menuItems) { item ->
                MenuCard(
                    label = item.label,
                    icon = item.icon,
                    onClick = item.onClick,
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }
    }
}
