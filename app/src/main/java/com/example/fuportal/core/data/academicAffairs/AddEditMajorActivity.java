package com.example.fuportal.core.data.academicAffairs;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout; // Import đúng
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Faculty;
import com.example.fuportal.core.data.model.Major;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddEditMajorActivity extends AppCompatActivity {

    private EditText etMajorName;
    private Spinner spinnerFaculty; // <-- SPINNER MỚI
    private Button btnSaveMajor;
    private TextView tvMajorTitle;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout; // Layout gốc

    private AppDatabase db;
    private List<Faculty> facultyList; // Danh sách Khoa
    private Major currentMajor;
    private boolean isEditMode = false;
    private int editMajorId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Bật Tràn viền
        setContentView(R.layout.activity_add_edit_major);

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

        // Ánh xạ View
        etMajorName = findViewById(R.id.etMajorName);
        spinnerFaculty = findViewById(R.id.spinnerFaculty);
        btnSaveMajor = findViewById(R.id.btnSaveMajor);
        tvMajorTitle = findViewById(R.id.tvMajorTitle);

        db = AppDatabase.getDatabase(getApplicationContext());
        facultyList = new ArrayList<>();

        // 1. Tải danh sách Khoa (Faculty) vào Spinner
        loadFacultiesIntoSpinner();

        // 2. Kiểm tra mode Add hay Edit
        if (getIntent().hasExtra("MAJOR_ID")) {
            isEditMode = true;
            editMajorId = getIntent().getIntExtra("MAJOR_ID", -1);
            tvMajorTitle.setText("Edit Major");
            getSupportActionBar().setTitle("Edit Major");
            loadMajorData();
        } else {
            isEditMode = false;
            tvMajorTitle.setText("Add New Major");
            getSupportActionBar().setTitle("Add New Major");
        }

        btnSaveMajor.setOnClickListener(v -> saveMajor());
    }

    private void loadFacultiesIntoSpinner() {
        Executors.newSingleThreadExecutor().execute(() -> {
            facultyList = db.facultyDao().getAllFaculties();
            // Lấy tên của các Khoa
            List<String> facultyNames = facultyList.stream()
                    .map(Faculty::getFacultyName)
                    .collect(Collectors.toList());

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, facultyNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFaculty.setAdapter(adapter);

                // Nếu đang ở mode Edit, chọn đúng Faculty
                if (currentMajor != null) {
                    selectFacultyInSpinner();
                }
            });
        });
    }

    private void loadMajorData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentMajor = db.majorDao().getMajorById(editMajorId);
            runOnUiThread(() -> {
                if (currentMajor != null) {
                    etMajorName.setText(currentMajor.getMajorName());
                    // Chọn Faculty trong Spinner
                    if (!facultyList.isEmpty()) {
                        selectFacultyInSpinner();
                    }
                }
            });
        });
    }

    private void selectFacultyInSpinner() {
        for (int i = 0; i < facultyList.size(); i++) {
            if (facultyList.get(i).getFacultyID() == currentMajor.getFacultyID()) {
                spinnerFaculty.setSelection(i);
                break;
            }
        }
    }

    private void saveMajor() {
        String name = etMajorName.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etMajorName.setError("Major name is required");
            etMajorName.requestFocus();
            return;
        }
        if (spinnerFaculty.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Please select a faculty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy FacultyID từ Spinner
        int selectedFacultyIndex = spinnerFaculty.getSelectedItemPosition();
        int facultyId = facultyList.get(selectedFacultyIndex).getFacultyID();

        btnSaveMajor.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (isEditMode) {
                // Logic Sửa (Update)
                currentMajor.setMajorName(name);
                currentMajor.setFacultyID(facultyId);
                db.majorDao().updateMajor(currentMajor);
            } else {
                // Logic Thêm (Create)
                db.majorDao().insertMajor(new Major(name, facultyId));
            }

            runOnUiThread(() -> {
                String message = isEditMode ? "Major updated successfully" : "Add new major successful";
                showSuccessDialogAndFinish(message);
            });
        });
    }

    private void showSuccessDialogAndFinish(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    // Xử lý nút "back" trên Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}