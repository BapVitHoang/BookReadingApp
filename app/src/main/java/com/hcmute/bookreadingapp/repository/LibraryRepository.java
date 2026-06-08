package com.hcmute.bookreadingapp.repository;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lưu trữ tiến độ đọc, yêu thích và sách mở gần đây (local).
 */
public class LibraryRepository {

    private static final String PREF_NAME = "BookReadingAppStorage";
    private static final String DEFAULT_LAST_BOOK = "Chưa có sách nào được mở gần đây";

    private final SharedPreferences prefs;

    public LibraryRepository(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveReadingProgress(String bookTitle, int progress) {
        prefs.edit()
                .putInt("progress_" + bookTitle, progress)
                .apply();
    }

    public int getReadingProgress(String bookTitle) {
        return prefs.getInt("progress_" + bookTitle, 0);
    }

    public void saveFavorite(String bookTitle, boolean isFavorite) {
        prefs.edit()
                .putBoolean("favorite_" + bookTitle, isFavorite)
                .apply();
    }

    public boolean isFavorite(String bookTitle) {
        return prefs.getBoolean("favorite_" + bookTitle, false);
    }

    public void saveLastBook(String bookTitle) {
        prefs.edit()
                .putString("last_book", bookTitle)
                .apply();
    }

    public String getLastBook() {
        return prefs.getString("last_book", DEFAULT_LAST_BOOK);
    }

    public boolean hasRecentBook() {
        return !DEFAULT_LAST_BOOK.equals(getLastBook());
    }
}
