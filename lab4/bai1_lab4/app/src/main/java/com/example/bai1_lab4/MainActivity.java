package com.example.bai1_lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Yêu cầu quyền SMS nếu chưa có
        initBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            initBroadcastReceiver();
        }
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    private void initBroadcastReceiver() {
        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReceive(context, intent);
            }
        };
    }

    private void processReceive(Context context, Intent intent) {
        Toast.makeText(context, getString(R.string.you_have_a_new_message), Toast.LENGTH_LONG).show();
        TextView textView = findViewById(R.id.tv_content);
        final String SMS_EXTRA = "pdus";
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] messages = (Object[]) bundle.get(SMS_EXTRA);
            StringBuilder sms = new StringBuilder();

            for (Object message : messages) {
                SmsMessage smsMessage;
                if (Build.VERSION.SDK_INT >= 23) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) message, "3gpp");
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) message);
                }
                String msgBody = smsMessage.getMessageBody();
                String address = smsMessage.getDisplayOriginatingAddress();
                sms.append(address).append(":\n").append(msgBody).append("\n");
            }
            textView.setText(sms.toString());
        }
    }
}
