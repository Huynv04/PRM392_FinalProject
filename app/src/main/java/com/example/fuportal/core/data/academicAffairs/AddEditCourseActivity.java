package com.example.fuportal.core.data.academicAffairs;


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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Course;
import com.example.fuportal.core.data.model.Major; // <-- Import Major

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors; // <-- Import Stream

public class AddEditCourseActivity extends AppCompatActivity {

    private EditText etCourseCode, etCourseName, etCredits;
    private Spinner spinnerMajor; // Spinner cho Chuyên ngành
    private Spinner spinnerPrerequisite;
    private List<Course> allCoursesList;
    private Button btnSaveCourse;
    private TextView tvCourseTitle;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;

    private AppDatabase db;
    private List<Major> majorList; // Danh sách Chuyên ngành
    private Course currentCourse;
    private boolean isEditMode = false;
    private int editCourseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_course);

        // Xử lý Tràn viền
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cài đặt Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ View
        etCourseCode = findViewById(R.id.etCourseCode);
        etCourseName = findViewById(R.id.etCourseName);
        etCredits = findViewById(R.id.etCredits);
        spinnerMajor = findViewById(R.id.spinnerMajor);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        spinnerPrerequisite = findViewById(R.id.spinnerPrerequisite);

        db = AppDatabase.getDatabase(getApplicationContext());
        majorList = new ArrayList<>();
        allCoursesList = new ArrayList<>();

        // 1. Tải danh sách Chuyên ngành (Major) vào Spinner
        loadMajorsIntoSpinner();

        // 2. Kiểm tra mode Add hay Edit
        if (getIntent().hasExtra("COURSE_ID")) {
            isEditMode = true;
            editCourseId = getIntent().getIntExtra("COURSE_ID", -1);
            tvCourseTitle.setText("Edit Course");
            getSupportActionBar().setTitle("Edit Course");
            loadCourseData();
        } else {
            isEditMode = false;
            tvCourseTitle.setText("Add New Course");
            getSupportActionBar().setTitle("Add New Course");
        }

        btnSaveCourse.setOnClickListener(v -> saveCourse());
    }

    private void loadMajorsIntoSpinner() {
        Executors.newSingleThreadExecutor().execute(() -> {
            majorList = db.majorDao().getAllMajors(); // Lấy Major từ CSDL

            List<String> majorNames = majorList.stream()
                    .map(Major::getMajorName)
                    .collect(Collectors.toList());

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, majorNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMajor.setAdapter(adapter);

                // Nếu đang Edit, chọn đúng Major
                if (currentCourse != null) {
                    selectMajorInSpinner();
                }
            });
        });
    }
    private void loadCoursesIntoSpinner() {
        Executors.newSingleThreadExecutor().execute(() -> {
            allCoursesList = db.courseDao().getAllCourses(); // Lấy tất cả môn học

            // Lấy tên các môn học
            List<String> courseNames = allCoursesList.stream()
                    .map(Course::getCourseName) // Dùng tên (hoặc CourseCode)
                    .collect(Collectors.toList());

            // THÊM "None" vào đầu danh sách
            courseNames.add(0, "None (No Prerequisite)");

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, courseNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPrerequisite.setAdapter(adapter);

                // Nếu đang Edit, chọn đúng môn tiên quyết
                if (currentCourse != null) {
                    selectPrerequisiteInSpinner();
                }
            });
        });
    }
    private void loadCourseData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentCourse = db.courseDao().getCourseById(editCourseId);
            runOnUiThread(() -> {
                if (currentCourse != null) {
                    etCourseCode.setText(currentCourse.getCourseCode());
                    etCourseName.setText(currentCourse.getCourseName());
                    etCredits.setText(String.valueOf(currentCourse.getCredits()));

                    if (!majorList.isEmpty()) {
                        selectMajorInSpinner();
                    }
                }
            });
        });
    }

    private void selectMajorInSpinner() {
        for (int i = 0; i < majorList.size(); i++) {
            if (majorList.get(i).getMajorID() == currentCourse.getMajorID()) {
                spinnerMajor.setSelection(i);
                break;
            }
        }
    }
    // --- 8. HÀM MỚI ĐỂ CHỌN MÔN TIÊN QUYẾT TRONG SPINNER ---
    private void selectPrerequisiteInSpinner() {
        Integer prerequisiteId = currentCourse.getPrerequisiteCourseID();

        if (prerequisiteId == null) {
            // Nếu môn này không có môn tiên quyết
            spinnerPrerequisite.setSelection(0); // Chọn "None"
            return;
        }
    }
    private void saveCourse() {
        String code = etCourseCode.getText().toString().trim();
        String name = etCourseName.getText().toString().trim();
        String creditsStr = etCredits.getText().toString().trim();

        // Validation
        if (code.isEmpty() || name.isEmpty() || creditsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerMajor.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Please select a major", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy MajorID từ Spinner
        int selectedMajorIndex = spinnerMajor.getSelectedItemPosition();
        int majorId = majorList.get(selectedMajorIndex).getMajorID();
        int credits = Integer.parseInt(creditsStr);
        // --- 9. LẤY PREREQUISITE ID TỪ SPINNER MỚI ---
        Integer prerequisiteId = null; // Mặc định là null
        int selectedPrerequisiteIndex = spinnerPrerequisite.getSelectedItemPosition();

        // Nếu không chọn "None" (index 0)
        if (selectedPrerequisiteIndex > 0) {
            // Lấy CourseID từ allCoursesList (vị trí là index - 1)
            prerequisiteId = allCoursesList.get(selectedPrerequisiteIndex - 1).getCourseID();
        }

        btnSaveCourse.setEnabled(false);
        final Integer finalPrerequisiteId = prerequisiteId;

        Executors.newSingleThreadExecutor().execute(() -> {
            if (isEditMode) {
                // Logic Sửa (Update)
                currentCourse.setCourseCode(code);
                currentCourse.setCourseName(name);
                currentCourse.setCredits(credits);
                currentCourse.setMajorID(majorId);
                currentCourse.setPrerequisiteCourseID(finalPrerequisiteId);
                db.courseDao().updateCourse(currentCourse);
            } else {
                // Logic Thêm (Create)
                // (Truyền null cho PrerequisiteCourseID)
                db.courseDao().insertCourse(new Course(code, name, credits, majorId, finalPrerequisiteId));
            }

            runOnUiThread(() -> {
                String message = isEditMode ? "Course updated successfully" : "Add new course successful";
                showSuccessDialogAndFinish(message);
            });
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