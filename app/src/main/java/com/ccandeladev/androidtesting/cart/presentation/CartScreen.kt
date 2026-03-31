package com.ccandeladev.androidtesting.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ccandeladev.androidtesting.R
import com.ccandeladev.androidtesting.cart.presentation.model.CartItemWithOffer
import com.ccandeladev.androidtesting.core.presentation.components.QuantitySelector
import com.ccandeladev.androidtesting.core.presentation.components.ShopTopAppbar
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CartScreen(
    onBack: () -> Unit, cartViewModel: CartViewModel = hiltViewModel()
) {

    //Collect uiState
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        cartViewModel.events.collect { event ->
            when (event) {
                is CartEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            ShopTopAppbar(
                title = stringResource(R.string.cart_title),
                onBackSelected = { onBack() })
        }
    ) { paddingValues ->

        when (val state = uiState) {
            CartUiState.Loading -> {
                CartLoadingStateScreen(modifier = Modifier)
            }

            is CartUiState.Error -> CartErrorStateScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = state,

                ) { cartViewModel.loadCart() }

            is CartUiState.Success -> CartSuccessStateScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = state,
                onIncreasedQuantity = { productId, quantity ->
                    cartViewModel.increaseQuantity(
                        productId, //The ID that comes from the CartItemCard
                        quantity //The quantity that comes from the CartItemCard
                    )
                },
                onDecreasedQuantity = { productId, quantity ->
                    cartViewModel.decreaseQuantity(
                        productId,
                        quantity
                    )
                }
            )

        }

    }

}


@Composable
fun CartErrorStateScreen(
    modifier: Modifier = Modifier,
    state: CartUiState.Error,
    onRetrySelected: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Error: ${state.message}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onRetrySelected() }) {
            Text(stringResource(R.string.btn_retry))
        }
    }
}

@Composable
fun CartLoadingStateScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun CartSuccessStateScreen(
    modifier: Modifier, state: CartUiState.Success,
    onIncreasedQuantity: (String, Int) -> Unit,
    onDecreasedQuantity: (String, Int) -> Unit,
) {

    Column(modifier = modifier) {

        if (state.cartItems.isEmpty()) {

            Column(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.emoji_cart),
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    stringResource(R.string.empty_cart),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.add_cart),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),//Space between cards
            ) {
                items(state.cartItems) { itemWithProduct ->
                    CartItemCard(
                        itemWithProduct,
                        onIncreasedQuantity = { productId, quantity ->
                            onIncreasedQuantity(
                                productId,
                                quantity
                            )
                        },
                        onDecreasedQuantity = { productId, quantity ->
                            onDecreasedQuantity(
                                productId,
                                quantity
                            )
                        },
                        onRemove = {})
                }

            }
        }
    }


}

@Composable
fun CartItemCard(
    itemWithProduct: CartItemWithOffer,
    onIncreasedQuantity: (String, Int) -> Unit,
    onDecreasedQuantity: (String, Int) -> Unit,
    onRemove: () -> Unit
) {

    val product = itemWithProduct.product
    val cartItem = itemWithProduct.cartItem

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("USD")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .weight(1f)
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp)),
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .weight(2f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                Text(product.name)
                //pending to add promo
                Text("Total: ${currencyFormatter.format(product.price)}")
                QuantitySelector(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(
                            4.dp
                        )
                    ),
                    quantity = cartItem.quantity.toString(),
                    canDecrease = cartItem.quantity > 1,
                    canIncrease = cartItem.quantity < product.stock,
                    onDecreaseSelected = { onDecreasedQuantity(product.id, cartItem.quantity) },
                    onIncreaseSelected = { onIncreasedQuantity(product.id, cartItem.quantity) }
                )
            }
        }
    }


}









































