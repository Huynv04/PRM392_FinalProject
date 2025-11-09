package com.example.fuportal.core.data.academicAffairs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast; // (Dùng để test các nút chưa làm)

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
// Import màn hình ManageCoursesActivity
import com.example.fuportal.auth.LoginActivity;
import com.example.fuportal.core.data.academicAffairs.ManageCoursesActivity;
import com.example.fuportal.core.util.SessionManager;

public class AcademicAffairsMainActivity extends AppCompatActivity {

    // Khai báo tất cả các nút
    private Button btnManageCourses, btnManageMajors, btnManageClasses, btnManageCalendar, btnManageFaculties;
    private Button btnManageLecturers, btnAssignLecturers, btnManageCampus;
    private Button btnManageRequests, btnPostAnnouncement, btnGenerateReports;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_academic_affairs_main);

        // Code xử lý viền màn hình (giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ tất cả các nút
        btnManageCourses = findViewById(R.id.btnManageCourses);
        btnManageMajors = findViewById(R.id.btnManageMajors);
        btnManageClasses = findViewById(R.id.btnManageClasses);
        btnManageCalendar = findViewById(R.id.btnManageCalendar);
        btnManageLecturers = findViewById(R.id.btnManageLecturers);
        btnAssignLecturers = findViewById(R.id.btnAssignLecturers);
        btnManageCampus = findViewById(R.id.btnManageCampus);
        btnManageRequests = findViewById(R.id.btnManageRequests);
        btnPostAnnouncement = findViewById(R.id.btnPostAnnouncement);
        btnGenerateReports = findViewById(R.id.btnGenerateReports);
        btnManageFaculties = findViewById(R.id.btnManageFaculties); // <-- 2. ÁNH XẠ NÚT MỚI

        sessionManager = new SessionManager(getApplicationContext());
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Xóa session đã lưu
            sessionManager.logoutUser();

            // Quay về màn hình Login
            Intent intent = new Intent(AcademicAffairsMainActivity.this, LoginActivity.class);
            // Xóa hết các Activity cũ khỏi bộ nhớ (để không back lại được)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 1. Manage Courses (UC08) - (Đã làm ở bước trước)
        btnManageCourses.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCoursesActivity.class);
            startActivity(intent);
        });

        // 2. Các chức năng khác (chưa làm)
        // Chúng ta sẽ hiển thị Toast tạm thời
        View.OnClickListener notImplementedListener = v -> {
            Button b = (Button) v;
            Toast.makeText(this, b.getText() + " - Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        };
        btnManageFaculties.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageFacultiesActivity.class);
            startActivity(intent);
        });

        btnManageMajors.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageMajorsActivity.class);
            startActivity(intent);
        });
        btnManageCampus.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCampusActivity.class);
            startActivity(intent);
        });
        btnManageCalendar = findViewById(R.id.btnManageCalendar); // Ánh xạ

        // Gán listener
        btnManageCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageSemestersActivity.class);
            startActivity(intent);
        });

        btnManageClasses = findViewById(R.id.btnManageClasses); // Ánh xạ
        // Gán listener
        btnManageClasses.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageClassesActivity.class);
            startActivity(intent);
        });
        btnManageLecturers.setOnClickListener(notImplementedListener);
        btnAssignLecturers.setOnClickListener(notImplementedListener);
        btnManageRequests.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageApplicationsActivity.class);
            startActivity(intent);
        });


        btnPostAnnouncement.setOnClickListener(notImplementedListener);
        btnGenerateReports.setOnClickListener(notImplementedListener);


    }
}