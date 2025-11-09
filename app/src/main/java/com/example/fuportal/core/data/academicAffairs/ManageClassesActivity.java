package com.example.fuportal.core.data.academicAffairs;


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
import com.example.fuportal.core.data.model.ClassDetail; // Import POJO
import com.example.fuportal.core.data.model.AcademicClass; // Import Entity
import com.example.fuportal.core.ui.adapter.ClassAdapter; // Import Adapter mới

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageClassesActivity extends AppCompatActivity implements ClassAdapter.OnClassActionsListener {

    private RecyclerView rvClasses;
    private ClassAdapter classAdapter;
    private AppDatabase db;
    private Button btnAddClass;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_classes);

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
        rvClasses = findViewById(R.id.rvClasses);
        btnAddClass = findViewById(R.id.btnAddClass);

        setupRecyclerView();
        loadClasses();

        btnAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(ManageClassesActivity.this, AddEditClassActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        classAdapter = new ClassAdapter(new ArrayList<>(), this);
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        rvClasses.setAdapter(classAdapter);
    }

    private void loadClasses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // DÙNG HÀM MỚI (JOIN)
            List<ClassDetail> classDetails = db.classDao().getAllClassDetails();
            runOnUiThread(() -> classAdapter.setClasses(classDetails));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }

    @Override
    public void onEditClick(ClassDetail classDetail) {
        Intent intent = new Intent(ManageClassesActivity.this, AddEditClassActivity.class);
        // Gửi ClassID để màn hình Edit biết tải dữ liệu
        intent.putExtra("CLASS_ID", classDetail.classID);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ClassDetail classDetail) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure to delete class: " + classDetail.courseName + " (" + classDetail.semesterName + ")?")
                .setPositiveButton("Yes", (dialog, which) -> deleteClassFromDatabase(classDetail))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClassFromDatabase(ClassDetail classDetail) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Để xóa, chúng ta cần tạo một đối tượng Class (Entity) giả
            // chỉ cần chứa Khóa chính (ClassID)
            AcademicClass classToDelete = new AcademicClass(0, 0, "", 0, 0); // (Giá trị giả)
            classToDelete.setClassID(classDetail.classID);

            db.classDao().deleteClass(classToDelete);

            runOnUiThread(this::loadClasses);
        });
    }
    @Override
    public void onItemClick(ClassDetail classDetail) {
        // Mở màn hình ManageSchedules
        Intent intent = new Intent(ManageClassesActivity.this, com.example.fuportal.core.data.academicAffairs.ManageSchedulesActivity.class);
        // Gửi ID và tên Lớp để hiển thị
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