package sudarshan.bhatt.shakenlight;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Parameter;

public class MainActivity extends AppCompatActivity implements
        SensorEventListener {

    private SensorManager sensorManager;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 2500;
    CameraManager cameraManager;
    private boolean isFlashOn = false;
    private Camera camera;
    private Camera.Parameters params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

//       sensorManager.getSensorList(Sensor.TYPE_ALL);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {

            Sensor s = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
         //   boolean type = s.isWakeUpSensor();
      //      Log.d("TAG", "type" + type);
            sensorManager.registerListener(this, s,
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW);

        }


    }

    //    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //We use the Math class to calculate the device's speed as shown below. The statically declared SHAKE_THRESHOLD variable is used to see whether a shake gesture has been detected or not. Modifying SHAKE_THRESHOLD increases or decreases the sensitivity so feel free to play with its value.
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            Log.d("TAG", "value" + event.values);
//            Log.d("TAG", "sensor" + event.sensor);
//            Log.d("TAG", "accuracy" + event.accuracy);
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            long curTime = System.currentTimeMillis();

            if (curTime - lastUpdate > 100) {
                long timeDiff = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / timeDiff * 10 * 1000;
                if (speed > SHAKE_THRESHOLD) {

                    if (isFlashOn) {
                        turnFlashOff();
                    } else {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {

                            } else {

                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                            }
                        } else {
                            turnFlashOn();
                        }


                    }

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }

        }


        //     Toast.makeText(getApplicationContext(),"vallah", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //      Toast.makeText(getApplicationContext(),"vallah vallah", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode ==100){
//            if(permissions[0].equals(Manifest.permission.CAMERA)){
                turnFlashOn();
//            }
        }
    }

    private void turnFlashOn() {
        try {
            Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//                        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (isFlashAvailable) {

                camera = Camera.open();
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
                isFlashOn = true;

            }
//                        String cameraId = cameraManager.getCameraIdList()[0];
//                        cameraManager.setTorchMode(cameraId, true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "shaked" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void turnFlashOff() {
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
        isFlashOn = false;
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
