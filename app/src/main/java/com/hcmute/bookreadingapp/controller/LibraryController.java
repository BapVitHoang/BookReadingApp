package com.hcmute.bookreadingapp.controller;

import android.content.Context;

import com.hcmute.bookreadingapp.repository.LibraryRepository;

public class LibraryController {

    private final LibraryRepository libraryRepository;

    public LibraryController(Context context) {
        libraryRepository = new LibraryRepository(context);
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
}
