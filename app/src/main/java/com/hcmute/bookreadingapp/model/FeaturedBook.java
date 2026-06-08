package com.hcmute.bookreadingapp.model;

import java.io.Serializable;

public class FeaturedBook implements Serializable {
    private String id;
    private String title;
    private String coverUrl;
    private boolean bestSeller;
    private String audioUrl;
    private String bookId;
    private int order;

    public FeaturedBook() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public boolean isBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(boolean bestSeller) {
        this.bestSeller = bestSeller;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
