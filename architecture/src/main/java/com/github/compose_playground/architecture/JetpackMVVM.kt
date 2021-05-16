package com.github.compose_playground.architecture

import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.compose_playground.architecture.data.ArticleBean
import com.github.compose_playground.architecture.data.DataRepository
import com.github.compose_playground.architecture.ui.SearchBarScreen
import com.github.compose_playground.architecture.ui.SearchResultScreen
import com.github.compose_playground.architecture.ui.theme.ComposePlaygroundTheme
import com.github.compose_playground.architecture.util.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltAndroidApp
class JetpackMvvmApplication : Application()

@AndroidEntryPoint
class JetpackMvvmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                JetpackMvvmApp()
            }
        }
    }
}

@Composable
fun JetpackMvvmApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question", route = "root") {
        composable("question") {
            JetpackMvvmQuestionDestination(
                // You could pass the nav controller to further composables,
                // but I like keeping nav logic in a single spot by using the hoisting pattern
                // hoisting probably won't work as well in deep hierarchies,
                // in which case CompositionLocal might be more appropriate
                onConfirm = { navController.navigate("result") },
                viewModel(navController, "root")
            )
        }
        composable("result") {

            JetpackMvvmResultDestination(
                viewModel(navController, "root")
            )
        }
    }

}


@Composable
fun JetpackMvvmQuestionDestination(
    onConfirm: () -> Unit,
    jetpackMvvmViewModel: JetpackMvvmViewModel
) {

    LaunchedEffect(Unit) {
        jetpackMvvmViewModel.navigateToResults
            .onEach { onConfirm() }
            .collect()
    }

    SearchBarScreen {
        jetpackMvvmViewModel.confirmAnswer(it)
    }

}

@Composable
fun JetpackMvvmResultDestination(
    jetpackMvvmViewModel: JetpackMvvmViewModel
) {

    val result by jetpackMvvmViewModel.result.collectAsState()
    val isLoading by jetpackMvvmViewModel.isLoading.collectAsState()


    SearchResultScreen(
        result, isLoading, jetpackMvvmViewModel.key.value
    )
}

@HiltViewModel
class JetpackMvvmViewModel @Inject constructor(
    private val answerService: DataRepository
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _result: MutableStateFlow<List<ArticleBean>> = MutableStateFlow(emptyList())
    val result = _result.asStateFlow()
    private val _key = MutableStateFlow("")
    val key = _key.asStateFlow()


    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _navigateToResults = Channel<Boolean>(Channel.BUFFERED)
    val navigateToResults = _navigateToResults.receiveAsFlow()

    fun confirmAnswer(answer: String) {
        viewModelScope.launch {
            _navigateToResults.send(true)
            _isLoading.value = true
            _key.value = answer
            delay(200)
            val result = withContext(Dispatchers.IO) { answerService.getArticlesList(answer) }
            _result.emit(result.data.datas)
            _isLoading.value = false
        }
    }
}
