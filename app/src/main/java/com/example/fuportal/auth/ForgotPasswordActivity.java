package com.example.fuportal.auth; // (Nhớ đổi thành package của bạn)

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Dùng để hiển thị OTP giả lập

import androidx.appcompat.app.AppCompatActivity;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.User;

import java.util.Random;
import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailForgot;
    private Button btnSendOtp;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmailForgot = findViewById(R.id.etEmailForgot);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        db = AppDatabase.getDatabase(getApplicationContext());

        btnSendOtp.setOnClickListener(v -> handleSendOtp());
    }

    private void handleSendOtp() {
        String email = etEmailForgot.getText().toString().trim();

        // Validation email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailForgot.setError("Please enter a valid email address");
            etEmailForgot.requestFocus();
            return;
        }

        // Vô hiệu hóa nút
        btnSendOtp.setEnabled(false);

        // Kiểm tra Email trong Room (trên luồng nền)
        Executors.newSingleThreadExecutor().execute(() -> {
            final User user = db.userDao().findUserByGmail(email); // Cần có hàm này trong UserDao

            runOnUiThread(() -> {
                // Kích hoạt lại nút
                btnSendOtp.setEnabled(true);

                if (user == null) {
                    etEmailForgot.setError("Email not registered");
                    etEmailForgot.requestFocus();
                } else {
                    simulateSendOtpAndNavigate(email);
                }
            });
        });
    }

    private void simulateSendOtpAndNavigate(String email) {
        // Tạo OTP ngẫu nhiên
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));

        // Hiển thị OTP qua Toast (thay vì gửi mail)
        Toast.makeText(this, "Simulated OTP (for testing): " + otp, Toast.LENGTH_LONG).show();

        // Chuyển sang màn hình VerifyOtpActivity
        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOtpActivity.class);
        intent.putExtra("USER_EMAIL", email);
        intent.putExtra("CORRECT_OTP", otp);
        startActivity(intent);
    }
}