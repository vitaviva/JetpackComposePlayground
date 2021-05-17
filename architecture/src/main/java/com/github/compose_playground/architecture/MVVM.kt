package com.github.compose_playground.architecture

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.github.compose_playground.architecture.data.ArticleBean
import com.github.compose_playground.architecture.data.DataRepository
import com.github.compose_playground.architecture.ui.SearchBarScreen
import com.github.compose_playground.architecture.ui.SearchResultScreen
import com.github.compose_playground.architecture.ui.theme.ComposePlaygroundTheme
import com.github.compose_playground.architecture.ui.theme.DestSearchBar
import com.github.compose_playground.architecture.ui.theme.DestSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

    LaunchedEffect(Unit) {
        with(mvvmViewModel) {
            bindNavStack(navController)
            destination
                .collect {
                    navController.navigate(it)
                }
        }
    }

    NavHost(navController, startDestination = DestSearchBar) {
        composable(DestSearchBar) {
            MvvmSearchBarScreen(
                mvvmViewModel = mvvmViewModel,
            )
        }
        composable(DestSearchResult) {
            MvvmSearchResultScreen(
                mvvmViewModel,
            )
        }
    }
}

@Composable
fun MvvmSearchBarScreen(
    mvvmViewModel: MvvmViewModel
) {
    SearchBarScreen {
        mvvmViewModel.searchKeyword(it)
    }

}

@Composable
fun MvvmSearchResultScreen(
    mvvmViewModel: MvvmViewModel
) {

    val result by mvvmViewModel.result.collectAsState()
    val isLoading by mvvmViewModel.isLoading.collectAsState()

    SearchResultScreen(result, isLoading, mvvmViewModel.key.value)


}

class MvvmViewModel(
    private val searchService: DataRepository,
) {

    private val coroutineScope = MainScope()
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _result: MutableStateFlow<List<ArticleBean>> = MutableStateFlow(emptyList())
    val result = _result.asStateFlow()
    private val _key = MutableStateFlow("")
    val key = _key.asStateFlow()
    private val _destination = MutableStateFlow(DestSearchBar)
    val destination = _destination.asStateFlow()


    fun searchKeyword(input: String) {
        coroutineScope.launch {
            _destination.value = DestSearchResult
            _isLoading.value = true
            _key.value = input
            val result = withContext(Dispatchers.IO) { searchService.getArticlesList(input) }
            _result.emit(result.data.datas)
            _isLoading.value = false
        }
    }

    fun bindNavStack(navController: NavController) {
        navController.addOnDestinationChangedListener { _, _, arguments ->
            run {
                _destination.value = requireNotNull(arguments?.getString(KEY_ROUTE))
            }
        }
    }
}

