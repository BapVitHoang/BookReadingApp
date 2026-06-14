package com.hcmute.bookreadingapp.repository;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.FeaturedBook;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    private static final String TAG = "BookRepository";
    private final FirebaseFirestore db;

    public BookRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnBooksFetchedListener {
        void onSuccess(List<Book> books);

        void onFailure(Exception e);
    }

    public interface OnBookFetchedListener {
        void onSuccess(Book book);

        void onFailure(Exception e);
    }

    public interface OnFeaturedBooksFetchedListener {
        void onSuccess(List<FeaturedBook> books);

        void onFailure(Exception e);
    }

    public void getAllBooks(OnBooksFetchedListener listener) {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> bookList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
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

    public void getFeaturedBooks(OnFeaturedBooksFetchedListener listener) {
        db.collection("featured_books")
                .orderBy("order")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<FeaturedBook> featuredList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FeaturedBook featuredBook = document.toObject(FeaturedBook.class);
                            featuredBook.setId(document.getId());
                            if (featuredBook.getBookId() == null || featuredBook.getBookId().isEmpty()) {
                                String bookId = document.getString("book_id");
                                if (bookId == null || bookId.isEmpty()) {
                                    bookId = document.getString("bookId");
                                }
                                featuredBook.setBookId(bookId);
                            }
                            featuredList.add(featuredBook);
                        }
                        listener.onSuccess(featuredList);
                    } else {
                        Log.w(TAG, "Error getting featured books.", task.getException());
                        listener.onFailure(task.getException());
                    }
                });
    }

    public void getBookById(String bookId, OnBookFetchedListener listener) {
        db.collection("books")
                .document(bookId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        Book book = task.getResult().toObject(Book.class);
                        if (book != null) {
                            book.setId(task.getResult().getId());
                        }
                        listener.onSuccess(book);
                    } else {
                        Exception error = task.getException() != null
                                ? task.getException()
                                : new Exception("Book not found");
                        listener.onFailure(error);
                    }
                });
    }
}
