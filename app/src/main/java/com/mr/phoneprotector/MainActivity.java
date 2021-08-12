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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    //constants
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 2;
    public static final int REQUEST_NETWORK_STATE_CODE = 3;
    public static String NOTIF_ID_STRING = "phn_prtctr01";
    public static int NOTIF_ID = 2054;
    //PREFERENCES KEYS
    public static final String PREF_NAME = "PHONE_PROTECTOR_PREF";
    public static final String KEY_EMAIL = "KEY_EMAIL";
    public static final String KEY_ATTEMPTS = "KEY_ATTEMPTS";
    public static final String KEY_CAMERA_F = "KEY_CAMERA_F";
    public static final String KEY_CAMERA_B = "KEY_CAMERA_B";
    public static final String KEY_COORDINATES = "KEY_COORDINATES";
    public static final String KEY_AUDIO = "KEY_AUDIO";

    SharedPreferences sharedPreferences;

    //Views
    SwitchCompat starterSwitch;
    EditText editTextAttempts;
    CheckBox checkBoxCameraFront;
    CheckBox checkBoxCameraBack;
    CheckBox checkBoxCoordinates;
    CheckBox checkBoxAudio;
    CheckBox checkBoxSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager dpm=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        starterSwitch = findViewById(R.id.starter_switch);
        if (dpm.isAdminActive(cn)) {
            starterSwitch.setChecked(true);
        }
        starterSwitch.setOnCheckedChangeListener(this);

        editTextAttempts = findViewById(R.id.editTextAttempts);
        editTextAttempts.setText(sharedPreferences.getString(KEY_ATTEMPTS, "5"));

        checkBoxCameraFront = findViewById(R.id.checkBoxCameraFront);
        if (sharedPreferences.getBoolean(KEY_CAMERA_F, false)) {
            checkBoxCameraFront.setChecked(true);
        }
        checkBoxCameraFront.setOnCheckedChangeListener(this);

        checkBoxCameraBack = findViewById(R.id.checkBoxCameraBack);
        if (sharedPreferences.getBoolean(KEY_CAMERA_B, false)) {
            checkBoxCameraBack.setChecked(true);
        }
        checkBoxCameraBack.setOnCheckedChangeListener(this);

        checkBoxCoordinates = findViewById(R.id.checkBoxCoordinates);
        if (sharedPreferences.getBoolean(KEY_COORDINATES, false)) {
            checkBoxCoordinates.setChecked(true);
        }
        checkBoxCoordinates.setOnCheckedChangeListener(this);

        checkBoxAudio = findViewById(R.id.checkBoxAudio);
        if (sharedPreferences.getBoolean(KEY_AUDIO, false)) {
            checkBoxAudio.setChecked(true);
        }
        checkBoxAudio.setOnCheckedChangeListener(this);

        checkBoxSendEmail = findViewById(R.id.checkBoxSendEmail);
        if (sharedPreferences.getBoolean(KEY_EMAIL, false)) {
            checkBoxSendEmail.setChecked(true);
        }
        checkBoxSendEmail.setOnCheckedChangeListener(this);



        /*
        if(!CheckPermissionsAudioRecord()) {
            requestPermissionsAudioRecord();
        }
        if(!CheckPermissionsLocation()) {
            requestPermissionsLocation();
        }
        if(!CheckPermissionsNetworkState()) {
            requestPermissionsNetworkState();
        }
        */

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
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (buttonView == findViewById(R.id.starter_switch)) {
            ComponentName cn=new ComponentName(this, AdminReceiver.class);
            DevicePolicyManager dpm=
                    (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

            if (isChecked){
                editor.putString(KEY_ATTEMPTS, editTextAttempts.getText().toString());
                editor.apply();

                Intent intent=
                        new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        getString(R.string.admin_req_expl));
                startActivity(intent);
            } else if (!isChecked) {
                dpm.removeActiveAdmin(cn);
            }
        }

        if (buttonView == findViewById(R.id.checkBoxCoordinates)) {
            if (isChecked) {
                if(!CheckPermissionsLocation()) {
                    requestPermissionsLocation();
                }
                editor.putBoolean(KEY_COORDINATES, true);
            }
            else {
                editor.putBoolean(KEY_COORDINATES, false);
            }
            editor.apply();
        }

        if (buttonView == findViewById(R.id.checkBoxCameraFront)) {
            if (isChecked) {
                //TODO PERMISSIONS FOR CAMERA
                editor.putBoolean(KEY_CAMERA_F, true);
            }
            else {
                editor.putBoolean(KEY_CAMERA_F, false);
            }
            editor.apply();
        }

        if (buttonView == findViewById(R.id.checkBoxCameraBack)) {
            if (isChecked) {
                //TODO PERMISSIONS FOR CAMERA
                editor.putBoolean(KEY_CAMERA_B, true);
            }
            else {
                editor.putBoolean(KEY_CAMERA_B, false);
            }
            editor.apply();
        }

        if (buttonView == findViewById(R.id.checkBoxAudio)) {
            if (isChecked) {
                if(!CheckPermissionsAudioRecord()) {
                    requestPermissionsAudioRecord();
                }
                editor.putBoolean(KEY_AUDIO, true);
            }
            else {
                editor.putBoolean(KEY_AUDIO, false);
            }
            editor.apply();
        }

        if (buttonView == findViewById(R.id.checkBoxSendEmail)) {
            if (isChecked) {
                editor.putBoolean(KEY_EMAIL, true);
            }
            else {
                editor.putBoolean(KEY_EMAIL, false);
            }
            editor.apply();
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