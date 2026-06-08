package com.hcmute.bookreadingapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hcmute.bookreadingapp.repository.LibraryRepository;

public class ReadingSyncService extends Service {

    private static final String TAG = "ReadingSyncService";

    public static final String EXTRA_BOOK_TITLE = "extra_book_title";
    public static final String EXTRA_PROGRESS = "extra_progress";
    public static final String EXTRA_SAVE_LAST_BOOK = "extra_save_last_book";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        final String bookTitle = intent.getStringExtra(EXTRA_BOOK_TITLE);
        final int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
        final boolean saveLastBook = intent.getBooleanExtra(EXTRA_SAVE_LAST_BOOK, false);

        if (bookTitle != null) {
            LibraryRepository libraryRepository = new LibraryRepository(this);
            libraryRepository.saveReadingProgress(bookTitle, progress);

            if (saveLastBook) {
                libraryRepository.saveLastBook(bookTitle);
            }

            Log.d(TAG, "Synced progress for " + bookTitle + ": " + progress + "%");
        }

        stopSelf(startId);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
