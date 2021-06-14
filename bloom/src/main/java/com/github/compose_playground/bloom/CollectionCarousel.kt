package com.github.compose_playground.bloom

import androidx.annotation.IdRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun CollectionCarousel(
    carouselState: CollectionsCarouselState,
    onPlantClick: (Plant) -> Unit
) {

    Row(
        Modifier.scrollable(
            state = rememberScrollableState { 0f },
            orientation = Orientation.Horizontal
        )
    ) {
        carouselState.plants.forEach { plant ->
            CarouselItem(Modifier.clickable {
                onPlantClick(plant)
            })
        }
    }
    if (carouselState.isExpended) {
        //...
    }

}

@Composable
fun CarouselItem(modifier: Modifier) {

}

data class PlantCollection(
    val name: String,
    @IdRes val asset: Int,
    val plants: List<Plant>
)

class CollectionsCarouselState(
    private val collections: List<PlantCollection>
) {
    var selectedIndex: Int? by mutableStateOf(null)
        private set

    val isExpended: Boolean
        get() = selectedIndex != null

    var plants by mutableStateOf(emptyList<Plant>())
        private set

    //...

    fun onCollectionClick(index: Int) {
        if (index >= collections.size || index < 0) return
        if (index == selectedIndex) {
            selectedIndex = null
        } else {
            plants = collections[index].plants
            selectedIndex = index
        }
    }
}