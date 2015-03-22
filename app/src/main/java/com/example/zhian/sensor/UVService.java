package com.example.zhian.sensor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UVService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLight;
    private float mLux;
    private int mNumLux;
    
    private NotificationManager mNotificationManager;
    private int mNotificationId = 1;
    
    private SensorEventListener selfSensor = this;
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public UVService() { }
    
    @Override
    public void onCreate() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        read();
        return super.onStartCommand(intent, flags, startId);
    };

    public void read() {
        mSensorManager.registerListener(selfSensor, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        final Runnable luxSampler = new Runnable() {
            @Override
            public void run() {
                Log.d(Constants.LOG_TAG, "LUX SAMPLER");
                Log.d(Constants.LOG_TAG, "mLux: " + mLux);
                float aveLux = mLux / mNumLux;
                if (aveLux >= 1000) {
//                    notifyUser(aveLux);
                    Log.d(Constants.LOG_TAG, "aveLux: " + aveLux);
                } else {
//                    cancelNotification();
                }
//                Log.d(Constants.LOG_TAG, "ave lux" + (mLux / mNumLux));
                mSensorManager.unregisterListener(selfSensor);
//                mLux = 0;
//                mNumLux = 0;
            }
        };
        scheduler.schedule(luxSampler, 3, TimeUnit.SECONDS);
    }
    
    public void notifyUser(float intensity) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.abc_ic_menu_paste_mtrl_am_alpha)
                .setContentTitle("UV Sensor Notification")
                .setContentText("You're in a really bright place, take care of your skin!")
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("com.example.zhian.EXTRA_INTENSITY", intensity);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }
    
    public void cancelNotification() {
        mNotificationManager.cancel(mNotificationId);
        
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // we want to calculate the average over the time period, so add them up
        mLux += event.values[0];
        mNumLux += 1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    
    @Override
    public void onDestroy() {
    }
}
