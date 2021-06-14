package com.github.compose_playground.bloom

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PlantsRepository {

    fun getCollections(): List<Collection<Plant>>

    val plants: Flow<PagingData<Plant>>

    fun getPlantDetails(id: Int): Flow<Plant>
}

data class Plant(val id: Int)
