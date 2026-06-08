package com.hcmute.bookreadingapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quản lý phiên đăng nhập của người dùng bằng SharedPreferences.
 * Dùng cho luồng login thủ công qua Firestore (không phải Firebase Auth).
 */
public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        // applicationContext để tránh giữ tham chiếu tới Activity (rò rỉ bộ nhớ)
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Lưu phiên sau khi đăng nhập thành công. */
    public void saveLogin(String userId, String email) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    /** Xóa phiên khi đăng xuất. */
    public void logout() {
        prefs.edit().clear().apply();
    }
}
