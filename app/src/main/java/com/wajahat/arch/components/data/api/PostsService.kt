package com.wajahat.arch.components.data.api

import com.wajahat.arch.components.data.model.response.PostsResponse
import com.wajahat.arch.components.data.model.response.SubscribersCountResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PostsService {

    @GET("discover.wordpress.com/posts")
    suspend fun getPosts(@Query("number") number: Int): PostsResponse

    @GET
    suspend fun getSubscribersCount(@Url url: String): SubscribersCountResponse
}