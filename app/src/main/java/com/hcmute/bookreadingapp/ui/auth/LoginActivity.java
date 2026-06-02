package com.hcmute.bookreadingapp.ui.auth;
import com.hcmute.bookreadingapp.MainActivity;
import com.hcmute.bookreadingapp.ui.auth.RegisterActivity;

import com.hcmute.bookreadingapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialButton btnLogin = findViewById(R.id.btn_login);
        TextView txtSignup = findViewById(R.id.txt_signup);

        btnLogin.setOnClickListener(v -> {
            // Chuyển sang trang chủ sau khi đăng nhập thành công
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        txtSignup.setOnClickListener(v -> {
            // Chuyển sang trang đăng ký
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}