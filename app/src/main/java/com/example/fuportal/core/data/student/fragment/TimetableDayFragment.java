package com.example.fuportal.core.data.student.fragment;

import android.content.Context; // <-- Import
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater; // <-- Import
import android.view.View; // <-- Import
import android.view.ViewGroup; // <-- Import
import android.widget.Toast;
import androidx.annotation.NonNull; // <-- Import
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // <-- Import
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.ScheduleDetail;
import com.example.fuportal.core.ui.adapter.TimetableAdapter;
import com.example.fuportal.core.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.Calendar;

public class TimetableDayFragment extends Fragment {

    private static final String ARG_SELECTED_DATE = "selected_date";
    private static final String ARG_ROLE_ID = "user_role_id";
    private static final String ARG_USER_ID = "user_id";
    private int userRoleId; // <-- SỬA THÀNH INT
        private String userId; // "SE180001" hoặc "LEC001"
    private static final String TAG = "TimetableDebug";

    private long selectedDateMillis;

    private RecyclerView rvTimetable;
    private TimetableAdapter adapter;
    private AppDatabase db;
    private SessionManager sessionManager;
    private String loggedInStudentId;

    // === SỬA LẠI HÀM newInstance ===
    public static TimetableDayFragment newInstance(long selectedDateMillis, String userId, int userRoleId) {
        TimetableDayFragment fragment = new TimetableDayFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SELECTED_DATE, selectedDateMillis);
        args.putString(ARG_USER_ID, userId);
        args.putInt(ARG_ROLE_ID, userRoleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDateMillis = getArguments().getLong(ARG_SELECTED_DATE);
            userId = getArguments().getString(ARG_USER_ID); // Lấy ID
            userRoleId = getArguments().getInt(ARG_ROLE_ID, -1); // Lấy Role
        }
        db = AppDatabase.getDatabase(getContext());
     }

    // === HÀM BỊ THIẾU NẰM Ở ĐÂY ===
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Tải layout (XML) của Fragment
        View view = inflater.inflate(R.layout.fragment_timetable_day, container, false);

        // 2. Ánh xạ RecyclerView
        rvTimetable = view.findViewById(R.id.rvTimetable);

        // 3. Cài đặt RecyclerView
        setupRecyclerView();

        // 4. BẮT ĐẦU TẢI LỊCH
        // (loadSchedule() sẽ lấy ID từ sessionManager)
        loadSchedule();

        return view;
    }

    // === BẠN CŨNG THIẾU HÀM NÀY ===
    private void setupRecyclerView() {
        adapter = new TimetableAdapter(new ArrayList<>(), userRoleId);
        rvTimetable.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTimetable.setAdapter(adapter);
    }

// (Trong file TimetableDayFragment.java)

    private void loadSchedule() {
        // Lấy ID sinh viên (phải lấy ở đây để đảm bảo an toàn)
        if (userId == null || selectedDateMillis == 0 || userRoleId == -1) {
            Log.e(TAG, "Lỗi: ID, Ngày, hoặc Role bị null!");
            return;
        }

        // Tính toán dayOfWeek
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDateMillis);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        Log.d(TAG, "--- Đang tải lịch cho ---");
        Log.d(TAG, "Role: " + userRoleId);
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "DayOfWeek: " + dayOfWeek);

        Executors.newSingleThreadExecutor().execute(() -> {
            final List<ScheduleDetail> schedules; // Khai báo ở ngoài
            try {
                // Logic gọi DAO (Đã đúng)
                if (userRoleId == 1) { // Student
                    schedules = db.scheduleDao().getStudentScheduleForDay(
                            userId,
                            dayOfWeek,
                            selectedDateMillis
                    );
                } else if (userRoleId == 2) { // Lecturer
                    schedules = db.scheduleDao().getLecturerScheduleForDay(
                            userId,
                            dayOfWeek,
                            selectedDateMillis
                    );
                } else {
                    schedules = new ArrayList<>();
                }

                Log.d(TAG, "Query Succeeded. Số lịch học tìm thấy: " + schedules.size());

                // === PHẦN BỊ THIẾU NẰM Ở ĐÂY ===
                // Cập nhật UI (phải nằm TRONG try-catch)
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter.setSchedules(schedules);
                    });
                }
                // ===============================

            } catch (Exception e) {
                // Bắt lỗi SQL (nếu có)
                Log.e(TAG, "LỖI KHI TRUY VẤN CSDL!", e);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi SQL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}