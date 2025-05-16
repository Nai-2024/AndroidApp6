package com.dinsalehy.simplepodcostapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: PodcastViewModel = viewModel()

            NavHost(navController = navController, startDestination = "landing") {
                composable("landing") {
                    LandingPage(onStartClick = {
                        navController.navigate("main")
                    })
                }
                composable("main") {
                    PodcastApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun LandingPage(onStartClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFEDE7F6))
        .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        // Welcome text
        Text(
            text = "🎧 Welcome to Nai PodCast!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Welcome image (use any URL or local drawable)
        Image(
            painter = rememberAsyncImagePainter(R.drawable.podcost),
            contentDescription = "Podcast image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(6.dp))
                .align(androidx.compose.ui.Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )

        // Start button
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .padding(top = 32.dp)
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
        ) {
            Text("Start Exploring Podcasts")
        }
    }
}


@Composable
fun PodcastApp(viewModel: PodcastViewModel) {
    // Holds the search term entered by the user, default is "technology"
    var searchTerm by remember { mutableStateOf("technology") }

    // Collects the list of podcasts from the ViewModel as state
    val podcasts by viewModel.podcasts.collectAsState()

    // Main vertical layout
    Column(modifier = Modifier.padding(16.dp)) {

        // Text field for user input (search)
        OutlinedTextField(
            value = searchTerm, // Current text input
            onValueChange = { searchTerm = it }, // Update value on change
            label = { Text("Search podcasts") }, // Label shown above input
            modifier = Modifier.fillMaxWidth() // Take full width
        )

        // Search button that triggers the ViewModel to fetch data
        Button(onClick = { viewModel.search(searchTerm) }) {
            Text("Search") // Button text
        }

        // List layout that scrolls vertically
        LazyColumn {
            // Break podcast list into groups of 2 to create rows
            items(podcasts.chunked(2)) { pair ->

                // A row to display 2 podcast items side-by-side
                Row(modifier = Modifier.fillMaxWidth()) {

                    // First podcast in the row
                    PodcastGridItem(podcast = pair[0], modifier = Modifier.weight(1f))

                    // If there’s a second podcast in the pair, show it
                    if (pair.size > 1) {
                        PodcastGridItem(podcast = pair[1], modifier = Modifier.weight(1f))
                    } else {
                        // If only one item, fill empty space with Spacer
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Composable
fun PodcastGridItem(podcast: ITunesPodcast, modifier: Modifier = Modifier) {
    val context = LocalContext.current // Get Android context for launching browser

    // Layout for each podcast item (image + text)
    Column(
        modifier = modifier
            .padding(8.dp) // Space around each item
            .clickable {
                // When clicked, open the podcast link in a browser
                podcast.collectionViewUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
    ) {
        // Show podcast image from URL using Coil
        Image(
            painter = rememberAsyncImagePainter(podcast.artworkUrl100), // Load image from URL
            contentDescription = podcast.collectionName, // Accessibility description
            modifier = Modifier
                .size(120.dp) // Image size
                .align(androidx.compose.ui.Alignment.CenterHorizontally), // Center image horizontally
            contentScale = ContentScale.Crop // Crop image to fit
        )

        // Show podcast title
        Text(
            text = podcast.collectionName, // Title text
            style = MaterialTheme.typography.titleMedium, // Use medium style
            modifier = Modifier
                .padding(top = 8.dp) // Space above text
                .align(androidx.compose.ui.Alignment.CenterHorizontally) // Center text
        )

        // Show artist/creator name
        Text(
            text = podcast.artistName, // Artist name text
            style = MaterialTheme.typography.bodySmall, // Small font
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally) // Center text
        )
    }
}
