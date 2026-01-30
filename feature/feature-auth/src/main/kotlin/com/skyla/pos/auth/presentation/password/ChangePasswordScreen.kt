package com.skyla.pos.auth.presentation.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordChanged: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Password changed successfully")
            onPasswordChanged()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Change Password",
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Current password field
            SkylaTextField(
                value = state.currentPassword,
                onValueChange = viewModel::onCurrentPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = "Current Password",
                placeholder = "Enter current password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                trailingIcon = {
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(
                            imageVector = if (currentPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (currentPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                        )
                    }
                },
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New password field
            SkylaTextField(
                value = state.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = "New Password",
                placeholder = "Enter new password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            imageVector = if (newPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (newPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                        )
                    }
                },
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm new password field
            SkylaTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = "Confirm New Password",
                placeholder = "Re-enter new password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                isError = state.confirmPassword.isNotEmpty() && state.newPassword != state.confirmPassword,
                errorMessage = if (state.confirmPassword.isNotEmpty() && state.newPassword != state.confirmPassword) {
                    "Passwords do not match"
                } else {
                    null
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (confirmPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                        )
                    }
                },
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            SkylaButton(
                text = "Change Password",
                onClick = viewModel::onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state.currentPassword.isNotBlank()
                    && state.newPassword.isNotBlank()
                    && state.confirmPassword.isNotBlank(),
                isLoading = state.isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
