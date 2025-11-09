package com.example.fuportal.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

// (Thay R.layout.activity_login bằng tên file XML của bạn)
import com.example.fuportal.R;
import com.example.fuportal.core.data.academicAffairs.AcademicAffairsMainActivity;
import com.example.fuportal.core.data.admin.AdminMainActivity;
import com.example.fuportal.core.data.examinationDept.ExaminationDeptMainActivity;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.data.lecturer.LecturerMainActivity;
import com.example.fuportal.core.data.student.StudentMainActivity;
import com.example.fuportal.core.util.SessionManager;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    // 1. Khai báo các biến giao diện
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private SessionManager sessionManager; // <-- 2. Khai báo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        // 2. Ánh xạ (link) biến Java với ID trong XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        sessionManager = new SessionManager(getApplicationContext());
        // 3. Thiết lập sự kiện khi nhấn nút Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi hàm xử lý đăng nhập
                handleLogin();
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLogin() {
        String loginInput = etUsername.getText().toString().trim();
        String plainTextPassword = etPassword.getText().toString().trim(); // Mật khẩu thô

        if (loginInput.isEmpty()) {
            etUsername.setError("Username or Gmail is required");
            etUsername.requestFocus();
            return;
        }
        // ... (validation cho password)

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        Executors.newSingleThreadExecutor().execute(() -> {

            User user = db.userDao().findUserByLoginInput(loginInput);

            boolean loginSuccess = false;

            if (user != null) {
                // 3. Lấy mật khẩu đã băm (hash) từ CSDL
                String storedHash = user.getHashedPassword();

                // 4. Dùng BCrypt để so sánh mật khẩu thô với hash
                // Đây là phần "xử lý mật khẩu mã hóa"
                try {
                    //giải mã mật khẩu đã mã hóa trong CSDL, rồi so sánh mk đã mã hóa với mật khẩu nhập
                    loginSuccess = BCrypt.checkpw(plainTextPassword, storedHash);
                } catch (Exception e) {
                    // (Lỗi này xảy ra nếu 'storedHash' không phải là hash hợp lệ)
                    loginSuccess = false;
                }

            }
            final boolean isLoginOk = loginSuccess;
            // 3. Xử lý kết quả
            runOnUiThread(() -> {
  //                    if (userRole == 1) {
//                        // 1. Thông báo đăng nhập thành công
//                        Toast.makeText(LoginActivity.this, "Login Successful! Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();
//                        // 2. Tạo Intent để chuyển sang MainActivity
//                        Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
//                        // (Tùy chọn: Gửi tên người dùng sang màn hình chính)
//                        // intent.putExtra("USER_NAME", user.getFullName());
//                        // 3. Bắt đầu Activity mới
//                        startActivity(intent);
//                        // 4. Đóng LoginActivity (để người dùng không quay lại được)
//                        finish();
                        if (isLoginOk) {
                            Toast.makeText(LoginActivity.this, "Login Successful! Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();
                            sessionManager.saveUserSession(user.getUserCode(), user.getFullName(), user.getRoleID());

                            // 1. Quyết định đi đâu
                            Intent intent;
                            switch (user.getRoleID()) {
                                case 1:
                                    intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(LoginActivity.this, LecturerMainActivity.class);
                                    break;
                                case 3:
                                    intent = new Intent(LoginActivity.this, AcademicAffairsMainActivity.class);
                                    break;
                                case 4:
                                    intent = new Intent(LoginActivity.this, ExaminationDeptMainActivity.class);
                                    break;
                                default: // Giả sử là Admin (Role 5)
                                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                    break;
                            }
                            startActivity(intent);
                            finish(); // Đóng LoginActivity

                } else {
                    // Đăng nhập thất bại
                    Toast.makeText(LoginActivity.this, "Invalid: " + loginInput + "/" + plainTextPassword, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}