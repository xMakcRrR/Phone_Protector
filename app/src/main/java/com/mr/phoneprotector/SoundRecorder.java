package com.mr.phoneprotector;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SoundRecorder {
    private MediaRecorder mediaRecorder;

    //var for sound file
    private File directory;
    private File fileName;
    private ContextWrapper contextWrapper;
    private String date;

    //for test
    MediaPlayer mediaPlayer;

    public SoundRecorder (ContextWrapper context) {
        this.contextWrapper = context;
        this.directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        this.date = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).format(new Date());
        this.fileName = new File(directory, "soundrec"+date+".3gp");
    }

    private void startRecord () {
        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fileName);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void stopRecord () {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void takeRecord () {
        startRecord();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecord();
            }
        }, 30000);
    }

    public void playRecord () {
        mediaPlayer = new MediaPlayer();
        try {
            // below method is used to set the
            // data source which will be our file name
            mediaPlayer.setDataSource(fileName.getPath());

            // below method will prepare our media player
            mediaPlayer.prepare();

            // below method will start our media player.
            mediaPlayer.start();
        } catch (IOException e) {
        }
    }

    public String getRecFilePath() {
        return this.fileName.getPath();
    }

    private void saving () {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        File byterDir = new File(""+Environment.getExternalStorageDirectory()+"/phone_prot/");

        if(!byterDir.exists()) {
            //Toast.makeText(getActivity(), "aboba", Toast.LENGTH_LONG).show();
            byterDir.mkdirs();
        };
    }
}