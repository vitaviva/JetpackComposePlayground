package com.github.compose_playground.bloom

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import kotlinx.coroutines.flow.Flow

@Composable
fun PlantList(plants: Flow<PagingData<Plant>>) {
    val pagedPlantItems = plants.collectAsLazyPagingItems()

    LazyColumn {
        if (pagedPlantItems.loadState.refresh == LoadState.Loading) {
            item { LoadingIndicator() }
        }

        itemsIndexed(pagedPlantItems) { index, plant ->
            if (plant != null) {
                PlantItem(plant)
            } else {
                PlantPlaceholder()
            }

        }

        if (pagedPlantItems.loadState.append == LoadState.Loading) {
            item { LoadingIndicator() }
        }
    }
}


@Composable
fun LoadingIndicator() {

}

@Composable
fun PlantItem(plant: Plant) {

}

@Composable
fun PlantPlaceholder() {

}