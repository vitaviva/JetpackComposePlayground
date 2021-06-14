package com.github.compose_playground.bloom

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BloomAcivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setContent {

            val navController = rememberNavController()

            Scaffold(
                bottomBar = {/*...*/ }
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable(route = "home") {
                        val homeViewModel: HomeViewModel = hiltNavGraphViewModel()
                        val uiState by homeViewModel.uiState.collectAsState()
                        val plantList = homeViewModel.pagedPlants
                        HomeScreen(uiState = uiState) { plant ->
                            navController.navigate("plant/${plant.id}")
                        }
                    }
                    composable(
                        route = "plant/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) {
                        val plantViewModel: PlantViewModel = hiltNavGraphViewModel()
                        val plant: Plant by plantViewModel.plantDetails.collectAsState(Plant(0))
                        PlantDetailScreen(plant = plant)
                    }
                }
            }


        }
    }
}