package com.example.bai4;

import android.os.Bundle;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStream, btnStop;
    TextView etURL;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etURL = findViewById(R.id.etURL);
        btnStream = findViewById(R.id.btnStream);
        btnStop = findViewById(R.id.btnStop);

        btnStream.setOnClickListener(view -> {
            new MediaPlayerAsyncTask().execute("https://tranaithuy03.github.io/lab3_NT118/gods.mp3");
        });

        btnStop.setOnClickListener(view -> stopPlaying());
    }

    private void startAudioStream(String url) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                 return true;
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setVolume(3f, 3f);
            mediaPlayer.setLooping(false);
        } catch (Exception e) {
            Log.d("mylog", "Error playing in SoundHandler: " + e.toString());
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class MediaPlayerAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String mp3Url = urls[0];
            startAudioStream(mp3Url);
            return null;
        }
    }
}