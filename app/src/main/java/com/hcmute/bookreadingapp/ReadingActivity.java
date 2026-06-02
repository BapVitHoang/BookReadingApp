package com.hcmute.bookreadingapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        TextView tvTitle = findViewById(R.id.tv_reading_title);
        TextView tvContent = findViewById(R.id.tv_reading_content);

        // Lấy dữ liệu sách từ Intent
        com.hcmute.bookreadingapp.model.Book book = (com.hcmute.bookreadingapp.model.Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            tvTitle.setText(book.getTitle() != null ? book.getTitle() : "Đang đọc sách");
            tvContent.setText(book.getContent() != null ? book.getContent() : "Nội dung cuốn sách đang được cập nhật...");
        }
    }
}