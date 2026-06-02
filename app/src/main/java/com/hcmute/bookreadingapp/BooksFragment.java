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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.hcmute.bookreadingapp.data.repository.BookRepository;
import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.ui.adapter.BookAdapter;


import java.util.List;

public class BooksFragment extends Fragment {

    private RecyclerView rvBooks;
    private BookAdapter bookAdapter;
    private BookRepository bookRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        rvBooks = view.findViewById(R.id.rv_books);
        MaterialCardView featuredCard = view.findViewById(R.id.card_featured);

        // Khởi tạo Adapter
        bookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });

        // Cấu hình RecyclerView lướt ngang
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvBooks.setAdapter(bookAdapter);

        // Khởi tạo Repository và lấy dữ liệu
        bookRepository = new BookRepository();
        fetchBooks();

        featuredCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchBooks() {
        bookRepository.getAllBooks(new BookRepository.OnBooksFetchedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                // Đưa danh sách sách vào Adapter để hiển thị
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
}