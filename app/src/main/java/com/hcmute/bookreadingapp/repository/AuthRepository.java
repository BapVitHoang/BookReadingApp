package com.hcmute.bookreadingapp.repository;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Bao bọc Firebase Authentication. Firebase tự duy trì phiên đăng nhập
 * qua các lần mở app nên không cần lưu session thủ công.
 */
public class AuthRepository {

    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public interface AuthCallback {
        void onSuccess();

        void onError(Exception e);
    }

    /** Còn user đang đăng nhập hay không (phiên do Firebase lưu trên thiết bị). */
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void register(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void logout() {
        auth.signOut();
    }
}
