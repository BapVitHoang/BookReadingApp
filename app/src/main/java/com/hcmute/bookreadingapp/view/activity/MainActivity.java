package com.hcmute.bookreadingapp.view.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.AudioPlaybackController;
import com.hcmute.bookreadingapp.receiver.PlaybackStateReceiver;
import com.hcmute.bookreadingapp.service.AudioService;
import com.hcmute.bookreadingapp.util.PlaybackBroadcast;
import com.hcmute.bookreadingapp.view.fragment.BooksFragment;
import com.hcmute.bookreadingapp.view.fragment.ChallengesFragment;
import com.hcmute.bookreadingapp.view.fragment.ExploreFragment;
import com.hcmute.bookreadingapp.view.fragment.LibraryFragment;
import com.hcmute.bookreadingapp.view.fragment.PodCourseFragment;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView miniPlayer;
    private View miniPlayerContainer;
    private View miniPlayerProgress;
    private View miniPlayerInfo;
    private ImageView imgMiniCover;
    private TextView tvMiniTitle;
    private TextView tvMiniSubtitle;
    private ImageButton btnMiniPlayPause;

    private AudioPlaybackController playbackController;
    private PlaybackStateReceiver playbackStateReceiver;
    private boolean playbackReceiverRegistered;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            AudioService service = playbackController.getService();
            if (service != null && service.isPrepared()) {
                updateMiniPlayerProgress(service.getCurrentPosition(), service.getDuration());
                if (service.isPlaying()) {
                    uiHandler.postDelayed(this, 500);
                }
            }
        }
    };

    private final PlaybackStateReceiver.Listener playbackBroadcastListener =
            new PlaybackStateReceiver.Listener() {
                @Override
                public void onPrepared(int duration, String title, String subtitle, String coverUrl) {
                    runOnUiThread(() -> {
                        showMiniPlayer(title, subtitle, coverUrl);
                        updateMiniPlayerProgress(0, duration);
                    });
                }

                @Override
                public void onStateChanged(boolean isPlaying) {
                    runOnUiThread(() -> {
                        updateMiniPlayerPlayButton(isPlaying);
                        if (isPlaying) {
                            uiHandler.post(progressUpdater);
                        } else {
                            uiHandler.removeCallbacks(progressUpdater);
                            AudioService service = playbackController.getService();
                            if (service != null) {
                                updateMiniPlayerProgress(
                                        service.getCurrentPosition(),
                                        service.getDuration()
                                );
                            }
                        }
                    });
                }

                @Override
                public void onComplete() {
                    runOnUiThread(() -> {
                        uiHandler.removeCallbacks(progressUpdater);
                        hideMiniPlayer();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> hideMiniPlayer());
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        playbackController = new AudioPlaybackController(this);
        playbackStateReceiver = new PlaybackStateReceiver();
        playbackStateReceiver.setListener(playbackBroadcastListener);

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
        ImageButton btnMiniRewind = findViewById(R.id.btn_mini_rewind);

        miniPlayerInfo.setOnClickListener(v -> openFullPlayer());

        btnMiniPlayPause.setOnClickListener(v -> {
            AudioService service = playbackController.getService();
            if (service == null) {
                return;
            }
            if (service.isPlaying()) {
                playbackController.pause();
            } else {
                playbackController.play();
            }
        });

        btnMiniRewind.setOnClickListener(v -> {
            AudioService service = playbackController.getService();
            if (service != null) {
                playbackController.rewind15Seconds();
                updateMiniPlayerProgress(service.getCurrentPosition(), service.getDuration());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerPlaybackReceiver();
        playbackController.bind(new AudioPlaybackController.ServiceCallback() {
            @Override
            public void onServiceConnected(AudioService service) {
                updateMiniPlayer();
            }

            @Override
            public void onServiceDisconnected() {
                uiHandler.removeCallbacks(progressUpdater);
                hideMiniPlayer();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        uiHandler.removeCallbacks(progressUpdater);
        unregisterPlaybackReceiver();
        playbackController.unbind();
    }

    private void registerPlaybackReceiver() {
        if (playbackReceiverRegistered) {
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackBroadcast.ACTION_PREPARED);
        filter.addAction(PlaybackBroadcast.ACTION_STATE_CHANGED);
        filter.addAction(PlaybackBroadcast.ACTION_COMPLETE);
        filter.addAction(PlaybackBroadcast.ACTION_ERROR);

        ContextCompat.registerReceiver(
                this,
                playbackStateReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
        playbackReceiverRegistered = true;
    }

    private void unregisterPlaybackReceiver() {
        if (!playbackReceiverRegistered) {
            return;
        }
        unregisterReceiver(playbackStateReceiver);
        playbackReceiverRegistered = false;
    }

    private void updateMiniPlayer() {
        AudioService service = playbackController.getService();
        if (service == null || !service.hasActiveTrack()) {
            hideMiniPlayer();
            return;
        }

        showMiniPlayer(
                service.getCurrentTitle(),
                service.getCurrentSubtitle(),
                service.getCurrentCoverUrl()
        );
        updateMiniPlayerPlayButton(service.isPlaying());
        updateMiniPlayerProgress(service.getCurrentPosition(), service.getDuration());

        if (service.isPlaying()) {
            uiHandler.removeCallbacks(progressUpdater);
            uiHandler.post(progressUpdater);
        }
    }

    private void showMiniPlayer(String title, String subtitle, String coverUrl) {
        miniPlayer.setVisibility(View.VISIBLE);
        tvMiniTitle.setText(title != null ? title : "");
        tvMiniSubtitle.setText(subtitle != null ? subtitle : "");

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
        AudioService service = playbackController.getService();
        if (service == null || !service.hasActiveTrack()) {
            return;
        }

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AudioPlayerActivity.EXTRA_AUDIO_URL, service.getOriginalAudioUrl());
        intent.putExtra(AudioPlayerActivity.EXTRA_TITLE, service.getCurrentTitle());
        intent.putExtra(AudioPlayerActivity.EXTRA_COVER_URL, service.getCurrentCoverUrl());
        startActivity(intent);
    }
}
