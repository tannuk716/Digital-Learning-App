package com.tannu.edureach

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.playerView)

        val videoUrl = intent.getStringExtra("VIDEO_URL")
        val isOffline = intent.getBooleanExtra("IS_OFFLINE", false)

        if (videoUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializePlayer(videoUrl, isOffline)

        com.tannu.edureach.utils.ProgressManager.updateStreak()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    private fun initializePlayer(url: String, isOffline: Boolean) {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true

        player?.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Toast.makeText(this@VideoPlayerActivity, "Error playing video: \${error.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                val distance = event.values[0]
                if (distance < 5f && distance < (proximitySensor?.maximumRange ?: 5f)) {

                    player?.pause()
                } else {
                    player?.play()
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}