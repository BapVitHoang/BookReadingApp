package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        TextView tvTitle = findViewById(R.id.tv_detail_title);
        TextView tvAuthor = findViewById(R.id.tv_detail_author);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        MaterialButton btnReadNow = findViewById(R.id.btn_read_now);

        // Lấy dữ liệu sách từ Intent
        com.hcmute.bookreadingapp.model.Book book = (com.hcmute.bookreadingapp.model.Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            tvTitle.setText(book.getTitle() != null ? book.getTitle() : "Tên cuốn sách");
            tvAuthor.setText("Tác giả: " + (book.getAuthor() != null ? book.getAuthor() : "Đang cập nhật"));
            tvDescription.setText(book.getDescription() != null ? book.getDescription() : "Đang cập nhật mô tả...");
        }

        btnReadNow.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, ReadingActivity.class);
            if (book != null) {
                intent.putExtra("book", book);
            }
            startActivity(intent);
        });
    }
}