package com.example.todolistapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * AlarmReceiver class for managing and setting up notifications
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        //intent that will lead us to main activity when the notification is pressed
       Intent i = new Intent(context, MainActivity.class);

       //setting up intent flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //pending intent for the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        //notification builder for its content and layout and other attributes
       NotificationCompat.Builder builder = new NotificationCompat.Builder((context), "todoandroid")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("To Do")
                .setContentText(extras.getString("task"))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
               .setContentIntent(pendingIntent);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);

        //check for permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                nmc.notify(123, builder.build()); //display notification
            } catch (SecurityException e) {

            }
        } else {
            // Handle the case where permission is not granted
            // notify the user or log this information
        }

    }
}
