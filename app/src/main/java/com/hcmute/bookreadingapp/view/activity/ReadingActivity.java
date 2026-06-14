package com.hcmute.bookreadingapp.view.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.LibraryController;
import com.hcmute.bookreadingapp.model.Book;

public class ReadingActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";

    private static final float[] FONT_SIZES_SP = {16f, 18f, 20f, 22f};
    private static final int CHARS_PER_PAGE = 1500;

    private Book book;
    private LibraryController libraryController;

    private NestedScrollView scrollView;
    private View appBar;
    private View footer;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvPageProgress;
    private SeekBar seekBar;
    private ImageView btnFavorite;
    private ImageView btnFontSettings;

    private String bookTitle = "";
    private int currentProgress;
    private int fontSizeIndex = 1;
    private boolean userSeeking;
    private boolean overlaysVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        libraryController = new LibraryController(this);
        book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);

        bindViews();
        setupToolbar();
        bindBookContent();
        setupScrollTracking();
        setupSeekBar();
        setupFavoriteButton();
        setupFontSettings();
        setupContentTapToggle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        persistReadingState(false);
    }

    private void bindViews() {
        scrollView = findViewById(R.id.readingScrollView);
        appBar = findViewById(R.id.reading_app_bar);
        footer = findViewById(R.id.reading_footer);
        tvTitle = findViewById(R.id.tv_reading_title);
        tvContent = findViewById(R.id.tv_reading_content);
        tvPageProgress = findViewById(R.id.tv_page_progress);
        seekBar = findViewById(R.id.reading_seek_bar);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnFontSettings = findViewById(R.id.btn_font_settings);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.reading_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bindBookContent() {
        if (book == null) {
            tvTitle.setText(getString(R.string.reading_default_title));
            tvContent.setText(getString(R.string.reading_empty_content));
            seekBar.setEnabled(false);
            return;
        }

        bookTitle = book.getTitle() != null ? book.getTitle() : getString(R.string.reading_default_title);
        tvTitle.setText(bookTitle);
        tvContent.setText(book.getContent() != null ? book.getContent() : getString(R.string.reading_empty_content));

        currentProgress = libraryController.getReadingProgress(bookTitle);
        seekBar.setProgress(currentProgress);
        updatePageLabel(currentProgress);
        updateFavoriteIcon(libraryController.isFavorite(bookTitle));

        scrollView.post(() -> scrollToProgress(currentProgress));
        libraryController.enqueueReadingSync(this, bookTitle, currentProgress, true);
    }

    private void setupScrollTracking() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (userSeeking) {
                        return;
                    }
                    int progress = computeScrollProgress();
                    if (progress != currentProgress) {
                        currentProgress = progress;
                        seekBar.setProgress(progress);
                        updatePageLabel(progress);
                    }
                });
    }

    private void setupSeekBar() {
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentProgress = progress;
                    scrollToProgress(progress);
                    updatePageLabel(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userSeeking = false;
                persistReadingState(false);
            }
        });
    }

    private void setupFavoriteButton() {
        btnFavorite.setOnClickListener(v -> {
            if (bookTitle.isEmpty()) {
                return;
            }
            boolean newValue = !libraryController.isFavorite(bookTitle);
            libraryController.setFavorite(bookTitle, newValue);
            updateFavoriteIcon(newValue);
        });
    }

    private void setupFontSettings() {
        applyFontSize();
        btnFontSettings.setOnClickListener(v -> {
            fontSizeIndex = (fontSizeIndex + 1) % FONT_SIZES_SP.length;
            applyFontSize();
            scrollView.post(() -> scrollToProgress(currentProgress));
        });
    }

    private void setupContentTapToggle() {
        tvContent.setOnClickListener(v -> toggleOverlays());
    }

    private void applyFontSize() {
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZES_SP[fontSizeIndex]);
    }

    private void toggleOverlays() {
        overlaysVisible = !overlaysVisible;
        int visibility = overlaysVisible ? View.VISIBLE : View.GONE;
        appBar.setVisibility(visibility);
        footer.setVisibility(visibility);
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        btnFavorite.setImageResource(
                isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );
    }

    private void updatePageLabel(int progress) {
        int totalPages = estimateTotalPages();
        int currentPage = progress <= 0
                ? 1
                : Math.max(1, Math.min(totalPages, (int) Math.ceil(progress * totalPages / 100.0)));
        tvPageProgress.setText(getString(R.string.reading_page_progress, currentPage, totalPages));
    }

    private int estimateTotalPages() {
        if (book == null || book.getContent() == null || book.getContent().isEmpty()) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil(book.getContent().length() / (double) CHARS_PER_PAGE));
    }

    private int computeScrollProgress() {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            return 100;
        }
        return Math.min(100, Math.max(0, (scrollView.getScrollY() * 100) / maxScroll));
    }

    private int getMaxScroll() {
        if (scrollView.getChildCount() == 0) {
            return 0;
        }
        View content = scrollView.getChildAt(0);
        return Math.max(0, content.getHeight() - scrollView.getHeight());
    }

    private void scrollToProgress(int progress) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            scrollView.scrollTo(0, 0);
            return;
        }
        int targetScroll = (progress * maxScroll) / 100;
        scrollView.scrollTo(0, targetScroll);
    }

    private void persistReadingState(boolean saveLastBook) {
        if (bookTitle.isEmpty()) {
            return;
        }
        libraryController.enqueueReadingSync(this, bookTitle, currentProgress, saveLastBook);
    }
}
