package com.skyla.pos.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.delay

@Composable
fun SkylaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
    placeholder: String = "Search...",
) {
    var internalQuery by rememberSaveable { mutableStateOf(query) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Debounce: emit changes after 300ms of inactivity
    LaunchedEffect(internalQuery) {
        delay(300L)
        if (internalQuery != query) {
            onQueryChange(internalQuery)
        }
    }

    // Sync external query changes
    LaunchedEffect(query) {
        if (query != internalQuery) {
            internalQuery = query
        }
    }

    OutlinedTextField(
        value = internalQuery,
        onValueChange = { internalQuery = it },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
            )
        },
        trailingIcon = {
            if (internalQuery.isNotEmpty()) {
                IconButton(onClick = {
                    internalQuery = ""
                    onQueryChange("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch?.invoke(internalQuery)
                keyboardController?.hide()
            }
        ),
        shape = MaterialTheme.shapes.medium,
    )
}
