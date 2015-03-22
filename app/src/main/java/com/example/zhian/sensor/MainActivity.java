package com.example.zhian.sensor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.zhian.sensor.LocationService.LocalBinder;

import javax.xml.transform.Result;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
    LocationService mLocationService;
    boolean mBound = false;
    private SensorManager mSensorManager;
    private Sensor mLight;
    private TextView tv;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mLocationService = binder.getService();
            mLocationService.sampleLocation();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tv = (TextView) findViewById(R.id.text_light);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Handler myHandler = new Handler();
        ResultReceiver rr = new MyResultReceiver(myHandler);
        Intent intent = new Intent(this, LocationService.class);
        Log.d(Constants.LOG_TAG, "put rr");
        intent.putExtra("rr", rr);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when activity comes to foreground. In onResume() method we
     * register our sensors listeners, important - sensor listeners are not and
     * should not be registered before, but in on resume method.
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.startService(new Intent(this, UVService.class));
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double mLux = event.values[0];
        tv.setText("" + mLux);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    
    public void sampleLocation(View v) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final ActionBarActivity self = this;
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                NotificationHelper.showOutdoorMovementNotification(self);
            }
        }, 3, TimeUnit.SECONDS);

    }
    
    public class MyResultReceiver extends ResultReceiver {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }
        
        public void onReceiveResult(int r, Bundle bundle) {
        }
        
    }
}
