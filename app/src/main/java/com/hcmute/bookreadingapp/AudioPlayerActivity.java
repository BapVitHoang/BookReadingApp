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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmute.bookreadingapp.service.AudioService;

public class AudioPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_AUDIO_URL = "extra_audio_url";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_COVER_URL = "extra_cover_url";

    private FloatingActionButton btnPlayPause;
    private TextView tvPlaybackStatus;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private TextView tvTitle;
    private TextView tvAuthor;
    private SeekBar seekBar;
    private ImageView imgCover;

    private String audioUrl;
    private String title;
    private String coverUrl;

    private boolean isPlaying = false;
    private boolean userSeeking = false;

    private AudioService audioService;
    private boolean bound = false;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (!bound || audioService == null) {
                return;
            }

            if (audioService.isPrepared()) {
                int position = audioService.getCurrentPosition();
                int duration = audioService.getDuration();
                updateProgressUi(position, duration);
            }

            // Keep ticking while playback is intended, even while the track is
            // still loading on the very first play (isPrepared == false). Once
            // the track becomes prepared, the timer syncs automatically.
            if (isPlaying || audioService.isPlaying()) {
                uiHandler.postDelayed(this, 500);
            }
        }
    };

    private void startProgressUpdates() {
        uiHandler.removeCallbacks(progressUpdater);
        uiHandler.post(progressUpdater);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            bound = true;

            audioService.setPlaybackListener(new AudioService.PlaybackListener() {
                @Override
                public void onPrepared(int duration) {
                    runOnUiThread(() -> {
                        seekBar.setMax(duration);
                        tvTotalTime.setText(formatTime(duration));
                        if (audioService != null && audioService.isPlaying()) {
                            isPlaying = true;
                            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                            tvPlaybackStatus.setText("Đang phát...");
                            startProgressUpdates();
                        } else {
                            tvPlaybackStatus.setText("Sẵn sàng phát");
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        isPlaying = false;
                        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                        tvPlaybackStatus.setText(message);
                        Toast.makeText(AudioPlayerActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onPlaybackComplete() {
                    runOnUiThread(() -> {
                        isPlaying = false;
                        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                        tvPlaybackStatus.setText("Đã phát xong");
                        uiHandler.removeCallbacks(progressUpdater);
                    });
                }

                @Override
                public void onPlaybackStateChanged(boolean playing) {
                    runOnUiThread(() -> {
                        isPlaying = playing;
                        btnPlayPause.setImageResource(
                                playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play
                        );
                        if (playing) {
                            tvPlaybackStatus.setText("Đang phát...");
                            startProgressUpdates();
                        } else if (audioService != null && audioService.isPrepared()) {
                            tvPlaybackStatus.setText("Đã tạm dừng");
                            uiHandler.removeCallbacks(progressUpdater);
                        }
                    });
                }
            });

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

        readIntentExtras();

        Toolbar toolbar = findViewById(R.id.player_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        btnPlayPause = findViewById(R.id.btn_play_pause);
        tvPlaybackStatus = findViewById(R.id.tv_playback_status);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        tvTitle = findViewById(R.id.player_title);
        tvAuthor = findViewById(R.id.player_author);
        seekBar = findViewById(R.id.player_seekbar);
        imgCover = findViewById(R.id.img_player_cover);

        bindBookInfo();
        setupSeekBar();

        btnPlayPause.setOnClickListener(v -> togglePlayback());
    }

    private void readIntentExtras() {
        Intent intent = getIntent();
        audioUrl = intent.getStringExtra(EXTRA_AUDIO_URL);
        title = intent.getStringExtra(EXTRA_TITLE);
        coverUrl = intent.getStringExtra(EXTRA_COVER_URL);
    }

    private void bindBookInfo() {
        tvTitle.setText(title != null && !title.isEmpty() ? title : "Sách nói");
        tvAuthor.setText("Sách nói");
        tvCurrentTime.setText("0:00");
        tvTotalTime.setText("0:00");
        seekBar.setProgress(0);
        tvPlaybackStatus.setText(
                audioUrl != null && !audioUrl.isEmpty()
                        ? "Nhấn Play để phát"
                        : "Chưa có link audio"
        );

        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(coverUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgCover);
        }
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userSeeking = false;
                if (bound && audioService != null) {
                    audioService.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    private void togglePlayback() {
        if (audioUrl == null || audioUrl.isEmpty()) {
            Toast.makeText(this, "Chưa có link audio cho sách này", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent serviceIntent = new Intent(this, AudioService.class);
        serviceIntent.putExtra(AudioService.EXTRA_AUDIO_URL, audioUrl);
        serviceIntent.putExtra(AudioService.EXTRA_TITLE, title);
        serviceIntent.putExtra(AudioService.EXTRA_COVER_URL, coverUrl);
        serviceIntent.putExtra(AudioService.EXTRA_SUBTITLE, "Đang phát sách nói");

        if (!isPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            if (bound && audioService != null && audioService.isPrepared()) {
                audioService.playAudio();
            }

            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            tvPlaybackStatus.setText("Đang tải audio...");
            startProgressUpdates();
        } else {
            if (bound && audioService != null) {
                audioService.pauseAudio();
            }

            isPlaying = false;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            tvPlaybackStatus.setText("Đã tạm dừng");
            uiHandler.removeCallbacks(progressUpdater);
        }
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
            if (audioService != null) {
                audioService.setPlaybackListener(null);
            }
            unbindService(serviceConnection);
            bound = false;
        }
    }

    private void syncUiWithService() {
        if (audioService != null && audioService.isPrepared()) {
            int duration = audioService.getDuration();
            seekBar.setMax(duration);
            tvTotalTime.setText(formatTime(duration));
            updateProgressUi(audioService.getCurrentPosition(), duration);
        }

        if (audioService != null && audioService.isPlaying()) {
            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            tvPlaybackStatus.setText("Đang phát...");
            startProgressUpdates();
        }
    }

    private void updateProgressUi(int position, int duration) {
        if (!userSeeking && duration > 0) {
            seekBar.setMax(duration);
            seekBar.setProgress(position);
        }
        tvCurrentTime.setText(formatTime(position));
        if (duration > 0) {
            tvTotalTime.setText(formatTime(duration));
        }
        if (isPlaying) {
            tvPlaybackStatus.setText("Đang phát...");
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
