package com.mr.phoneprotector;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.util.Log;

public class AdminReceiver extends DeviceAdminReceiver {
    private static int attempts;
    SharedPreferences sharedPreferences;

    @Override
    public void onEnabled(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences(MainActivity.
                PREF_NAME, Context.MODE_PRIVATE);
        attempts = Integer.parseInt(sharedPreferences.
                getString(MainActivity.KEY_ATTEMPTS, "5"));
        Log.d("Amogus", "OnEnabled "+ attempts);
    }

    @Override
    public void onPasswordFailed (Context context, Intent intent, UserHandle user) {
        sharedPreferences = context.getSharedPreferences(MainActivity.
                PREF_NAME, Context.MODE_PRIVATE);
        attempts = Integer.parseInt(sharedPreferences.
                getString(MainActivity.KEY_ATTEMPTS, "5"));
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.
                getSystemService(Context.DEVICE_POLICY_SERVICE);

        Log.d("Amogus", "OnPasFail " + devicePolicyManager.
                getCurrentFailedPasswordAttempts() + " " + attempts);

        if (devicePolicyManager.getCurrentFailedPasswordAttempts() > attempts) {
            Log.d("Amogus", "Online_check " + WorkingService.onlineCheck());
            if (!WorkingService.onlineCheck()) {
                context.startForegroundService(new Intent(context, WorkingService.class));
            }
        }
    }

    @Override
    public void onPasswordSucceeded (Context context, Intent intent, UserHandle user) {
        sharedPreferences = context.getSharedPreferences(MainActivity.
                PREF_NAME, Context.MODE_PRIVATE);
        attempts = Integer.parseInt(sharedPreferences.
                getString(MainActivity.KEY_ATTEMPTS, "5"));
        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.d("Amogus", "OnPassSucc "+ devicePolicyManager.
                getCurrentFailedPasswordAttempts() + " " + attempts);
    }
}
