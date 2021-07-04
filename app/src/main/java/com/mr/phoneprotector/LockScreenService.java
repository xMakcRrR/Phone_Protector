package com.mr.phoneprotector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class LockScreenService extends Service {
    private boolean online = false;

    public LockScreenService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //Creating notification for foregroundService
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(MainActivity.NOTIF_ID_STRING,
                "Phone Protector Channel",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string.notification_channel_description));
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, MainActivity.NOTIF_ID_STRING)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_text));

        Notification notification = builder.build();

        startForeground(MainActivity.NOTIF_ID, notification);
        online = true;

        Toast.makeText(this, "Service online", Toast.LENGTH_LONG).show();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        online = false;
        Toast.makeText(this, "Service offline", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}