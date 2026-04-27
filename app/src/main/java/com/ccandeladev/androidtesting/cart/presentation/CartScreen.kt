package com.ccandeladev.androidtesting.cart.presentation

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ccandeladev.androidtesting.R
import com.ccandeladev.androidtesting.cart.domain.model.CartSummary
import com.ccandeladev.androidtesting.cart.presentation.model.CartItemWithOffer
import com.ccandeladev.androidtesting.core.presentation.components.QuantitySelector
import com.ccandeladev.androidtesting.core.presentation.components.ShopTopAppbar
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CartScreen(
    onBack: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {

    //Collect uiState
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    // Launches a coroutine in the composition scope that will execute when this composable enters the composition
    // Unit as the key means it will only execute ONCE when the screen first loads
    LaunchedEffect(Unit) {
        // Collects (listens to) events emitted by the ViewModel's Flow
        // The flow remains active as long as the composable is on the screen
        cartViewModel.events.collect { event ->
            // Handles each event based on its type
            when (event) {
                // If the event is of type ShowMessage (to display a message to the user)
                is CartEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message)
                // Additional event types can be added here in the future
                // is CartEvent.NavigateToDetail -> ...
                // is CartEvent.ShowError -> ...
            }
        }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            ShopTopAppbar(
                title = stringResource(R.string.title_cart),
                onBackSelected = { onBack() })
        }
    ) { paddingValues ->

        when (val state = uiState) {
            CartUiState.Loading -> {
                CartLoadingStateScreen(modifier = Modifier)
            }

            is CartUiState.Error -> {
                CartErrorStateScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = state,

                    ) { cartViewModel.refresh() }
            }

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
                }, onRemove = { productId ->
                    cartViewModel.removeFromCart(productId = productId)
                }
            )

        }

    }

}


@Composable
fun CartErrorStateScreen(
    modifier: Modifier = Modifier,
    state: CartUiState.Error,
    onRefresh: () -> Unit
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
        Button(onClick = { onRefresh() }) {
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
    modifier: Modifier,
    state: CartUiState.Success,
    onIncreasedQuantity: (String, Int) -> Unit,
    onDecreasedQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(java.util.Locale.US).apply {
            currency = Currency.getInstance("USD")
        }
    }

    Column(modifier = modifier) {

        // Composable to animate the content
        AnimatedContent(state.cartItems.isEmpty()) { isEmpty ->
            if (isEmpty) {

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.emoji_cart),
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        stringResource(R.string.txt_empty_cart),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.txt_add_cart),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            } else {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),//Space between cards
                ) {
                    items(state.cartItems, key = { it.cartItem.productId }) { itemWithProduct ->
                        CartItemCard(
                            modifier = Modifier.animateItem(),//Animate when remove
                            itemWithProduct = itemWithProduct,
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
                            onRemove = { productId -> onRemove(productId) },//To delete product
                            currencyFormatter = currencyFormatter
                        )

                    }

                }

            }
        }

        if (state.cartItems.isNotEmpty() && state.summary != null) {
            CartSummaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                summary = state.summary,
                currencyFormatter = currencyFormatter
            )
        }

    }


}

@Composable
fun CartSummaryCard(modifier: Modifier, summary: CartSummary, currencyFormatter: NumberFormat) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            //when there are no discounts
            Text(
                stringResource(R.string.title_summary_card),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.txt_subtotal),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    currencyFormatter.format(
                        summary.subTotal
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            //If we have something with discount
            if (summary.discountTotal > 0) {
                //Discounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.txt_total_discount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        currencyFormatter.format(
                            summary.discountTotal
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                }

            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            //Final price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.txt_total_price),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    currencyFormatter.format(
                        summary.finalTotal
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

            }

        }
    }
}

@Composable
fun CartItemCard(
    modifier: Modifier,
    itemWithProduct: CartItemWithOffer,
    onIncreasedQuantity: (String, Int) -> Unit,
    onDecreasedQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit,
    currencyFormatter: NumberFormat
) {

    val product = itemWithProduct.item.product
    val offer = itemWithProduct.item.offer
    val cartItem = itemWithProduct.cartItem

    val unitPrice = when (offer) {
        is ProductOffer.Percent -> offer.discountedPrice
        is ProductOffer.BuyXPayY -> product.price
        null -> product.price
    }

    val hasDiscount = offer is ProductOffer.Percent
    val itemTotal = unitPrice * cartItem.quantity

    /**
     * To delete with a swipe -> slide with your finger
     */
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            onRemove(cartItem.productId)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            val progress = dismissState.progress //Swipe progress(0.0 to 1.0)
            val isOpened = progress > 0.5f // Change when it´s > 0.5

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isOpened) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.errorContainer
                    ),

                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    if (isOpened) Icons.Default.DeleteSweep
                    else Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            val scale = 0.8f + (progress * 0.4f)
                            scaleX = scale
                            scaleY = scale
                        },
                    tint = if (isOpened) MaterialTheme.colorScheme.onError
                    else MaterialTheme.colorScheme.error
                )
            }
        }) {
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

                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )


                    //add promo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasDiscount) {
                            //Price before discount
                            Text(
                                text = currencyFormatter.format(product.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            //Price with discount
                            Text(
                                "${currencyFormatter.format(unitPrice)} p/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )


                        } else {
                            //Price without discount
                            Text(
                                "${currencyFormatter.format(unitPrice)} p/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                    }

                    Text(
                        "Total: ${currencyFormatter.format(itemTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    QuantitySelector(
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(
                                4.dp
                            )
                        ),
                        quantity = cartItem.quantity.toString(),
                        canDecrease = cartItem.quantity > 1,
                        canIncrease = cartItem.quantity < product.stock,
                        onDecreaseSelected = {
                            onDecreasedQuantity(
                                product.id,
                                cartItem.quantity
                            )
                        },
                        onIncreaseSelected = {
                            onIncreasedQuantity(
                                product.id,
                                cartItem.quantity
                            )
                        }
                    )
                }
            }
        }
    }
}











































