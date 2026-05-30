package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MaterialButton btnRegister = findViewById(R.id.btn_register);
        TextView txtLogin = findViewById(R.id.txt_login);

        btnRegister.setOnClickListener(v -> {
            // Sau khi đăng ký, quay lại đăng nhập hoặc vào thẳng app
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        txtLogin.setOnClickListener(v -> {
            finish(); // Quay lại màn hình đăng nhập
        });
    }
}