package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hcmute.bookreadingapp.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        TextInputEditText edtEmail = findViewById(R.id.edt_email);
        TextInputEditText edtPassword = findViewById(R.id.edt_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        TextView txtSignup = findViewById(R.id.txt_signup);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText() != null
                    ? edtEmail.getText().toString().trim() : "";
            String password = edtPassword.getText() != null
                    ? edtPassword.getText().toString() : "";

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            login(email, password, btnLogin);
        });

        txtSignup.setOnClickListener(v -> {
            // Chuyển sang trang đăng ký
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password, MaterialButton btnLogin) {
        btnLogin.setEnabled(false);

        db.collection("user_login")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);

                    if (!task.isSuccessful() || task.getResult() == null) {
                        Toast.makeText(this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (task.getResult().isEmpty()) {
                        Toast.makeText(this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    String storedPassword = document.getString("password");

                    if (password.equals(storedPassword)) {
                        sessionManager.saveLogin(document.getId(), email);
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
