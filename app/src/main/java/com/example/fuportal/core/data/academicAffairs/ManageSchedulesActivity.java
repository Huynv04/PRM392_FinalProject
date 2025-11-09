package com.example.fuportal.core.data.academicAffairs; // (Đặt trong package của bạn)

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView; // Import
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
import com.example.fuportal.core.data.model.Schedule;
import com.example.fuportal.core.data.model.TimeSlot; // Import
import com.example.fuportal.core.ui.adapter.ScheduleAdapter; // Import Adapter mới

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageSchedulesActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionsListener {

    private RecyclerView rvSchedules;
    private ScheduleAdapter scheduleAdapter;
    private AppDatabase db;
    private Button btnAddSchedule;
    private Toolbar toolbar;
    private LinearLayout mainLayout;
    private TextView tvClassInfo;

    private int classId; // ID của lớp đang sửa
    private String classInfo; // Tên (để hiển thị)
    private List<TimeSlot> timeSlotList = new ArrayList<>(); // Danh sách Ca học

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_schedules);

        // Lấy ClassID và thông tin từ Intent
        if (getIntent().hasExtra("CLASS_ID")) {
            classId = getIntent().getIntExtra("CLASS_ID", -1);
            classInfo = getIntent().getStringExtra("CLASS_INFO");
        } else {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng nếu không có ID
            return;
        }

        // --- Xử lý UI (Tràn viền, Toolbar) ---
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
        rvSchedules = findViewById(R.id.rvSchedules);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        tvClassInfo = findViewById(R.id.tvClassInfo);

        // Hiển thị tên lớp đang sửa
        tvClassInfo.setText("Class: " + classInfo);

        setupRecyclerView();
        loadTimeSlotsAndSchedules(); // Tải cả hai

        btnAddSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ManageSchedulesActivity.this, AddScheduleActivity.class);
            intent.putExtra("CLASS_ID", classId); // Gửi ClassID sang
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với danh sách TimeSlot (rỗng lúc đầu)
        scheduleAdapter = new ScheduleAdapter(new ArrayList<>(), timeSlotList, this);
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(scheduleAdapter);
    }

    // Tải TimeSlots (cần cho Adapter) và Schedul
    private void loadTimeSlotsAndSchedules() {
        Executors.newSingleThreadExecutor().execute(() -> {
            timeSlotList = db.timeSlotDao().getAllTimeSlots();
            List<Schedule> schedules = db.scheduleDao().getSchedulesForClass(classId);

            runOnUiThread(() -> {
                // Cập nhật lại Adapter với TimeSlots mới
                scheduleAdapter = new ScheduleAdapter(schedules, timeSlotList, this);
                rvSchedules.setAdapter(scheduleAdapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimeSlotsAndSchedules();
    }

    @Override
    public void onDeleteClick(Schedule schedule) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Schedule")
                .setMessage("Are you sure to delete this schedule?")
                .setPositiveButton("Yes", (dialog, which) -> deleteScheduleFromDatabase(schedule))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteScheduleFromDatabase(Schedule schedule) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.scheduleDao().deleteSchedule(schedule);
            runOnUiThread(this::loadTimeSlotsAndSchedules);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}