package com.github.compose_playground.architecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

class MviActivity : AppCompatActivity() {

    private val mviViewModel = MviViewModel(DataRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                MviApp(mviViewModel)
            }
        }
    }
}

@Composable
fun MviApp(
    mviViewModel: MviViewModel
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            MviQuestionDestination(
                mviViewModel = mviViewModel,
                // You could pass the nav controller to further composables,
                // but I like keeping nav logic in a single spot by using the hoisting pattern
                // hoisting probably won't work as well in deep hierarchies,
                // in which case CompositionLocal might be more appropriate
                onConfirm = { navController.navigate("result") },
            )
        }
        composable("result") {
            MviResultDestination(
                mviViewModel,
            )
        }
    }
}

@Composable
fun MviQuestionDestination(
    mviViewModel: MviViewModel,
    onConfirm: () -> Unit
) {
    LaunchedEffect(Unit) {
        mviViewModel.navigateToResults
            .onEach {
                when (it) {
                    MviViewModel.OneShotEvent.NavigateToResults -> onConfirm()
                }
            }
            .collect()
    }

    SearchBarScreen {
        mviViewModel.onAction(MviViewModel.UiAction.AnswerConfirmed(it))
    }

}

@Composable
fun MviResultDestination(
    mviViewModel: MviViewModel
) {
    val viewState by mviViewModel.viewState.collectAsState()

    SearchResultScreen(
        viewState.result, viewState.isLoading, viewState.key
    )

}

class MviViewModel(
    private val answerService: DataRepository,
) {

    private val coroutineScope = MainScope()

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _navigateToResults = Channel<OneShotEvent>(Channel.BUFFERED)
    val navigateToResults = _navigateToResults.receiveAsFlow()

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.AnswerConfirmed -> {
                coroutineScope.launch {
                    _viewState.value = _viewState.value.copy(isLoading = true)
                    val result =
                        withContext(Dispatchers.IO) { answerService.getArticlesList(uiAction.answer) }
                    _viewState.value =
                        _viewState.value.copy(result = result.data.datas, key = uiAction.answer)
                    _navigateToResults.send(OneShotEvent.NavigateToResults)
                    _viewState.value = _viewState.value.copy(isLoading = false)
                }
            }
        }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val result: List<ArticleBean> = emptyList(),
        val key: String = ""
    )

    sealed class OneShotEvent {
        object NavigateToResults : OneShotEvent()
    }

    sealed class UiAction {
        class AnswerConfirmed(val answer: String) : UiAction()
    }
}
