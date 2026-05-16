package com.hcmute.bookreadingapp;

public class FeaturedBook {

    private final int coverResId;
    private final int titleResId;
    private final int contentDescriptionResId;
    private final boolean bestseller;

    public FeaturedBook(int coverResId, int titleResId, int contentDescriptionResId, boolean bestseller) {
        this.coverResId = coverResId;
        this.titleResId = titleResId;
        this.contentDescriptionResId = contentDescriptionResId;
        this.bestseller = bestseller;
    }

    public int getCoverResId() {
        return coverResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getContentDescriptionResId() {
        return contentDescriptionResId;
    }

    public boolean isBestseller() {
        return bestseller;
    }
}
