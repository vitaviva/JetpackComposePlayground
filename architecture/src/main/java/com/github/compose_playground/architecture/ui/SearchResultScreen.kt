package com.github.compose_playground.architecture.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.compose_playground.architecture.data.ArticleBean

@Composable
fun SearchResultScreen(result: List<ArticleBean>, answer:String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        result.forEach { articleBean ->

            ResultItem(articleBean.title ?: "", articleBean.desc ?: "", answer)
        }
    }
}
