package com.hcmute.bookreadingapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmute.bookreadingapp.service.AudioService;

public class AudioPlayerActivity extends AppCompatActivity {

    private FloatingActionButton btnPlayPause;
    private TextView tvPlaybackStatus;
    private boolean isPlaying = false;

    private AudioService audioService;
    private boolean bound = false;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (bound && audioService != null && audioService.isPlaying()) {
                int position = audioService.getCurrentPosition();
                int duration = audioService.getDuration();
                tvPlaybackStatus.setText(
                        "Đang phát: " + formatTime(position) + " / " + formatTime(duration)
                );
                uiHandler.postDelayed(this, 500);
            }
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            bound = true;
            syncUiWithService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            audioService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestNotificationPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        btnPlayPause = findViewById(R.id.btn_play_pause);
        tvPlaybackStatus = findViewById(R.id.tv_playback_status);

        btnPlayPause.setOnClickListener(v -> {
            Intent intent = new Intent(AudioPlayerActivity.this, AudioService.class);

            if (!isPlaying) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }

                if (bound && audioService != null) {
                    audioService.playAudio();
                }

                isPlaying = true;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                tvPlaybackStatus.setText("Đang phát sách nói...");
                uiHandler.post(progressUpdater);
            } else {
                if (bound && audioService != null) {
                    audioService.pauseAudio();
                }

                stopService(intent);

                isPlaying = false;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                tvPlaybackStatus.setText("Đã tạm dừng");
                uiHandler.removeCallbacks(progressUpdater);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        uiHandler.removeCallbacks(progressUpdater);

        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    private void syncUiWithService() {
        if (audioService != null && audioService.isPlaying()) {
            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            uiHandler.post(progressUpdater);
        }
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }
    }
}
