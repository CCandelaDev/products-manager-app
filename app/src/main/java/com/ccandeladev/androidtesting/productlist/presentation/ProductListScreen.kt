package com.ccandeladev.androidtesting.productlist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProductListScreen(productListViewModel: ProductListViewModel = hiltViewModel()) {
    // To get hooked on the life cycle
    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

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
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        when (val uiState = uiState) {
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
                Column (
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                ){
                    Text("Show Products")
                }
            }
        }

    }
}