package com.example.fuportal.core.data.academicAffairs; // (Đặt trong package của bạn)

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.fuportal.core.data.model.Schedule;
import com.example.fuportal.core.data.model.TimeSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddScheduleActivity extends AppCompatActivity {

    private Spinner spinnerDayOfWeek, spinnerTimeSlot;
    private EditText etRoomNumber;
    private Button btnSaveSchedule;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;

    private AppDatabase db;
    private int classId;
    private List<TimeSlot> timeSlotList = new ArrayList<>();
    private List<String> dayList = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_schedule);

        // Lấy ClassID
        classId = getIntent().getIntExtra("CLASS_ID", -1);
        if (classId == -1) {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý Tràn viền
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

        // Ánh xạ View
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek);
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot);
        etRoomNumber = findViewById(R.id.etRoomNumber);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);

        db = AppDatabase.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Tải dữ liệu cho Spinners
        loadSpinnerData();

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());
    }

    private void loadSpinnerData() {
        // 1. Tải danh sách Thứ
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dayList);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(dayAdapter);

        // 2. Tải TimeSlots (từ CSDL)
        btnSaveSchedule.setEnabled(false); // Vô hiệu hóa nút Save
        executorService.execute(() -> {
            timeSlotList = db.timeSlotDao().getAllTimeSlots();

            runOnUiThread(() -> {
                // Dùng hàm toString() của TimeSlot để hiển thị
                ArrayAdapter<TimeSlot> slotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlotList);
                slotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTimeSlot.setAdapter(slotAdapter);

                btnSaveSchedule.setEnabled(true); // Bật lại nút Save
            });
        });
    }

    private void saveSchedule() {
        String room = etRoomNumber.getText().toString().trim();

        if (room.isEmpty()) {
            etRoomNumber.setError("Room number is required");
            etRoomNumber.requestFocus();
            return;
        }

        // Lấy DayOfWeek (2 -> 7)
        int dayOfWeek = spinnerDayOfWeek.getSelectedItemPosition() + 2;

        // Lấy SlotID
        TimeSlot selectedSlot = (TimeSlot) spinnerTimeSlot.getSelectedItem();
        int slotId = selectedSlot.getSlotID();

        btnSaveSchedule.setEnabled(false);

        executorService.execute(() -> {
            db.scheduleDao().insertSchedule(new Schedule(classId, dayOfWeek, slotId, room));

            runOnUiThread(() -> {
                showSuccessDialogAndFinish("Add new schedule successful");
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