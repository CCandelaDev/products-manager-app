package com.ccandeladev.androidtesting.productlist.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.ccandeladev.androidtesting.productlist.domain.model.SortOption
import com.ccandeladev.androidtesting.productlist.presentation.ProductListUiState

@Composable
fun FiltersMenu(
    modifier: Modifier = Modifier,
    state: ProductListUiState.Success,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Categories", fontStyle = FontStyle.Italic)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All", style = MaterialTheme.typography.labelSmall) }
                )
                state.categories.forEach { category ->
                    FilterChip(
                        selected = category.equals(state.selectedCategory, ignoreCase = true),
                        onClick = { onCategorySelected(category) },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            HorizontalDivider()

            Text("Order by", fontStyle = FontStyle.Italic)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                // .horizontalScroll(rememberScrollState()),
                , horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.sortOption == SortOption.PRICE_ASC,
                    onClick = { onSortSelected(SortOption.PRICE_ASC) },
                    label = { Text("Price ↑", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.sortOption == SortOption.PRICE_DES,
                    onClick = { onSortSelected(SortOption.PRICE_DES) },
                    label = { Text("Price ↓", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.sortOption == SortOption.DISCOUNT,
                    onClick = { onSortSelected(SortOption.DISCOUNT) },
                    label = { Text("Discount", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}