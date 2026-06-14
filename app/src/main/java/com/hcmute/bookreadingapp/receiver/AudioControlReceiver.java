package com.hcmute.bookreadingapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.hcmute.bookreadingapp.service.AudioService;

/**
 * Nhận thao tác play/pause/rewind từ notification và chuyển tới {@link AudioService}.
 */
public class AudioControlReceiver extends BroadcastReceiver {

    public static final String ACTION_PLAY =
            "com.hcmute.bookreadingapp.action.AUDIO_PLAY";
    public static final String ACTION_PAUSE =
            "com.hcmute.bookreadingapp.action.AUDIO_PAUSE";
    public static final String ACTION_REWIND =
            "com.hcmute.bookreadingapp.action.AUDIO_REWIND";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }

        Intent serviceIntent = new Intent(context, AudioService.class);
        serviceIntent.setAction(intent.getAction());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
