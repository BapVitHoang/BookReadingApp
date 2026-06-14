package com.hcmute.bookreadingapp.controller;

import com.hcmute.bookreadingapp.repository.AuthRepository;

public class AuthController {

    public interface AuthResultCallback {
        void onSuccess();

        void onValidationError(String message);

        void onError(String message);
    }

    private final AuthRepository authRepository;

    public AuthController() {
        authRepository = new AuthRepository();
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    public void login(String email, String password, AuthResultCallback callback) {
        String trimmedEmail = email == null ? "" : email.trim();
        if (trimmedEmail.isEmpty() || password == null || password.isEmpty()) {
            callback.onValidationError("Vui lòng nhập email và mật khẩu");
            return;
        }

        authRepository.login(trimmedEmail, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                callback.onError("Email hoặc mật khẩu không đúng");
            }
        });
    }

    public void register(String email, String password, AuthResultCallback callback) {
        String trimmedEmail = email == null ? "" : email.trim();
        if (trimmedEmail.isEmpty() || password == null || password.isEmpty()) {
            callback.onValidationError("Vui lòng nhập email và mật khẩu");
            return;
        }
        if (password.length() < 6) {
            callback.onValidationError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        authRepository.register(trimmedEmail, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Đăng ký thất bại");
            }
        });
    }

    public void logout() {
        authRepository.logout();
    }
}
