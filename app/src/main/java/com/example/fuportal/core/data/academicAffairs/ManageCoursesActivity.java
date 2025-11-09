package com.example.fuportal.core.data.academicAffairs; // (Đặt trong package của bạn)


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.example.fuportal.core.data.model.Course;
import com.example.fuportal.core.ui.adapter.CourseAdapter; // Import Adapter mới

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageCoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseActionsListener {

    private RecyclerView rvCourses;
    private CourseAdapter courseAdapter;
    private AppDatabase db;
    private Button btnAddCourse;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_courses);

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
        rvCourses = findViewById(R.id.rvCourses);
        btnAddCourse = findViewById(R.id.btnAddCourse);

        setupRecyclerView();
        loadCourses();

        // Xử lý nút "Add New"
        btnAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCoursesActivity.this, AddEditCourseActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseAdapter(new ArrayList<>(), this);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(courseAdapter);
    }

    private void loadCourses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Course> courses = db.courseDao().getAllCourses();
            runOnUiThread(() -> courseAdapter.setCourses(courses));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    @Override
    public void onEditClick(Course course) {
        Intent intent = new Intent(ManageCoursesActivity.this, AddEditCourseActivity.class);
        intent.putExtra("COURSE_ID", course.getCourseID());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure to delete this course: '" + course.getCourseName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCourseFromDatabase(course))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCourseFromDatabase(Course course) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.courseDao().deleteCourse(course);
            runOnUiThread(this::loadCourses);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}