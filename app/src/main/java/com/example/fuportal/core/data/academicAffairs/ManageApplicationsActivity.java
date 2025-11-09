package com.example.fuportal.core.data.academicAffairs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.admin.ManageAppTypesActivity;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.ApplicationDetail;
import com.example.fuportal.core.ui.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageApplicationsActivity extends AppCompatActivity {

    private RecyclerView rvApplications;
    private ApplicationAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    private static final int REQUEST_CODE_PROCESS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_applications);

        // Khởi tạo
        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Application Requests");
        }
        // ... (Code ViewCompat setup giữ nguyên) ...

        // Ánh xạ View
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
        // Tái sử dụng Adapter của Student, nhưng thêm listener
        adapter = new ApplicationAdapter(new ArrayList<>(), this::onApplicationClick); // Truyền hàm xử lý
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(adapter);
    }

    private void loadApplications() {
        executorService.execute(() -> {
            // Lấy TẤT CẢ đơn từ (hàm mới)
            List<ApplicationDetail> appDetails = db.applicationDao().getAllApplicationsDetails();
            runOnUiThread(() -> adapter.setApplicationList(appDetails));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }

    // Xử lý khi click vào một đơn từ
    private void onApplicationClick(ApplicationDetail app) {
        // Mở màn hình xử lý đơn
        Intent intent = new Intent(this, ProcessApplicationActivity.class);
        intent.putExtra("APPLICATION_ID", app.applicationID);
        startActivityForResult(intent, REQUEST_CODE_PROCESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROCESS && resultCode == RESULT_OK) {
            loadApplications(); // Tải lại danh sách sau khi xử lý xong
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}