package com.example.fuportal.core.ui.adapter;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.fuportal.core.data.student.fragment.TimetableDayFragment;
import java.util.concurrent.TimeUnit; // Import

public class TimetableViewPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_DAYS = 7;
    private long startOfWeekMillis; // Ngày T2 (dạng long)
    private String userId;
    private int userRoleId;

    // --- SỬA CONSTRUCTOR ---
    public TimetableViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                     long startOfWeekMillis,
                                     String userId,
                                     int userRoleId) {
        super(fragmentActivity);
        this.startOfWeekMillis = startOfWeekMillis;
        this.userId = userId;
        this.userRoleId = userRoleId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // position 0 = T2, 1 = T3, ..., 6 = CN

        // Tính ngày cụ thể (long) cho tab này
        long dateForDayMillis = startOfWeekMillis + TimeUnit.DAYS.toMillis(position);

        // Truyền ngày cụ thể này cho Fragment
        return TimetableDayFragment.newInstance(dateForDayMillis, userId, userRoleId);    }

    @Override
    public int getItemCount() {
        return NUM_DAYS;
    }
}