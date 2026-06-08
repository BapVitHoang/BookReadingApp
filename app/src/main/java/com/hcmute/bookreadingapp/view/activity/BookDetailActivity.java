package com.hcmute.bookreadingapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.model.Book;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        TextView tvTitle = findViewById(R.id.tv_detail_title);
        TextView tvAuthor = findViewById(R.id.tv_detail_author);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        MaterialButton btnReadNow = findViewById(R.id.btn_read_now);

        Book book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);

        if (book != null) {
            tvTitle.setText(book.getTitle() != null ? book.getTitle() : "Tên cuốn sách");
            tvAuthor.setText("Tác giả: " + (book.getAuthor() != null ? book.getAuthor() : "Đang cập nhật"));
            tvDescription.setText(book.getDescription() != null ? book.getDescription() : "Đang cập nhật mô tả...");
        }

        btnReadNow.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, ReadingActivity.class);
            if (book != null) {
                intent.putExtra(ReadingActivity.EXTRA_BOOK, book);
            }
            startActivity(intent);
        });
    }
}
