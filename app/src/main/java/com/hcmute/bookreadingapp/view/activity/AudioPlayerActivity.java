package com.hcmute.bookreadingapp.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.AudioPlaybackController;
import com.hcmute.bookreadingapp.model.AudioTrack;
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

    private AudioPlaybackController playbackController;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            AudioService audioService = playbackController.getService();
            if (audioService == null) {
                return;
            }

            if (audioService.isPrepared()) {
                updateProgressUi(audioService.getCurrentPosition(), audioService.getDuration());
            }

            if (isPlaying || audioService.isPlaying()) {
                uiHandler.postDelayed(this, 500);
            }
        }
    };

    private final AudioService.PlaybackListener playbackListener = new AudioService.PlaybackListener() {
        @Override
        public void onPrepared(int duration) {
            runOnUiThread(() -> {
                seekBar.setMax(duration);
                tvTotalTime.setText(formatTime(duration));
                AudioService service = playbackController.getService();
                if (service != null && service.isPlaying()) {
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
                } else {
                    AudioService service = playbackController.getService();
                    if (service != null && service.isPrepared()) {
                        tvPlaybackStatus.setText("Đã tạm dừng");
                    }
                    uiHandler.removeCallbacks(progressUpdater);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestNotificationPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playbackController = new AudioPlaybackController(this);

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
        audioUrl = getIntent().getStringExtra(EXTRA_AUDIO_URL);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        coverUrl = getIntent().getStringExtra(EXTRA_COVER_URL);
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
                playbackController.seekTo(seekBar.getProgress());
            }
        });
    }

    private void togglePlayback() {
        AudioTrack track = new AudioTrack(audioUrl, title, coverUrl, "Đang phát sách nói");
        if (!track.hasAudioUrl()) {
            Toast.makeText(this, "Chưa có link audio cho sách này", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPlaying) {
            playbackController.startTrack(track);

            AudioService service = playbackController.getService();
            if (service != null && service.isPrepared()) {
                playbackController.play();
            }

            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            tvPlaybackStatus.setText("Đang tải audio...");
            startProgressUpdates();
        } else {
            playbackController.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            tvPlaybackStatus.setText("Đã tạm dừng");
            uiHandler.removeCallbacks(progressUpdater);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        playbackController.bind(new AudioPlaybackController.ServiceCallback() {
            @Override
            public void onServiceConnected(AudioService service) {
                service.setPlaybackListener(playbackListener);
                syncUiWithService();
            }

            @Override
            public void onServiceDisconnected() {
                uiHandler.removeCallbacks(progressUpdater);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        uiHandler.removeCallbacks(progressUpdater);
        playbackController.unbind();
    }

    private void syncUiWithService() {
        AudioService service = playbackController.getService();
        if (service != null && service.isPrepared()) {
            int duration = service.getDuration();
            seekBar.setMax(duration);
            tvTotalTime.setText(formatTime(duration));
            updateProgressUi(service.getCurrentPosition(), duration);
        }

        if (service != null && service.isPlaying()) {
            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            tvPlaybackStatus.setText("Đang phát...");
            startProgressUpdates();
        }
    }

    private void startProgressUpdates() {
        uiHandler.removeCallbacks(progressUpdater);
        uiHandler.post(progressUpdater);
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
