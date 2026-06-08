package com.hcmute.bookreadingapp.model;

/**
 * Dữ liệu một track audio cần phát.
 */
public class AudioTrack {

    private final String audioUrl;
    private final String title;
    private final String coverUrl;
    private final String subtitle;

    public AudioTrack(String audioUrl, String title, String coverUrl, String subtitle) {
        this.audioUrl = audioUrl;
        this.title = title;
        this.coverUrl = coverUrl;
        this.subtitle = subtitle;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean hasAudioUrl() {
        return audioUrl != null && !audioUrl.isEmpty();
    }
}
