package com.example.fuportal.core.data.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import
import android.widget.Button; // Import
import android.widget.Toast; // Import

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.auth.LoginActivity;
import com.example.fuportal.core.util.SessionManager;

public class StudentMainActivity extends AppCompatActivity {

    // 1. Khai báo các nút
    private Button btnMyTimetable, btnCourseRegistration, btnViewGrades, btnViewAttendance;
    private Button btnViewExamSchedule, btnManageApplications, btnViewAnnouncements;
    private Button btnViewCourseCatalog, btnViewLecturerInfo; // <-- VẪN THIẾU
    private SessionManager sessionManager; // <-- 2. Khai báo
    private String loggedInStudentId; // <-- 3. Biến để lưu ID
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_main);

        // Code xử lý tràn viền (Giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionManager = new SessionManager(getApplicationContext());

        // 2. Ánh xạ các nút
        btnMyTimetable = findViewById(R.id.btnMyTimetable);
        btnViewGrades = findViewById(R.id.btnViewGrades); // Ánh xạ
        btnCourseRegistration = findViewById(R.id.btnCourseRegistration); // <-- THIẾU CÁI NÀY
        btnViewExamSchedule = findViewById(R.id.btnViewExamSchedule);
        btnManageApplications = findViewById(R.id.btnManageApplications);
        btnViewAttendance = findViewById(R.id.btnViewAttendance); // <-- THIẾU CÁI NÀY
        btnViewAnnouncements = findViewById(R.id.btnViewAnnouncements);
        btnViewCourseCatalog = findViewById(R.id.btnViewCourseCatalog); // <-- THIẾU CÁI NÀY
        btnViewLecturerInfo = findViewById(R.id.btnViewLecturerInfo); // <-- THIẾU CÁI NÀY
        btnLogout = findViewById(R.id.btnLogout);
        loggedInStudentId = sessionManager.getLoggedInUserCode();
        // === 2. THÊM TOAST ĐỂ KIỂM TRA ID ===
        Toast.makeText(this, "Logged in User ID: " + loggedInStudentId, Toast.LENGTH_LONG).show();
        if (loggedInStudentId == null || loggedInStudentId.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            // (Bạn có thể chuyển về LoginActivity ở đây)
            finish(); // Đóng màn hình này
            return;
        }
        btnLogout.setOnClickListener(v -> { // <-- Giờ btnLogout đã được ánh xạ
            sessionManager.logoutUser();
            Intent intent = new Intent(StudentMainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 3. Gán sự kiện click

        // (Sự kiện click cho các chức năng chưa làm)
        View.OnClickListener notImplementedListener = v -> {
            Button b = (Button) v;
            Toast.makeText(this, b.getText() + " - Not Implemented Yet", Toast.LENGTH_SHORT).show();
        };

        // Gán listener
        btnCourseRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseRegistrationActivity.class);
            startActivity(intent);
        });
        btnMyTimetable.setOnClickListener(v -> {
            // Intent intent = new Intent(this, TimetableActivity.class); // (Code cũ)

            // === SỬA THÀNH CODE NÀY ===
            Intent intent = new Intent(this, TimetableActivity.class);
            intent.putExtra("USER_ID", loggedInStudentId); // (loggedInStudentId đã có)
            intent.putExtra("USER_ROLE_ID", sessionManager.getLoggedInUserRole()); // <-- SỬA DÒNG NÀY
            startActivity(intent);
            // ========================
        });
        btnViewGrades.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentClassListActivity.class);
            startActivity(intent);
        });
        btnViewAttendance.setOnClickListener(v -> {
            // Mở màn hình chọn lớp
            Intent intent = new Intent(this, StudentClassListActivity.class);
            // Chúng ta sẽ tái sử dụng StudentClassListActivity
            // Nhưng cần báo cho nó biết là phải chuyển sang ViewAttendanceDetailActivity
            intent.putExtra("ACTION", "VIEW_ATTENDANCE");
            startActivity(intent);
        });

        btnViewExamSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewExamScheduleActivity.class);
            startActivity(intent);
        });


        btnManageApplications.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, ApplicationManagementActivity.class);
                    startActivity(intent);
                }); // (UC30)
        btnViewAnnouncements.setOnClickListener(notImplementedListener); // (UC32)

    }
}