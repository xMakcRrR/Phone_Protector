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
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class WorkingService extends Service {
    private static boolean isOnline;
    private SharedPreferences sharedPreferences;
    private SoundRecorder soundRecorder;
    private CoordinatesTaker coordinatesTaker;
    private EmailSender emailSender;

    private String coordinates;
    private String soundPath;
    
    public WorkingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Amogus", "Service online");
        isOnline = true;

        // Notification for foreground ///////
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
        ////////////////////////////////////////

        sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(MainActivity.KEY_AUDIO, false)) {
            soundRecorder = new SoundRecorder(this);

            soundRecorder.takeRecordWithDuration(30000);

            //soundRecorder.takeRecord();
        }

        if (sharedPreferences.getBoolean(MainActivity.KEY_COORDINATES, false)) {
            coordinatesTaker = new CoordinatesTaker(this);
            coordinatesTaker.takeCoordinatesIn();
        }

        if (sharedPreferences.getBoolean(MainActivity.KEY_EMAIL_SWITCH, false)) {
            emailSender = new EmailSender(getApplicationContext().getString(R.string.logg),
                                        getApplicationContext().getString(R.string.pass));
            Thread emailT = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();

                    if (soundRecorder == null) {
                        soundPath = "";
                    } else {
                        soundPath = soundRecorder.getRecFilePath();
                    }
                    if (coordinatesTaker == null) {
                        coordinates = "";
                    } else {
                        coordinates = coordinatesTaker.getCoordinatesString();
                    }


                    String recipient = sharedPreferences.getString(MainActivity.
                            KEY_EMAIL_EDIT, "");
                    try {
                        Log.d("Amogus", "Email start");
                        emailSender.sendMailWithAttachment("Subject", "PhonerProt",
                                recipient, coordinates, soundPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            final Handler emailHandler = new Handler();
            emailHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    emailT.start();
                }
            }, 40000);
        }

        //Stop sefl after some time
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isOnline = false;
                stopSelf();
            }
        }, 60000);
        return super.onStartCommand(intent, flags, startId);
    }
    
    public static boolean onlineCheck () {
        return isOnline;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}