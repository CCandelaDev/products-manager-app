package com.ccandeladev.androidtesting.detail.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor() : ViewModel() {

    //For the states
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    //For the events
    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    //Load product
    fun loadProduct(productId: String){

    }

    // For persistence
    fun addToCart(){

    }
}