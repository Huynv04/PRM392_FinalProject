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
import com.example.fuportal.core.data.model.Semester;
import com.example.fuportal.core.ui.adapter.SemesterAdapter; // Import Adapter mới

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageSemestersActivity extends AppCompatActivity implements SemesterAdapter.OnSemesterActionsListener {

    private RecyclerView rvSemesters;
    private SemesterAdapter semesterAdapter;
    private AppDatabase db;
    private Button btnAddSemester;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_semesters);

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
        rvSemesters = findViewById(R.id.rvSemesters);
        btnAddSemester = findViewById(R.id.btnAddSemester);

        setupRecyclerView();
        loadSemesters();

        btnAddSemester.setOnClickListener(v -> {
            Intent intent = new Intent(ManageSemestersActivity.this, AddEditSemesterActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        semesterAdapter = new SemesterAdapter(new ArrayList<>(), this);
        rvSemesters.setLayoutManager(new LinearLayoutManager(this));
        rvSemesters.setAdapter(semesterAdapter);
    }

    private void loadSemesters() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Semester> semesters = db.semesterDao().getAllSemesters();
            runOnUiThread(() -> semesterAdapter.setSemesters(semesters));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSemesters();
    }

    @Override
    public void onEditClick(Semester semester) {
        Intent intent = new Intent(ManageSemestersActivity.this, AddEditSemesterActivity.class);
        intent.putExtra("SEMESTER_ID", semester.getSemesterID());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Semester semester) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Semester")
                .setMessage("Are you sure to delete: '" + semester.getSemesterName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> deleteSemesterFromDatabase(semester))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteSemesterFromDatabase(Semester semester) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.semesterDao().deleteSemester(semester);
            runOnUiThread(this::loadSemesters);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}