package com.ccandeladev.androidtesting.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ccandeladev.androidtesting.cart.domain.usecase.AddToCartUseCase
import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.core.domain.model.AppError.DataBaseError
import com.ccandeladev.androidtesting.core.domain.model.AppError.NetworkError
import com.ccandeladev.androidtesting.core.domain.model.AppError.NotFoundError
import com.ccandeladev.androidtesting.core.domain.model.AppError.UnknownError
import com.ccandeladev.androidtesting.core.domain.model.AppError.Validation
import com.ccandeladev.androidtesting.detail.domain.usecase.GetProductDetailWithOfferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailWithOfferUseCase: GetProductDetailWithOfferUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) :
    ViewModel() {
    private val productIdState: MutableStateFlow<String?> = MutableStateFlow(null) //Initial state


    val uiState: StateFlow<ProductDetailUiState> = productIdState
        .filterNotNull() //
        .flatMapLatest { productId ->
            flow {
                try {
                    getProductDetailWithOfferUseCase(productId = productId).collect { product ->
                        emit(ProductDetailUiState.Success(product))
                    }
                } catch (e: Throwable) {
                    emit(ProductDetailUiState.Error(e.message ?: "Unknown error"))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProductDetailUiState.Loading
        )


    //For the events
    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    //Setter for productId
    fun setProductId(productId: String) {
        productIdState.value = productId
    }


    // For persistence
    fun addToCart() {
        val snapShot = uiState.value
        val product = (snapShot as? ProductDetailUiState.Success)?.item?.product?.id ?: return

        viewModelScope.launch {
            try {
                addToCartUseCase(productId = product)
                _events.emit(ProductDetailEvent.SUCCESS_ADD_TO_CART)
            } catch (e: AppError) {
                handleError(e)
            } catch (e: Exception) {
                handleError(UnknownError(e.message))
            }
        }
    }

    private suspend fun handleError(e: AppError) {
        val newEvent = when (e) {
            NetworkError -> {
                ProductDetailEvent.NETWORK_ERROR
            }

            is Validation.InsufficientStock -> {
                ProductDetailEvent.INSUFICIENT_STOCK_ERROR
            }

            is UnknownError, DataBaseError, NotFoundError, Validation.QuantityMustBePositive -> {
                ProductDetailEvent.UNKNOWN_ERROR
            }

        }
        _events.emit(newEvent)
    }

}
