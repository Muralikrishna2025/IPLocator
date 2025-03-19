package uk.ac.tees.mad.iplocator.ui.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

@Composable
fun RememberShakeSensor(
    debounceTime: Long = 2000, // 2 seconds between shakes
    onShakeDetected: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    var lastShakeTimestamp by remember { mutableStateOf(0L) }

    val sensorListener = remember {
        object : SensorEventListener {
            private val shakeThreshold = 30f
            private var lastX = 0f
            private var lastY = 0f
            private var lastZ = 0f
            private var lastUpdate: Long = 0

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdate > 100) { // Throttle events to every 100ms
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val deltaX = x - lastX
                        val deltaY = y - lastY
                        val deltaZ = z - lastZ

                        val speed = sqrt(
                            (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()
                        )

                        if (speed > shakeThreshold) {
                            if (currentTime - lastShakeTimestamp > debounceTime) {
                                lastShakeTimestamp = currentTime
                                onShakeDetected()
                            }
                        }

                        lastX = x
                        lastY = y
                        lastZ = z
                        lastUpdate = currentTime
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(
            sensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_UI
        )
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}