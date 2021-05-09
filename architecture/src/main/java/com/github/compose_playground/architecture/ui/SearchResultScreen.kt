package com.github.compose_playground.architecture.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.compose_playground.architecture.data.ArticleBean

@Preview
@Composable
fun SearchResultScreen(
    result: List<ArticleBean> = emptyList(),
    isLoading: Boolean = false,
    answer: String = ""
) {

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {

        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

            Text("Result of searching $answer:")

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
            )

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

    }


}
