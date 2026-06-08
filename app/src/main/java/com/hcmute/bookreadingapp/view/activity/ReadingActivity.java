package com.hcmute.bookreadingapp.view.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.model.Book;

public class ReadingActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        TextView tvTitle = findViewById(R.id.tv_reading_title);
        TextView tvContent = findViewById(R.id.tv_reading_content);

        Book book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);

        if (book != null) {
            tvTitle.setText(book.getTitle() != null ? book.getTitle() : "Đang đọc sách");
            tvContent.setText(book.getContent() != null ? book.getContent() : "Nội dung cuốn sách đang được cập nhật...");
        }
    }
}
