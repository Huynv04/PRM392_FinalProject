package com.example.fuportal.core.data.student;


import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.ApplicationDetail;
import com.example.fuportal.core.ui.adapter.ApplicationAdapter;
import com.example.fuportal.core.ui.adapter.ApplicationAdapter.OnApplicationClickListener;
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewApplicationsActivity extends AppCompatActivity implements OnApplicationClickListener {
    private RecyclerView rvApplications;
    private ApplicationAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private SessionManager sessionManager;
    private String studentId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_applications);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        sessionManager = new SessionManager(getApplicationContext());
        studentId = sessionManager.getLoggedInUserCode();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Application Requests");
        }

        rvApplications = findViewById(R.id.rvApplications);
        setupRecyclerView();
        loadApplications();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        adapter = new ApplicationAdapter(new ArrayList<>(), this);
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(adapter);
    }

    private void loadApplications() {
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Error: Student ID not found.", Toast.LENGTH_LONG).show();
            return;
        }

        executorService.execute(() -> {
            try {
                List<ApplicationDetail> appDetails = db.applicationDao().getStudentApplications(studentId);
                runOnUiThread(() -> {
                    adapter.setApplicationList(appDetails);
                    if (appDetails.isEmpty()) {
                        Toast.makeText(this, "You have not sent any applications yet.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error loading applications: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onApplicationClick(ApplicationDetail app) {
        // Đây là nơi bạn xử lý khi sinh viên nhấn vào một đơn từ
        Toast.makeText(this, "Application ID: " + app.applicationID + " clicked.", Toast.LENGTH_SHORT).show();

        // (Tương lai: Bạn có thể mở một màn hình chi tiết đơn từ ở đây)
    }
}