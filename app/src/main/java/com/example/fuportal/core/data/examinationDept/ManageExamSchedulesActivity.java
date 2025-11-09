package com.example.fuportal.core.data.examinationDept;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
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
import com.example.fuportal.core.data.model.ExamSchedule;
import com.example.fuportal.core.data.model.ExamScheduleDetail;
import com.example.fuportal.core.ui.adapter.ExamScheduleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageExamSchedulesActivity extends AppCompatActivity
        implements ExamScheduleAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private ExamScheduleAdapter adapter;
    private Button btnAddExam;
    private AppDatabase db;
    private ExecutorService executorService;
    private LinearLayout mainLayout;
    private Toolbar toolbar;

    private static final int REQUEST_CODE_ADD_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_exam_schedules);

        // Khởi tạo Database và Executor
        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Xử lý Tràn viền
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cấu hình Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ View
        recyclerView = findViewById(R.id.rvExamSchedules);
        btnAddExam = findViewById(R.id.btnAddExam); // Đã sửa ID từ FAB sang Button

        // Cấu hình RecyclerView
        adapter = new ExamScheduleAdapter(this, this); // 'this' là listener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Xử lý nút Add New Exam Schedule
        btnAddExam.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditExamScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
        });

        // Tải dữ liệu
        loadExamSchedules();
    }

    private void loadExamSchedules() {
        executorService.execute(() -> {
            List<ExamScheduleDetail> details = db.examScheduleDao().getAllExamScheduleDetails();
            runOnUiThread(() -> {
                adapter.setExamList(details);
                if (details.isEmpty()) {
                    Toast.makeText(this, "No exam schedules found.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Tải lại dữ liệu khi quay lại từ màn hình Add/Edit
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_EDIT && resultCode == RESULT_OK) {
            loadExamSchedules();
        }
    }

    // --- XỬ LÝ SỰ KIỆN TỪ ADAPTER ---

    @Override
    public void onEditClick(ExamScheduleDetail exam) {
        Intent intent = new Intent(this, AddEditExamScheduleActivity.class);
        intent.putExtra("EXAM_ID", exam.getExamID());
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
    }

    @Override
    public void onDeleteClick(ExamScheduleDetail exam) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete the exam for " + exam.getCourseCode() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteExam(exam))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteExam(ExamScheduleDetail detail) {
        executorService.execute(() -> {
            ExamSchedule examToDelete = new ExamSchedule();
            examToDelete.setExamID(detail.getExamID());

            db.examScheduleDao().deleteExamSchedule(examToDelete);

            runOnUiThread(this::loadExamSchedules); // Tải lại danh sách
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}