package com.example.fuportal.core.data.student;


import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.ClassDetail;
import com.example.fuportal.core.ui.adapter.LecturerClassAdapter; // Tái sử dụng Adapter
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Implement interface của Adapter
public class StudentClassListActivity extends AppCompatActivity implements LecturerClassAdapter.OnClassClickListener {

    private RecyclerView rvStudentClasses;
    private LecturerClassAdapter adapter; // Tái sử dụng Adapter
    private AppDatabase db;
    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private SessionManager sessionManager;
    private String loggedInStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_class_list);

        sessionManager = new SessionManager(getApplicationContext());
        loggedInStudentId = sessionManager.getLoggedInUserCode();
        if (loggedInStudentId == null || sessionManager.getLoggedInUserRole() != 1) { // 1 = Student
            Toast.makeText(this, "Error: Student ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý Tràn viền
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cài đặt Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = AppDatabase.getDatabase(getApplicationContext());
        rvStudentClasses = findViewById(R.id.rvStudentClasses);

        setupRecyclerView();
        loadEnrolledClasses();
    }

    private void setupRecyclerView() {
        adapter = new LecturerClassAdapter(new ArrayList<>(), this); // Tái sử dụng
        rvStudentClasses.setLayoutManager(new LinearLayoutManager(this));
        rvStudentClasses.setAdapter(adapter);
    }

    private void loadEnrolledClasses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // DÙNG HÀM MỚI (Lọc theo SV)
            List<ClassDetail> classDetails = db.classDao().getEnrolledClassesForStudent(loggedInStudentId);
            runOnUiThread(() -> adapter.setClasses(classDetails));
        });
    }

    // Xử lý khi nhấn vào 1 lớp
    @Override
    public void onItemClick(ClassDetail classDetail) {
        String action = getIntent().getStringExtra("ACTION");

        Intent intent;
        if ("VIEW_ATTENDANCE".equals(action)) {
            // Nếu đến từ nút View Attendance
            intent = new Intent(this, ViewAttendanceDetailActivity.class); // Màn hình mới
        } else {
            // Mặc định (đến từ nút View Grades)
            intent = new Intent(this, ViewGradeDetailActivity.class);
        }

        intent.putExtra("CLASS_ID", classDetail.classID);
        intent.putExtra("CLASS_INFO", classDetail.courseName + " (" + classDetail.semesterName + ")");
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}