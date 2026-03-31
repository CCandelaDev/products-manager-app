package com.ccandeladev.androidtesting.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ccandeladev.androidtesting.cart.presentation.CartScreen
import com.ccandeladev.androidtesting.detail.presentation.ProductDetailScreen
import com.ccandeladev.androidtesting.productlist.presentation.ProductListScreen
import com.ccandeladev.androidtesting.settings.presentation.SettingsScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)

    //Entries from each one the views
    val entries = entryProvider<NavKey>
    {
        entry<Screen.ProductList> {
            ProductListScreen(
                navigateToSettings = { backStack.add(Screen.Settings) },
                navigateToProductDetail = { productId ->
                    backStack.add(
                        Screen.ProductDetail(
                            productId
                        )
                    )
                },
                navigateToCart = {backStack.add(Screen.Cart)}
            )
        }
        entry<Screen.Cart> {
            CartScreen(onBack = {backStack.removeLastOrNull()})
        }
        entry<Screen.Settings> {
            //SettingsScreen(onBack = {backStack.add(Screen.ProductList)})
            SettingsScreen(onBack = { backStack.removeLastOrNull() })
        }
        entry<Screen.ProductDetail> { route ->
            ProductDetailScreen(productId = route.productId, onBack = { backStack.removeLastOrNull() })
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() }
    )
}