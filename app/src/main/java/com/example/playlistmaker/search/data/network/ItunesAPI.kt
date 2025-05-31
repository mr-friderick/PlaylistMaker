package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search")
    fun search(@Query("term") text: String, @Query("entity") entity: String = "song"): Call<TracksSearchResponse>
}