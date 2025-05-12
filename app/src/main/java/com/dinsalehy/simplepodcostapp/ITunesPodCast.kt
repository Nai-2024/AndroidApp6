package com.dinsalehy.simplepodcostapp

import retrofit2.http.GET
import retrofit2.http.Query

data class ITunesPodcast(
    val collectionId: Long, // Unique ID of the podcast
    val collectionName: String, // Name/title of the podcast
    val artistName: String, // Name of the creator/artist
    val artworkUrl100: String, // URL to a 100x100 image of the podcast cover
    val collectionViewUrl: String? // URL to open the podcast in a browser (nullable)
)

data class ITunesResponse(
    val results: List<ITunesPodcast> // List of podcast items returned by the search
)

interface PodcastApiService {
    // Performs a GET request to search for podcasts
    @GET("search?media=podcast")
    suspend fun searchPodcasts(@Query("term") term: String): ITunesResponse
    // 'term' is passed dynamically when calling this function
}
