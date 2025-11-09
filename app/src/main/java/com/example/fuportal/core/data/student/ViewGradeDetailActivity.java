package com.example.fuportal.core.data.student;


import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.example.fuportal.core.data.model.GradeDetail;
import com.example.fuportal.core.ui.adapter.GradeDetailAdapter; // Import Adapter mới
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewGradeDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TextView tvClassInfoGrades;
    private RecyclerView rvGradeDetails;

    private AppDatabase db;
    private ExecutorService executorService;
    private GradeDetailAdapter adapter;
    private SessionManager sessionManager;

    private int classId;
    private String loggedInStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_grade_detail);

        // Lấy ID Lớp
        if (getIntent().hasExtra("CLASS_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            String classInfo = getIntent().getStringExtra("CLASS_INFO");

            tvClassInfoGrades = findViewById(R.id.tvClassInfoGrades);
            tvClassInfoGrades.setText(classInfo);
        } else {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy ID Sinh viên
        sessionManager = new SessionManager(getApplicationContext());
        loggedInStudentId = sessionManager.getLoggedInUserCode();
        if (loggedInStudentId == null) {
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

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        rvGradeDetails = findViewById(R.id.rvGradeDetails);

        setupRecyclerView();
        loadGradeDetails();
    }

    private void setupRecyclerView() {
        adapter = new GradeDetailAdapter(new ArrayList<>());
        rvGradeDetails.setLayoutManager(new LinearLayoutManager(this));
        rvGradeDetails.setAdapter(adapter);
    }

    private void loadGradeDetails() {
        executorService.execute(() -> {
            // Tái sử dụng hàm JOIN của GradeComponentDao (đã tạo cho Lecturer)
            List<GradeDetail> gradeDetails = db.gradeComponentDao().getGradesForStudentInClass(classId, loggedInStudentId);

            runOnUiThread(() -> adapter.setGradeDetails(gradeDetails));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}