package com.hcmute.bookreadingapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hcmute.bookreadingapp.R;

public class AudioService extends Service {

    private static final String CHANNEL_ID = "audio_service_channel";
    private static final int NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(
                NOTIFICATION_ID,
                createNotification()
        );

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(
                    this,
                    R.raw.sample_speech
            );

            mediaPlayer.start();
        } else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }

        return START_STICKY;
    }

    private android.app.Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BookReadingApp")
                .setContentText("Đang phát sách nói")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Audio Service Channel",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopForeground(true);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}