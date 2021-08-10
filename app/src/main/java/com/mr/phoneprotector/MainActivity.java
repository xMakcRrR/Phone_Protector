package com.mr.phoneprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    //constants
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 2;
    public static final int REQUEST_NETWORK_STATE_CODE = 3;

    public static String NOTIF_ID_STRING = "phn_prtctr01";
    public static int NOTIF_ID = 2054;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////
        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager dpm=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        //for admin receiver
        SwitchCompat starterSwitch = findViewById(R.id.starter_switch);
        if (starterSwitch != null) {
            starterSwitch.setOnCheckedChangeListener(this);
        }

        if (dpm.isAdminActive(cn)) {
            starterSwitch.setOnCheckedChangeListener(null);
            starterSwitch.setChecked(true);
            starterSwitch.setOnCheckedChangeListener(this);
        } else {
            starterSwitch.setOnCheckedChangeListener(null);
            starterSwitch.setChecked(false);
            starterSwitch.setOnCheckedChangeListener(this);
        }
        ////////





        if(!CheckPermissionsAudioRecord()) {
            requestPermissionsAudioRecord();
        }
        if(!CheckPermissionsLocation()) {
            requestPermissionsLocation();
        }
        if(!CheckPermissionsNetworkState()) {
            requestPermissionsNetworkState();
        }

        /* for foreground service
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
         */
    }

    /* for foreground service
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
     */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager dpm=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        Toast.makeText(this,
                (isChecked ? getString(R.string.switch_toggled_true) : getString(R.string.switch_toggled_false)),
                Toast.LENGTH_SHORT).show();

        if (isChecked){
            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "vkluchi");
            startActivity(intent);
        } else if (!isChecked) {
            dpm.removeActiveAdmin(cn);
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

    private void requestPermissionsAudioRecord() {
        // this method is used to request the permission.
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private void requestPermissionsLocation() {
        // this method is used to request the permission.
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION_CODE);
    }

    private void requestPermissionsNetworkState() {
        // this method is used to request the permission.
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                REQUEST_NETWORK_STATE_CODE);
    }

    public boolean CheckPermissionsAudioRecord() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public boolean CheckPermissionsLocation() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public boolean CheckPermissionsNetworkState() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_NETWORK_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
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
                                "Permission Granted for Audio Record",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Permission Denied for Audio Record",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_LOCATION_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToCoarse = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean permissionToFine = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (permissionToCoarse && permissionToFine) {
                        Toast.makeText(getApplicationContext(),
                                "Permission Granted for location", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Permission Denied for location", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_NETWORK_STATE_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToNetworkState = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (permissionToNetworkState) {
                        Toast.makeText(getApplicationContext(),
                                "Permission Granted for network state", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Permission Granted for network state", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


}