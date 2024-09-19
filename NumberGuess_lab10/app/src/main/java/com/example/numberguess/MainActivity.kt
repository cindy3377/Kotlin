package com.example.numberguess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class NumberGuessing : ComponentActivity() {

    private val secretNumber = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumberGuessingUI(secretNumber)
        }
    }
}

@Composable
fun NumberGuessingUI(secretNumber: Int) {
    var userGuess by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("Result will appear here") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userGuess,
            onValueChange = { userGuess = it },
            label = { Text("Enter your guess") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            resultMessage = try {
                val guess = userGuess.toInt()
                when {
                    guess > secretNumber -> "Too high! Try a lower number."
                    guess < secretNumber -> "Too low! Try a higher number."
                    else -> "Congratulations! You've guessed the correct number."
                }
            } catch (e: NumberFormatException) {
                "Invalid input. Please enter a valid number."
            }
        }) {
            Text("Submit Guess")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultMessage)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNumberGuessingUI() {
    NumberGuessingUI(secretNumber = 4)
}
