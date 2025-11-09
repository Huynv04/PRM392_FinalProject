package com.example.fuportal.core.data.student;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Course;
import com.example.fuportal.core.data.model.Major;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private TextView tvCourseCode, tvCourseName, tvCredits, tvMajorName, tvPrerequisite;

    private AppDatabase db;
    private ExecutorService executorService;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_detail);

        // Lấy ID môn học từ Intent
        courseId = getIntent().getIntExtra("COURSE_ID", -1);
        if (courseId == -1) {
            Toast.makeText(this, "Error: Course ID not found.", Toast.LENGTH_SHORT).show();
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

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ View
        tvCourseCode = findViewById(R.id.tvCourseCode);
        tvCourseName = findViewById(R.id.tvCourseName);
        tvCredits = findViewById(R.id.tvCredits);
        tvMajorName = findViewById(R.id.tvMajorName);
        tvPrerequisite = findViewById(R.id.tvPrerequisite);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        loadCourseDetails();
    }

    private void loadCourseDetails() {
        executorService.execute(() -> {
            // 1. Lấy thông tin Môn học (Course)
            Course course = db.courseDao().getCourseById(courseId);

            // 2. Lấy thông tin Chuyên ngành (Major)
            Major major = db.majorDao().getMajorById(course.getMajorID());

            // 3. (Tùy chọn) Lấy thông tin Môn tiên quyết
            String prerequisiteName = "None";
            if (course.getPrerequisiteCourseID() != null) {
                Course prerequisiteCourse = db.courseDao().getCourseById(course.getPrerequisiteCourseID());
                if (prerequisiteCourse != null) {
                    prerequisiteName = prerequisiteCourse.getCourseCode() + " (" + prerequisiteCourse.getCourseName() + ")";
                }
            }
            final String finalPrerequisiteName = prerequisiteName;

            // 4. Cập nhật UI
            runOnUiThread(() -> {
                tvCourseCode.setText(course.getCourseCode());
                tvCourseName.setText(course.getCourseName());
                tvCredits.setText(String.valueOf(course.getCredits()));

                if (major != null) {
                    tvMajorName.setText(major.getMajorName());
                } else {
                    tvMajorName.setText("N/A");
                }

                tvPrerequisite.setText(finalPrerequisiteName);
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}