package com.hcmute.bookreadingapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AudioUrlUtils {

    private static final Pattern DRIVE_FILE_PATTERN =
            Pattern.compile("/file/d/([a-zA-Z0-9_-]+)");
    private static final Pattern DRIVE_ID_PATTERN =
            Pattern.compile("[?&]id=([a-zA-Z0-9_-]+)");

    private AudioUrlUtils() {
    }

    public static String toDirectUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        String trimmed = url.trim();

        Matcher fileMatcher = DRIVE_FILE_PATTERN.matcher(trimmed);
        if (fileMatcher.find()) {
            return "https://drive.google.com/uc?export=download&id=" + fileMatcher.group(1);
        }

        Matcher idMatcher = DRIVE_ID_PATTERN.matcher(trimmed);
        if (trimmed.contains("drive.google.com") && idMatcher.find()) {
            return "https://drive.google.com/uc?export=download&id=" + idMatcher.group(1);
        }

        return trimmed;
    }
}
