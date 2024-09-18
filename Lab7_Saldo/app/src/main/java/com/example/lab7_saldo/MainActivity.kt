package com.example.lab7_saldo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis
import androidx.compose.foundation.layout.fillMaxSize

const val N = 100

class Account {
    private var amount: Double = 0.0
    private val mutex = Mutex()

    suspend fun deposit(amount: Double) {
        val x = this.amount
        delay(1)

        mutex.withLock {
            this.amount = x + amount
        }
    }

    fun saldo(): Double = amount
}

fun withTimeMeasurement(title: String, isActive: Boolean = true, code: () -> Unit) {
    if (!isActive) return
    val time = measureTimeMillis { code() }
    println("MSU: operation in '$title' took $time ms")
}

data class Saldos(val saldo1: Double, val saldo2: Double)

fun bankProcess(account: Account): Saldos {
    var saldo1: Double = 0.0
    var saldo2: Double = 0.0

    withTimeMeasurement("Single coroutine deposit $N times") {
        runBlocking {
            launch {
                for (i in 1..N) {
                    account.deposit(1.0)
                }
                saldo1 = account.saldo()
            }
        }
    }

    withTimeMeasurement("Two $N times deposit coroutines together") {
        runBlocking {
            launch {
                for (i in 1..N) {
                    account.deposit(1.0)
                }
            }
            launch {
                for (i in 1..N) {
                    account.deposit(1.0)
                }
            }
            saldo2 = account.saldo()
        }
    }

    return Saldos(saldo1, saldo2)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val results = bankProcess(Account())

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowResults(saldo1 = results.saldo1, saldo2 = results.saldo2)
                }
            }
        }
    }
}

@Composable
fun ShowResults(saldo1: Double, saldo2: Double) {
    Column {
        Text(text = "Saldo1: $saldo1")
        Text(text = "Saldo2: $saldo2")
    }
}