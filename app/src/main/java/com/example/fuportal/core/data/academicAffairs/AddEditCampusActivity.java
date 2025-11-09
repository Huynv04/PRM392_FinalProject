package com.example.fuportal.core.data.academicAffairs;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Campus;

import java.util.concurrent.Executors;

public class AddEditCampusActivity extends AppCompatActivity {

    private EditText etCampusName, etCampusAddress;
    private Button btnSaveCampus;
    private TextView tvCampusTitle;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;

    private AppDatabase db;
    private Campus currentCampus;
    private boolean isEditMode = false;
    private int editCampusId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_campus);

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
        etCampusName = findViewById(R.id.etCampusName);
        etCampusAddress = findViewById(R.id.etCampusAddress);
        btnSaveCampus = findViewById(R.id.btnSaveCampus);
        tvCampusTitle = findViewById(R.id.tvCampusTitle);

        db = AppDatabase.getDatabase(getApplicationContext());

        // Kiểm tra mode Add hay Edit
        if (getIntent().hasExtra("CAMPUS_ID")) {
            isEditMode = true;
            editCampusId = getIntent().getIntExtra("CAMPUS_ID", -1);
            tvCampusTitle.setText("Edit Campus");
            getSupportActionBar().setTitle("Edit Campus");
            loadCampusData();
        } else {
            isEditMode = false;
            tvCampusTitle.setText("Add New Campus");
            getSupportActionBar().setTitle("Add New Campus");
        }

        btnSaveCampus.setOnClickListener(v -> saveCampus());
    }

    private void loadCampusData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentCampus = db.campusDao().getCampusById(editCampusId);
            runOnUiThread(() -> {
                if (currentCampus != null) {
                    etCampusName.setText(currentCampus.getCampusName());
                    etCampusAddress.setText(currentCampus.getAddress());
                }
            });
        });
    }

    private void saveCampus() {
        String name = etCampusName.getText().toString().trim();
        String address = etCampusAddress.getText().toString().trim();

        if (name.isEmpty()) {
            etCampusName.setError("Campus name is required");
            etCampusName.requestFocus();
            return;
        }

        btnSaveCampus.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (isEditMode) {
                // Logic Sửa (Update)
                currentCampus.setCampusName(name);
                currentCampus.setAddress(address);
                db.campusDao().updateCampus(currentCampus);
            } else {
                // Logic Thêm (Create)
                db.campusDao().insertCampus(new Campus(name, address));
            }

            runOnUiThread(() -> {
                String message = isEditMode ? "Campus updated successfully" : "Add new campus successful";
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}