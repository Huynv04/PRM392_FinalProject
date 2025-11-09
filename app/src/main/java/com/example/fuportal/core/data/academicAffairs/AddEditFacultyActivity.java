package com.example.fuportal.core.data.academicAffairs; // (Đặt trong package của bạn)

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
 import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Faculty;

import java.util.concurrent.Executors;

public class AddEditFacultyActivity extends AppCompatActivity {

    private EditText etFacultyName;
    private Button btnSaveFaculty;
    private TextView tvFacultyTitle;
    private CoordinatorLayout mainLayout; // <-- 1. KHAI BÁO LAYOUT GỐC
    private AppDatabase db;
    private Faculty currentFaculty; // Dùng để lưu faculty nếu ở mode Edit
    private boolean isEditMode = false;
    private Toolbar toolbar; // 2. KHAI BÁO TOOLBAR

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_faculty);
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // --- 3. ÁNH XẠ VÀ CÀI ĐẶT TOOLBAR ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 4. Bật nút back "<"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etFacultyName = findViewById(R.id.etFacultyName);
        btnSaveFaculty = findViewById(R.id.btnSaveFaculty);
        tvFacultyTitle = findViewById(R.id.tvFacultyTitle);

        db = AppDatabase.getDatabase(getApplicationContext());

        // 1. Kiểm tra xem có phải mode Edit không
        if (getIntent().hasExtra("FACULTY_ID")) {
            isEditMode = true;
            int facultyId = getIntent().getIntExtra("FACULTY_ID", -1);
            tvFacultyTitle.setText("Edit Faculty");
            loadFacultyData(facultyId); // Tải dữ liệu cũ
        } else {
            isEditMode = false;
            tvFacultyTitle.setText("Add New Faculty");
        }

        btnSaveFaculty.setOnClickListener(v -> saveFaculty());
    }

    private void loadFacultyData(int facultyId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentFaculty = db.facultyDao().getFacultyById(facultyId);
            runOnUiThread(() -> {
                if (currentFaculty != null) {
                    etFacultyName.setText(currentFaculty.getFacultyName());
                }
            });
        });
    }

    private void saveFaculty() {
        String name = etFacultyName.getText().toString().trim();

        if (name.isEmpty()) {
            etFacultyName.setError("Faculty name is required");
            etFacultyName.requestFocus();
            return;
        }

        // Vô hiệu hóa nút
        btnSaveFaculty.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (isEditMode) {
                // 2. Logic Sửa (Update)
                currentFaculty.setFacultyName(name);
                db.facultyDao().updateFaculty(currentFaculty);
            } else {
                // 3. Logic Thêm (Create)
                db.facultyDao().insertFaculty(new Faculty(name));
            }

            // Quay về màn hình danh sách (trên luồng chính)
            runOnUiThread(() -> {
                // Hiển thị thông báo (Theo yêu cầu của bạn)
                String message = isEditMode ? "Faculty updated successfully" : "Add new faculty successful";
                showSuccessDialogAndFinish(message);
            });
        });
    }

    // Hiển thị thông báo thành công và quay lại
    private void showSuccessDialogAndFinish(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    finish(); // Đóng Activity này và quay lại màn hình List
                })
                .setCancelable(false)
                .show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Gọi hàm back
        return true;
    }
}