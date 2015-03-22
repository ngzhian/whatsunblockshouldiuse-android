package com.example.zhian.sensor;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service {
    private List<Location> locations = new ArrayList<>();
    private Location lastLocation = null;
    private String lastUVIndex = null;
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Context context = this;
    private final IBinder mBinder = new LocalBinder();
    private ResultReceiver mRr = null;
    
    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRr = (ResultReceiver) intent.getParcelableExtra("rr");
        Log.d(Constants.LOG_TAG, "mRr" + mRr.toString());
        return super.onStartCommand(intent, flags, startId);
    }

    protected void sampleLocation() {
        final Runnable sampler = new Runnable() {
            @Override
            public void run() {
                LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                String provider = LocationManager.NETWORK_PROVIDER;
                if (mLocationManager.isProviderEnabled(provider)) {
                    android.location.Location gotLoc = mLocationManager.getLastKnownLocation(provider);
                    if (gotLoc != null) {
                        gotLocation(gotLoc);
                    }
                } else {
                    Intent settingsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                }
            }
            
            private void gotLocation(android.location.Location location) {
                lastLocation = new Location(location.getLatitude(), location.getLongitude());
                Log.d(Constants.LOG_TAG, "Saving location" + location.toString());
                locations.add(lastLocation);
                if (locations.size() == 4) {
                    checkMovement();
                }
            }
        };
        final Runnable UVIndexSampler = new Runnable() {
            @Override
            public void run() {
                try {
                    lastUVIndex = ForecastService.getForecast(
                            lastLocation.getLatitude(), lastLocation.getLongitude());
                    Log.d(Constants.LOG_TAG, "Getting forecast " + lastUVIndex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        Log.d(Constants.LOG_TAG, "sampleActivity");
        scheduler.schedule(sampler, 0, TimeUnit.SECONDS);
        scheduler.schedule(sampler, 3, TimeUnit.SECONDS);
        scheduler.schedule(sampler, 6, TimeUnit.SECONDS);
        scheduler.schedule(sampler, 9, TimeUnit.SECONDS);
        scheduler.schedule(UVIndexSampler, 9, TimeUnit.SECONDS);
        scheduler.schedule(UVIndexSampler, 9, TimeUnit.SECONDS);
    }

    private void checkMovement() {
        MovementChecker mc = new MovementChecker(locations);
        boolean moving = mc.check();
        if (moving) {
            // was likely moving
            NotificationHelper.showOutdoorMovementNotification(this);
        } else {
            // was probably sitting down somewhere
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                            .setContentTitle("Your safe")
                            .setContentText("Have fun indoors");

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        scheduler.shutdown();
    }
}
