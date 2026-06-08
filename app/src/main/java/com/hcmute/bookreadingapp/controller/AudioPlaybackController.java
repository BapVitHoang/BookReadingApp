package com.hcmute.bookreadingapp.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.hcmute.bookreadingapp.model.AudioTrack;
import com.hcmute.bookreadingapp.service.AudioService;

/**
 * Điều phối bind/unbind và lệnh phát audio tới {@link AudioService}.
 */
public class AudioPlaybackController {

    public interface ServiceCallback {
        void onServiceConnected(AudioService service);

        void onServiceDisconnected();
    }

    private final Context context;
    private AudioService audioService;
    private boolean bound;
    private ServiceCallback serviceCallback;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            bound = true;
            if (serviceCallback != null) {
                serviceCallback.onServiceConnected(audioService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
            bound = false;
            if (serviceCallback != null) {
                serviceCallback.onServiceDisconnected();
            }
        }
    };

    public AudioPlaybackController(Context context) {
        this.context = context;
    }

    public void bind(ServiceCallback callback) {
        this.serviceCallback = callback;
        Intent intent = new Intent(context, AudioService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        if (bound) {
            if (audioService != null) {
                audioService.setPlaybackListener(null);
            }
            context.unbindService(serviceConnection);
            bound = false;
            audioService = null;
        }
        serviceCallback = null;
    }

    public boolean isBound() {
        return bound;
    }

    public AudioService getService() {
        return audioService;
    }

    public void setPlaybackListener(AudioService.PlaybackListener listener) {
        if (audioService != null) {
            audioService.setPlaybackListener(listener);
        }
    }

    public void startTrack(AudioTrack track) {
        Intent serviceIntent = new Intent(context, AudioService.class);
        serviceIntent.putExtra(AudioService.EXTRA_AUDIO_URL, track.getAudioUrl());
        serviceIntent.putExtra(AudioService.EXTRA_TITLE, track.getTitle());
        serviceIntent.putExtra(AudioService.EXTRA_COVER_URL, track.getCoverUrl());
        serviceIntent.putExtra(AudioService.EXTRA_SUBTITLE, track.getSubtitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    public void play() {
        if (audioService != null) {
            audioService.playAudio();
        }
    }

    public void pause() {
        if (audioService != null) {
            audioService.pauseAudio();
        }
    }

    public void rewind15Seconds() {
        if (audioService != null) {
            audioService.rewind15Seconds();
        }
    }

    public void seekTo(int position) {
        if (audioService != null) {
            audioService.seekTo(position);
        }
    }
}
