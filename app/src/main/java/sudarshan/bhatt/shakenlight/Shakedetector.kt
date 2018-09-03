package sudarshan.bhatt.shakenlight

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.*
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast



class Shakedetector : Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null

    private var lastUpdate: Long = 0
    private var last_x: Float = 0.toFloat()
    private var last_y: Float = 0.toFloat()
    private var last_z: Float = 0.toFloat()
    internal var cameraManager: CameraManager? = null
    private var isFlashOn = false
    private var camera: Camera? = null
    private var params: Camera.Parameters? = null

    override fun onBind(intent: Intent): IBinder? {

        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER).size != 0) {

            val s = sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER)[0]
            //   boolean type = s.isWakeUpSensor();
            //      Log.d("TAG", "type" + type);
            sensorManager!!.registerListener(this, s,
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW)

        }
        //        Toast.makeText(getApplicationContext(), "from service", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

//        val notification = Notification.Builder(this, "")
//                .setContentTitle(getText(R.string.notification_title))
//                .setContentText(getText(R.string.notification_message))
//                .setSmallIcon(R.drawable.icon)
//                .setContentIntent(pendingIntent)
//                .setTicker(getText(R.string.ticker_text))
//                .build()
        createNotificationChannel()
        var notificationBuilder = NotificationCompat.Builder(this,"SUD")
                .setContentTitle("Hello");
        val notification = notificationBuilder.build()
        startForeground(1, notification)

        return    super.onStartCommand(intent, flags, startId)


    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SUD"//getString(R.string.channel_name)
            val description = "Description"//getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SUD", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            //            Log.d("TAG", "value" + event.values);
            //            Log.d("TAG", "sensor" + event.sensor);
            //            Log.d("TAG", "accuracy" + event.accuracy);
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            val curTime = System.currentTimeMillis()
            Log.d("TAG", "curTime - lastUpdate" + (curTime - lastUpdate))
            if (curTime - lastUpdate > 150) {
                val timeDiff = curTime - lastUpdate
                lastUpdate = curTime

                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / timeDiff * 10f * 1000f
                //                long lastShake = curTime - lastUpdate;

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("TAG", "lastShake$timeDiff")
                    Log.d("TAG", "speed in service$speed")
                    if (isFlashOn) {
                        turnFlashOff()
                    } else {
                        turnFlashOn()
                    }
                }

                last_x = x
                last_y = y
                last_z = z
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    private fun turnFlashOn() {
        try {
            Log.d("TAG", "turnFlashOn service")
            var isFlashAvailable: Boolean? = false
            val packageManager = applicationContext.packageManager

            try {
                isFlashAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
            } catch (e: Exception) {
                Log.d("TAG", e.toString())
            }

            //            Boolean isFlashAvailable = getApplicationContext().getPackageManager()
            //                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            //                        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (isFlashAvailable!!) {

                camera = Camera.open()
                params = camera!!.parameters
                params!!.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera!!.parameters = params
                camera!!.startPreview()
                isFlashOn = true

            }
            //                        String cameraId = cameraManager.getCameraIdList()[0];
            //                        cameraManager.setTorchMode(cameraId, true);
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "shaked" + e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun turnFlashOff() {
        Log.d("TAG", "turnFlashOff service")
        params = camera!!.parameters
        params!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
        camera!!.parameters = params
        camera!!.stopPreview()
        camera!!.release()
        isFlashOn = false
    }

    companion object {
        private val SHAKE_THRESHOLD = 3500
        private val MAX_TOTAL_DURATION_OF_SHAKE = 400
    }
}
