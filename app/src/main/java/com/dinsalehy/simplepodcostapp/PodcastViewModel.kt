package com.dinsalehy.simplepodcostapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Define a ViewModel class to manage podcast data for the UI
class PodcastViewModel: ViewModel() {

    // A private mutable state flow holding a list of podcasts- initially empty
    private val _podcasts = MutableStateFlow<List<ITunesPodcast>>(emptyList())

    // It shares the list of podcasts with the UI in a way that it can be read but not changed from outside.
    val podcasts: StateFlow<List<ITunesPodcast>> = _podcasts

    // Lazily initialize the Retrofit API service for making HTTP requests to the iTunes API
    private val api: PodcastApiService by lazy {
        Retrofit.Builder() // Start building the Retrofit instance
            .baseUrl("https://itunes.apple.com/") // Set the base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Add a converter to parse JSON using Gson
            .build() // Build the Retrofit instance
            .create(PodcastApiService::class.java) // Create an implementation of the PodcastApiService interface
    }

    // A function to search for podcasts based on a search term
    fun search(term: String) {
        // Launch a coroutine in the ViewModel's scope
        viewModelScope.launch {
            try {
                // Call the API to search for podcasts using the given term
                val response = api.searchPodcasts(term)

                // Filter out any podcast that doesn't have a valid URL and update the state
                _podcasts.value = response.results.mapNotNull { podcast ->
                    if (podcast.collectionViewUrl != null) podcast else null
                }
            } catch (e: Exception) {
                // Log the error and reset the list to empty if the request fails
                Log.e("PodcastViewModel", "Search failed", e)
                _podcasts.value = emptyList()
            }
        }
    }
}
