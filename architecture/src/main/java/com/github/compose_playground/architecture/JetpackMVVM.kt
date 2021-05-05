package com.github.compose_playground.architecture

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.compose_playground.architecture.data.ArticleBean
import com.github.compose_playground.architecture.data.DataRepository
import com.github.compose_playground.architecture.ui.SearchBarScreen
import com.github.compose_playground.architecture.ui.SearchResultScreen
import com.github.compose_playground.architecture.ui.theme.ComposePlaygroundTheme
import com.github.compose_playground.architecture.ui.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
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
class JetpackMvvmActivity : ComponentActivity() {

    val viewModel : JetpackMvvmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                JetpackMvvmApp(viewModel)
            }
        }
        transparentStatusBar(this)
    }
}

@Composable
fun JetpackMvvmApp(viewModel: JetpackMvvmViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            JetpackMvvmQuestionDestination(
                // You could pass the nav controller to further composables,
                // but I like keeping nav logic in a single spot by using the hoisting pattern
                // hoisting probably won't work as well in deep hierarchies,
                // in which case CompositionLocal might be more appropriate
                onConfirm = { navController.navigate("result") },
                viewModel
            )
        }
        composable("result") {
            JetpackMvvmResultDestination(viewModel)
        }
    }

}

@Composable
fun JetpackMvvmQuestionDestination(
    onConfirm: () -> Unit,
    jetpackMvvmViewModel: JetpackMvvmViewModel
) {
//    val jetpackMvvmViewModel: JetpackMvvmViewModel = viewModel()

    // We only want the event stream to be attached once
    // even if there are multiple re-compositions
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
//    val jetpackMvvmViewModel: JetpackMvvmViewModel = viewModel()

    val result by jetpackMvvmViewModel.result.collectAsState()

    if (jetpackMvvmViewModel.isLoading.value) {
        CircularProgressIndicator()
    } else {
        SearchResultScreen(
            result, jetpackMvvmViewModel.key.value
        )
    }
}

//@HiltViewModel
class JetpackMvvmViewModel/* @Inject*/ constructor(

) : ViewModel() {

    private val answerService: DataRepository = DataRepository()
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
            _isLoading.value = true
            _key.value = answer
            val result = withContext(Dispatchers.IO) { answerService.getArticlesList(answer) }
            _result.emit(result.data.datas)
            _navigateToResults.send(true)
            _isLoading.value = false
        }
    }
}
