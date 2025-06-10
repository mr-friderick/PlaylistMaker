package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(
    private val iTunesService: ItunesAPI
) : NetworkClient {

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