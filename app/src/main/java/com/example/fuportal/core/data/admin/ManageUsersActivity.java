package com.example.fuportal.core.data.admin;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher; // <-- Import
import android.view.View; // <-- Import
import android.widget.AdapterView; // <-- Import
import android.widget.ArrayAdapter; // <-- Import
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner; // <-- Import
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// ... (các import khác)

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.data.model.UserDetail;
import com.example.fuportal.core.ui.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageUsersActivity extends AppCompatActivity implements UserAdapter.OnUserActionsListener {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private Button btnAddUser;
    private Toolbar toolbar;
    private LinearLayout mainLayout;

    // --- BIẾN LỌC (MỚI) ---
    private EditText etSearchQuery;
    private Spinner spinnerUserStatus;
    private String currentSearchQuery = "";
    // 0: All, 1: Active, 2: Deactive.
    private int currentStatusFilter = 0;
    // ----------------------

    private static final int REQUEST_CODE_ADD_EDIT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_users);

        // Khởi tạo
        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Ánh xạ và Toolbar
        // ... (Toolbar, ViewCompat code giữ nguyên) ...
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage User Accounts");
        }

        rvUsers = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);

        // --- ÁNH XẠ FILTER ---
        etSearchQuery = findViewById(R.id.etSearchQuery);
        spinnerUserStatus = findViewById(R.id.spinnerUserStatus);

        setupRecyclerView();
        setupFilters(); // <-- Cài đặt Spinner và Listener
        loadUsers(); // Tải lần đầu

        // Xử lý nút Add
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditUserActivity.class);
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
        userAdapter = new UserAdapter(new ArrayList<>(), this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void setupFilters() {
        // Cài đặt Spinner Status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_status_filters, // <-- Bạn cần tạo Array này trong res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserStatus.setAdapter(adapter);

        // 1. Listener cho Spinner
        spinnerUserStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position 0: All (0), 1: Active (1), 2: Deactive (2)
                currentStatusFilter = position;
                loadUsers(); // Tải lại dữ liệu
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 2. Listener cho Ô tìm kiếm
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cập nhật chuỗi tìm kiếm và tải lại
                currentSearchQuery = s.toString().toLowerCase();
                loadUsers();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUsers() {
        // Dùng giá trị đã lọc (currentSearchQuery, currentStatusFilter)
        final String query = currentSearchQuery;
        final int status = currentStatusFilter;

        executorService.execute(() -> {
            // GỌI HÀM DAO ĐÃ CẬP NHẬT
            List<UserDetail> userDetails = db.userDao().getFilteredUserDetails(query, status);
            runOnUiThread(() -> userAdapter.setUserList(userDetails));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
    @Override
    public void onEditClick(UserDetail user) {
        // Logic khi nhấn Edit
        Intent intent = new Intent(this, AddEditUserActivity.class);
        intent.putExtra("USER_CODE", user.userCode);
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
    }

    @Override
    public void onDeleteClick(UserDetail user) {
        // Logic Xóa/Kích hoạt (Deactivate/Activate)
        String action = user.isActive ? "Deactivate" : "Activate";
        String statusText = user.isActive ? "Deactivate" : "Reactivate";

        new AlertDialog.Builder(this)
                .setTitle("Confirm " + statusText)
                .setMessage("Are you sure you want to " + statusText.toLowerCase() + " user: " + user.fullName + "?")
                .setPositiveButton(statusText, (dialog, which) -> toggleUserStatus(user.userCode, user.isActive))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Hàm Xóa/Kích hoạt mềm
    private void toggleUserStatus(String userCode, boolean isActive) {
        executorService.execute(() -> {
            if (isActive) {
                db.userDao().deactivateUser(userCode); // Deactivate
            } else {
                db.userDao().activateUser(userCode); // Activate
            }
            runOnUiThread(this::loadUsers); // Tải lại danh sách
        });
    }

    // ... (onEditClick, onDeleteClick, deleteUser, onActivityResult, onSupportNavigateUp giữ nguyên)
}