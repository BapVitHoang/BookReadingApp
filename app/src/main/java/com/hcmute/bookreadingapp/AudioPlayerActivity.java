package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmute.bookreadingapp.service.AudioService;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AudioPlayerActivity extends AppCompatActivity {

    private FloatingActionButton btnPlayPause;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestNotificationPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        btnPlayPause = findViewById(R.id.btn_play_pause);

        btnPlayPause.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AudioPlayerActivity.this,
                    AudioService.class
            );

            if (!isPlaying) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }

                isPlaying = true;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);

            } else {

                stopService(intent);

                isPlaying = false;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }
        });
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