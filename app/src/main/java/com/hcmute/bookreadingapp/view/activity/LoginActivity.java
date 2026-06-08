package com.hcmute.bookreadingapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.AuthController;

public class LoginActivity extends AppCompatActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authController = new AuthController();

        // Firebase Auth tự duy trì phiên — nếu còn user thì vào thẳng màn hình chính
        if (authController.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        TextInputEditText edtEmail = findViewById(R.id.edt_email);
        TextInputEditText edtPassword = findViewById(R.id.edt_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        TextView txtSignup = findViewById(R.id.txt_signup);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText() != null
                    ? edtEmail.getText().toString().trim() : "";
            String password = edtPassword.getText() != null
                    ? edtPassword.getText().toString() : "";

            btnLogin.setEnabled(false);
            authController.login(email, password, new AuthController.AuthResultCallback() {
                @Override
                public void onSuccess() {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    goToMain();
                }

                @Override
                public void onValidationError(String message) {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
