package com.github.compose_playground.architecture.data

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@ViewModelScoped
class DataRepository @Inject constructor(){

    private val okhttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }


    suspend fun getArticlesList(key: String) =
        apiService.getArticlesList(key)
}