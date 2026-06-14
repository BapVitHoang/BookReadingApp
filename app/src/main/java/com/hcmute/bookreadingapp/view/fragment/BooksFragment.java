package com.hcmute.bookreadingapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.BookController;
import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.FeaturedBook;
import com.hcmute.bookreadingapp.view.activity.AudioPlayerActivity;
import com.hcmute.bookreadingapp.view.activity.BookDetailActivity;
import com.hcmute.bookreadingapp.view.adapter.BookAdapter;
import com.hcmute.bookreadingapp.view.adapter.FeaturedBookAdapter;

import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment {

    private static final String TAG = "BooksFragment";

    private ViewPager2 vpFeatured;
    private BookAdapter bookAdapter;
    private FeaturedBookAdapter featuredBookAdapter;
    private BookController bookController;
    private List<Book> allBooks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        RecyclerView rvBooks = view.findViewById(R.id.rv_books);
        vpFeatured = view.findViewById(R.id.vp_featured);

        bookController = new BookController();

        bookAdapter = new BookAdapter(this::openBookDetail);

        featuredBookAdapter = new FeaturedBookAdapter(new FeaturedBookAdapter.OnFeaturedBookListener() {
            @Override
            public void onPlayClick(FeaturedBook featuredBook) {
                Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
                intent.putExtra(AudioPlayerActivity.EXTRA_AUDIO_URL, featuredBook.getAudioUrl());
                intent.putExtra(AudioPlayerActivity.EXTRA_TITLE, featuredBook.getTitle());
                intent.putExtra(AudioPlayerActivity.EXTRA_COVER_URL, featuredBook.getCoverUrl());
                startActivity(intent);
            }

            @Override
            public void onCardClick(FeaturedBook featuredBook) {
                openBookDetail(toBook(featuredBook));
            }
        });

        rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        rvBooks.setAdapter(bookAdapter);

        setupFeaturedCarousel();
        loadBooks();

        return view;
    }

    private void setupFeaturedCarousel() {
        vpFeatured.setAdapter(featuredBookAdapter);
        vpFeatured.setOffscreenPageLimit(3);

        float density = getResources().getDisplayMetrics().density;
        int peekWidth = (int) (48 * density);
        RecyclerView recyclerView = (RecyclerView) vpFeatured.getChildAt(0);
        recyclerView.setPadding(peekWidth, 0, peekWidth, 0);
        recyclerView.setClipToPadding(false);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setNestedScrollingEnabled(false);

        vpFeatured.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            float scale = 1f - (absPos * 0.12f);
            if (scale < 0.85f) {
                scale = 0.85f;
            }
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(1f - (absPos * 0.15f));
        });
    }

    private void loadBooks() {
        bookController.loadAllBooks(new BookController.BooksCallback() {
            @Override
            public void onSuccess(List<Book> books) {
                allBooks = books;
                bookAdapter.setBookList(books);
                Log.d(TAG, "Loaded " + books.size() + " books");
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải sách: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        bookController.loadFeaturedBooks(new BookController.FeaturedBooksCallback() {
            @Override
            public void onSuccess(List<FeaturedBook> books) {
                featuredBookAdapter.setFeaturedBooks(books);
                if (!books.isEmpty()) {
                    vpFeatured.setCurrentItem(0, false);
                }
                Log.d(TAG, "Loaded " + books.size() + " featured books");
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải sách nổi bật: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openBookDetail(Book book) {
        if (getActivity() == null || book == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.EXTRA_BOOK, book);
        startActivity(intent);
    }

    private Book toBook(FeaturedBook featuredBook) {
        if (featuredBook == null) {
            return new Book();
        }

        Book matchedBook = findBookForFeatured(featuredBook);
        if (matchedBook != null) {
            return matchedBook;
        }

        Book book = new Book();
        book.setId(featuredBook.getBookId());
        book.setTitle(featuredBook.getTitle());
        book.setCoverUrl(featuredBook.getCoverUrl());
        return book;
    }

    private Book findBookForFeatured(FeaturedBook featuredBook) {
        if (featuredBook == null || allBooks.isEmpty()) {
            return null;
        }

        String bookId = featuredBook.getBookId();
        String title = featuredBook.getTitle();

        for (Book book : allBooks) {
            if (bookId != null && !bookId.isEmpty() && bookId.equals(book.getId())) {
                return book;
            }
            if (titlesMatch(title, book.getTitle())) {
                return book;
            }
        }
        return null;
    }

    private boolean titlesMatch(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return left.trim().equalsIgnoreCase(right.trim());
    }
}
