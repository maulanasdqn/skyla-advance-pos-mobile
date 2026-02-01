package com.skyla.pos.users.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyla.pos.model.User
import com.skyla.pos.model.UserRole
import com.skyla.pos.ui.components.ActiveStatusChip
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaPaginatedList
import com.skyla.pos.ui.components.SkylaSearchBar
import com.skyla.pos.ui.components.SkylaStatusChip
import com.skyla.pos.ui.components.SkylaTopBar
import com.skyla.pos.ui.theme.StatusSuccess
import com.skyla.pos.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToForm: (String?) -> Unit,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Users",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { onNavigateToForm(null) }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add user",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add user",
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SkylaSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                placeholder = "Search users...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            if (uiState.error != null && uiState.users.isEmpty()) {
                SkylaErrorView(
                    message = uiState.error!!,
                    onRetry = viewModel::refresh,
                )
            } else {
                SkylaPaginatedList(
                    items = uiState.users,
                    isLoading = uiState.isLoading || uiState.isLoadingMore,
                    hasMore = uiState.hasMorePages,
                    onLoadMore = viewModel::loadMoreUsers,
                    emptyTitle = "No Users",
                    emptyMessage = "No users found. Tap + to add a new user.",
                    itemContent = { _, user ->
                        UserListItem(
                            user = user,
                            onClick = { onNavigateToDetail(user.id) },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onClick: () -> Unit,
) {
    SkylaCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                ActiveStatusChip(isActive = user.isActive)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoleBadge(role = user.role)
            }
        }
    }
}

@Composable
private fun RoleBadge(role: UserRole) {
    val (label, color) = when (role) {
        UserRole.ADMIN -> "Admin" to StatusWarning
        UserRole.MANAGER -> "Manager" to StatusSuccess
        UserRole.CASHIER -> "Cashier" to MaterialTheme.colorScheme.primary
    }
    SkylaStatusChip(
        label = label,
        color = color,
    )
}
