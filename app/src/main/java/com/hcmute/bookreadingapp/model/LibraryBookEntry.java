package com.hcmute.bookreadingapp.model;

/**
 * Sách trong thư viện cá nhân kèm tiến độ đọc và link audio (nếu có).
 */
public class LibraryBookEntry {

    private final Book book;
    private final int progress;
    private final String audioUrl;

    public LibraryBookEntry(Book book, int progress, String audioUrl) {
        this.book = book;
        this.progress = progress;
        this.audioUrl = audioUrl;
    }

    public Book getBook() {
        return book;
    }

    public int getProgress() {
        return progress;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public boolean hasAudio() {
        return audioUrl != null && !audioUrl.isEmpty();
    }
}
