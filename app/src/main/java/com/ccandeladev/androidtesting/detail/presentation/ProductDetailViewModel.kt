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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailWithOfferUseCase: GetProductDetailWithOfferUseCase,
    private val addToCartUseCase: AddToCartUseCase
) :
    ViewModel() {

    //For the states
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    //For the events
    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    // Keeps a reference to the current loading job so we can cancel it
    // if loadProduct is called again before the previous one finishes
    private var productJob: Job? = null // Job

    //Load product
    fun loadProduct(productId: String) {

        _uiState.value = _uiState.value.copy(isLoading = true)

        // Keeps a reference to the current loading job so we can cancel it
        // if loadProduct is called again before the previous one finishes
        productJob?.cancel()

        productJob = getProductDetailWithOfferUseCase(productId = productId)
            .onEach { product ->
                // Update the UI state with the new product and hide the loading indicator
                _uiState.value = _uiState.value.copy(item = product, isLoading = false)
            }
            .catch { e: Throwable ->
                // Hide loading and emit a one-time error event to the UI
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (e is AppError) {
                    handleError(e)
                } else {
                    handleError(UnknownError(e.message))
                }
            }
            // Launch the flow in viewModelScope so it is automatically
            // canceled when the ViewModel is destroyed
            .launchIn(viewModelScope)
    }

    // For persistence
    fun addToCart() {
        val product = _uiState.value.item?.product?.id ?: return
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
