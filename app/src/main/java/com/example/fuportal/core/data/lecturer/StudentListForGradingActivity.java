package com.example.fuportal.core.data.lecturer; // (Package của bạn)

import android.content.Intent;
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
import com.example.fuportal.core.data.model.User; // Import User
import com.example.fuportal.core.ui.adapter.StudentListAdapter; // Import Adapter

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentListForGradingActivity extends AppCompatActivity implements StudentListAdapter.OnStudentClickListener {

    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TextView tvClassInfoGrades;
    private RecyclerView rvStudentsForGrading;

    private AppDatabase db;
    private ExecutorService executorService;
    private StudentListAdapter adapter;

    private int classId;
    private String classInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_list_for_grading);

        // Lấy ID Lớp
        if (getIntent().hasExtra("CLASS_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            classInfo = getIntent().getStringExtra("CLASS_INFO");

            tvClassInfoGrades = findViewById(R.id.tvClassInfoGrades);
            tvClassInfoGrades.setText(classInfo);
        } else {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
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
        rvStudentsForGrading = findViewById(R.id.rvStudentsForGrading);

        setupRecyclerView();
        loadStudentList();
    }

    private void setupRecyclerView() {
        adapter = new StudentListAdapter(new ArrayList<>(), this);
        rvStudentsForGrading.setLayoutManager(new LinearLayoutManager(this));
        rvStudentsForGrading.setAdapter(adapter);
    }

    private void loadStudentList() {
        executorService.execute(() -> {
            // Lấy danh sách SV (UC25)
            List<User> students = db.userDao().getStudentsByClassId(classId);

            runOnUiThread(() -> adapter.setStudents(students));
        });
    }

    // --- Xử lý khi nhấn vào 1 Sinh viên ---
    @Override
    public void onStudentClick(User student) {
        // (Đây là Màn hình 5 - Chúng ta sẽ làm ở bước tiếp theo)
        Intent intent = new Intent(this, StudentGradeEntryActivity.class);
        intent.putExtra("CLASS_ID", classId);
        intent.putExtra("STUDENT_ID", student.getUserCode());
        intent.putExtra("STUDENT_NAME", student.getFullName());
        startActivity(intent);

     }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}