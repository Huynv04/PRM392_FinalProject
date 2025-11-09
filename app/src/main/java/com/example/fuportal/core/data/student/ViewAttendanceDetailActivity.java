package com.example.fuportal.core.data.student;


import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.example.fuportal.core.data.model.Attendance;
import com.example.fuportal.core.data.model.AttendanceDetail;
import com.example.fuportal.core.data.model.ClassScheduleRule;
import com.example.fuportal.core.data.model.Semester;
import com.example.fuportal.core.data.model.SessionAttendanceDetail;
import com.example.fuportal.core.ui.adapter.AttendanceDetailAdapter;
import com.example.fuportal.core.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

public class ViewAttendanceDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TextView tvClassInfoAttendance;
    private RecyclerView rvAttendanceDetails;

    private AppDatabase db;
    private ExecutorService executorService;
    private AttendanceDetailAdapter adapter;
    private SessionManager sessionManager;

    private int classId;
    private String loggedInStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_attendance_detail);

        // Lấy ID Lớp
        if (getIntent().hasExtra("CLASS_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            String classInfo = getIntent().getStringExtra("CLASS_INFO");
            tvClassInfoAttendance = findViewById(R.id.tvClassInfoAttendance);
            tvClassInfoAttendance.setText(classInfo);
        } else {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy ID Sinh viên
        sessionManager = new SessionManager(getApplicationContext());
        loggedInStudentId = sessionManager.getLoggedInUserCode();
        if (loggedInStudentId == null) {
            Toast.makeText(this, "Error: Student ID not found.", Toast.LENGTH_SHORT).show();
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
        rvAttendanceDetails = findViewById(R.id.rvAttendanceDetails);

        setupRecyclerView();
        loadAttendanceData();
    }

    private void setupRecyclerView() {
        adapter = new AttendanceDetailAdapter(new ArrayList<>());
        rvAttendanceDetails.setLayoutManager(new LinearLayoutManager(this));
        rvAttendanceDetails.setAdapter(adapter);
    }

    // Hàm này thay thế hàm loadAllSessions cũ
    private void loadAttendanceData() {
        // Lấy ngày hiện tại
        final long currentMillis = System.currentTimeMillis();

        executorService.execute(() -> {
            try {
                // Bước 1: Lấy các quy tắc lịch (T2, T4, Giờ, Ngày kỳ)
                List<ClassScheduleRule> rules = db.scheduleDao().getClassScheduleRules(classId);

                if (rules.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy lịch học cho lớp này.", Toast.LENGTH_LONG).show());
                    return;
                }

                // Bước 2: Lấy tất cả các bản ghi điểm danh đã có
                List<Attendance> records = db.scheduleDao().getAllAttendanceRecords(classId, loggedInStudentId);

                // Chuyển danh sách records thành Map để tra cứu nhanh (millis -> status)
                Map<Long, String> attendanceMap = new HashMap<>();
                for (Attendance record : records) {
                    attendanceMap.put(record.getSessionDate(), record.getStatus());
                }

                // Bước 3: Tạo danh sách tất cả các buổi học thực tế (Date Generation)
                List<SessionAttendanceDetail> allSessions = generateSessions(rules, attendanceMap);

                // Cập nhật UI
                runOnUiThread(() -> {
                    adapter.setAttendanceDetails(allSessions);
                });

            } catch (Exception e) {
                // Lỗi SQL hoặc Logic
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
    // --- HÀM TẠO SESSIONS THỰC TẾ (CHẠY TRÊN BACKGROUND THREAD) ---
    private List<SessionAttendanceDetail> generateSessions(List<ClassScheduleRule> rules, Map<Long, String> attendanceMap) {
        if (rules.isEmpty()) return new ArrayList<>();

        List<SessionAttendanceDetail> sessions = new ArrayList<>();

        // Lấy ngày bắt đầu và kết thúc từ rule đầu tiên
        ClassScheduleRule sampleRule = rules.get(0);
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(sampleRule.semesterStartDate);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(sampleRule.semesterEndDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // VD: T2

        while (current.getTimeInMillis() <= end.getTimeInMillis()) {
            final int currentDayOfWeek = current.get(Calendar.DAY_OF_WEEK);

            for (ClassScheduleRule rule : rules) {

                // So sánh ngày trong Calendar với Quy tắc CSDL (2=T2, 7=T7)
                if (currentDayOfWeek == rule.dayOfWeek) {

                    long sessionDateMillis = current.getTimeInMillis();

                    SessionAttendanceDetail session = new SessionAttendanceDetail();
                    session.roomNumber = rule.roomNumber;
                    session.sessionDateMillis = sessionDateMillis;

                    // Tạo chuỗi hiển thị
                    session.sessionDateStr = dayNameFormat.format(current.getTime()) + " (" + dateFormat.format(current.getTime()) + ")";
                    session.timeSlotStr = rule.startTime.substring(0, 5) + " - " + rule.endTime.substring(0, 5);

                    // Tra cứu Status
                    String status = attendanceMap.get(sessionDateMillis);
                    session.attendanceStatus = (status != null && !status.isEmpty()) ? status : "Not Yet";

                    sessions.add(session);
                }
            }

            // Chuyển sang ngày tiếp theo
            current.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Sắp xếp theo ngày (từ cũ đến mới)
        Collections.sort(sessions, Comparator.comparingLong(s -> s.sessionDateMillis));
        return sessions;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}