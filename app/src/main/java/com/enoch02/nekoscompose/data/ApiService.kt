package com.enoch02.nekoscompose.data

import com.enoch02.nekoscompose.data.model.Category
import com.enoch02.nekoscompose.data.model.NekoResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/** Reference [https://docs.nekos.best/api/endpoints.html] */
interface ApiService {

    @GET("endpoints")
    suspend fun getCategories(): Map<String, Category>

    @GET("neko")
    suspend fun getRandomNeko(@Query("amount") amount: String): NekoResult

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("type") type: String,
        @Query("category") category: String,
        @Query("amount") amount: String,
    ): NekoResult

    companion object {
        var apiService: ApiService? = null

        fun getInstance(): ApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("https://nekos.best/api/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}