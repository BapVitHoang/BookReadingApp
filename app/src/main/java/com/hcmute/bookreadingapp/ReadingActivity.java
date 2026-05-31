package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hcmute.bookreadingapp.service.ReadingSyncService;
import com.hcmute.bookreadingapp.storage.StorageManager;

public class ReadingActivity extends AppCompatActivity {

    private SeekBar readingSeekBar;
    private TextView tvPageProgress;

    private final String bookTitle = "Tên Cuốn Sách";
    private final int totalPages = 320;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        readingSeekBar = findViewById(R.id.reading_seek_bar);
        tvPageProgress = findViewById(R.id.tv_page_progress);

        StorageManager.saveLastBook(this, bookTitle);
        loadSavedProgress();

        readingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePageText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                syncProgressInBackground(progress);

                Toast.makeText(
                        ReadingActivity.this,
                        "Đang đồng bộ tiến độ đọc: " + progress + "%",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadSavedProgress() {
        int savedProgress = StorageManager.getReadingProgress(this, bookTitle);

        if (savedProgress == 0) {
            savedProgress = 15;
        }

        readingSeekBar.setProgress(savedProgress);
        updatePageText(savedProgress);
    }

    private void updatePageText(int progress) {
        int currentPage = Math.max(1, progress * totalPages / 100);

        tvPageProgress.setText(
                "Trang " + currentPage + " / " + totalPages
        );
    }

    private void syncProgressInBackground(int progress) {
        Intent intent = new Intent(this, ReadingSyncService.class);
        intent.putExtra(ReadingSyncService.EXTRA_BOOK_TITLE, bookTitle);
        intent.putExtra(ReadingSyncService.EXTRA_PROGRESS, progress);
        intent.putExtra(ReadingSyncService.EXTRA_SAVE_LAST_BOOK, true);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        syncProgressInBackground(readingSeekBar.getProgress());
    }
}
