package com.example.bai2_lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import java.util.Objects;

public class PowerStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(context == null) {
            return;
        }
        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, "Power connected", Toast.LENGTH_SHORT).show();
        }
        else if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Toast.makeText(context, "Power disconnected", Toast.LENGTH_SHORT).show();
        }
    }
}
