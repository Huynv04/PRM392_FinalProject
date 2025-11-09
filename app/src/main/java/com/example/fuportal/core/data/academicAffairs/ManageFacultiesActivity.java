package com.example.fuportal.core.data.academicAffairs; // (Đặt trong package của bạn)

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Faculty;
import com.example.fuportal.core.ui.adapter.FacultyAdapter;
import androidx.appcompat.widget.Toolbar; // <-- 1. IMPORT Toolbar
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Implement interface của Adapter
public class ManageFacultiesActivity extends AppCompatActivity implements FacultyAdapter.OnFacultyActionsListener {

    private RecyclerView rvFaculties;
    private FacultyAdapter facultyAdapter;
    private AppDatabase db;
    private Button btnAddFaculty;
    private Toolbar toolbar; // <-- 2. Khai báo Toolbar
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_faculties);
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = AppDatabase.getDatabase(getApplicationContext());
        rvFaculties = findViewById(R.id.rvFaculties);
        btnAddFaculty = findViewById(R.id.btnAddFaculty);

        setupRecyclerView();
        loadFaculties();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Đặt toolbar này làm ActionBar chính

        // Bật nút "Up" (nút back)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // 1. Xử lý nút "Add New"
        btnAddFaculty.setOnClickListener(v -> {
            // Mở màn hình Add/Edit ở chế độ "Add" (không gửi ID)
            Intent intent = new Intent(ManageFacultiesActivity.this, AddEditFacultyActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter và truyền "this" (Activity) vào làm listener
        facultyAdapter = new FacultyAdapter(new ArrayList<>(), this);
        rvFaculties.setLayoutManager(new LinearLayoutManager(this));
        rvFaculties.setAdapter(facultyAdapter);
    }

    private void loadFaculties() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Faculty> faculties = db.facultyDao().getAllFaculties();
            runOnUiThread(() -> facultyAdapter.setFaculties(faculties));
        });
    }

    // Tải lại danh sách mỗi khi quay lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        loadFaculties();
    }

    // --- 2. Xử lý nút "Edit" (Từ Adapter) ---
    @Override
    public void onEditClick(Faculty faculty) {
        // Mở màn hình Add/Edit ở chế độ "Edit" (gửi kèm ID)
        Intent intent = new Intent(ManageFacultiesActivity.this, AddEditFacultyActivity.class);
        intent.putExtra("FACULTY_ID", faculty.getFacultyID());
        startActivity(intent);
    }

    // --- 3. Xử lý nút "Delete" (Từ Adapter) ---
    @Override
    public void onDeleteClick(Faculty faculty) {
        // Hiển thị Dialog xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Delete Faculty")
                .setMessage("Are you sure to delete this faculty: '" + faculty.getFacultyName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Nếu nhấn "Yes", thực hiện xóa
                    deleteFacultyFromDatabase(faculty);
                })
                .setNegativeButton("Cancel", null) // Nhấn "Cancel" không làm gì cả
                .show();
    }

    private void deleteFacultyFromDatabase(Faculty faculty) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.facultyDao().deleteFaculty(faculty);
            // Tải lại danh sách sau khi xóa
            runOnUiThread(this::loadFaculties);
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Gọi hàm back
        return true;
    }
}