package com.example.fuportal.core.data.lecturer;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.auth.LoginActivity; // Import màn hình Login
import com.example.fuportal.core.data.student.TimetableActivity;
import com.example.fuportal.core.util.SessionManager; // Import SessionManager


public class LecturerMainActivity extends AppCompatActivity {

    // 1. Khai báo
    private Button btnViewTeachingSchedule, btnManageMyClasses, btnLogout;
    private SessionManager sessionManager;
    private String loggedInLecturerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecturer_main);

        // 2. Khởi tạo Session
        sessionManager = new SessionManager(getApplicationContext());
        loggedInLecturerId = sessionManager.getLoggedInUserCode();

        // 3. Xử lý Tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 4. Kiểm tra an toàn (nếu chưa login)
        if (loggedInLecturerId == null || loggedInLecturerId.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 5. Ánh xạ các nút
        btnViewTeachingSchedule = findViewById(R.id.btnViewTeachingSchedule);
         btnLogout = findViewById(R.id.btnLogout);
        btnManageMyClasses = findViewById(R.id.btnManageMyClasses); // Ánh xạ


        // 6. Gán sự kiện click


        // Gán listener
        btnViewTeachingSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimetableActivity.class); // <-- TÁI SỬ DỤNG
            intent.putExtra("USER_ID", loggedInLecturerId); // (loggedInLecturerId đã có)
            intent.putExtra("USER_ROLE_ID", sessionManager.getLoggedInUserRole());
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(LecturerMainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


         btnManageMyClasses.setOnClickListener(v -> {
            Intent intent = new Intent(this, LecturerClassListActivity.class);
            startActivity(intent);
        });
    }
}