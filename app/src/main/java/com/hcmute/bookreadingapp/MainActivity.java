package com.hcmute.bookreadingapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.hcmute.bookreadingapp.service.AudioService;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView miniPlayer;
    private View miniPlayerContainer;
    private View miniPlayerProgress;
    private View miniPlayerInfo;
    private ImageView imgMiniCover;
    private TextView tvMiniTitle;
    private TextView tvMiniSubtitle;
    private ImageButton btnMiniPlayPause;
    private ImageButton btnMiniRewind;

    private AudioService audioService;
    private boolean bound = false;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (bound && audioService != null && audioService.isPrepared()) {
                updateMiniPlayerProgress(
                        audioService.getCurrentPosition(),
                        audioService.getDuration()
                );
                if (audioService.isPlaying()) {
                    uiHandler.postDelayed(this, 500);
                }
            }
        }
    };

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
                        updateMiniPlayer();
                        updateMiniPlayerProgress(0, duration);
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> hideMiniPlayer());
                }

                @Override
                public void onPlaybackComplete() {
                    runOnUiThread(() -> {
                        uiHandler.removeCallbacks(progressUpdater);
                        hideMiniPlayer();
                    });
                }

                @Override
                public void onPlaybackStateChanged(boolean isPlaying) {
                    runOnUiThread(() -> {
                        updateMiniPlayerPlayButton(isPlaying);
                        if (isPlaying) {
                            uiHandler.post(progressUpdater);
                        } else {
                            uiHandler.removeCallbacks(progressUpdater);
                            if (audioService != null) {
                                updateMiniPlayerProgress(
                                        audioService.getCurrentPosition(),
                                        audioService.getDuration()
                                );
                            }
                        }
                    });
                }
            });

            updateMiniPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            audioService = null;
            uiHandler.removeCallbacks(progressUpdater);
            hideMiniPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(0, insets.top, 0, 0);
            return windowInsets;
        });

        setupMiniPlayer();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BooksFragment())
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_books) {
                selectedFragment = new BooksFragment();
            } else if (id == R.id.nav_podcourse) {
                selectedFragment = new PodCourseFragment();
            } else if (id == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
            } else if (id == R.id.nav_challenge) {
                selectedFragment = new ChallengesFragment();
            } else if (id == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private void setupMiniPlayer() {
        miniPlayer = findViewById(R.id.mini_player);
        miniPlayerContainer = findViewById(R.id.mini_player_container);
        miniPlayerProgress = findViewById(R.id.mini_player_progress);
        miniPlayerInfo = findViewById(R.id.mini_player_info);
        imgMiniCover = findViewById(R.id.img_mini_cover);
        tvMiniTitle = findViewById(R.id.tv_mini_title);
        tvMiniSubtitle = findViewById(R.id.tv_mini_subtitle);
        btnMiniPlayPause = findViewById(R.id.btn_mini_play_pause);
        btnMiniRewind = findViewById(R.id.btn_mini_rewind);

        miniPlayerInfo.setOnClickListener(v -> openFullPlayer());

        btnMiniPlayPause.setOnClickListener(v -> {
            if (!bound || audioService == null) {
                return;
            }
            if (audioService.isPlaying()) {
                audioService.pauseAudio();
            } else {
                audioService.playAudio();
            }
        });

        btnMiniRewind.setOnClickListener(v -> {
            if (bound && audioService != null) {
                audioService.rewind15Seconds();
                updateMiniPlayerProgress(
                        audioService.getCurrentPosition(),
                        audioService.getDuration()
                );
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
            if (audioService != null) {
                audioService.setPlaybackListener(null);
            }
            unbindService(serviceConnection);
            bound = false;
            audioService = null;
        }
    }

    private void updateMiniPlayer() {
        if (!bound || audioService == null || !audioService.hasActiveTrack()) {
            hideMiniPlayer();
            return;
        }

        miniPlayer.setVisibility(View.VISIBLE);
        tvMiniTitle.setText(audioService.getCurrentTitle());
        tvMiniSubtitle.setText(audioService.getCurrentSubtitle());
        updateMiniPlayerPlayButton(audioService.isPlaying());

        updateMiniPlayerProgress(
                audioService.getCurrentPosition(),
                audioService.getDuration()
        );

        if (audioService.isPlaying()) {
            uiHandler.removeCallbacks(progressUpdater);
            uiHandler.post(progressUpdater);
        }

        String coverUrl = audioService.getCurrentCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(coverUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgMiniCover);
        } else {
            imgMiniCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void updateMiniPlayerProgress(int position, int duration) {
        if (miniPlayerContainer == null || miniPlayerProgress == null) {
            return;
        }

        Runnable applyProgress = () -> {
            int containerWidth = miniPlayerContainer.getWidth();
            if (containerWidth <= 0) {
                return;
            }

            float fraction = duration > 0 ? (float) position / duration : 0f;
            fraction = Math.max(0f, Math.min(1f, fraction));

            int progressWidth = (int) (containerWidth * fraction);
            ViewGroup.LayoutParams params = miniPlayerProgress.getLayoutParams();
            params.width = progressWidth;
            miniPlayerProgress.setLayoutParams(params);
        };

        if (miniPlayerContainer.getWidth() == 0) {
            miniPlayerContainer.post(applyProgress);
        } else {
            applyProgress.run();
        }
    }

    private void updateMiniPlayerPlayButton(boolean isPlaying) {
        if (miniPlayer.getVisibility() != View.VISIBLE) {
            updateMiniPlayer();
            return;
        }
        btnMiniPlayPause.setImageResource(
                isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play
        );
    }

    private void hideMiniPlayer() {
        miniPlayer.setVisibility(View.GONE);
    }

    private void openFullPlayer() {
        if (!bound || audioService == null || !audioService.hasActiveTrack()) {
            return;
        }

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AudioPlayerActivity.EXTRA_AUDIO_URL, audioService.getOriginalAudioUrl());
        intent.putExtra(AudioPlayerActivity.EXTRA_TITLE, audioService.getCurrentTitle());
        intent.putExtra(AudioPlayerActivity.EXTRA_COVER_URL, audioService.getCurrentCoverUrl());
        startActivity(intent);
    }
}
