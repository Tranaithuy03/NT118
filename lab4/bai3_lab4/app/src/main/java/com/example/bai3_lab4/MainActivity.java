package com.example.bai3_lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private ReentrantLock reentrantLock;
    private Switch swAutoResponse;
    private LinearLayout llButtons;
    private Button btnSafe, btnMayday;
    private ArrayList<String> requesters;
    private ArrayAdapter<String> adapter;
    private ListView lvMessages;
    private BroadcastReceiver broadcastReceiver;
    public static boolean isRunning;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private final String AUTO_RESPONSE = "auto_response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsByIds();
        initVariables();
        handleOnClickListenner();
    }

    private void findViewsByIds() {
        swAutoResponse = (Switch) findViewById(R.id.sw_auto_response);
        llButtons = (LinearLayout) findViewById(R.id.ll_buttons);
        lvMessages = (ListView) findViewById(R.id.lv_messages);
        btnSafe = (Button) findViewById(R.id.btn_safe);
        btnMayday = (Button) findViewById(R.id.btn_mayday);
    }

    private void respond(String to, String response) {
        reentrantLock.lock();
        requesters.remove (to) ;
        adapter.notifyDataSetChanged ();
        reentrantLock.unlock();

        // Send the message
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(to, null, response, null, null);
    }

    public void respond(boolean ok) {
        String okString = getString(
                R.string.i_am_safe_and_well);
        String notOkString = getString(
                R.string.tell_my_mother_i_love_her);
        String outputString = ok ? okString : notOkString;
        ArrayList<String> requestersCopy = (ArrayList<String>) requesters.clone();
        for (String to : requestersCopy)
            respond(to, outputString);
    }
    public void processReceiveAddresses (ArrayList<String> addresses) {
        for (int i = 0; i < addresses.size(); i++) {
            if (!requesters.contains(addresses.get(i))) {
                reentrantLock.lock();
                requesters.add(addresses.get(i));
                adapter.notifyDataSetChanged();
                reentrantLock.unlock();
            }
                if (swAutoResponse.isChecked())
                    respond(true);
            }
    }

    private void handleOnClickListenner() {
        btnSafe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                respond(true);
            }
        });
        btnMayday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                respond(false);
            }
        });
        swAutoResponse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) llButtons.setVisibility(View.GONE);
                else llButtons.setVisibility(View.VISIBLE);
                // Save auto response setting
                editor.putBoolean(AUTO_RESPONSE, isChecked);
                editor.commit();
            }
        });
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<String> addresses = intent.getStringArrayListExtra(SmsReceiver.SMS_MESSAGE_ADDRESS_KEY);
                processReceiveAddresses(addresses);
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        if (broadcastReceiver == null) {
            initBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(SmsReceiver.SMS_FORWARD_BROADCAST_RECEIVER);
        registerReceiver(broadcastReceiver, intentFilter);
    }
    private void initVariables() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();
        reentrantLock = new ReentrantLock();
        requesters = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, requesters);
        lvMessages.setAdapter(adapter);
        // Load auto response setting
        boolean autoResponse = sharedPreferences.
                getBoolean(AUTO_RESPONSE, false);
        swAutoResponse.setChecked(autoResponse);
        if (autoResponse) llButtons.setVisibility(View.GONE);
        initBroadcastReceiver();
        try {
            ArrayList<String> addresses = getIntent().getStringArrayListExtra(SmsReceiver.SMS_MESSAGE_ADDRESS_KEY);
            processReceiveAddresses(addresses);
        } catch (Exception e) {
            System.out.println("Error nè" + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        unregisterReceiver(broadcastReceiver);
    }

}