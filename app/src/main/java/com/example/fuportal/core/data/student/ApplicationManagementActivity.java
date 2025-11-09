package com.example.fuportal.core.data.student;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;

// Đặt trong package 'student'
public class ApplicationManagementActivity extends AppCompatActivity {

    private Button btnViewRequests, btnSendNew;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_application_management);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Application Management");
        }

        btnViewRequests = findViewById(R.id.btnViewApplicationsSent);
        btnSendNew = findViewById(R.id.btnSendNewApplication);

        // Chuyển hướng đến màn hình View List
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewApplicationsActivity.class);
            startActivity(intent);
        });

        // Chuyển hướng đến màn hình Gửi đơn mới
        btnSendNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, SendApplicationActivity.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}