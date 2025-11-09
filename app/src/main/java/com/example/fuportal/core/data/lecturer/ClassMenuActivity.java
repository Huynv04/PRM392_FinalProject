package com.example.fuportal.core.data.lecturer;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;

public class ClassMenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TextView tvClassInfoMenu;
    private Button btnGoToAttendance, btnGoToGrades;

    private int classId;
    private String classInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class_menu);

        // Lấy ID Lớp và Tên Lớp
        if (getIntent().hasExtra("CLASS_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            classInfo = getIntent().getStringExtra("CLASS_INFO");
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

        // Ánh xạ
        tvClassInfoMenu = findViewById(R.id.tvClassInfoMenu);
        btnGoToAttendance = findViewById(R.id.btnGoToAttendance);
        btnGoToGrades = findViewById(R.id.btnGoToGrades);

        // Hiển thị tên lớp
        tvClassInfoMenu.setText(classInfo);

        // Gán sự kiện

        // 1. Nhấn nút "Manage Attendance"
        btnGoToAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageAttendanceActivity.class);
            intent.putExtra("CLASS_ID", classId);
            intent.putExtra("CLASS_INFO", classInfo);
            startActivity(intent);
        });

        // 2. Nhấn nút "Manage Grades"
        btnGoToGrades.setOnClickListener(v -> {
            // (Chúng ta sẽ tạo màn hình này ở bước tiếp theo)
             Intent intent = new Intent(this, StudentListForGradingActivity.class);
             intent.putExtra("CLASS_ID", classId);
             intent.putExtra("CLASS_INFO", classInfo);
             startActivity(intent);
            Toast.makeText(this, "Chức năng 'Manage Grades' đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}