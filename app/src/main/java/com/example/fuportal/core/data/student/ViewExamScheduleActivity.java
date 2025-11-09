package com.example.fuportal.core.data.student;


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
import com.example.fuportal.core.data.model.ExamScheduleDetail;
import com.example.fuportal.core.ui.adapter.ExamScheduleAdapter; // Tái sử dụng Adapter của Khảo thí
import com.example.fuportal.core.ui.adapter.StudentExamScheduleAdapter;
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewExamScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentExamScheduleAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private String loggedInStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_exam_schedule);
        // Ánh xạ Toolbar và RecyclerView ngay lập tức sau setContentView
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rvExamSchedules); // <-- ÁNH XẠ REC. VIEW
         // Khởi tạo Session và lấy ID
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        loggedInStudentId = sessionManager.getLoggedInUserCode();

        if (loggedInStudentId == null || sessionManager.getLoggedInUserRole() != 1) { // 1 = Student
            Toast.makeText(this, "Error: Student ID not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

          setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ View

        // Cấu hình RecyclerView - SỬA ĐỂ DÙNG ADAPTER MỚI
        adapter = new StudentExamScheduleAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        // Khởi tạo Database và Executor
        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        loadStudentExamSchedules();

        // Xử lý Tràn viền (giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadStudentExamSchedules() {
        executorService.execute(() -> {
            // DÙNG HÀM DAO MỚI
            List<ExamScheduleDetail> examDetails = db.examScheduleDao().getStudentExamSchedules(loggedInStudentId);
            runOnUiThread(() -> {
                adapter.setExamList(examDetails);
                if (examDetails.isEmpty()) {
                    Toast.makeText(this, "No exams currently scheduled.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}