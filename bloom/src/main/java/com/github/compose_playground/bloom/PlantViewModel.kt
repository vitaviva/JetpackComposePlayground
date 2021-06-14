package com.github.compose_playground.bloom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class PlantViewModel @Inject constructor(
    plantsRepository: PlantsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val plantDetails: Flow<Plant> = plantsRepository.getPlantDetails(
        savedStateHandle.get<Int>("id")!!
    )
}
