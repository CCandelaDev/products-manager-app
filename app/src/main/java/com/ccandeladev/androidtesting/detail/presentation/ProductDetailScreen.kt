package com.ccandeladev.androidtesting.detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ccandeladev.androidtesting.core.presentation.components.ShopTopAppbar
import com.ccandeladev.androidtesting.detail.presentation.components.AddToCartButton
import com.ccandeladev.androidtesting.productlist.domain.model.ProductOffer

@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    productDetailViewModel: ProductDetailViewModel = hiltViewModel()
) {

    val uiState by productDetailViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    // to get productId
    LaunchedEffect(productId) {
        productDetailViewModel.setProductId(productId = productId)
    }

    LaunchedEffect(Unit) {
        productDetailViewModel.events.collect { event ->
            when (event) {
                ProductDetailEvent.INSUFICIENT_STOCK_ERROR -> {
                    snackBarHostState.showSnackbar("Insufficient stock")
                }

                ProductDetailEvent.NETWORK_ERROR -> {
                    snackBarHostState.showSnackbar("Insufficient stock")
                }

                ProductDetailEvent.UNKNOWN_ERROR -> {
                    snackBarHostState.showSnackbar("Insufficient stock")
                }

                ProductDetailEvent.SUCCESS_ADD_TO_CART -> {
                    snackBarHostState.showSnackbar("Product added to cart")
                }
            }

        }
    }


    val screenTitle = (uiState as? ProductDetailUiState.Success)?.item?.product?.name.orEmpty()

    val successState = uiState as? ProductDetailUiState.Success

    Scaffold(
        topBar = {
            ShopTopAppbar(
                title = screenTitle,
                onBackSelected = { onBack() })
        },
        bottomBar = {
            AddToCartButton(
                product = successState?.item?.product,
                isLoading = uiState is ProductDetailUiState.Loading
            ) { productDetailViewModel.addToCart() }

        },
        snackbarHost = { SnackbarHost(snackBarHostState) }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }

                is ProductDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                is ProductDetailUiState.Success -> {
                    state.item?.let { productWithOffer ->
                        val product = productWithOffer.product
                        val offer = productWithOffer.offer
                        val discountedPrice = when (offer) {
                            is ProductOffer.BuyXPayY -> null
                            is ProductOffer.Percent -> offer.discountedPrice
                            null -> null
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    AsyncImage(
                                        model = product.imageUrl,
                                        contentDescription = product.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                        placeholder = rememberVectorPainter(Icons.Default.Image),
                                        error = rememberVectorPainter(Icons.Default.BrokenImage)
                                    )

                                    Text(
                                        product.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                product.category,
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                ),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            if (product.description.isNotBlank()) {
                                                Text(text = product.description)
                                            }
                                            HorizontalDivider()

                                            if (discountedPrice != null) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(//crossed out
                                                        product.price.toString(),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        textDecoration = TextDecoration.LineThrough
                                                    )
                                                    Text(
                                                        text = "$discountedPrice €",
                                                        style = MaterialTheme.typography.displaySmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                //Discount percent
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.errorContainer
                                                ) {
                                                    Text(
                                                        "${(offer as ProductOffer.Percent).percent.toInt()}% OFF",
                                                        modifier = Modifier.padding(
                                                            horizontal = 12.dp,
                                                            vertical = 6.dp
                                                        ),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                }

                                            } else {
                                                Text(
                                                    product.price.toString(),
                                                    style = MaterialTheme.typography.displaySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            if (offer is ProductOffer.BuyXPayY) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.errorContainer
                                                ) {
                                                    Text(
                                                        "OFFER 3x2 ${offer.label} OFF",
                                                        modifier = Modifier.padding(
                                                            horizontal = 12.dp,
                                                            vertical = 6.dp
                                                        ),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                }
                                            }

                                            HorizontalDivider()
                                            val hasStock = product.stock > 0
                                            val containerColor = if (hasStock) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.errorContainer
                                            }
                                            val contentColor = if (hasStock) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onErrorContainer
                                            }
                                            // Stock available
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                Text(
                                                    text = "Stock available",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = containerColor
                                                ) {
                                                    Text(
                                                        if (hasStock) "${product.stock} units" else "Out of stock",
                                                        modifier = Modifier.padding(
                                                            horizontal = 12.dp, vertical = 6.dp,
                                                        ),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = contentColor
                                                    )
                                                }

                                            }

                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}



