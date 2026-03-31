package com.ccandeladev.androidtesting.productlist.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    filtersVisible: Boolean = true,
    onFiltersSelected: (Boolean) -> Unit,
    onSettingsSelected: () -> Unit,
    onCartSelected: () -> Unit
) {

    TopAppBar(
        title = { Text(text = "MarketPlace") },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Icon shop",

                )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            //To open the filters
            IconButton(onClick = { onFiltersSelected(!filtersVisible) }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if (filtersVisible) "Hide filters" else "Show filters",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            //To navigate to settings
            IconButton(onClick = { onSettingsSelected() }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            //To navigate to cart
            IconButton(onClick = { onCartSelected()}) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}