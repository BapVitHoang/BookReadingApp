package com.hcmute.bookreadingapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageManager {

    private static final String PREF_NAME = "BookReadingAppStorage";

    public static void saveReadingProgress(Context context, String bookTitle, int progress) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putInt("progress_" + bookTitle, progress)
                .apply();
    }

    public static int getReadingProgress(Context context, String bookTitle) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return prefs.getInt("progress_" + bookTitle, 0);
    }

    public static void saveFavorite(Context context, String bookTitle, boolean isFavorite) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putBoolean("favorite_" + bookTitle, isFavorite)
                .apply();
    }

    public static boolean isFavorite(Context context, String bookTitle) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return prefs.getBoolean("favorite_" + bookTitle, false);
    }

    public static void saveLastBook(Context context, String bookTitle) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        prefs.edit()
                .putString("last_book", bookTitle)
                .apply();
    }

    public static String getLastBook(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return prefs.getString("last_book", "Chưa có sách nào được mở gần đây");
    }
}