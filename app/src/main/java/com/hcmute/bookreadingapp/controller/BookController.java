package com.hcmute.bookreadingapp.controller;

import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.FeaturedBook;
import com.hcmute.bookreadingapp.repository.BookRepository;

import java.util.List;

public class BookController {

    public interface BooksCallback {
        void onSuccess(List<Book> books);

        void onError(String message);
    }

    public interface FeaturedBooksCallback {
        void onSuccess(List<FeaturedBook> books);

        void onError(String message);
    }

    public interface BookDetailCallback {
        void onSuccess(Book book);

        void onError(String message);
    }

    private final BookRepository bookRepository;

    public BookController() {
        bookRepository = new BookRepository();
    }

    public void loadAllBooks(BooksCallback callback) {
        bookRepository.getAllBooks(new BookRepository.OnBooksFetchedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                callback.onSuccess(books);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Lỗi tải sách");
            }
        });
    }

    public void loadFeaturedBooks(FeaturedBooksCallback callback) {
        bookRepository.getFeaturedBooks(new BookRepository.OnFeaturedBooksFetchedListener() {
            @Override
            public void onSuccess(List<FeaturedBook> books) {
                callback.onSuccess(books);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Lỗi tải sách nổi bật");
            }
        });
    }

    public void loadBookById(String bookId, BookDetailCallback callback) {
        if (bookId == null || bookId.isEmpty()) {
            callback.onError("Không tìm thấy sách");
            return;
        }

        bookRepository.getBookById(bookId, new BookRepository.OnBookFetchedListener() {
            @Override
            public void onSuccess(Book book) {
                callback.onSuccess(book);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onError("Không tìm thấy sách");
            }
        });
    }
}
