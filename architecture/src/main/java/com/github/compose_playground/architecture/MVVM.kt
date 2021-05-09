package com.github.compose_playground.architecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.compose_playground.architecture.data.ArticleBean
import com.github.compose_playground.architecture.data.DataRepository
import com.github.compose_playground.architecture.ui.SearchBarScreen
import com.github.compose_playground.architecture.ui.SearchResultScreen
import com.github.compose_playground.architecture.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MvvmActivity : AppCompatActivity() {

    // To inject via dagger, pass the Provider into the nav graph
    // and then run .get() on the provider to create an instance
    private val mvvmViewModel = MvvmViewModel(DataRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                MvvmApp(mvvmViewModel)
            }
        }
    }
}

@Composable
fun MvvmApp(
    mvvmViewModel: MvvmViewModel
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            MvvmQuestionDestination(
                mvvmViewModel = mvvmViewModel,
                // You could pass the nav controller to further composables,
                // but I like keeping nav logic in a single spot by using the hoisting pattern
                // hoisting probably won't work as well in deep hierarchies,
                // in which case CompositionLocal might be more appropriate
                onConfirm = { navController.navigate("result") },
            )
        }
        composable("result") {
            MvvmResultDestination(
                mvvmViewModel,
            )
        }
    }
}

@Composable
fun MvvmQuestionDestination(
    mvvmViewModel: MvvmViewModel,
    onConfirm: () -> Unit
) {
    LaunchedEffect(Unit) {
        mvvmViewModel.navigateToResults
            .onEach { onConfirm() }
            .collect()
    }

    SearchBarScreen {
        mvvmViewModel.confirmAnswer(it)
    }

}

@Composable
fun MvvmResultDestination(
    mvvmViewModel: MvvmViewModel
) {

    val result by mvvmViewModel.result.collectAsState()
    val isLoading by mvvmViewModel.isLoading.collectAsState()

    SearchResultScreen(result, isLoading, mvvmViewModel.key.value)


}

class MvvmViewModel(
    private val answerService: DataRepository,
) {

    private val coroutineScope = MainScope()
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
        coroutineScope.launch {
            _isLoading.value = true
            _key.value = answer
            val result = withContext(Dispatchers.IO) { answerService.getArticlesList(answer) }
            _result.emit(result.data.datas)
            _navigateToResults.send(true)
            _isLoading.value = false
        }
    }
}

