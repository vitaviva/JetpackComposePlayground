package com.github.compose_playground.architecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.compose_playground.architecture.data.ArticleBean
import com.github.compose_playground.architecture.data.DataRepository
import com.github.compose_playground.architecture.ui.SearchBarScreen
import com.github.compose_playground.architecture.ui.SearchResultScreen
import com.github.compose_playground.architecture.ui.theme.ComposePlaygroundTheme
import com.github.compose_playground.architecture.ui.transparentStatusBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoArchitectureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                NoArchitectureApp()
            }
        }
        transparentStatusBar(this)
    }
}

@Composable
fun NoArchitectureApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            SearchBarScreen(
                onConfirm = {
                    val validatedAnswer = if (it.isBlank()) "wrong answer" else it
                    navController.navigate("result/$validatedAnswer")
                }
            )
        }
        composable(
            "result/{answer}",
            listOf(navArgument("answer") { type = NavType.StringType }),
        ) {
            NoArchitectureResultDestination(
                answer = it.arguments?.getString("answer") ?: ""
            )
        }
    }
}

@Composable
fun NoArchitectureResultDestination(
    answer: String
) {

    val isLoading = remember { mutableStateOf(false) }

    val dataRepository = remember { DataRepository() }

    var result: List<ArticleBean> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        isLoading.value = true
        result = withContext(Dispatchers.IO) { dataRepository.getArticlesList(answer).data.datas }
        isLoading.value = false
    }

    SearchResultScreen(result, isLoading.value , answer)

}