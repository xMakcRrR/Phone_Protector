package com.mr.phoneprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    //constants
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    public static String NOTIF_ID_STRING = "phn_prtctr01";
    public static int NOTIF_ID = 2054;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!CheckPermissions()) {
            requestPermissions();
        }

        SwitchCompat starterSwitch = findViewById(R.id.starter_switch);
        if (starterSwitch != null) {
            starterSwitch.setOnCheckedChangeListener(this);
        }

        //Checking for active service
        if (isMyServiceRunning(LockScreenService.class)) {
            starterSwitch.setChecked(true);
        } else {
            starterSwitch.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this,
                (isChecked ? getString(R.string.switch_toggled_true) : getString(R.string.switch_toggled_false)),
                Toast.LENGTH_SHORT).show();

        if (isChecked){
            startService(new Intent(this, LockScreenService.class));
        } else if (!isChecked) {
            stopService(new Intent(this, LockScreenService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestPermissions() {
        // this method is used to request the permission.
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // this method is called when user will grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(),
                                "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}