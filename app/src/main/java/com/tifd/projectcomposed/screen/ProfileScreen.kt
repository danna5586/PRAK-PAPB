package com.tifd.projectcomposed.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tifd.projectcomposed.data.GithubUser
import com.tifd.projectcomposed.data.RetrofitInstance

@Composable
fun ProfileScreen(username: String) {
    val githubService = RetrofitInstance.api
    var profile by remember { mutableStateOf<GithubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            profile = githubService.getUser(username)
            isLoading = false
        } catch (e: Exception) {
            isError = true
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (isError) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Failed to load GitHub profile", color = Color.Red)
        }
    } else {
        profile?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Gambar profil bulat
                Image(
                    painter = rememberAsyncImagePainter(it.avatarUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape) // Membuat gambar menjadi bulat
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape), // Menambahkan border
                    contentScale = ContentScale.Crop // Memastikan gambar sesuai dengan bentuk lingkaran
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Menampilkan informasi pengguna
                Text(text = "Username: ${it.username}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Name: ${it.name ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Followers: ${it.followers}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Following: ${it.following}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}