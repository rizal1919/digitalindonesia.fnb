package com.digitalindonesia.fnb; // ganti sesuai package project kamu

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etPrivateKey, etPassword;
    private ImageView ivTogglePassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvForgotPassword, tvRegister;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hubungkan view dari XML
        etPrivateKey = findViewById(R.id.etPrivateKey);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        tvForgotPassword.setOnClickListener(v -> {
            // TODO: arahkan ke halaman lupa password
            Toast.makeText(this, "Fitur lupa password belum diimplementasi", Toast.LENGTH_SHORT).show();
        });

        tvRegister.setOnClickListener(v -> {
            // TODO: ganti RegisterActivity.class sesuai activity daftar kamu
            // startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            Toast.makeText(this, "Arahkan ke halaman daftar", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Buka/tutup teks password dan ganti ikon mata,
     * sambil menjaga posisi kursor tetap di akhir teks.
     */
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_visible);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_hidden);
        }

        // Pertahankan posisi kursor di akhir teks setelah tipe input berubah
        Editable text = etPassword.getText();
        if (text != null) {
            etPassword.setSelection(text.length());
        }
    }

    private void attemptLogin() {
        String privateKey = etPrivateKey.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
//        if (TextUtils.isEmpty(privateKey)) {
//            etPrivateKey.setError("Private key tidak boleh kosong");
//            etPrivateKey.requestFocus();
//            return;
//        }

//        if (privateKey.length() < 8) {
//            etPrivateKey.setError("Private key tidak valid");
//            etPrivateKey.requestFocus();
//            return;
//        }

//        if (TextUtils.isEmpty(password)) {
//            etPassword.setError("Password tidak boleh kosong");
//            etPassword.requestFocus();
//            return;
//        }
//
//        if (password.length() < 6) {
//            etPassword.setError("Password minimal 6 karakter");
//            etPassword.requestFocus();
//            return;
//        }

        // Simulasi proses login (ganti dengan pemanggilan API / autentikasi asli)
        showLoading(true);

        btnLogin.postDelayed(() -> {
            showLoading(false);

            // TODO: ganti logika ini dengan hasil autentikasi asli
            boolean loginSuccess = true;

            if (loginSuccess) {
                Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                // TODO: arahkan ke halaman utama, contoh:
                // Arahkan ke MainActivity (yang menampung DashboardFragment)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Tutup LoginActivity agar tidak bisa di-back
            } else {
                Toast.makeText(LoginActivity.this, "Private key atau password salah", Toast.LENGTH_SHORT).show();
            }
        }, 1500); // delay 1.5 detik sebagai simulasi loading
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("Masuk");
        }
    }
}