package com.example.fuportal.core.data.academicAffairs; // (Đặt trong package của bạn)

import android.os.Bundle;
import android.view.View; // Import View
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
import com.example.fuportal.core.data.model.Course;
import com.example.fuportal.core.data.model.Major;
import com.example.fuportal.core.data.model.Semester;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.data.model.AcademicClass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService; // Import
import java.util.concurrent.Executors; // Import
import java.util.stream.Collectors;

public class AddEditClassActivity extends AppCompatActivity {

    // Views
    private EditText etMaxSize;
    private Spinner spinnerCourse, spinnerSemester, spinnerLecturer, spinnerCampus;
    private Button btnSaveClass;
    private TextView tvClassTitle;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;

    private AppDatabase db;
    private boolean isEditMode = false;
    private int editClassId = -1;
    private AcademicClass currentClass;

    // Danh sách dữ liệu cho 4 Spinners
    private List<Course> courseList = new ArrayList<>();
    private List<Semester> semesterList = new ArrayList<>();
    private List<User> lecturerList = new ArrayList<>();
    private List<Campus> campusList = new ArrayList<>();

    // Dùng ExecutorService để quản lý luồng
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_class);

        // --- Xử lý Tràn viền và Toolbar (Giữ nguyên) ---
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // --- Kết thúc Toolbar ---

        // Ánh xạ View
        etMaxSize = findViewById(R.id.etMaxSize);
        spinnerCourse = findViewById(R.id.spinnerCourse);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        spinnerLecturer = findViewById(R.id.spinnerLecturer);
        spinnerCampus = findViewById(R.id.spinnerCampus);
        btnSaveClass = findViewById(R.id.btnSaveClass);
        tvClassTitle = findViewById(R.id.tvClassTitle);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor(); // Khởi tạo Executor

        // --- SỬA LỖI LOGIC: Vô hiệu hóa nút Save ngay từ đầu ---
        btnSaveClass.setEnabled(false);
        // (Chúng ta sẽ bật lại sau khi TẤT CẢ dữ liệu đã tải xong)

        // 1. Tải dữ liệu cho 4 Spinners
        loadAllSpinnerData(); // Hàm này sẽ tự xử lý logic Add/Edit

        // 2. Gán sự kiện cho nút Save
        btnSaveClass.setOnClickListener(v -> saveClass());
    }

    private void loadAllSpinnerData() {
        executorService.execute(() -> {
            // Lấy 4 danh sách từ CSDL
            courseList = db.courseDao().getAllCourses();
            semesterList = db.semesterDao().getAllSemesters();
            lecturerList = db.userDao().getAllLecturers();
            campusList = db.campusDao().getAllCampuses();

            // Lấy tên
            List<String> courseNames = courseList.stream().map(Course::getCourseName).collect(Collectors.toList());
            List<String> semesterNames = semesterList.stream().map(Semester::getSemesterName).collect(Collectors.toList());
            List<String> lecturerNames = lecturerList.stream().map(User::getFullName).collect(Collectors.toList());
            List<String> campusNames = campusList.stream().map(Campus::getCampusName).collect(Collectors.toList());

            // --- SỬA LỖI LOGIC: Xử lý Add/Edit sau khi đã có dữ liệu Spinners ---
            if (getIntent().hasExtra("CLASS_ID")) {
                isEditMode = true;
                editClassId = getIntent().getIntExtra("CLASS_ID", -1);
                currentClass = db.classDao().getClassById(editClassId); // Tải dữ liệu Edit
            } else {
                isEditMode = false;
            }

            // Cập nhật Spinners trên luồng chính
            runOnUiThread(() -> {
                // Đổ dữ liệu vào Spinners
                spinnerCourse.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseNames));
                spinnerSemester.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesterNames));
                spinnerLecturer.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lecturerNames));
                spinnerCampus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, campusNames));

                // Cập nhật tiêu đề (Title)
                if (isEditMode && currentClass != null) {
                    tvClassTitle.setText("Edit Class");
                    getSupportActionBar().setTitle("Edit Class");
                    etMaxSize.setText(String.valueOf(currentClass.getMaxSize()));
                    selectSpinnersData(); // Chọn item cũ
                } else {
                    tvClassTitle.setText("Add New Class");
                    getSupportActionBar().setTitle("Add New Class");
                }

                // --- SỬA LỖI LOGIC: Bật nút Save khi tất cả đã sẵn sàng ---
                btnSaveClass.setEnabled(true);
            });
        });
    }

    // (Hàm loadClassData() không cần thiết nữa, đã gộp vào loadAllSpinnerData)

    // Hàm helper để chọn đúng 4 Spinners khi ở mode Edit
    private void selectSpinnersData() {
        // (Hàm này giữ nguyên, nó sẽ chạy đúng vì được gọi sau khi list đã đầy)
        // 1. Chọn Course
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getCourseID() == currentClass.getCourseID()) {
                spinnerCourse.setSelection(i); break;
            }
        }
        // 2. Chọn Semester
        for (int i = 0; i < semesterList.size(); i++) {
            if (semesterList.get(i).getSemesterID() == currentClass.getSemesterID()) {
                spinnerSemester.setSelection(i); break;
            }
        }
        // 3. Chọn Lecturer
        for (int i = 0; i < lecturerList.size(); i++) {
            if (lecturerList.get(i).getUserCode().equals(currentClass.getLecturerID())) {
                spinnerLecturer.setSelection(i); break;
            }
        }
        // 4. Chọn Campus
        for (int i = 0; i < campusList.size(); i++) {
            if (campusList.get(i).getCampusID() == currentClass.getCampusID()) {
                spinnerCampus.setSelection(i); break;
            }
        }
    }

    private void saveClass() {
        String maxSizeStr = etMaxSize.getText().toString().trim();

        // Validation
        if (maxSizeStr.isEmpty()) {
            etMaxSize.setError("Max size is required");
            etMaxSize.requestFocus();
            return;
        }
        // Kiểm tra xem các Spinner đã tải xong dữ liệu chưa (an toàn)
        if (courseList.isEmpty() || semesterList.isEmpty() || lecturerList.isEmpty() || campusList.isEmpty()) {
            Toast.makeText(this, "Data is still loading, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ID từ 4 Spinners
        int courseId = courseList.get(spinnerCourse.getSelectedItemPosition()).getCourseID();
        int semesterId = semesterList.get(spinnerSemester.getSelectedItemPosition()).getSemesterID();
        String lecturerId = lecturerList.get(spinnerLecturer.getSelectedItemPosition()).getUserCode();
        int campusId = campusList.get(spinnerCampus.getSelectedItemPosition()).getCampusID();
        int maxSize = Integer.parseInt(maxSizeStr);

        btnSaveClass.setEnabled(false); // Vô hiệu hóa nút khi đang lưu

        executorService.execute(() -> {
            if (isEditMode) {
                currentClass.setCourseID(courseId);
                currentClass.setSemesterID(semesterId);
                currentClass.setLecturerID(lecturerId);
                currentClass.setCampusID(campusId);
                currentClass.setMaxSize(maxSize);
                db.classDao().updateClass(currentClass);
            } else {
                db.classDao().insertClass(new AcademicClass(courseId, semesterId, lecturerId, campusId, maxSize));
            }

            runOnUiThread(() -> {
                String message = isEditMode ? "Class updated successfully" : "Add new class successful";
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