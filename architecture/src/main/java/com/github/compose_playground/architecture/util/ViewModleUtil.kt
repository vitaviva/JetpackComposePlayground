package com.github.compose_playground.architecture.util

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.getBackStackEntry


@Composable
inline fun <reified VM : ViewModel> viewModel(
    navController: NavController,
    graphId: String = ""
): VM =
    hiltNavGraphViewModel(
        backStackEntry = navController.getBackStackEntry(graphId)
    )

