package com.example.zhian.sensor;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by ZhiAn on 16/3/2015.
 */
public class NotificationHelper {
   public static void showOutdoorMovementNotification(Context context) {
       final NotificationManager mNotificationManager = (NotificationManager)
               context.getSystemService(Context.NOTIFICATION_SERVICE);
       Bitmap iconBitmap =
               BitmapFactory.decodeResource(context.getResources(), R.drawable.icon80x80);
       final NotificationCompat.Builder mBuilder =
               new NotificationCompat.Builder(context)
                       .setPriority(Notification.PRIORITY_MAX)
                       .setSmallIcon(R.drawable.icon80x80)
                       .setLargeIcon(iconBitmap)
                       .setContentTitle("Take care of your skin!")
                       .setContentText("It seems like you're been outdoors for a while...")
                       .setAutoCancel(true);

       mNotificationManager.notify(1, mBuilder.build());
   }
}
