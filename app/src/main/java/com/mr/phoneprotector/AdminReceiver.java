package com.mr.phoneprotector;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
    }

    @Override
    public void onPasswordFailed (Context context, Intent intent, UserHandle user) {
        Log.d("Amogus", "OnPassFail");
        context.startForegroundService(new Intent(context, WorkingService.class));
    }

    @Override
    public void onPasswordSucceeded (Context context, Intent intent, UserHandle user) {
        Log.d("Amogus", "OnPassSucc");
    }
}
