package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.Schedule;
import com.example.fuportal.core.data.model.TimeSlot; // Import

import java.util.List;
import java.util.Map; // Import
import java.util.HashMap; // Import

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private OnScheduleActionsListener listener;
    // Map để tra cứu TimeSlot (để hiển thị giờ)
    private Map<Integer, TimeSlot> timeSlotMap;
    // Map để tra cứu tên Thứ
    private Map<Integer, String> dayMap;

    public interface OnScheduleActionsListener {
        void onDeleteClick(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> scheduleList, List<TimeSlot> timeSlotList, OnScheduleActionsListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;

        // Tạo Map tra cứu TimeSlot
        this.timeSlotMap = new HashMap<>();
        for (TimeSlot slot : timeSlotList) {
            this.timeSlotMap.put(slot.getSlotID(), slot);
        }

        // Tạo Map tra cứu Thứ
        this.dayMap = new HashMap<>();
        dayMap.put(2, "Monday");
        dayMap.put(3, "Tuesday");
        dayMap.put(4, "Wednesday");
        dayMap.put(5, "Thursday");
        dayMap.put(6, "Friday");
        dayMap.put(7, "Saturday");
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);

        // Hiển thị tên Thứ
        holder.tvScheduleDay.setText(dayMap.getOrDefault(schedule.getDayOfWeek(), "Unknown Day"));

        // Hiển thị Giờ
        TimeSlot slot = timeSlotMap.get(schedule.getSlotID());
        if (slot != null) {
            holder.tvScheduleSlot.setText(slot.toString()); // Dùng hàm toString() của TimeSlot
        } else {
            holder.tvScheduleSlot.setText("Unknown Slot");
        }

        holder.tvScheduleRoom.setText("Room: " + schedule.getRoomNumber());

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(schedule));
    }

    @Override
    public int getItemCount() {
        return scheduleList == null ? 0 : scheduleList.size();
    }

    public void setSchedules(List<Schedule> newSchedules) {
        this.scheduleList = newSchedules;
        notifyDataSetChanged();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvScheduleDay, tvScheduleSlot, tvScheduleRoom;
        Button btnDelete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScheduleDay = itemView.findViewById(R.id.tvScheduleDay);
            tvScheduleSlot = itemView.findViewById(R.id.tvScheduleSlot);
            tvScheduleRoom = itemView.findViewById(R.id.tvScheduleRoom);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}