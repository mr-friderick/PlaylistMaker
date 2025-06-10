package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
): TracksRepository {

    override fun searchTracks(expression: String): Pair<ArrayList<Track>, Boolean> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        val result = when (response.resultCode) {
            HttpStatus.OK -> {
                val foundTracks = (response as TracksSearchResponse).results
                    .map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeToMMSS(),
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl
                        )
                    }
                    .toCollection(ArrayList())
                foundTracks to false
            }
            HttpStatus.NOT_CONNECTION ->  {
                arrayListOf<Track>() to true
            }
            else -> {
                arrayListOf<Track>() to false
            }
        }
        return result
    }
}