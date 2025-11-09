package com.example.fuportal.core.data.examinationDept; // (Package của bạn)

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.auth.LoginActivity; // Import Login
import com.example.fuportal.core.util.SessionManager; // Import Session

public class ExaminationDeptMainActivity extends AppCompatActivity {

    // 1. Khai báo các nút
    private Button btnManageExamSchedule, btnAssignInvigilators, btnManageExamResults;
    private Button btnPostExamAnnouncements, btnGenerateExamReports, btnLogout;

    private SessionManager sessionManager;
    private String loggedInStaffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_examination_dept_main);

        // 2. Khởi tạo Session và kiểm tra Login
        sessionManager = new SessionManager(getApplicationContext());
        loggedInStaffId = sessionManager.getLoggedInUserCode();

        if (loggedInStaffId == null || sessionManager.getLoggedInUserRole() != 4) { // 4 = ExaminationDept
            Toast.makeText(this, "Access Denied. Please login.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Xử lý Tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 4. Ánh xạ các nút
        btnManageExamSchedule = findViewById(R.id.btnManageExamSchedule);
        btnAssignInvigilators = findViewById(R.id.btnAssignInvigilators);
        btnManageExamResults = findViewById(R.id.btnManageExamResults);
        btnPostExamAnnouncements = findViewById(R.id.btnPostExamAnnouncements);
        btnGenerateExamReports = findViewById(R.id.btnGenerateExamReports);
        btnLogout = findViewById(R.id.btnLogout);

        // 5. Gán sự kiện click

        // (Sự kiện click cho các chức năng chưa làm)
        View.OnClickListener notImplementedListener = v -> {
            Button b = (Button) v;
            Toast.makeText(this, b.getText() + " - Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        };

        // UC18: Manage Exam Schedule (Đã có Activity)
        btnManageExamSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageExamSchedulesActivity.class);
            startActivity(intent);
        });

        // Gán listener cho các UC còn lại
        btnAssignInvigilators.setOnClickListener(notImplementedListener); // UC19
        btnManageExamResults.setOnClickListener(notImplementedListener); // UC20
        btnPostExamAnnouncements.setOnClickListener(notImplementedListener); // UC22
        btnGenerateExamReports.setOnClickListener(notImplementedListener); // UC21

        // 6. Logout
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(ExaminationDeptMainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}