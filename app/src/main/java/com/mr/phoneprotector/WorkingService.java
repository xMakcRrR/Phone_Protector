package com.mr.phoneprotector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class WorkingService extends Service {
    private static boolean isOnline;
    private SharedPreferences sharedPreferences;
    private SoundRecorder soundRecorder;
    private CoordinatesTaker coordinatesTaker;
    private EmailSender emailSender;

    private String coordinates;
    private String soundPath;
    private String photoBPath;
    private String photoFPath;
    private PicturesTaker picturesTakerB;
    private PicturesTaker picturesTakerF;
    
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

        if (sharedPreferences.getBoolean(MainActivity.KEY_CAMERA_B, false) ||
                sharedPreferences.getBoolean(MainActivity.KEY_PHOTOB_SAVE, false)) {
            picturesTakerB = new PicturesTaker(this);

            picturesTakerB.readyCamera(PicturesTaker.CAMERACHOICE_B);
        }

        if (sharedPreferences.getBoolean(MainActivity.KEY_CAMERA_F, false) ||
                sharedPreferences.getBoolean(MainActivity.KEY_PHOTOF_SAVE, false)) {
            picturesTakerF = new PicturesTaker(this);

            picturesTakerF.readyCamera(PicturesTaker.CAMERACHOICE_F);
        }

        if (sharedPreferences.getBoolean(MainActivity.KEY_AUDIO, false) ||
                sharedPreferences.getBoolean(MainActivity.KEY_AUDIO_SAVE, false)) {
            soundRecorder = new SoundRecorder(this);

            Thread soundT = new Thread(new Runnable() {
                @Override
                public void run() {
                    soundRecorder.takeRecordWithDuration(30000);
                    /*
                    while (!Thread.currentThread().isInterrupted()) {
                        //working while recording
                    }

                     */
                }
            });
            soundT.start();

            //TODO Thread interrupt
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    soundT.interrupt();
                    Log.d("Amogus", "soundT " + soundT.isInterrupted());
                }
            }, 30500);
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

                    if (!sharedPreferences.getBoolean(MainActivity.KEY_AUDIO, false)) {
                        soundPath = "";
                    } else {
                        soundPath = soundRecorder.getRecFilePath();
                    }
                    if (coordinatesTaker == null) {
                        coordinates = "";
                    } else {
                        coordinates = coordinatesTaker.getCoordinatesString();
                    }
                    if (!sharedPreferences.getBoolean(MainActivity.KEY_CAMERA_B, false)) {
                        photoBPath = "";
                    } else {
                        photoBPath = picturesTakerB.getFilePath();
                    }
                    if (!sharedPreferences.getBoolean(MainActivity.KEY_CAMERA_F, false)) {
                        photoFPath = "";
                    } else {
                        photoFPath = picturesTakerF.getFilePath();
                    }


                    String recipient = sharedPreferences.getString(MainActivity.
                            KEY_EMAIL_EDIT, "");
                    try {
                        Log.d("Amogus", "Email start");
                        emailSender.sendMailWithAttachment("Subject", "PhonerProt",
                                recipient, coordinates, soundPath, photoBPath, photoFPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    emailT.start();
                    Log.d("Amogus", "emailT started");
                }
            }, 40000);
            /*
            final Handler emailHandler = new Handler();
            emailHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    emailT.start();
                }
            }, 40000);
             */
        }

        //Stop sefl after some time
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //clear files if needed
                if (!sharedPreferences.getBoolean(MainActivity.KEY_AUDIO_SAVE, false) && (soundRecorder != null)) {
                    File file = new File(soundRecorder.getRecFilePath());
                    file.delete();
                }
                if (!sharedPreferences.getBoolean(MainActivity.KEY_PHOTOB_SAVE, false) && (picturesTakerB != null)) {
                    File file = new File(picturesTakerB.getFilePath());
                    file.delete();
                }
                if (!sharedPreferences.getBoolean(MainActivity.KEY_PHOTOF_SAVE, false) && (picturesTakerF != null)) {
                    File file = new File(picturesTakerF.getFilePath());
                    file.delete();
                }



                isOnline = false;
                stopSelf();
            }
        }, 60000);
        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isOnline = false;
                stopSelf();
            }
        }, 60000);
         */
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