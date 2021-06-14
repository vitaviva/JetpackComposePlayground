package com.github.compose_playground.bloom

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPlantClick: (Plant) -> Unit
) {

    SearchPlants()
    CollectionCarousel(carouselState = uiState.carouselState) {
        onPlantClick(it)
    }

    if (uiState.loading) {

    } else {

    }
}


@Composable
fun SearchPlants() {

}
