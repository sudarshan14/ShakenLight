package sudarshan.bhatt.shakenlight

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.*
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private val lastUpdate: Long = 0
    private val last_x: Float = 0.toFloat()
    private val last_y: Float = 0.toFloat()
    private val last_z: Float = 0.toFloat()
    internal var cameraManager: CameraManager? = null
    private var isFlashOn = false
    private var camera: Camera? = null
    private var params: Camera.Parameters? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//
//        //       sensorManager.getSensorList(Sensor.TYPE_ALL);
//        if (sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER).size != 0) {
//
//            val s = sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER)[0]
//            //   boolean type = s.isWakeUpSensor();
//            //      Log.d("TAG", "type" + type);
//            sensorManager!!.registerListener(this, s,
//                    SensorManager.SENSOR_STATUS_ACCURACY_LOW)
//
//        }


    }

    override fun onStart() {

        super.onStart()
        checkPerMission()

    }

    fun checkPerMission() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100);
            }
        } else {
            startService(Intent(this, Shakedetector::class.java))
        }


    }


    //    @TargetApi(Build.VERSION_CODES.M)
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        //We use the Math class to calculate the device's speed as shown below. The statically declared SHAKE_THRESHOLD variable is used to see whether a shake gesture has been detected or not. Modifying SHAKE_THRESHOLD increases or decreases the sensitivity so feel free to play with its value.
        //        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        ////            Log.d("TAG", "value" + event.values);
        ////            Log.d("TAG", "sensor" + event.sensor);
        ////            Log.d("TAG", "accuracy" + event.accuracy);
        //            float x = sensorEvent.values[0];
        //            float y = sensorEvent.values[1];
        //            float z = sensorEvent.values[2];
        //            long curTime = System.currentTimeMillis();
        //
        //            if (curTime - lastUpdate > 100) {
        //                long timeDiff = (curTime - lastUpdate);
        //                lastUpdate = curTime;
        //
        //                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / timeDiff * 10 * 1000;
        //
        //                if (speed > SHAKE_THRESHOLD) {
        //                    Log.d("TAG", "speed" + speed);
        //                    if (isFlashOn) {
        //                        turnFlashOff();
        //                    } else {
        //
        //
        //                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        //
        //                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
        //
        //                            } else {
        //
        //                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        //                            }
        //                        } else {
        //                            turnFlashOn();
        //                        }
        //
        //
        //                    }
        //
        //                }
        //
        //                last_x = x;
        //                last_y = y;
        //                last_z = z;
        //            }
        //
        //        }


        //     Toast.makeText(getApplicationContext(),"vallah", Toast.LENGTH_LONG).show();

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //      Toast.makeText(getApplicationContext(),"vallah vallah", Toast.LENGTH_LONG).show();
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == 100) {
            //            if(permissions[0].equals(Manifest.permission.CAMERA)){
            startService(Intent(this, Shakedetector::class.java))
            //            }
        }
    }

    private fun turnFlashOn() {
        try {
            Log.d("TAG", "turnFlashOn")
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
        try {
            Log.d("TAG", "turnFlashOff")
            params = camera!!.parameters
            params!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera!!.parameters = params
            camera!!.stopPreview()
            camera!!.release()
            isFlashOn = false
        } catch (e: Exception) {
            Log.e("TAG", "turnFlashOff error" + e.toString())
        }

    }

    companion object {
        private val SHAKE_THRESHOLD = 3500
    }
//
//
//    NotificationManager notificationManager =
//            (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//    // 2. Create a PendingIntent
//    int color = 0;
//    Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        // sIcon = R.drawable.ic_mode_edit_white_24dp;
//        // The system draws notification icons in white and action icons
//        // in
//        // dark gray
//        // color = 0x008000;
//        color = 0x008000;
//    }
//
//    // 3. Create and send a notification
//    Notification notification = new Notification.Builder(this)
//            .setSmallIcon(R.drawable.clock)
//            .setContentTitle("Notification")
//            .setContentText("event")
//            .setContentIntent(pendingNotificationIntent)
//            .setStyle(new Notification.BigTextStyle().bigText("Big text"))
//            //   .setColor(color)
//
//            .setAutoCancel(true)
//            .build();
//        notificationManager.notify(0, notification);
//    //       Toast.makeText(getApplicationContext(),"vallah"+val, Toast.LENGTH_LONG).show();
}
