package com.example.fuportal.core.data.academicAffairs;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Application;
import com.example.fuportal.core.data.model.ApplicationDetail;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.util.SessionManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessApplicationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvAppType, tvStudentInfo, tvContent;
    private EditText etResponseContent;
    private Spinner spinnerStatus;
    private Button btnProcessSave;

    private AppDatabase db;
    private ExecutorService executorService;
    private SessionManager sessionManager;

    private int applicationId;
    private Application currentApplication;
    private List<String> statusOptions = Arrays.asList("Pending", "Approved", "Rejected");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_process_application);

        // Khởi tạo
        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        sessionManager = new SessionManager(getApplicationContext());

        // Lấy ID Đơn từ
        applicationId = getIntent().getIntExtra("APPLICATION_ID", -1);
        if (applicationId == -1) {
            Toast.makeText(this, "Error: Application ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ Views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        tvAppType = findViewById(R.id.tvAppType);
        tvStudentInfo = findViewById(R.id.tvStudentInfo);
        tvContent = findViewById(R.id.tvContent);
        etResponseContent = findViewById(R.id.etResponseContent);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnProcessSave = findViewById(R.id.btnProcessSave);

        // Setup Views
        setupStatusSpinner();
        loadApplicationData();

        // Gán sự kiện Save
        btnProcessSave.setOnClickListener(v -> processApplication());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupStatusSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void loadApplicationData() {
        executorService.execute(() -> {
            // Lấy Application gốc và tên Sinh viên
            currentApplication = db.applicationDao().getApplicationById(applicationId);
            User student = db.userDao().getUserByCode(currentApplication.getStudentID());

            runOnUiThread(() -> {
                if (currentApplication != null && student != null) {

                    // Lấy vị trí status hiện tại
                    int currentStatusIndex = statusOptions.indexOf(currentApplication.getStatus());

                    // Cập nhật UI
                    tvStudentInfo.setText("Student: " + student.getFullName() + " (" + student.getUserCode() + ")");
                    tvContent.setText(currentApplication.getContent());
                    etResponseContent.setText(currentApplication.getResponseContent());

                    // Cài đặt Spinner Status
                    if (currentStatusIndex >= 0) {
                        spinnerStatus.setSelection(currentStatusIndex);
                    }
                } else {
                    Toast.makeText(this, "Application data not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void processApplication() {
        String selectedStatus = spinnerStatus.getSelectedItem().toString();
        String responseContent = etResponseContent.getText().toString().trim();
        String handlerId = sessionManager.getLoggedInUserCode(); // Lấy ID của nhân viên đang xử lý (Academic Affairs)

        if (selectedStatus.equals("Pending")) {
            Toast.makeText(this, "Please select 'Approved' or 'Rejected'.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnProcessSave.setEnabled(false);

        executorService.execute(() -> {
            try {
                // Gọi hàm DAO để cập nhật Status, Response, và HandlerID
                db.applicationDao().updateApplicationStatus(
                        applicationId,
                        selectedStatus,
                        responseContent,
                        handlerId
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Process Application request successfully.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK); // Báo hiệu thành công cho màn hình List
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error processing application: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnProcessSave.setEnabled(true);
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}