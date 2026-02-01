package com.skyla.pos.users.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyla.pos.common.UiState
import com.skyla.pos.common.toReadableDateTime
import com.skyla.pos.model.User
import com.skyla.pos.model.UserRole
import com.skyla.pos.ui.components.ActiveStatusChip
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaConfirmDialog
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaOutlinedButton
import com.skyla.pos.ui.components.SkylaStatusChip
import com.skyla.pos.ui.components.SkylaTopBar
import com.skyla.pos.ui.theme.StatusSuccess
import com.skyla.pos.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isActionLoading by viewModel.isActionLoading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showDeactivateDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UserDetailEvent.UserDeleted -> {
                    onNavigateBack()
                }
                is UserDetailEvent.UserDeactivated -> {
                    snackbarHostState.showSnackbar("User deactivated successfully")
                }
                is UserDetailEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showDeleteDialog) {
        SkylaConfirmDialog(
            title = "Delete User",
            message = "Are you sure you want to delete this user? This action cannot be undone.",
            confirmText = "Delete",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteUser()
            },
            onDismiss = { showDeleteDialog = false },
            isDestructive = true,
        )
    }

    if (showDeactivateDialog) {
        SkylaConfirmDialog(
            title = "Deactivate User",
            message = "Are you sure you want to deactivate this user? They will no longer be able to log in.",
            confirmText = "Deactivate",
            onConfirm = {
                showDeactivateDialog = false
                viewModel.deactivateUser()
            },
            onDismiss = { showDeactivateDialog = false },
        )
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "User Detail",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (uiState is UiState.Success) {
                        val user = (uiState as UiState.Success<User>).data
                        IconButton(onClick = { onNavigateToEdit(user.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit user",
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete user",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                SkylaLoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            is UiState.Error -> {
                SkylaErrorView(
                    message = state.message,
                    onRetry = viewModel::loadUser,
                    modifier = Modifier.padding(paddingValues),
                )
            }
            is UiState.Idle -> { /* waiting */ }
            is UiState.Success -> {
                UserDetailContent(
                    user = state.data,
                    isActionLoading = isActionLoading,
                    onDeactivate = { showDeactivateDialog = true },
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    user: User,
    isActionLoading: Boolean,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Header
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ActiveStatusChip(isActive = user.isActive)
                }

                Spacer(modifier = Modifier.height(8.dp))

                DetailRow(label = "Email", value = user.email)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Role",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RoleBadge(role = user.role)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Details
        SkylaCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                user.lastLoginAt?.let {
                    DetailRow(label = "Last Login", value = it.toReadableDateTime())
                } ?: DetailRow(label = "Last Login", value = "Never")

                DetailRow(label = "Created", value = user.createdAt.toReadableDateTime())
                DetailRow(label = "Updated", value = user.updatedAt.toReadableDateTime())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        if (user.isActive) {
            SkylaOutlinedButton(
                text = "Deactivate User",
                onClick = onDeactivate,
                isLoading = isActionLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
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
