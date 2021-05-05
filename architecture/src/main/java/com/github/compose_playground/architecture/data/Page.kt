package com.github.compose_playground.architecture.data

data class Page<T>(
    val curPage: Int,
    val offset: Int,
    val pageCount: Int,
    val size: Int,
    val total: Int,
    val datas: T
)