package com.hcmute.bookreadingapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.BookController;
import com.hcmute.bookreadingapp.model.Book;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";

    private Book book;
    private BookController bookController;

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvDescription;
    private ImageView imgCover;
    private MaterialButton btnReadNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_book_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_app_bar), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(0, insets.top, 0, 0);
            return windowInsets;
        });

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_detail_title);
        tvAuthor = findViewById(R.id.tv_detail_author);
        tvDescription = findViewById(R.id.tv_detail_description);
        imgCover = findViewById(R.id.img_detail_cover);
        btnReadNow = findViewById(R.id.btn_read_now);

        bookController = new BookController();
        book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);

        bindBook(book);
        loadFullBookIfNeeded();

        btnReadNow.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, ReadingActivity.class);
            if (book != null) {
                intent.putExtra(ReadingActivity.EXTRA_BOOK, book);
            }
            startActivity(intent);
        });
    }

    private void bindBook(Book book) {
        if (book == null) {
            return;
        }

        tvTitle.setText(book.getTitle() != null ? book.getTitle() : "Tên cuốn sách");
        tvAuthor.setText("Tác giả: " + (book.getAuthor() != null ? book.getAuthor() : "Đang cập nhật"));
        tvDescription.setText(book.getDescription() != null ? book.getDescription() : "Đang cập nhật mô tả...");

        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(this)
                    .load(book.getCoverUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgCover);
        } else {
            imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void loadFullBookIfNeeded() {
        if (book == null || !needsFullBook(book)) {
            return;
        }

        String bookId = book.getId();
        if (bookId == null || bookId.isEmpty()) {
            return;
        }

        bookController.loadBookById(bookId, new BookController.BookDetailCallback() {
            @Override
            public void onSuccess(Book loadedBook) {
                book = loadedBook;
                bindBook(book);
            }

            @Override
            public void onError(String message) {
                // Giữ thông tin cơ bản từ featured book.
            }
        });
    }

    private boolean needsFullBook(Book book) {
        boolean missingDescription = book.getDescription() == null || book.getDescription().isEmpty();
        boolean missingContent = book.getContent() == null || book.getContent().isEmpty();
        return missingDescription || missingContent;
    }
}
