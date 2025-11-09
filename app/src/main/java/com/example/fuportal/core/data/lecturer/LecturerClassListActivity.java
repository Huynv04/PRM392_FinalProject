package com.example.fuportal.core.data.lecturer;


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
import com.example.fuportal.core.ui.adapter.LecturerClassAdapter; // Import Adapter mới
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LecturerClassListActivity extends AppCompatActivity implements LecturerClassAdapter.OnClassClickListener {

    private RecyclerView rvLecturerClasses;
    private LecturerClassAdapter adapter;
    private AppDatabase db;
    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private SessionManager sessionManager;
    private String loggedInLecturerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecturer_class_list);

        sessionManager = new SessionManager(getApplicationContext());
        loggedInLecturerId = sessionManager.getLoggedInUserCode();
        if (loggedInLecturerId == null || sessionManager.getLoggedInUserRole() != 2) {
            Toast.makeText(this, "Error: Lecturer ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        db = AppDatabase.getDatabase(getApplicationContext());
        rvLecturerClasses = findViewById(R.id.rvLecturerClasses);

        setupRecyclerView();
        loadLecturerClasses();
    }

    private void setupRecyclerView() {
        adapter = new LecturerClassAdapter(new ArrayList<>(), this);
        rvLecturerClasses.setLayoutManager(new LinearLayoutManager(this));
        rvLecturerClasses.setAdapter(adapter);
    }

    private void loadLecturerClasses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ClassDetail> classDetails = db.classDao().getClassesForLecturer(loggedInLecturerId);
            runOnUiThread(() -> adapter.setClasses(classDetails));
        });
    }

    // Xử lý khi nhấn vào 1 lớp
    @Override
    public void onItemClick(ClassDetail classDetail) {
        // Mở Màn hình 2 (ManageAttendanceActivity)
        Intent intent = new Intent(this, ClassMenuActivity.class); // <-- SỬA DÒNG NÀY
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