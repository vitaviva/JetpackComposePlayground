package com.github.compose_playground.architecture.data

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    /**
     * 搜索
     */
    @FormUrlEncoded
    @POST("article/query/0/json")
    suspend fun getArticlesList(
        @Field("k") key: String?
    ): ApiResponse<Page<List<ArticleBean>>>


}
