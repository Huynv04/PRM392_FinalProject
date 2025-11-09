package com.example.fuportal.core.ui.adapter; // (Đặt trong package của bạn)

import android.content.Context; // <-- Import
import android.graphics.Color; // <-- Importimport android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ScheduleDetail; // Import POJO
import java.util.List;
import androidx.core.content.ContextCompat; // <-- Import

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {

    private List<ScheduleDetail> scheduleList;
    private int userRoleId; // <-- 1. THÊM BIẾN NÀY
    public TimetableAdapter(List<ScheduleDetail> scheduleList, int userRoleId) {
        this.scheduleList = scheduleList;
        this.userRoleId = userRoleId;
    }
    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timetable_item, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        ScheduleDetail detail = scheduleList.get(position);
        holder.tvCourseName.setText(detail.courseName);
        holder.tvTime.setText("Time: " + detail.startTime + " - " + detail.endTime);
        holder.tvRoom.setText("Room: " + detail.roomNumber);
        holder.tvLecturer.setText("Lecturer: " + detail.lecturerName);
        // === THÊM LOGIC MỚI TẠI ĐÂY ===
        Context context = holder.itemView.getContext();

        if (userRoleId == 2) { // 2 = Lecturer
            holder.tvAttendanceStatus.setVisibility(View.GONE); // ẨN ĐI
        } else { // 1 = Student (hoặc các role khác)
            holder.tvAttendanceStatus.setVisibility(View.VISIBLE); // HIỆN RA
            String status = detail.attendanceStatus;

            if ("Present".equals(status)) {
                holder.tvAttendanceStatus.setText("Status: Present");
                holder.tvAttendanceStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            } else if ("Absent".equals(status)) {
                holder.tvAttendanceStatus.setText("Status: Absent");
                holder.tvAttendanceStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            } else {
                holder.tvAttendanceStatus.setText("Status: Not Yet");
                holder.tvAttendanceStatus.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList == null ? 0 : scheduleList.size();
    }

    public void setSchedules(List<ScheduleDetail> newSchedules) {
        this.scheduleList = newSchedules;
        notifyDataSetChanged();
    }

    class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvTime, tvRoom, tvLecturer;
        TextView tvAttendanceStatus; // <-- Ánh xạ

        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvLecturer = itemView.findViewById(R.id.tvLecturer);
            tvAttendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus); // <-- Ánh xạ        }
        }
    }}