package com.skyla.pos.categories.presentation.list

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyla.pos.model.Category
import com.skyla.pos.ui.components.ActiveStatusChip
import com.skyla.pos.ui.components.SkylaCard
import com.skyla.pos.ui.components.SkylaErrorView
import com.skyla.pos.ui.components.SkylaPaginatedList
import com.skyla.pos.ui.components.SkylaSearchBar
import com.skyla.pos.ui.components.SkylaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onNavigateToForm: (String?) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: CategoryListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SkylaTopBar(
                title = "Categories",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { onNavigateToForm(null) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add category",
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
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add category",
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
                placeholder = "Search categories...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            if (uiState.error != null && uiState.categories.isEmpty()) {
                SkylaErrorView(
                    message = uiState.error!!,
                    onRetry = viewModel::refresh,
                )
            } else {
                SkylaPaginatedList(
                    items = uiState.categories,
                    isLoading = uiState.isLoading || uiState.isLoadingMore,
                    hasMore = uiState.hasMorePages,
                    onLoadMore = viewModel::loadMoreCategories,
                    emptyTitle = "No Categories",
                    emptyMessage = "No categories found. Tap + to add a new category.",
                    itemContent = { _, category ->
                        CategoryListItem(
                            category = category,
                            onClick = { onNavigateToForm(category.id) },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun CategoryListItem(
    category: Category,
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
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val description = category.description
                    if (!description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                ActiveStatusChip(isActive = category.isActive)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sort Order: ${category.sortOrder}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                category.parentId?.let {
                    Text(
                        text = "Parent: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
