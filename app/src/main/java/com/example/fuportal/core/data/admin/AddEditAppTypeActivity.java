package com.example.fuportal.core.data.admin;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.fuportal.core.data.model.ApplicationType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditAppTypeActivity extends AppCompatActivity {

    private EditText etTypeName;
    private Button btnSaveAppType;
    private TextView tvTitle;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;

    private AppDatabase db;
    private ExecutorService executorService;
    private ApplicationType currentAppType;
    private boolean isEditMode = false;
    private int editAppTypeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_app_type); // Cần tạo layout này

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Ánh xạ View
        etTypeName = findViewById(R.id.etTypeName);
        btnSaveAppType = findViewById(R.id.btnSaveAppType);
        tvTitle = findViewById(R.id.tvAppTypeTitle);
        toolbar = findViewById(R.id.toolbar);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // (Xử lý EdgeToEdge insets)

        // Kiểm tra mode Add hay Edit
        if (getIntent().hasExtra("TYPE_ID")) {
            isEditMode = true;
            editAppTypeId = getIntent().getIntExtra("TYPE_ID", -1);
            tvTitle.setText("Edit Application Type");
            getSupportActionBar().setTitle("Edit Type");
            loadAppTypeData();
        } else {
            tvTitle.setText("Add New Application Type");
            getSupportActionBar().setTitle("Add New Type");
        }
        // Xử lý Tràn viền (giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSaveAppType.setOnClickListener(v -> saveAppType());
    }

    private void loadAppTypeData() {
        executorService.execute(() -> {
            currentAppType = db.applicationTypeDao().getAppTypeById(editAppTypeId);
            runOnUiThread(() -> {
                if (currentAppType != null) {
                    etTypeName.setText(currentAppType.getTypeName());
                }
            });
        });
    }

    private void saveAppType() {
        String name = etTypeName.getText().toString().trim();

        if (name.isEmpty()) {
            etTypeName.setError("Type name is required");
            etTypeName.requestFocus();
            return;
        }

        btnSaveAppType.setEnabled(false);

        executorService.execute(() -> {
            try {
                if (isEditMode) {
                    // UPDATE
                    currentAppType.setTypeName(name);
                    db.applicationTypeDao().updateAppType(currentAppType);
                } else {
                    // ADD (isActive = true)
                    db.applicationTypeDao().insertAppType(new ApplicationType(name, true));
                }

                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    showSuccessDialogAndFinish(isEditMode ? "Type updated successfully." : "New type added.");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error saving type: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnSaveAppType.setEnabled(true);
                });
            }
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