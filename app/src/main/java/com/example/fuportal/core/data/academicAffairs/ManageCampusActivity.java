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
import com.example.fuportal.core.data.model.Campus;
import com.example.fuportal.core.ui.adapter.CampusAdapter; // Import Adapter mới

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageCampusActivity extends AppCompatActivity implements CampusAdapter.OnCampusActionsListener {

    private RecyclerView rvCampuses;
    private CampusAdapter campusAdapter;
    private AppDatabase db;
    private Button btnAddCampus;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_campus);

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
        rvCampuses = findViewById(R.id.rvCampuses);
        btnAddCampus = findViewById(R.id.btnAddCampus);

        setupRecyclerView();
        loadCampuses();

        // 1. Xử lý nút "Add New"
        btnAddCampus.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCampusActivity.this, AddEditCampusActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        campusAdapter = new CampusAdapter(new ArrayList<>(), this);
        rvCampuses.setLayoutManager(new LinearLayoutManager(this));
        rvCampuses.setAdapter(campusAdapter);
    }

    private void loadCampuses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Campus> campuses = db.campusDao().getAllCampuses();
            runOnUiThread(() -> campusAdapter.setCampuses(campuses));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCampuses();
    }

    // --- 2. Xử lý nút "Edit" ---
    @Override
    public void onEditClick(Campus campus) {
        Intent intent = new Intent(ManageCampusActivity.this, AddEditCampusActivity.class);
        intent.putExtra("CAMPUS_ID", campus.getCampusID());
        startActivity(intent);
    }

    // --- 3. Xử lý nút "Delete" ---
    @Override
    public void onDeleteClick(Campus campus) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Campus")
                .setMessage("Are you sure to delete this campus: '" + campus.getCampusName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCampusFromDatabase(campus))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCampusFromDatabase(Campus campus) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.campusDao().deleteCampus(campus);
            runOnUiThread(this::loadCampuses);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}