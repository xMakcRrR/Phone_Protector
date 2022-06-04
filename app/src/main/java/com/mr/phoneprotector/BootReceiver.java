package com.mr.phoneprotector;

import static android.content.Context.DEVICE_POLICY_SERVICE;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("Amogus", "Boot");
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        ComponentName cn=new ComponentName(context, AdminReceiver.class);
        DevicePolicyManager dpm=
                (DevicePolicyManager)context.getSystemService(DEVICE_POLICY_SERVICE);

        /*
        if (dpm.isAdminActive(cn)) {
            Intent intentA=
                    new Intent(DevicePolicyManager.ACTION);
            context.startActivity(intentA);

         */
    }
}