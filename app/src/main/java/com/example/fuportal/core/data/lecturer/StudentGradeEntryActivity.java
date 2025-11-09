package com.example.fuportal.core.data.lecturer; // (Package của bạn)

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout; // Import
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Grade; // Import
import com.example.fuportal.core.data.model.GradeDetail; // Import
import com.example.fuportal.core.ui.adapter.GradeEntryAdapter; // Import

import java.util.List;
import java.util.Map; // Import
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentGradeEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout mainLayout; // Layout gốc là CoordinatorLayout
    private TextView tvStudentNameGrades;
    private RecyclerView rvGradeComponents;
    private Button btnSaveGrades;

    private AppDatabase db;
    private ExecutorService executorService;
    private GradeEntryAdapter adapter;

    private int classId;
    private String studentId;
    private String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_grade_entry); // Đổi layout

        // Lấy ID Lớp và Sinh viên
        if (getIntent().hasExtra("CLASS_ID") && getIntent().hasExtra("STUDENT_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            studentId = getIntent().getStringExtra("STUDENT_ID");
            studentName = getIntent().getStringExtra("STUDENT_NAME");

            tvStudentNameGrades = findViewById(R.id.tvStudentNameGrades);
            tvStudentNameGrades.setText("Grading for: " + studentName + " (" + studentId + ")");
        } else {
            Toast.makeText(this, "Error: Class or Student ID not found.", Toast.LENGTH_SHORT).show();
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
        rvGradeComponents = findViewById(R.id.rvGradeComponents);
        btnSaveGrades = findViewById(R.id.btnSaveGrades);

        // Tải các đầu điểm và điểm đã có
        loadGradeComponents();

        // Xử lý nút Save
        btnSaveGrades.setOnClickListener(v -> saveGrades());
    }

    private void loadGradeComponents() {
        executorService.execute(() -> {
            // Dùng hàm JOIN mới
            List<GradeDetail> gradeDetails = db.gradeComponentDao().getGradesForStudentInClass(classId, studentId);

            runOnUiThread(() -> {
                adapter = new GradeEntryAdapter(gradeDetails);
                rvGradeComponents.setLayoutManager(new LinearLayoutManager(this));
                rvGradeComponents.setAdapter(adapter);
            });
        });
    }

    private void saveGrades() {
        // Lấy Map điểm số từ Adapter
        Map<Integer, Float> scoresToSave = adapter.getScores();
        btnSaveGrades.setEnabled(false);

        executorService.execute(() -> {
            try {
                // Lặp qua Map và lưu từng điểm
                for (Map.Entry<Integer, Float> entry : scoresToSave.entrySet()) {
                    int componentId = entry.getKey();
                    float score = entry.getValue();

                    Grade grade = new Grade(studentId, componentId, score);
                    db.gradeDao().upsertGrade(grade); // Dùng Insert hoặc Replace
                }

                runOnUiThread(() -> showSuccessDialogAndFinish("Grades saved successfully."));
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error saving grades.", Toast.LENGTH_SHORT).show();
                    btnSaveGrades.setEnabled(true);
                });
            }
        });
    }

    private void showSuccessDialogAndFinish(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish()) // Đóng màn hình này
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}