package com.skyla.pos.customers.presentation.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.ui.components.SkylaButton
import com.skyla.pos.ui.components.SkylaLoadingScreen
import com.skyla.pos.ui.components.SkylaTextField
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    onNavigateBack: () -> Unit,
    onCustomerSaved: () -> Unit,
    viewModel: CustomerFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CustomerFormEvent.CustomerSaved -> {
                    onCustomerSaved()
                }
                is CustomerFormEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = if (uiState.isEditMode) "Edit Customer" else "New Customer",
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isFormLoading) {
            SkylaLoadingScreen(modifier = Modifier.padding(paddingValues))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Customer Information",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = "Customer Name",
                placeholder = "Enter customer name",
                leadingIcon = Icons.Default.Person,
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChanged,
                label = "Phone Number (optional)",
                placeholder = "Enter phone number",
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                label = "Email Address (optional)",
                placeholder = "Enter email address",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            SkylaTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChanged,
                label = "Notes (optional)",
                placeholder = "Enter any notes about this customer",
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            SkylaButton(
                text = if (uiState.isEditMode) "Update Customer" else "Create Customer",
                onClick = viewModel::saveCustomer,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
