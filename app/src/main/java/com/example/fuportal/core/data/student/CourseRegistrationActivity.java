package com.example.fuportal.core.data.student; // (Đặt trong package của bạn)

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException; // <-- QUAN TRỌNG
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast; // Dùng để thông báo

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.example.fuportal.core.data.model.Enrollment; // Import
import com.example.fuportal.core.ui.adapter.CourseRegistrationAdapter;
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CourseRegistrationActivity extends AppCompatActivity implements CourseRegistrationAdapter.OnRegisterClickListener {

    private RecyclerView rvAvailableClasses;
    private CourseRegistrationAdapter adapter;
    private AppDatabase db;
    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private SessionManager sessionManager; // <-- 2. Khai báo
    private String loggedInStudentId; // <-- 3. Biến để lưu ID

    // --- ID Sinh viên (Tạm thời Hardcode) ---
    // (Sau này bạn sẽ lấy ID này từ SharedPreferences sau khi Login)
     // -----------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_registration);

        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sessionManager = new SessionManager(getApplicationContext());
        loggedInStudentId = sessionManager.getLoggedInUserCode();

        db = AppDatabase.getDatabase(getApplicationContext());
        rvAvailableClasses = findViewById(R.id.rvAvailableClasses);

        setupRecyclerView();
        loadAvailableClasses();
    }

    private void setupRecyclerView() {
        adapter = new CourseRegistrationAdapter(new ArrayList<>(), this);
        rvAvailableClasses.setLayoutManager(new LinearLayoutManager(this));
        rvAvailableClasses.setAdapter(adapter);
    }

    private void loadAvailableClasses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Lấy TẤT CẢ các lớp học (dùng POJO)
            // (Bạn có thể lọc theo Học kỳ (Semester) hiện tại nếu muốn)
            List<ClassDetail> classDetails = db.classDao().getAllClassDetails();
            runOnUiThread(() -> adapter.setClasses(classDetails));
        });
    }

    // --- Xử lý khi nhấn nút "Register" ---
    @Override
    public void onRegisterClick(ClassDetail classDetail) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Registration")
                .setMessage("Are you sure you want to register for: " + classDetail.courseName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    registerForClass(classDetail);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void registerForClass(ClassDetail classDetail) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // 1. Tạo bản ghi Enrollment mới
                Enrollment newEnrollment = new Enrollment(
                        loggedInStudentId,
                        classDetail.classID,
                        System.currentTimeMillis(), // Ngày đăng ký là ngày hiện tại
                        "Enrolled"
                );

                // 2. Cố gắng INSERT vào CSDL
                db.enrollmentDao().insertEnrollment(newEnrollment);

                // 3. Nếu thành công (không ném Exception)
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    // (Bạn có thể vô hiệu hóa nút "Register" cho item này nếu muốn)
                });

            } catch (SQLiteConstraintException e) {
                // 4. LỖI: Bị trùng (do UNIQUE constraint)
                // Có nghĩa là sinh viên này ĐÃ đăng ký lớp này rồi
                runOnUiThread(() -> {
                    Toast.makeText(this, "You are already registered for this class.", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                // 5. Các lỗi khác
                runOnUiThread(() -> {
                    Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    @Override
    public void onItemClick(ClassDetail classDetail) {
        // Mở màn hình CourseDetail
        Intent intent = new Intent(this, CourseDetailActivity.class);
        // Gửi CourseID (đã lấy được từ POJO)
        intent.putExtra("COURSE_ID", classDetail.courseID);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}