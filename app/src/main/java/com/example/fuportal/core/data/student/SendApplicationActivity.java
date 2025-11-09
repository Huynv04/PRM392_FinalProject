package com.example.fuportal.core.data.student;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Application;
import com.example.fuportal.core.data.model.ApplicationType;
import com.example.fuportal.core.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SendApplicationActivity extends AppCompatActivity {

    private Spinner spinnerAppType;
    private EditText etContent;
    private TextView tvStudentCode, tvSubmissionDate;
    private Button btnSend;
    private Toolbar toolbar;

    private AppDatabase db;
    private ExecutorService executorService;
    private SessionManager sessionManager;

    private List<ApplicationType> availableTypes = new ArrayList<>();
    private String currentStudentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_application);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        sessionManager = new SessionManager(getApplicationContext());
        currentStudentCode = sessionManager.getLoggedInUserCode();

        // Ánh xạ
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Send New Application");
        }

        spinnerAppType = findViewById(R.id.spinnerAppType);
        etContent = findViewById(R.id.etContent);
        tvStudentCode = findViewById(R.id.tvStudentCode);
        tvSubmissionDate = findViewById(R.id.tvSubmissionDate);
        btnSend = findViewById(R.id.btnSend);

        // Hiển thị thông tin mặc định
        tvStudentCode.setText("Student Code: " + currentStudentCode);

        // Format ngày hôm nay
        String todayDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        tvSubmissionDate.setText("Submission Date: " + todayDate);

        loadApplicationTypes();

        btnSend.setOnClickListener(v -> sendApplication());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadApplicationTypes() {
        executorService.execute(() -> {
            // Chỉ lấy các loại đơn đang isActive
            availableTypes = db.applicationTypeDao().getAllActiveAppTypes();

            List<String> typeNames = availableTypes.stream()
                    .map(ApplicationType::getTypeName)
                    .collect(Collectors.toList());

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        typeNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAppType.setAdapter(adapter);
            });
        });
    }

    private void sendApplication() {
        if (availableTypes.isEmpty()) {
            Toast.makeText(this, "No application types available.", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = etContent.getText().toString().trim();
        if (content.isEmpty() || content.length() < 10) {
            etContent.setError("Please enter detailed content (min 10 characters).");
            etContent.requestFocus();
            return;
        }

        // Lấy AppTypeID
        int selectedIndex = spinnerAppType.getSelectedItemPosition();
        int appTypeID = availableTypes.get(selectedIndex).getAppTypeID();
        long submissionTime = System.currentTimeMillis();

        // Vô hiệu hóa nút
        btnSend.setEnabled(false);

        executorService.execute(() -> {
            try {
                Application newApp = new Application(currentStudentCode, appTypeID, content, submissionTime);
                db.applicationDao().insertApplication(newApp);

                runOnUiThread(() -> {
                    // Hiển thị thông báo và quay lại màn hình quản lý
                    new AlertDialog.Builder(this)
                            .setTitle("Success")
                            .setMessage("Application sent successfully.")
                            .setPositiveButton("OK", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error sending application: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnSend.setEnabled(true);
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