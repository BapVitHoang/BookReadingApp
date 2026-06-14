package com.hcmute.bookreadingapp.util;

import android.content.Context;
import android.content.Intent;

/**
 * Broadcast nội bộ app để đồng bộ trạng thái phát audio từ {@link com.hcmute.bookreadingapp.service.AudioService}.
 */
public final class PlaybackBroadcast {

    public static final String ACTION_PREPARED =
            "com.hcmute.bookreadingapp.action.PLAYBACK_PREPARED";
    public static final String ACTION_STATE_CHANGED =
            "com.hcmute.bookreadingapp.action.PLAYBACK_STATE_CHANGED";
    public static final String ACTION_COMPLETE =
            "com.hcmute.bookreadingapp.action.PLAYBACK_COMPLETE";
    public static final String ACTION_ERROR =
            "com.hcmute.bookreadingapp.action.PLAYBACK_ERROR";

    public static final String EXTRA_DURATION = "extra_duration";
    public static final String EXTRA_IS_PLAYING = "extra_is_playing";
    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_COVER_URL = "extra_cover_url";

    private PlaybackBroadcast() {
    }

    private static Intent baseIntent(Context context, String action) {
        Intent intent = new Intent(action);
        intent.setPackage(context.getPackageName());
        return intent;
    }

    public static void sendPrepared(Context context, int duration,
                                    String title, String subtitle, String coverUrl) {
        Intent intent = baseIntent(context, ACTION_PREPARED);
        intent.putExtra(EXTRA_DURATION, duration);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_SUBTITLE, subtitle);
        intent.putExtra(EXTRA_COVER_URL, coverUrl);
        context.sendBroadcast(intent);
    }

    public static void sendStateChanged(Context context, boolean isPlaying) {
        Intent intent = baseIntent(context, ACTION_STATE_CHANGED);
        intent.putExtra(EXTRA_IS_PLAYING, isPlaying);
        context.sendBroadcast(intent);
    }

    public static void sendComplete(Context context) {
        context.sendBroadcast(baseIntent(context, ACTION_COMPLETE));
    }

    public static void sendError(Context context, String message) {
        Intent intent = baseIntent(context, ACTION_ERROR);
        intent.putExtra(EXTRA_ERROR_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
