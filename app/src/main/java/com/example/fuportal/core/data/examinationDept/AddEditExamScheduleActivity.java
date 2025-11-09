package com.example.fuportal.core.data.examinationDept;

import android.app.DatePickerDialog;
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
import com.example.fuportal.core.data.model.AcademicClass;
import com.example.fuportal.core.data.model.Course;
import com.example.fuportal.core.data.model.ExamSchedule;
import com.example.fuportal.core.data.model.Semester;
import com.example.fuportal.core.data.model.TimeSlot;
import com.example.fuportal.core.data.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddEditExamScheduleActivity extends AppCompatActivity {

    private EditText etRoomNumber;
    private Spinner spinnerClass, spinnerTimeSlot, spinnerInvigilator;
    private TextView tvExamDate, tvExamTitle;
    private Button btnSaveExam;
    private CoordinatorLayout mainLayout;
    private Toolbar toolbar;

    private AppDatabase db;
    private ExecutorService executorService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private Calendar examDate = Calendar.getInstance();
    private ExamSchedule currentExam;
    private boolean isEditMode = false;
    private int editExamId = -1;

    // Danh sách dữ liệu cho Spinners
    private List<AcademicClass> classList = new ArrayList<>();
    private List<TimeSlot> timeSlotList = new ArrayList<>();
    private List<User> invigilatorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_exam);

        // Xử lý Tràn viền
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ Toolbar và cài đặt
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ View
        etRoomNumber = findViewById(R.id.etRoomNumber);
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot);
        spinnerInvigilator = findViewById(R.id.spinnerInvigilator);
        tvExamDate = findViewById(R.id.tvExamDate);
        btnSaveExam = findViewById(R.id.btnSaveExam);
        tvExamTitle = findViewById(R.id.tvExamTitle);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Tải dữ liệu chính
        loadAllSpinnerData();

        // Kiểm tra mode Add hay Edit
        if (getIntent().hasExtra("EXAM_ID")) {
            isEditMode = true;
            editExamId = getIntent().getIntExtra("EXAM_ID", -1);
            tvExamTitle.setText("Edit Exam Slot");
            getSupportActionBar().setTitle("Edit Exam Slot");
            loadExamData(); // Tải dữ liệu cũ
        } else {
            setCalendarToDay(examDate);
            updateDateText();
            tvExamTitle.setText("Add Exam Slot");
            getSupportActionBar().setTitle("Add Exam Slot");
        }

        // Sự kiện
        tvExamDate.setOnClickListener(v -> showDatePicker());
        btnSaveExam.setOnClickListener(v -> saveExamSchedule());
    }
    private void setCalendarToDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    private void loadAllSpinnerData() {
        btnSaveExam.setEnabled(false);
        executorService.execute(() -> {
            // Lấy dữ liệu
            classList = db.classDao().getAllClasses();
            timeSlotList = db.timeSlotDao().getAllTimeSlots();
            invigilatorList = db.userDao().getAllStaffAndLecturers();

            // Tạo danh sách hiển thị cho Class Spinner
            List<String> classNames = classList.stream()
                    .map(c -> getCourseInfo(c.getCourseID(), c.getSemesterID())) // Helper
                    .collect(Collectors.toList());
            List<String> invigilatorNames = invigilatorList.stream()
                    .map(User::getFullName)
                    .collect(Collectors.toList());

            // Thêm tùy chọn "None" cho Invigilator
            invigilatorNames.add(0, "None / Auto Assign");

            runOnUiThread(() -> {
                // Đổ dữ liệu vào Spinners
                spinnerClass.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames));
                spinnerTimeSlot.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlotList));
                spinnerInvigilator.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, invigilatorNames));

                btnSaveExam.setEnabled(true);
                if (currentExam != null) {
                    selectSpinnersData(); // Chọn item nếu đang ở Edit mode
                }
            });
        });
    }

    private void loadExamData() {
        executorService.execute(() -> {
            ExamSchedule exam = db.examScheduleDao().getExamScheduleById(editExamId);
            currentExam = exam;

            runOnUiThread(() -> {
                if (currentExam != null) {
                    // Cập nhật ngày thi
                    examDate.setTimeInMillis(currentExam.getExamDate());
                    updateDateText();

                    etRoomNumber.setText(currentExam.getRoomNumber());
                    selectSpinnersData();
                }
            });
        });
    }

    // Hàm helper để chọn giá trị cũ trong Spinner khi Edit
    private void selectSpinnersData() {
        // Chọn Class
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getClassID() == currentExam.getClassID()) {
                spinnerClass.setSelection(i); break;
            }
        }
        // Chọn TimeSlot
        for (int i = 0; i < timeSlotList.size(); i++) {
            if (timeSlotList.get(i).getStartTime().equals(currentExam.getStartTime())) {
                spinnerTimeSlot.setSelection(i); break;
            }
        }
        // Chọn Invigilator
        if (currentExam.getInvigilatorID() != null) {
            for (int i = 0; i < invigilatorList.size(); i++) {
                if (invigilatorList.get(i).getUserCode().equals(currentExam.getInvigilatorID())) {
                    // +1 vì index 0 là "None / Auto Assign"
                    spinnerInvigilator.setSelection(i + 1);
                    break;
                }
            }
        } else {
            spinnerInvigilator.setSelection(0); // Chọn "None"
        }
    }

    // Hàm helper để hiển thị Course/Semester (Chạy trên BACKGROUND)
    private String getCourseInfo(int courseId, int semesterId) {
        Course course = db.courseDao().getCourseById(courseId);
        Semester semester = db.semesterDao().getSemesterById(semesterId);
        if (course != null && semester != null) {
            return course.getCourseCode() + " - " + semester.getSemesterName();
        }
        return "N/A";
    }

    // Hàm hiển thị Date Picker
    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            examDate.set(year, month, dayOfMonth);
            updateDateText();
        };

        new DatePickerDialog(this, dateSetListener,
                examDate.get(Calendar.YEAR),
                examDate.get(Calendar.MONTH),
                examDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateText() {
        tvExamDate.setText(dateFormat.format(examDate.getTime()));
    }

    private void saveExamSchedule() {
        String room = etRoomNumber.getText().toString().trim();

        // Validation
        if (room.isEmpty()) {
            Toast.makeText(this, "Room number is required", Toast.LENGTH_SHORT).show();
            etRoomNumber.requestFocus();
            return;
        }

        // Lấy dữ liệu
        int classIndex = spinnerClass.getSelectedItemPosition();
        TimeSlot selectedSlot = (TimeSlot) spinnerTimeSlot.getSelectedItem();
        int invigilatorIndex = spinnerInvigilator.getSelectedItemPosition();

        int classId = classList.get(classIndex).getClassID();

        String invigilatorId;
        if (invigilatorIndex > 0) {
            // Lấy Invigilator từ list (bỏ qua index 0 là 'None')
            invigilatorId = invigilatorList.get(invigilatorIndex - 1).getUserCode();
        } else {
            invigilatorId = null;
        }

        long examDateMillis = examDate.getTimeInMillis();
        String startTime = selectedSlot.getStartTime();
        String endTime = selectedSlot.getEndTime();

        btnSaveExam.setEnabled(false);

        executorService.execute(() -> {
            try {
                ExamSchedule examToSave = new ExamSchedule(classId, examDateMillis, startTime, endTime, room, invigilatorId);

                if (isEditMode) {
                    examToSave.setExamID(editExamId);
                    db.examScheduleDao().updateExamSchedule(examToSave);
                } else {
                    db.examScheduleDao().insertExamSchedule(examToSave);
                }

                // Trả về RESULT_OK cho màn hình danh sách (ManageExamSchedulesActivity)
                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    showSuccessDialogAndFinish("Exam slot saved successfully.");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error saving exam: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnSaveExam.setEnabled(true);
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