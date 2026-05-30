package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.hcmute.bookreadingapp.storage.StorageManager;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tvContinueReading;
    private MaterialButton btnReadNow;

    private MaterialButton btnFavorite;

    private final String bookTitle = "Tên Cuốn Sách";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        tvContinueReading = findViewById(R.id.tv_continue_reading);
        btnReadNow = findViewById(R.id.btn_read_now);
        btnFavorite = findViewById(R.id.btn_favorite);

        loadReadingProgress();
        loadFavoriteStatus();

        btnReadNow.setOnClickListener(v -> {
            StorageManager.saveLastBook(this, bookTitle);
            Intent intent = new Intent(BookDetailActivity.this, ReadingActivity.class);
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(v -> {
            boolean currentStatus = StorageManager.isFavorite(this, bookTitle);

            StorageManager.saveFavorite(
                    this,
                    bookTitle,
                    !currentStatus
            );

            loadFavoriteStatus();
        });
    }

    private void loadReadingProgress() {
        int progress = StorageManager.getReadingProgress(this, bookTitle);

        tvContinueReading.setText(
                "Continue Reading: " + progress + "%"
        );
    }

    private void loadFavoriteStatus() {
        boolean isFavorite = StorageManager.isFavorite(this, bookTitle);

        if (isFavorite) {
            btnFavorite.setText("♥ Đã thêm vào yêu thích");
        } else {
            btnFavorite.setText("♡ Thêm vào yêu thích");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadReadingProgress();

        loadFavoriteStatus();
    }
}