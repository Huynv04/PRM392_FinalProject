package com.example.fuportal.core.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.AttendanceDetail;
import com.example.fuportal.core.data.model.SessionAttendanceDetail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceDetailAdapter extends RecyclerView.Adapter<AttendanceDetailAdapter.AttendanceViewHolder> {

    private List<SessionAttendanceDetail> attendanceList; // <-- SỬ DỤNG POJO MỚI
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // Thứ (Mon, Tue...)

    public AttendanceDetailAdapter(List<SessionAttendanceDetail> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_detail_item, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        SessionAttendanceDetail detail = attendanceList.get(position); // <-- SỬ DỤNG POJO MỚI
                Context context = holder.itemView.getContext();

        holder.tvSessionDay.setText(detail.sessionDateStr); // Thứ và Ngày (VD: T2 (04/09/2025))
        holder.tvSessionTime.setText(detail.timeSlotStr);   // Giờ (VD: 07:30 - 09:00)
        holder.tvSessionRoom.setText(detail.roomNumber);

        // 2. Xử lý màu Status
        String status = detail.attendanceStatus;

        if ("Present".equals(status)) {
            holder.tvAttendanceStatus.setText("Present");
            holder.tvAttendanceStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else if ("Absent".equals(status)) {
            holder.tvAttendanceStatus.setText("Absent");
            holder.tvAttendanceStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            // Not Yet (Null)
            holder.tvAttendanceStatus.setText("Not Yet");
            holder.tvAttendanceStatus.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    // Helper: Chuyển số (2=T2) thành chữ
    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case 2: return "Mon";
            case 3: return "Tue";
            case 4: return "Wed";
            case 5: return "Thu";
            case 6: return "Fri";
            case 7: return "Sat";
            default: return "";
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList == null ? 0 : attendanceList.size();
    }

    public void setAttendanceDetails(List<SessionAttendanceDetail> newDetails) {
        this.attendanceList = newDetails;
        notifyDataSetChanged();
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionDay, tvSessionTime, tvSessionRoom, tvAttendanceStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionDay = itemView.findViewById(R.id.tvSessionDay);
            tvSessionTime = itemView.findViewById(R.id.tvSessionTime);
            tvSessionRoom = itemView.findViewById(R.id.tvSessionRoom);
            tvAttendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus);
        }
    }
}