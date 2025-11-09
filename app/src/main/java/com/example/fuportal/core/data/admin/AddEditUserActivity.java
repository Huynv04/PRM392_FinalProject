package com.example.fuportal.core.data.admin;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent; // Import
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Campus;
import com.example.fuportal.core.data.model.Role;
import com.example.fuportal.core.data.model.User; // Entity gốc
import com.example.fuportal.core.util.DateHelper;

import org.mindrot.jbcrypt.BCrypt;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddEditUserActivity extends AppCompatActivity {

    private EditText etUserCode, etFullName, etUsername, etGmail, etPassword;
    private Spinner spinnerRole, spinnerCampus;
    private TextView tvUserTitle, tvDateOfBirth, tvPasswordLabel;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    Button btnSaveUser;

    private AppDatabase db;
    private ExecutorService executorService;
    private User currentUser;
    private boolean isEditMode = false;
    private String editUserCode;
    String finalPasswordHash = "";

    private Calendar dateOfBirth = Calendar.getInstance();

    // Data lists for Spinners
    private List<Role> roleList = new ArrayList<>();
    private List<Campus> campusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_user);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Ánh xạ View
        etUserCode = findViewById(R.id.etUserCode);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etGmail = findViewById(R.id.etGmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        spinnerCampus = findViewById(R.id.spinnerCampus);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvUserTitle = findViewById(R.id.tvUserTitle);
        tvPasswordLabel = findViewById(R.id.tvPasswordLabel);
          btnSaveUser = findViewById(R.id.btnSaveUser);

        // Setup Toolbar và EdgeToEdge
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainLayout = findViewById(R.id.main);
        // ... (Code ViewCompat setup giữ nguyên) ...

        // Xử lý DatePicker
        tvDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Load dữ liệu cho Spinners và kiểm tra Mode
        loadSpinnerData();

        btnSaveUser.setOnClickListener(v -> saveUser());
        // Xử lý Tràn viền (giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadSpinnerData() {
        executorService.execute(() -> {
            roleList = db.roleDao().getAllRoles();
            campusList = db.campusDao().getAllCampuses();

            // Xử lý chế độ Edit
            if (getIntent().hasExtra("USER_CODE")) {
                isEditMode = true;
                editUserCode = getIntent().getStringExtra("USER_CODE");
                currentUser = db.userDao().getUserByCode(editUserCode);
            }

            // Tạo danh sách hiển thị
            List<String> roleNames = roleList.stream().map(Role::getRoleName).collect(Collectors.toList());
            List<String> campusNames = new ArrayList<>();
            campusNames.add("None / System Admin"); // Index 0
            campusNames.addAll(campusList.stream().map(Campus::getCampusName).collect(Collectors.toList()));

            runOnUiThread(() -> {
                // Setup Spinners
                spinnerRole.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleNames));
                spinnerCampus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, campusNames));

                // Cài đặt UI theo Mode
                if (isEditMode && currentUser != null) {
                    setupEditMode(currentUser);
                } else {
                    tvUserTitle.setText("Add New User");
                    getSupportActionBar().setTitle("Add New User");
                    etUserCode.setEnabled(true);
                }
            });
        });
    }

    private void setupEditMode(User user) {
        tvUserTitle.setText("Edit User: " + user.getUserCode());
        getSupportActionBar().setTitle("Edit User");

        etUserCode.setText(user.getUserCode());
        etUserCode.setEnabled(false); // Không cho sửa PK

        etFullName.setText(user.getFullName());
        etUsername.setText(user.getUsername());
        etGmail.setText(user.getGmail());

        // Ẩn/Đổi label cho Password
        tvPasswordLabel.setText("Password (Leave blank to keep old)");
        etPassword.setHint("Leave blank to keep current");

        // Setup Date
        dateOfBirth.setTimeInMillis(user.getDateOfBirth());
        tvDateOfBirth.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(user.getDateOfBirth())));

        // Select Role và Campus
        selectSpinner(spinnerRole, roleList.stream().map(Role::getRoleID).collect(Collectors.toList()), user.getRoleID());
        selectCampusSpinner(user.getCampusID());
    }

    // Hàm helper chọn vị trí Spinner
    private void selectSpinner(Spinner spinner, List<Integer> idList, int targetId) {
        int position = idList.indexOf(targetId);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void selectCampusSpinner(Integer campusId) {
        if (campusId == null) {
            spinnerCampus.setSelection(0);
            return;
        }
        for (int i = 0; i < campusList.size(); i++) {
            if (campusList.get(i).getCampusID() == campusId) {
                spinnerCampus.setSelection(i + 1); // +1 vì index 0 là "None"
                return;
            }
        }
    }


    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            dateOfBirth.set(year, month, dayOfMonth);
            tvDateOfBirth.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateOfBirth.getTime()));
        };

        new DatePickerDialog(this, dateSetListener,
                dateOfBirth.get(Calendar.YEAR),
                dateOfBirth.get(Calendar.MONTH),
                dateOfBirth.get(Calendar.DAY_OF_MONTH)).show();
    }

    private Integer getSelectedCampusId() {
        int selectedIndex = spinnerCampus.getSelectedItemPosition();
        if (selectedIndex == 0) {
            return null;
        }
        return campusList.get(selectedIndex - 1).getCampusID();
    }

    private void saveUser() {
        String userCode = etUserCode.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String gmail = etGmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userCode.isEmpty() || fullName.isEmpty() || username.isEmpty() || gmail.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEditMode && password.isEmpty()) {
            Toast.makeText(this, "Password is required for new users.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy IDs từ Spinners
        int roleId = roleList.get(spinnerRole.getSelectedItemPosition()).getRoleID();
        Integer campusId = getSelectedCampusId();

        // Xác định mật khẩu (hash)

        if (isEditMode) {
            finalPasswordHash = currentUser.getHashedPassword(); // Mặc định giữ lại
        }

        if (!password.isEmpty()) {
            // HASH MẬT KHẨU MỚI NẾU CÓ NHẬP
            finalPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        } else if (!isEditMode) {
            // Lỗi này không nên xảy ra do Validation, nhưng thêm vào để đảm bảo
            Toast.makeText(this, "Internal Error: Password missing.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Vô hiệu hóa nút Save
        btnSaveUser.setEnabled(false);

        // Chạy DB operation
        executorService.execute(() -> {
            try {
                User userToSave;
                if (isEditMode) {
                    // UPDATE
                    userToSave = currentUser;
                    userToSave.setFullName(fullName);
                    userToSave.setUsername(username);
                    userToSave.setGmail(gmail);
                    userToSave.setHashedPassword(finalPasswordHash);
                    userToSave.setRoleID(roleId);
                    userToSave.setCampusID(campusId);
                    userToSave.setDateOfBirth(dateOfBirth.getTimeInMillis());

                    db.userDao().updateUser(userToSave);
                } else {
                    // ADD (isActive = true)
                    userToSave = new User(userCode, fullName, username, gmail, finalPasswordHash, dateOfBirth.getTimeInMillis(), null, null, roleId, campusId, true);
                    db.userDao().insertUser(userToSave);
                }

                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    showSuccessDialogAndFinish(isEditMode ? "User updated successfully." : "New user added.");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnSaveUser.setEnabled(true);
                });
            }
        });
    }

    private void showSuccessDialogAndFinish(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}