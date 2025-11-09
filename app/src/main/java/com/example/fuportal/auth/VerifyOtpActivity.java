package com.example.fuportal.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fuportal.R;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp;
    private TextView tvOtpMessage;

    private String correctOtp;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvOtpMessage = findViewById(R.id.tvOtpMessage);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL");
        correctOtp = intent.getStringExtra("CORRECT_OTP");

        // --- ĐÂY LÀ PHẦN THAY ĐỔI ---
        // 2. Hiển thị thông báo KÈM THEO OTP (để test)
        if (userEmail != null && correctOtp != null) {
            tvOtpMessage.setText("Enter the 6-digit OTP sent to \n" + userEmail + "\n(OTP: " + correctOtp + ")");
        } else {
            tvOtpMessage.setText("Enter the 6-digit OTP."); // Fallback
        }

        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp.getText().toString().trim();

            if (enteredOtp.length() != 6) {
                etOtp.setError("OTP must be 6 digits");
                return;
            }

            // So sánh OTP
            if (enteredOtp.equals(correctOtp)) {
                // ĐÚNG: Chuyển sang màn hình ResetPassword
                Intent resetIntent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                resetIntent.putExtra("USER_EMAIL", userEmail); // Gửi email sang
                startActivity(resetIntent);
                finish(); // Đóng màn hình này
            } else {
                // SAI: Báo lỗi
                etOtp.setError("Invalid OTP. Please try again.");
            }
        });
    }
}