package com.hcmute.bookreadingapp;

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
import com.hcmute.bookreadingapp.data.repository.BookRepository;
import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.FeaturedBook;
import com.hcmute.bookreadingapp.ui.adapter.BookAdapter;
import com.hcmute.bookreadingapp.ui.adapter.FeaturedBookAdapter;

import java.util.List;

public class BooksFragment extends Fragment {

    private RecyclerView rvBooks;
    private ViewPager2 vpFeatured;
    private BookAdapter bookAdapter;
    private FeaturedBookAdapter featuredBookAdapter;
    private BookRepository bookRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        rvBooks = view.findViewById(R.id.rv_books);
        vpFeatured = view.findViewById(R.id.vp_featured);

        bookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });

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
                if (featuredBook.getBookId() != null && !featuredBook.getBookId().isEmpty()) {
                    bookRepository.getBookById(featuredBook.getBookId(), new BookRepository.OnBookFetchedListener() {
                        @Override
                        public void onSuccess(Book book) {
                            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                            intent.putExtra("book", book);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Không tìm thấy sách", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        rvBooks.setAdapter(bookAdapter);

        setupFeaturedCarousel();

        bookRepository = new BookRepository();
        fetchBooks();
        fetchFeaturedBooks();

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

    private void fetchBooks() {
        bookRepository.getAllBooks(new BookRepository.OnBooksFetchedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                bookAdapter.setBookList(books);
                Log.d("BooksFragment", "Loaded " + books.size() + " books");
            }

            @Override
            public void onFailure(Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFeaturedBooks() {
        bookRepository.getFeaturedBooks(new BookRepository.OnFeaturedBooksFetchedListener() {
            @Override
            public void onSuccess(List<FeaturedBook> books) {
                featuredBookAdapter.setFeaturedBooks(books);
                if (!books.isEmpty()) {
                    vpFeatured.setCurrentItem(0, false);
                }
                Log.d("BooksFragment", "Loaded " + books.size() + " featured books");
            }

            @Override
            public void onFailure(Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải sách nổi bật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
