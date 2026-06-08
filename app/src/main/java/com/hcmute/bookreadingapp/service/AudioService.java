package com.hcmute.bookreadingapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.util.AudioUrlUtils;

import java.io.IOException;

public class AudioService extends Service {

    public static final String EXTRA_AUDIO_URL = "extra_audio_url";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_COVER_URL = "extra_cover_url";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";

    private static final String TAG = "AudioService";
    private static final String CHANNEL_ID = "audio_service_channel";
    private static final int NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new AudioBinder();
    private String currentTitle = "Đang phát sách nói";
    private String currentSubtitle = "Đang phát";
    private String currentCoverUrl;
    private String originalAudioUrl;
    private String currentAudioUrl;
    private boolean isPrepared;
    private boolean playWhenPrepared;

    public interface PlaybackListener {
        void onPrepared(int duration);

        void onError(String message);

        void onPlaybackComplete();

        void onPlaybackStateChanged(boolean isPlaying);
    }

    private PlaybackListener playbackListener;

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());

        if (intent != null) {
            String audioUrl = intent.getStringExtra(EXTRA_AUDIO_URL);
            String title = intent.getStringExtra(EXTRA_TITLE);
            String coverUrl = intent.getStringExtra(EXTRA_COVER_URL);
            String subtitle = intent.getStringExtra(EXTRA_SUBTITLE);

            if (title != null && !title.isEmpty()) {
                currentTitle = title;
            }
            if (subtitle != null && !subtitle.isEmpty()) {
                currentSubtitle = subtitle;
            }
            if (coverUrl != null && !coverUrl.isEmpty()) {
                currentCoverUrl = coverUrl;
            }
            if (audioUrl != null && !audioUrl.isEmpty()) {
                originalAudioUrl = audioUrl;
                playWhenPrepared = true;
                prepareAudio(audioUrl);
            }
        }

        return START_STICKY;
    }

    public void setPlaybackListener(PlaybackListener listener) {
        this.playbackListener = listener;
        if (listener != null && isPrepared && mediaPlayer != null) {
            listener.onPrepared(mediaPlayer.getDuration());
        }
    }

    public void prepareAudio(String audioUrl) {
        String directUrl = AudioUrlUtils.toDirectUrl(audioUrl);
        if (directUrl == null) {
            notifyError("Không có link audio");
            return;
        }

        if (directUrl.equals(currentAudioUrl) && mediaPlayer != null && isPrepared) {
            return;
        }

        releasePlayer();
        currentAudioUrl = directUrl;
        isPrepared = false;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(directUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                isPrepared = true;
                updateNotification();
                if (playWhenPrepared) {
                    mp.start();
                    playWhenPrepared = false;
                }
                if (playbackListener != null) {
                    playbackListener.onPrepared(mp.getDuration());
                }
                notifyPlaybackStateChanged();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (playbackListener != null) {
                    playbackListener.onPlaybackComplete();
                }
                notifyPlaybackStateChanged();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                notifyError("Không thể phát audio. Kiểm tra lại link hoặc quyền truy cập file.");
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "Failed to set data source", e);
            notifyError("Lỗi tải audio: " + e.getMessage());
        }
    }

    public void playAudio() {
        if (mediaPlayer != null && isPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateNotification();
            notifyPlaybackStateChanged();
        }
    }

    public void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification();
            notifyPlaybackStateChanged();
        }
    }

    public void rewind15Seconds() {
        if (mediaPlayer != null && isPrepared) {
            int newPosition = Math.max(0, mediaPlayer.getCurrentPosition() - 15_000);
            mediaPlayer.seekTo(newPosition);
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.seekTo(position);
        }
    }

    public void stopAudio() {
        releasePlayer();
        stopForeground(true);
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPrepared = false;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isPrepared() {
        return isPrepared && mediaPlayer != null;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null && isPrepared ? mediaPlayer.getDuration() : 0;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public String getCurrentSubtitle() {
        return currentSubtitle;
    }

    public String getCurrentCoverUrl() {
        return currentCoverUrl;
    }

    public String getOriginalAudioUrl() {
        return originalAudioUrl;
    }

    public boolean hasActiveTrack() {
        return isPrepared && mediaPlayer != null;
    }

    private void notifyPlaybackStateChanged() {
        if (playbackListener != null) {
            playbackListener.onPlaybackStateChanged(isPlaying());
        }
    }

    private void notifyError(String message) {
        if (playbackListener != null) {
            playbackListener.onError(message);
        }
    }

    private android.app.Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BookReadingApp")
                .setContentText(currentTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createNotification());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
