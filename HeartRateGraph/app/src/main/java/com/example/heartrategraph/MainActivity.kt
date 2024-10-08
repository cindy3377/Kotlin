package com.example.heartrategraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.heartrategraph.ui.theme.HeartRateGraphTheme
import java.security.KeyStore.Entry


class MainActivity : ComponentActivity() {

    // ViewModel to hold heart rate data
    private val heartRateViewModel: HeartRateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeartRateGraphTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pass ViewModel to the Composable
                    HeartRateApp(
                        modifier = Modifier.padding(innerPadding),
                        heartRateViewModel = heartRateViewModel
                    )
                }
            }
        }

        heartRateViewModel.addHeartRateValue(72)
        heartRateViewModel.addHeartRateValue(75)
        heartRateViewModel.addHeartRateValue(78)
    }
}

class HeartRateViewModel : ViewModel() {
    private val _heartRateValues = MutableLiveData<MutableList<Int>>(mutableListOf())
    val heartRateValues: LiveData<MutableList<Int>> = _heartRateValues

    fun addHeartRateValue(value: Int) {
        val currentList = _heartRateValues.value ?: mutableListOf()
        currentList.add(value)
        _heartRateValues.postValue(currentList)
    }
}

@Composable
fun HeartRateApp(modifier: Modifier = Modifier, heartRateViewModel: HeartRateViewModel) {
    val heartRateValues = heartRateViewModel.heartRateValues.observeAsState(emptyList())

    // Display the list of heart rate values
    Column(modifier = modifier.fillMaxSize()) {
        heartRateValues.value.forEachIndexed { index, bpm ->
            Text(text = "Measurement $index: $bpm BPM")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
        }) {
            Text(text = "Show Heart Rate Graph")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HeartRateGraphTheme {
        HeartRateApp(heartRateViewModel = HeartRateViewModel())  // Use an empty ViewModel for preview
    }
}

@Composable
fun HeartRateGraph(modifier: Modifier = Modifier, heartRateValues: List<Int>) {
    val entries = heartRateValues.mapIndexed { index, value ->
        Entry(index.toFloat(), value.toFloat())  // Measurement index as X, BPM as Y
    }

    val dataSet = LineDataSet(entries, "Heart Rate (BPM)")
    val lineData = LineData(dataSet)

    val chart = LineChart(LocalContext.current)
    chart.data = lineData
    chart.invalidate()  // Refresh the chart

    AndroidView(
        factory = { chart },
        modifier = modifier.fillMaxSize()
    )
}
