package com.example.fuportal.core.data.student;

import android.app.DatePickerDialog; // <-- Import
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView; // <-- Import
import android.widget.Toast;

import java.util.Calendar; // <-- Import
import java.text.SimpleDateFormat; // <-- Import
import java.util.Locale; // <-- Import

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fuportal.R;
import com.example.fuportal.core.ui.adapter.TimetableViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TimetableActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LinearLayout mainLayout;

    private TextView btnSelectWeek; // <-- Sửa thành TextView (hoặc Button)
    private Calendar selectedWeekStart = Calendar.getInstance(); // Lưu ngày bắt đầu của tuần
    private SimpleDateFormat weekFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final String[] tabTitles = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private String userId;
    private int userRoleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timetable);

        // ... (Code xử lý Tràn viền và Toolbar) ...
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
        if (getIntent().hasExtra("USER_ID") && getIntent().hasExtra("USER_ROLE_ID")) {
            userId = getIntent().getStringExtra("USER_ID");
            userRoleId = getIntent().getIntExtra("USER_ROLE_ID", -1);
        } else {
            Toast.makeText(this, "Error: User ID="+userId+" or Role not found:"+userRoleId, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Ánh xạ
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnSelectWeek = findViewById(R.id.btnSelectWeek);

        // 1. Cài đặt ngày mặc định là tuần này
        calculateWeekStart(Calendar.getInstance()); // Tính ngày T2 của tuần này
        setupViewPagerAndTabs(); // Cài đặt ViewPager

        // 2. Xử lý khi nhấn nút chọn tuần
        btnSelectWeek.setOnClickListener(v -> showDatePicker());
    }

    // Hàm hiển thị Lịch
    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);

            // Tính toán lại ngày T2 của tuần được chọn
            calculateWeekStart(newDate);

            // 3. TẠO LẠI ViewPager với tuần mới
            setupViewPagerAndTabs();
        };

        new DatePickerDialog(this, dateSetListener,
                selectedWeekStart.get(Calendar.YEAR),
                selectedWeekStart.get(Calendar.MONTH),
                selectedWeekStart.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Hàm tính toán ngày đầu tuần (Thứ 2)
    private void calculateWeekStart(Calendar selectedDate) {
        // (Set FirstDayOfWeek là THỨ 2)
        int day = selectedDate.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) {
            selectedDate.add(Calendar.DAY_OF_YEAR, -6); // Nếu là CN, lùi 6 ngày
        } else {
            selectedDate.add(Calendar.DAY_OF_YEAR, Calendar.MONDAY - day); // Lùi về T2
        }

        // Đặt giờ, phút, giây về 0 (đầu ngày)
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        this.selectedWeekStart = selectedDate;
    }

    // Cài đặt/Tải lại ViewPager và Tabs
    private void setupViewPagerAndTabs() {
        // Cập nhật text trên nút
        Calendar weekEnd = (Calendar) selectedWeekStart.clone();
        weekEnd.add(Calendar.DAY_OF_YEAR, 6); // T2 + 6 ngày = CN
        btnSelectWeek.setText(
                "Week: " + weekFormat.format(selectedWeekStart.getTime()) +
                        " - " + weekFormat.format(weekEnd.getTime())
        );

        // 4. Tạo Adapter mới VỚI ngày bắt đầu của tuần
        TimetableViewPagerAdapter viewPagerAdapter = new TimetableViewPagerAdapter(
                this,
                selectedWeekStart.getTimeInMillis(),
                userId,     // <-- Truyền vào
                userRoleId    // <-- Truyền vào
        );        viewPager.setAdapter(viewPagerAdapter);

        // 5. Kết nối TabLayout (phải làm lại)
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // (Tùy chọn) Tự động chọn tab của ngày hôm nay
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (today == Calendar.SUNDAY) {
            viewPager.setCurrentItem(6); // Tab Chủ Nhật
        } else {
            viewPager.setCurrentItem(today - 2); // (T2=2 -> index 0)
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}