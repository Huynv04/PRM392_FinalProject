package com.example.fuportal.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Import TextView

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;

import org.mindrot.jbcrypt.BCrypt; // Import thư viện hash
import java.util.concurrent.Executors;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private TextView tvPasswordError; // TextView để hiển thị lỗi
    private AppDatabase db;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvPasswordError = findViewById(R.id.tvPasswordError); // Ánh xạ TextView lỗi
        db = AppDatabase.getDatabase(getApplicationContext());

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Ẩn lỗi cũ
        tvPasswordError.setVisibility(View.GONE);

        // Validation
        if (newPassword.isEmpty() || newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            // HIỂN THỊ LỖI BẰNG TEXTVIEW (Không dùng setError)
            tvPasswordError.setText("Passwords do not match");
            tvPasswordError.setVisibility(View.VISIBLE);
            etConfirmPassword.requestFocus(); // Focus vào ô confirm
            return;
        }

        // Mật khẩu khớp -> Băm và Cập nhật CSDL
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Vô hiệu hóa nút
        btnResetPassword.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.userDao().updatePasswordByGmail(userEmail, hashedPassword); // Cần có hàm này trong UserDao

            runOnUiThread(() -> {
                // Kích hoạt lại nút (dù không cần thiết vì sẽ đóng màn hình)
                // btnResetPassword.setEnabled(true);
                showSuccessDialog();
            });
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Password reset successfully.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Chuyển về màn hình Login
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}