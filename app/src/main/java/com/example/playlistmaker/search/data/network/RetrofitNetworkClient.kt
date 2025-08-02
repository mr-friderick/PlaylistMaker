package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val iTunesService: ItunesAPI
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        val result = withContext(Dispatchers.IO) {
            kotlin.runCatching {
                when(dto) {
                    is TracksSearchRequest -> {
                        val response = iTunesService.search(dto.expression)
                        response.apply { resultCode = HttpStatus.OK }
                    }
                    else -> Response().apply { resultCode = HttpStatus.BAD_REQUEST }
                }
            }.getOrDefault(
                Response().apply { resultCode = HttpStatus.NOT_CONNECTION }
            )
        }
        return result
    }
}