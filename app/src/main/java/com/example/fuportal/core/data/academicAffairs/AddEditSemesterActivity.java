package com.example.fuportal.core.data.academicAffairs;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.fuportal.core.data.model.Semester;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddEditSemesterActivity extends AppCompatActivity {

    private EditText etSemesterName;
    private TextView tvStartDate, tvEndDate, tvRegStartDate, tvRegEndDate;
    private Button btnSaveSemester;
    private TextView tvSemesterTitle;
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;

    private AppDatabase db;
    private Semester currentSemester;
    private boolean isEditMode = false;
    private int editSemesterId = -1;

    // Biến để lưu trữ 4 ngày
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private Calendar regStartDate = Calendar.getInstance();
    private Calendar regEndDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_semester);

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
        etSemesterName = findViewById(R.id.etSemesterName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvRegStartDate = findViewById(R.id.tvRegStartDate);
        tvRegEndDate = findViewById(R.id.tvRegEndDate);
        btnSaveSemester = findViewById(R.id.btnSaveSemester);
        tvSemesterTitle = findViewById(R.id.tvSemesterTitle);

        db = AppDatabase.getDatabase(getApplicationContext());

        // Cài đặt Date Pickers
        setupDatePickers();

        // Kiểm tra mode Add hay Edit
        if (getIntent().hasExtra("SEMESTER_ID")) {
            isEditMode = true;
            editSemesterId = getIntent().getIntExtra("SEMESTER_ID", -1);
            tvSemesterTitle.setText("Edit Semester");
            getSupportActionBar().setTitle("Edit Semester");
            loadSemesterData();
        } else {
            isEditMode = false;
            tvSemesterTitle.setText("Add New Semester");
            getSupportActionBar().setTitle("Add New Semester");
        }

        btnSaveSemester.setOnClickListener(v -> saveSemester());
    }

    private void setupDatePickers() {
        tvStartDate.setOnClickListener(v -> showDatePickerDialog(startDate, tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePickerDialog(endDate, tvEndDate));
        tvRegStartDate.setOnClickListener(v -> showDatePickerDialog(regStartDate, tvRegStartDate));
        tvRegEndDate.setOnClickListener(v -> showDatePickerDialog(regEndDate, tvRegEndDate));
    }

    private void showDatePickerDialog(Calendar calendar, TextView tv) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // Cập nhật TextView
            tv.setText(dateFormat.format(calendar.getTime()));
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadSemesterData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentSemester = db.semesterDao().getSemesterById(editSemesterId);
            runOnUiThread(() -> {
                if (currentSemester != null) {
                    etSemesterName.setText(currentSemester.getSemesterName());

                    // Cập nhật Calendar và TextView
                    startDate.setTimeInMillis(currentSemester.getStartDate());
                    endDate.setTimeInMillis(currentSemester.getEndDate());
                    regStartDate.setTimeInMillis(currentSemester.getRegistrationStartDate());
                    regEndDate.setTimeInMillis(currentSemester.getRegistrationEndDate());

                    tvStartDate.setText(dateFormat.format(startDate.getTime()));
                    tvEndDate.setText(dateFormat.format(endDate.getTime()));
                    tvRegStartDate.setText(dateFormat.format(regStartDate.getTime()));
                    tvRegEndDate.setText(dateFormat.format(regEndDate.getTime()));
                }
            });
        });
    }

    private void saveSemester() {
        String name = etSemesterName.getText().toString().trim();

        // Validation (thêm kiểm tra ngày)
        if (name.isEmpty() || tvStartDate.getText().equals("Select Date") || tvEndDate.getText().equals("Select Date")
                || tvRegStartDate.getText().equals("Select Date") || tvRegEndDate.getText().equals("Select Date")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveSemester.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            // Lấy 4 ngày (dạng long) từ Calendars
            long start = startDate.getTimeInMillis();
            long end = endDate.getTimeInMillis();
            long regStart = regStartDate.getTimeInMillis();
            long regEnd = regEndDate.getTimeInMillis();

            if (isEditMode) {
                currentSemester.setSemesterName(name);
                currentSemester.setStartDate(start);
                currentSemester.setEndDate(end);
                currentSemester.setRegistrationStartDate(regStart);
                currentSemester.setRegistrationEndDate(regEnd);
                db.semesterDao().updateSemester(currentSemester);
            } else {
                db.semesterDao().insertSemester(new Semester(name, start, end, regStart, regEnd));
            }

            runOnUiThread(() -> {
                String message = isEditMode ? "Semester updated successfully" : "Add new semester successful";
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