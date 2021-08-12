package com.mr.phoneprotector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class WorkingService extends Service {
    public WorkingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Amogus", "Service online");

        //Notification for foreground
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
        //

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(MainActivity.KEY_EMAIL, false)) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();

                    try {
                        EmailSender emailSender = new EmailSender(getApplicationContext().
                                getString(R.string.logg),
                                getApplicationContext().getString(R.string.pass));
                        emailSender.sendMail("Subj", "amogus hijacking ur phone!!!",
                                "ya", "makc-rybin@mail.ru");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            Log.d("Amogus", "Mailus");
        }

        //Stop sefl after some time
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 20000);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}