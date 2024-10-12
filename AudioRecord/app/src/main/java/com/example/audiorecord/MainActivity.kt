package com.example.audiorecord

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.AudioManager
import android.media.AudioTrack
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AudioRecorder(private val context: Context) {
    private val sampleRate = 44100 // Sample rate in Hz
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO // Mono input
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT // 16-bit PCM
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    fun startRecording() {
        // Check for permission before starting recording
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission denied for audio recording", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize)
            audioRecord?.startRecording()

            isRecording = true
            val audioData = ByteArray(bufferSize)

            Thread {
                val outputStream = FileOutputStream(File(context.filesDir, "audio_record.pcm"))
                while (isRecording) {
                    val read = audioRecord?.read(audioData, 0, audioData.size)
                    if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                        outputStream.write(audioData, 0, read ?: 0)
                    }
                }
                outputStream.close()
            }.start()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}

class AudioPlayer(private val context: Context) {
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioTrack: AudioTrack? = null

    fun startPlaying() {
        // Check for permission before playing audio
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission denied for audio playback", Toast.LENGTH_SHORT).show()
            return
        }

        // Wrap the code in try-catch to handle potential SecurityException
        try {
            val file = File(context.filesDir, "audio_record.pcm")
            val inputStream = FileInputStream(file)
            val audioData = ByteArray(bufferSize)

            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM
            )
            audioTrack?.play()

            Thread {
                var read: Int
                while (inputStream.read(audioData).also { read = it } != -1) {
                    audioTrack?.write(audioData, 0, read)
                }
                inputStream.close()
            }.start()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Failed to start playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopPlaying() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer

    // Define ActivityResultLauncher for permission request
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AudioRecorder and AudioPlayer
        audioRecorder = AudioRecorder(this)
        audioPlayer = AudioPlayer(this)

        val recordButton = findViewById<Button>(R.id.recordButton)
        val stopRecordButton = findViewById<Button>(R.id.stopRecordButton)
        val playButton = findViewById<Button>(R.id.playButton)

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted, start recording
                audioRecorder.startRecording()
            } else {
                // Permission is denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        recordButton.setOnClickListener {
            if (hasPermissions()) {
                audioRecorder.startRecording()
            } else {
                requestPermission()
            }
        }

        stopRecordButton.setOnClickListener {
            audioRecorder.stopRecording()
        }

        playButton.setOnClickListener {
            audioPlayer.startPlaying()
        }
    }

    // Method to check for permissions
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}
