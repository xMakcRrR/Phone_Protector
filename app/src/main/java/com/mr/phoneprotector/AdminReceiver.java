package com.mr.phoneprotector;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onPasswordFailed (Context context, Intent intent, UserHandle user) {
        Toast.makeText(context, "nepravilno blyat", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onPasswordSucceeded (Context context, Intent intent, UserHandle user) {
        Toast.makeText(context, "krasava", Toast.LENGTH_LONG)
                .show();
    }
}
