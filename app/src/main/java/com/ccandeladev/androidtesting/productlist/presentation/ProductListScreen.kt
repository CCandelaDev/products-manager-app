package com.ccandeladev.androidtesting.productlist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ccandeladev.androidtesting.productlist.domain.model.ProductWithOffer
import com.ccandeladev.androidtesting.productlist.presentation.components.FiltersMenu
import com.ccandeladev.androidtesting.productlist.presentation.components.HomeTopAppBar
import com.ccandeladev.androidtesting.productlist.presentation.components.ProductItem

@Composable
fun ProductListScreen(
    productListViewModel: ProductListViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
    navigateToProductDetail: (String) -> Unit,
    navigateToCart: () -> Unit
) {
    // To get hooked on the life cycle
    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    val filtersVisible by productListViewModel.filterVisible.collectAsStateWithLifecycle()

    //When any event launch -> to do action
    LaunchedEffect(Unit) {
        productListViewModel.events.collect { event ->
            when (event) {
                is ProductListEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                filtersVisible = filtersVisible,
                onFiltersSelected = { showFilter -> productListViewModel.setFilterVisible(showFilter) },
                onSettingsSelected = { navigateToSettings() },
                onCartSelected = { navigateToCart() }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is ProductListUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProductListUiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues), contentAlignment = Alignment.Center
                ) {
                    Text("Error", fontSize = 30.sp, color = Color.Red)
                }

            }

            is ProductListUiState.Success -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                ) {
                    // Animation to show/hide filters
                    AnimatedVisibility(
                        visible = filtersVisible,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        FiltersMenu(
                            state = state,
                            onCategorySelected = { category ->
                                productListViewModel.setCategory(
                                    category
                                )
                            },
                            onSortSelected = { sortOption ->
                                productListViewModel.setSortOption(
                                    sortOption
                                )
                            }
                        )
                    }


                    Text(
                        "${state.inventory.size} products found in the inventory",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    if (state.inventory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Text("🔎", style = MaterialTheme.typography.displayMedium)
                                Text(
                                    "No products were found",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )

                            }
                        }
                    } else {
                        LazyColumn() {
                            items(state.inventory) { item: ProductWithOffer ->
                                ProductItem(
                                    item = item,
                                    onClick = { navigateToProductDetail(it.product.id) })
                            }
                        }
                    }

                }


            }
        }

    }
}


