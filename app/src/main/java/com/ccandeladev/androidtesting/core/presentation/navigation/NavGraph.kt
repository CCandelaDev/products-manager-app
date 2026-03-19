package com.ccandeladev.androidtesting.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ccandeladev.androidtesting.productlist.presentation.ProductListScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)

    //Entries from each one the views
    val entries = entryProvider<NavKey>
    {
        entry<Screen.ProductList> {
            ProductListScreen()
        }
        entry<Screen.Cart> {
            Text("Cart", fontSize = 20.sp)
        }
        entry<Screen.Settings> {
            Text("Settings", fontSize = 20.sp)
        }
        entry<Screen.ProductDetail> {
            Text("Detail", fontSize = 20.sp)
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() }
    )
}