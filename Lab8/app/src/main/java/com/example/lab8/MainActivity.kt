package com.example.lab8

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkImageCoroutineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("This is a special test file")
                        Spacer(modifier = Modifier.height(48.dp))
                        LoadImageFromNetwork()
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkImageCoroutineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

@Composable
fun LoadImageFromNetwork() {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launch a coroutine to download the image
    LaunchedEffect(Unit) {
        bitmap = downloadImage("https://users.metropolia.fi/~jarkkov/folderimage.jpg")
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        bitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null)
        } ?: Text("Loading image...")
    }
}

suspend fun downloadImage(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val inputStream: InputStream = url.openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NetworkImageCoroutineTheme {
        LoadImageFromNetwork()
    }
}
