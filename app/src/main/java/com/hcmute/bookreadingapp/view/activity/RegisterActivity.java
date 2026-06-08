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

public class RegisterActivity extends AppCompatActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authController = new AuthController();

        TextInputEditText edtEmail = findViewById(R.id.edt_reg_email);
        TextInputEditText edtPassword = findViewById(R.id.edt_reg_password);
        MaterialButton btnRegister = findViewById(R.id.btn_register);
        TextView txtLogin = findViewById(R.id.txt_login);

        btnRegister.setOnClickListener(v -> {
            String email = edtEmail.getText() != null
                    ? edtEmail.getText().toString().trim() : "";
            String password = edtPassword.getText() != null
                    ? edtPassword.getText().toString() : "";

            btnRegister.setEnabled(false);
            authController.register(email, password, new AuthController.AuthResultCallback() {
                @Override
                public void onSuccess() {
                    btnRegister.setEnabled(true);
                    // createUser tự đăng nhập user luôn -> vào thẳng màn hình chính
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onValidationError(String message) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        txtLogin.setOnClickListener(v -> finish());
    }
}
