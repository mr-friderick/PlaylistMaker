package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    companion object {
        const val ITUNES_BASE_URL_SEARCH = "https://itunes.apple.com/"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL_SEARCH)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        val result = when(dto) {
            is TracksSearchRequest -> {
                kotlin.runCatching {
                    val response = iTunesService.search(dto.expression).execute()
                    val body = response.body() ?: Response()
                    body.apply { resultCode = response.code() }
                }.getOrDefault(Response().apply { resultCode = HttpStatus.NOT_CONNECTION })
            }
            else -> Response().apply { resultCode = HttpStatus.BAD_REQUEST }
        }
        return result
    }
}