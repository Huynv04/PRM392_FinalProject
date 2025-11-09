package com.example.fuportal.core.data.admin; // Package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.auth.LoginActivity;
import com.example.fuportal.core.util.SessionManager;

public class AdminMainActivity extends AppCompatActivity {

    private Button btnManageUsers, btnManageAppTypes, btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        sessionManager = new SessionManager(getApplicationContext());

        // Kiểm tra quyền (RoleID 5 = Admin)
        if (sessionManager.getLoggedInUserRole() != 5) {
            Toast.makeText(this, "Access Denied: Not an Admin.", Toast.LENGTH_LONG).show();
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Ánh xạ
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageAppTypes = findViewById(R.id.btnManageAppTypes);
        btnLogout = findViewById(R.id.btnLogout);

        // Xử lý Tràn viền (giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gán sự kiện
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUsersActivity.class); // <-- Mở màn hình quản lý User
            startActivity(intent);
        });

        btnManageAppTypes.setOnClickListener(v -> {
             Intent intent = new Intent(this, ManageAppTypesActivity.class);
             startActivity(intent);
         });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}