package com.mr.phoneprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    public static final String KEY_EMAIL_SWITCH = "KEY_EMAIL_SWITCH";
    public static final String KEY_ATTEMPTS = "KEY_ATTEMPTS";
    public static final String KEY_CAMERA_F = "KEY_CAMERA_F";
    public static final String KEY_CAMERA_B = "KEY_CAMERA_B";
    public static final String KEY_COORDINATES = "KEY_COORDINATES";
    public static final String KEY_AUDIO = "KEY_AUDIO";
    public static final String KEY_EMAIL_EDIT = "KEY_EMAIL_EDIT";
    public static final String KEY_PHOTO_SAVE = "KEY_PHOTO_SAVE";
    public static final String KEY_AUDIO_SAVE = "KEY_AUDIO_SAVE";

    SharedPreferences sharedPreferences;

    //Views
    SwitchCompat starterSwitch;
    EditText editTextAttempts;
    EditText editTextEmail;
    CheckBox checkBoxCameraFront;
    CheckBox checkBoxCameraBack;
    CheckBox checkBoxCoordinates;
    CheckBox checkBoxAudio;
    CheckBox checkBoxSendEmail;
    CheckBox checkBoxPhotoSave;
    CheckBox checkBoxSoundSave;

    //TODO checkBoxes for save options

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager dpm=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        //stater switch prep
        starterSwitch = findViewById(R.id.starter_switch);
        if (dpm.isAdminActive(cn)) {
            starterSwitch.setChecked(true);
        }
        starterSwitch.setOnCheckedChangeListener(this);

        //edit text pin attempts prep
        editTextAttempts = findViewById(R.id.editTextAttempts);
        String s_attempts = sharedPreferences.getString(KEY_ATTEMPTS, "5");
        if (Integer.parseInt(s_attempts) == 0) {
            editTextAttempts.setText("5");
        } else {
            editTextAttempts.setText(s_attempts);
        }
        editTextAttempts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(KEY_ATTEMPTS, editTextAttempts.getText().toString());
                editor.apply();
                Log.d("Amogus", sharedPreferences.getString(KEY_ATTEMPTS, "defVal"));
            }
        });

        //edit text email address prep
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setText(sharedPreferences.getString(KEY_EMAIL_EDIT, ""));
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(KEY_EMAIL_EDIT, editTextEmail.getText().toString());
                editor.apply();
                Log.d("Amogus", sharedPreferences.getString(KEY_EMAIL_EDIT, "defVal"));
            }
        });

        //camera front switch prep
        checkBoxCameraFront = findViewById(R.id.checkBoxCameraFront);
        if (sharedPreferences.getBoolean(KEY_CAMERA_F, false)) {
            checkBoxCameraFront.setChecked(true);
        }
        checkBoxCameraFront.setOnCheckedChangeListener(this);

        //camera back switch prep
        checkBoxCameraBack = findViewById(R.id.checkBoxCameraBack);
        if (sharedPreferences.getBoolean(KEY_CAMERA_B, false)) {
            checkBoxCameraBack.setChecked(true);
        }
        checkBoxCameraBack.setOnCheckedChangeListener(this);

        //gps switch prep
        checkBoxCoordinates = findViewById(R.id.checkBoxCoordinates);
        if (sharedPreferences.getBoolean(KEY_COORDINATES, false)) {
            checkBoxCoordinates.setChecked(true);
        }
        checkBoxCoordinates.setOnCheckedChangeListener(this);

        //audio switch prep
        checkBoxAudio = findViewById(R.id.checkBoxAudio);
        if (sharedPreferences.getBoolean(KEY_AUDIO, false)) {
            checkBoxAudio.setChecked(true);
        }
        checkBoxAudio.setOnCheckedChangeListener(this);

        //email switch prep
        checkBoxSendEmail = findViewById(R.id.checkBoxSendEmail);
        if (sharedPreferences.getBoolean(KEY_EMAIL_SWITCH, false)) {
            checkBoxSendEmail.setChecked(true);
        }
        checkBoxSendEmail.setOnCheckedChangeListener(this);

        //photo save switch prep
        checkBoxPhotoSave = findViewById(R.id.checkBoxCameraPhotoSave);
        if (sharedPreferences.getBoolean(KEY_PHOTO_SAVE, false)) {
            checkBoxPhotoSave.setChecked(true);
        }
        checkBoxPhotoSave.setOnCheckedChangeListener(this);

        //photo save switch prep
        checkBoxSoundSave = findViewById(R.id.checkBoxSoundSave);
        if (sharedPreferences.getBoolean(KEY_AUDIO_SAVE, false)) {
            checkBoxSoundSave.setChecked(true);
        }
        checkBoxSoundSave.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (buttonView == findViewById(R.id.starter_switch)) {
            ComponentName cn=new ComponentName(this, AdminReceiver.class);
            DevicePolicyManager dpm=
                    (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

            if (isChecked){
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

        else if (buttonView == findViewById(R.id.checkBoxCoordinates)) {
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

        else if (buttonView == findViewById(R.id.checkBoxCameraFront)) {
            if (isChecked) {
                //TODO PERMISSIONS FOR CAMERA
                editor.putBoolean(KEY_CAMERA_F, true);
            }
            else {
                editor.putBoolean(KEY_CAMERA_F, false);
            }
            editor.apply();
        }

        else if (buttonView == findViewById(R.id.checkBoxCameraBack)) {
            if (isChecked) {
                //TODO PERMISSIONS FOR CAMERA
                editor.putBoolean(KEY_CAMERA_B, true);
            }
            else {
                editor.putBoolean(KEY_CAMERA_B, false);
            }
            editor.apply();
        }

        else if (buttonView == findViewById(R.id.checkBoxAudio)) {
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

        else if (buttonView == findViewById(R.id.checkBoxSendEmail)) {
            if (isChecked) {
                editor.putBoolean(KEY_EMAIL_SWITCH, true);
            }
            else {
                editor.putBoolean(KEY_EMAIL_SWITCH, false);
            }
            editor.apply();
        }

        else if (buttonView == findViewById(R.id.checkBoxCameraPhotoSave)) {
            if (isChecked) {
                editor.putBoolean(KEY_PHOTO_SAVE, true);
            }
            else {
                editor.putBoolean(KEY_PHOTO_SAVE, false);
            }
            editor.apply();
        }

        else if (buttonView == findViewById(R.id.checkBoxSoundSave)) {
            if (isChecked) {
                editor.putBoolean(KEY_AUDIO_SAVE, true);
            }
            else {
                editor.putBoolean(KEY_AUDIO_SAVE, false);
            }
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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