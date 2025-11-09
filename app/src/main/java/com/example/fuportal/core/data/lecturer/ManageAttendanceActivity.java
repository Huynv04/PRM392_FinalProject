package com.example.fuportal.core.data.lecturer;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.Attendance;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.ui.adapter.AttendanceAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageAttendanceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TextView tvClassInfo, tvSelectDate;
    private RecyclerView rvStudentAttendance;
    private Button btnSaveAttendance;

    private AppDatabase db;
    private ExecutorService executorService;
    private AttendanceAdapter adapter;

    private int classId;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault()); // (VD: "Thứ Hai")
    private List<User> studentList = new ArrayList<>(); // Danh sách SV của lớp
    private Map<String, String> attendanceStatusMap = new HashMap<>(); // Trạng thái điểm danh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_attendance);

        // Lấy ID Lớp
        if (getIntent().hasExtra("CLASS_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            String classInfo = getIntent().getStringExtra("CLASS_INFO");
            tvClassInfo = findViewById(R.id.tvClassInfo);
            tvClassInfo.setText(classInfo);
        } else {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý Tràn viền và Toolbar
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

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Ánh xạ
        tvSelectDate = findViewById(R.id.tvSelectDate);
        rvStudentAttendance = findViewById(R.id.rvStudentAttendance);
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);
        btnSaveAttendance.setEnabled(false); // Vô hiệu hóa nút Save ban đầu

        // Cài đặt
        setupRecyclerView();

        // Mặc định chọn ngày hôm nay
        setCalendarToDay(selectedDate);
        updateDateText();
        loadStudentList();
          checkIfClassIsScheduledOnThisDay(selectedDate);

        // Sự kiện
        tvSelectDate.setOnClickListener(v -> showDatePicker());
        btnSaveAttendance.setOnClickListener(v -> saveAttendanceData());
    }

    private void setupRecyclerView() {
        adapter = new AttendanceAdapter(studentList, attendanceStatusMap);
        rvStudentAttendance.setLayoutManager(new LinearLayoutManager(this));
        rvStudentAttendance.setAdapter(adapter);
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            setCalendarToDay(newDate); // Chuẩn hóa
            selectedDate = newDate;
            updateDateText();
            // 2. Kiểm tra xem ngày này có lịch học không
            checkIfClassIsScheduledOnThisDay(selectedDate);
        };
        new DatePickerDialog(this, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Cập nhật text hiển thị ngày
    private void updateDateText() {
        tvSelectDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    // Đặt giờ phút giây về 0
    private void setCalendarToDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    // --- HÀM KIỂM TRA LOGIC MỚI ---
    private void checkIfClassIsScheduledOnThisDay(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK); // 1=CN, 2=T2...
        long selectedDateMillis = date.getTimeInMillis();

        executorService.execute(() -> {
            // 1. Kiểm tra xem có lịch vào Thứ này không
            int scheduleCount = db.scheduleDao().checkScheduleExists(classId, dayOfWeek);

            // 2. Lấy điểm danh (nếu có)
            List<Attendance> existingAttendance = db.attendanceDao().getAttendanceForClassByDate(classId, selectedDateMillis);

            // 3. Tạo Map trạng thái mới
            Map<String, String> newStatusMap = new HashMap<>();
            for (Attendance att : existingAttendance) {
                newStatusMap.put(att.getStudentID(), att.getStatus());
            }

            runOnUiThread(() -> {
                if (scheduleCount > 0) {
                    // HỢP LỆ: Cho phép điểm danh
                    attendanceStatusMap.clear();
                    attendanceStatusMap.putAll(newStatusMap);
                    adapter.notifyDataSetChanged(); // Cập nhật trạng thái check
                    btnSaveAttendance.setEnabled(true); // Bật nút Save
                } else {
                    // KHÔNG HỢP LỆ: Báo lỗi
                    String dayName = dayOfWeekFormat.format(date.getTime());
                    Toast.makeText(this, "Error: This class does not meet on " + dayName, Toast.LENGTH_LONG).show();

                    // Xóa danh sách điểm danh (nếu đang hiển thị)
                    attendanceStatusMap.clear();
                    adapter.notifyDataSetChanged();
                    btnSaveAttendance.setEnabled(false); // Tắt nút Save
                }
            });
        });
    }
    private void loadStudentList() {
        executorService.execute(() -> {
            if (studentList.isEmpty()) {
                studentList.addAll(db.userDao().getStudentsByClassId(classId));
            }
            runOnUiThread(() -> {
                // Cập nhật adapter với danh sách SV
                adapter = new AttendanceAdapter(studentList, attendanceStatusMap);
                rvStudentAttendance.setAdapter(adapter);
            });
        });
    }

    private void saveAttendanceData() {
        long sessionDateMillis = selectedDate.getTimeInMillis();
        Map<String, String> dataToSave = adapter.getAttendanceData();

        btnSaveAttendance.setEnabled(false); // Vô hiệu hóa nút

        executorService.execute(() -> {
            try {
                // Lặp qua Map và lưu vào CSDL
                for (Map.Entry<String, String> entry : dataToSave.entrySet()) {
                    String studentId = entry.getKey();
                    String status = entry.getValue();

                    Attendance attendanceRecord = new Attendance(studentId, classId, sessionDateMillis, status);

                    // Dùng UPSERT (Insert hoặc Replace)
                    db.attendanceDao().upsertAttendance(attendanceRecord);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Attendance saved successfully!", Toast.LENGTH_SHORT).show();
                    btnSaveAttendance.setEnabled(true);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error saving attendance.", Toast.LENGTH_SHORT).show();
                    btnSaveAttendance.setEnabled(true);
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}