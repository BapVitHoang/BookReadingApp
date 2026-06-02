package com.hcmute.bookreadingapp.data.repository;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hcmute.bookreadingapp.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private static final String TAG = "BookRepository";
    private final FirebaseFirestore db;

    public BookRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Interface callback để trả dữ liệu về sau khi fetch xong (vì Firebase chạy bất đồng bộ)
    public interface OnBooksFetchedListener {
        void onSuccess(List<Book> books);
        void onFailure(Exception e);
    }

    public void getAllBooks(OnBooksFetchedListener listener) {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> bookList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Tự động map dữ liệu từ document sang object Book
                            Book book = document.toObject(Book.class);
                            // Gắn id của document vào object
                            book.setId(document.getId());
                            bookList.add(book);
                        }
                        listener.onSuccess(bookList);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onFailure(task.getException());
                    }
                });
    }
}
