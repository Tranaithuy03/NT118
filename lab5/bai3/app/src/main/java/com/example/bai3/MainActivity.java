package com.example.bai3;

import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button btnQuickJob, btnSlowJob;
    private TextView tvStatus;
    private SlowTask slowTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIds();
        // Init slowtask
        slowTask = new SlowTask(MainActivity.this, tvStatus);
        // Handle onClickListenner
        btnQuickJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                tvStatus.setText(sdf.format(new Date()));
            }
        });
        btnSlowJob.setOnClickListener(
                v -> {
                    if (slowTask == null || slowTask.getStatus() != AsyncTask.Status.RUNNING) {
                        slowTask = new SlowTask(MainActivity.this, tvStatus);
                        slowTask.execute();
                    }
                }
        );
    }

    private void findViewByIds() {
        btnQuickJob = (Button) findViewById(R.id.btn_quick_job);
        btnSlowJob = (Button) findViewById(R.id.btn_slow_job);
        tvStatus = (TextView) findViewById(R.id.tv_status);
    }
}