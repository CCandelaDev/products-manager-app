package com.ccandeladev.androidtesting.detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccandeladev.androidtesting.productlist.domain.model.Product

@Composable
fun AddToCartButtonWithStock(
    modifier: Modifier = Modifier,
    product: Product,
    isLoading: Boolean,
    addToCart: () -> Unit
) {
    BottomAppBar(
        modifier = modifier
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        //To avoid buttons system cover it up
        windowInsets = BottomAppBarDefaults.windowInsets,
    ) {
        Button(
            onClick = {addToCart()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = !isLoading, //To avoid click when the product is being added to the card
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = null
                )
                Text(
                    text = "Add to cart",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}