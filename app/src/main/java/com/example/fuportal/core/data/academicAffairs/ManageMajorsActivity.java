package com.example.fuportal.core.data.academicAffairs;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout; // Import LinearLayout

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
import com.example.fuportal.core.data.model.Major;
import com.example.fuportal.core.ui.adapter.MajorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageMajorsActivity extends AppCompatActivity implements MajorAdapter.OnMajorActionsListener {

    private RecyclerView rvMajors;
    private MajorAdapter majorAdapter;
    private AppDatabase db;
    private Button btnAddMajor;
    private Toolbar toolbar;
    private LinearLayout mainLayout; // Layout gốc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Bật Tràn viền
        setContentView(R.layout.activity_manage_majors);

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
        rvMajors = findViewById(R.id.rvMajors);
        btnAddMajor = findViewById(R.id.btnAddMajor);

        setupRecyclerView();
        loadMajors();

        // 1. Xử lý nút "Add New"
        btnAddMajor.setOnClickListener(v -> {
            Intent intent = new Intent(ManageMajorsActivity.this, AddEditMajorActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        majorAdapter = new MajorAdapter(new ArrayList<>(), this);
        rvMajors.setLayoutManager(new LinearLayoutManager(this));
        rvMajors.setAdapter(majorAdapter);
    }

    private void loadMajors() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Major> majors = db.majorDao().getAllMajors();
            runOnUiThread(() -> majorAdapter.setMajors(majors));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMajors(); // Tải lại danh sách
    }

    // --- 2. Xử lý nút "Edit" ---
    @Override
    public void onEditClick(Major major) {
        Intent intent = new Intent(ManageMajorsActivity.this, AddEditMajorActivity.class);
        intent.putExtra("MAJOR_ID", major.getMajorID());
        startActivity(intent);
    }

    // --- 3. Xử lý nút "Delete" ---
    @Override
    public void onDeleteClick(Major major) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Major")
                .setMessage("Are you sure to delete this major: '" + major.getMajorName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> deleteMajorFromDatabase(major))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMajorFromDatabase(Major major) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.majorDao().deleteMajor(major);
            runOnUiThread(this::loadMajors);
        });
    }

    // Xử lý nút "back" trên Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}