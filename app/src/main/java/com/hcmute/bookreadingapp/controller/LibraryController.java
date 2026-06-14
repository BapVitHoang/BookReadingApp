package com.hcmute.bookreadingapp.controller;

import android.content.Context;
import android.content.Intent;

import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.FeaturedBook;
import com.hcmute.bookreadingapp.model.LibraryBookEntry;
import com.hcmute.bookreadingapp.repository.BookRepository;
import com.hcmute.bookreadingapp.repository.LibraryRepository;
import com.hcmute.bookreadingapp.service.ReadingSyncService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryController {

    public static class LibraryData {
        private final LibraryBookEntry recentBook;
        private final List<LibraryBookEntry> inProgressBooks;
        private final List<LibraryBookEntry> favoriteBooks;

        public LibraryData(LibraryBookEntry recentBook,
                           List<LibraryBookEntry> inProgressBooks,
                           List<LibraryBookEntry> favoriteBooks) {
            this.recentBook = recentBook;
            this.inProgressBooks = inProgressBooks;
            this.favoriteBooks = favoriteBooks;
        }

        public LibraryBookEntry getRecentBook() {
            return recentBook;
        }

        public List<LibraryBookEntry> getInProgressBooks() {
            return inProgressBooks;
        }

        public List<LibraryBookEntry> getFavoriteBooks() {
            return favoriteBooks;
        }
    }

    public interface LibraryDataCallback {
        void onSuccess(LibraryData data);

        void onError(String message);
    }

    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;

    public LibraryController(Context context) {
        libraryRepository = new LibraryRepository(context);
        bookRepository = new BookRepository();
    }

    public String getRecentlyOpenedLabel() {
        String lastBook = libraryRepository.getLastBook();
        if (libraryRepository.hasRecentBook()) {
            return "Mở gần đây: " + lastBook;
        }
        return "Mở gần đây: Chưa có sách nào";
    }

    public void saveReadingProgress(String bookTitle, int progress) {
        libraryRepository.saveReadingProgress(bookTitle, progress);
    }

    public void saveLastBook(String bookTitle) {
        libraryRepository.saveLastBook(bookTitle);
    }

    public int getReadingProgress(String bookTitle) {
        return libraryRepository.getReadingProgress(bookTitle);
    }

    public boolean isFavorite(String bookTitle) {
        return libraryRepository.isFavorite(bookTitle);
    }

    public void setFavorite(String bookTitle, boolean isFavorite) {
        libraryRepository.saveFavorite(bookTitle, isFavorite);
    }

    public void syncReadingProgress(String bookTitle, int progress, boolean saveLastBook) {
        libraryRepository.saveReadingProgress(bookTitle, progress);
        if (saveLastBook) {
            libraryRepository.saveLastBook(bookTitle);
        }
    }

    public void enqueueReadingSync(Context context, String bookTitle, int progress, boolean saveLastBook) {
        Intent intent = new Intent(context, ReadingSyncService.class);
        intent.putExtra(ReadingSyncService.EXTRA_BOOK_TITLE, bookTitle);
        intent.putExtra(ReadingSyncService.EXTRA_PROGRESS, progress);
        intent.putExtra(ReadingSyncService.EXTRA_SAVE_LAST_BOOK, saveLastBook);
        context.startService(intent);
    }

    public void loadLibraryData(LibraryDataCallback callback) {
        bookRepository.getAllBooks(new BookRepository.OnBooksFetchedListener() {
            @Override
            public void onSuccess(List<Book> books) {
                bookRepository.getFeaturedBooks(new BookRepository.OnFeaturedBooksFetchedListener() {
                    @Override
                    public void onSuccess(List<FeaturedBook> featuredBooks) {
                        Map<String, String> audioByTitle = buildAudioUrlMap(featuredBooks);
                        callback.onSuccess(buildLibraryData(books, audioByTitle));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onSuccess(buildLibraryData(books, new HashMap<>()));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Không thể tải thư viện");
            }
        });
    }

    private LibraryData buildLibraryData(List<Book> books, Map<String, String> audioByTitle) {
        LibraryBookEntry recentBook = null;
        if (libraryRepository.hasRecentBook()) {
            String recentTitle = libraryRepository.getLastBook();
            int progress = libraryRepository.getReadingProgress(recentTitle);
            recentBook = createEntry(recentTitle, progress, books, audioByTitle);
        }

        List<LibraryBookEntry> inProgressBooks = new ArrayList<>();
        String recentTitle = libraryRepository.hasRecentBook()
                ? libraryRepository.getLastBook()
                : null;
        for (Map.Entry<String, Integer> entry : libraryRepository.getReadingProgressMap().entrySet()) {
            if (recentTitle != null && recentTitle.equals(entry.getKey())) {
                continue;
            }
            LibraryBookEntry libraryEntry = createEntry(
                    entry.getKey(),
                    entry.getValue(),
                    books,
                    audioByTitle
            );
            if (libraryEntry != null) {
                inProgressBooks.add(libraryEntry);
            }
        }

        List<LibraryBookEntry> favoriteBooks = new ArrayList<>();
        for (String title : libraryRepository.getFavoriteBookTitles()) {
            int progress = libraryRepository.getReadingProgress(title);
            LibraryBookEntry libraryEntry = createEntry(title, progress, books, audioByTitle);
            if (libraryEntry != null) {
                favoriteBooks.add(libraryEntry);
            }
        }

        return new LibraryData(recentBook, inProgressBooks, favoriteBooks);
    }

    private LibraryBookEntry createEntry(String title, int progress,
                                         List<Book> books, Map<String, String> audioByTitle) {
        Book book = findBookByTitle(books, title);
        if (book == null) {
            book = new Book();
            book.setTitle(title);
            book.setAuthor("Đang cập nhật");
        }
        String audioUrl = audioByTitle.get(title);
        return new LibraryBookEntry(book, progress, audioUrl);
    }

    private Book findBookByTitle(List<Book> books, String title) {
        if (title == null) {
            return null;
        }
        for (Book book : books) {
            if (book.getTitle() != null && book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    private Map<String, String> buildAudioUrlMap(List<FeaturedBook> featuredBooks) {
        Map<String, String> audioByTitle = new HashMap<>();
        for (FeaturedBook featuredBook : featuredBooks) {
            if (featuredBook.getTitle() != null
                    && featuredBook.getAudioUrl() != null
                    && !featuredBook.getAudioUrl().isEmpty()) {
                audioByTitle.put(featuredBook.getTitle(), featuredBook.getAudioUrl());
            }
        }
        return audioByTitle;
    }
}
