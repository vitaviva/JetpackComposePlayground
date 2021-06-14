package com.github.compose_playground.bloom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val plantCollections: List<Collection<Plant>> = emptyList(),
    val loading: Boolean = false,
    val refreshError: Boolean = false,
    val carouselState: CollectionsCarouselState = CollectionsCarouselState(emptyList())
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plantsRepository: PlantsRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(HomeUiState(loading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    val pagedPlants: Flow<PagingData<Plant>> = plantsRepository.plants

    init {

        viewModelScope.launch {
            val collections = plantsRepository.getCollections()
            _uiState.value = HomeUiState(plantCollections = collections)
        }
    }
}