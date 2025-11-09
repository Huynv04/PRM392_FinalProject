package com.example.fuportal.core.data.admin;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.ApplicationType;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.ui.adapter.ApplicationTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageAppTypesActivity extends AppCompatActivity implements ApplicationTypeAdapter.OnAppTypeActionsListener {

    private RecyclerView rvAppTypes;
    private ApplicationTypeAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private Button btnAddAppType;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    private static final int REQUEST_CODE_ADD_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_app_types);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Ánh xạ và Toolbar
        // ... (Toolbar, ViewCompat code giữ nguyên) ...
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Application Types");
        }

        // Ánh xạ Views
        mainLayout = findViewById(R.id.main);
        rvAppTypes = findViewById(R.id.rvAppTypes);
        btnAddAppType = findViewById(R.id.btnAddAppType);

        setupRecyclerView();
        loadAppTypes();

        // Xử lý nút Add
        btnAddAppType.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditAppTypeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
        });
        // Xử lý Tràn viền (giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        adapter = new ApplicationTypeAdapter(new ArrayList<>(), this);
        rvAppTypes.setLayoutManager(new LinearLayoutManager(this));
        rvAppTypes.setAdapter(adapter);
    }

    private void loadAppTypes() {
        executorService.execute(() -> {
            // Lấy TẤT CẢ các loại đơn (kể cả đã bị xóa mềm)
            List<ApplicationType> appTypes = db.applicationTypeDao().getAllAppTypes();
            runOnUiThread(() -> adapter.setAppTypeList(appTypes));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppTypes();
    }

    @Override
    public void onEditClick(ApplicationType type) {
        // Mở màn hình Edit
        Intent intent = new Intent(this, AddEditAppTypeActivity.class);
        intent.putExtra("TYPE_ID", type.getAppTypeID());
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
    }

    @Override
    public void onDeleteClick(ApplicationType type) {
        // Logic Xóa mềm / Kích hoạt lại
        String action = type.isActive() ? "Remove" : "Restore";

        new AlertDialog.Builder(this)
                .setTitle("Confirm " + action)
                .setMessage("Are you sure you want to " + action.toLowerCase() + " the type: " + type.getTypeName() + "?")
                .setPositiveButton(action, (dialog, which) -> toggleAppTypeStatus(type.getAppTypeID(), type.isActive()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Hàm Xóa/Kích hoạt mềm
    private void toggleAppTypeStatus(int typeId, boolean isActive) {
        executorService.execute(() -> {
            if (isActive) {
                db.applicationTypeDao().softDeleteAppType(typeId); // Xóa mềm
            } else {
                db.applicationTypeDao().activateAppType(typeId); // Kích hoạt lại
            }
            runOnUiThread(this::loadAppTypes); // Tải lại danh sách
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_EDIT && resultCode == RESULT_OK) {
            loadAppTypes();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}