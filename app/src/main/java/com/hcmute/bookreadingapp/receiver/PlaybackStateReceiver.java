package com.hcmute.bookreadingapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hcmute.bookreadingapp.util.PlaybackBroadcast;

/**
 * Nhận broadcast trạng thái phát audio từ {@link com.hcmute.bookreadingapp.service.AudioService}.
 */
public class PlaybackStateReceiver extends BroadcastReceiver {

    public interface Listener {
        void onPrepared(int duration, String title, String subtitle, String coverUrl);

        void onStateChanged(boolean isPlaying);

        void onComplete();

        void onError(String message);
    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        if (PlaybackBroadcast.ACTION_PREPARED.equals(action)) {
            listener.onPrepared(
                    intent.getIntExtra(PlaybackBroadcast.EXTRA_DURATION, 0),
                    intent.getStringExtra(PlaybackBroadcast.EXTRA_TITLE),
                    intent.getStringExtra(PlaybackBroadcast.EXTRA_SUBTITLE),
                    intent.getStringExtra(PlaybackBroadcast.EXTRA_COVER_URL)
            );
        } else if (PlaybackBroadcast.ACTION_STATE_CHANGED.equals(action)) {
            listener.onStateChanged(intent.getBooleanExtra(PlaybackBroadcast.EXTRA_IS_PLAYING, false));
        } else if (PlaybackBroadcast.ACTION_COMPLETE.equals(action)) {
            listener.onComplete();
        } else if (PlaybackBroadcast.ACTION_ERROR.equals(action)) {
            listener.onError(intent.getStringExtra(PlaybackBroadcast.EXTRA_ERROR_MESSAGE));
        }
    }
}
