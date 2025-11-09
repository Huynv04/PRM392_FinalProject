package com.example.fuportal.core.ui.adapter; // (Package của bạn)

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.User; // Import User

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<User> studentList;
    // Map để lưu trạng thái điểm danh: <StudentId, "Present" or "Absent">
    private Map<String, String> attendanceStatusMap;

    public AttendanceAdapter(List<User> studentList, Map<String, String> initialStatusMap) {
        this.studentList = studentList;
        this.attendanceStatusMap = initialStatusMap;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_student_item, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        User student = studentList.get(position);
        holder.tvStudentCode.setText(student.getUserCode());
        holder.tvStudentName.setText(student.getFullName());

        // 1. Tắt listener tạm thời (tránh kích hoạt khi cuộn)
        holder.rgAttendance.setOnCheckedChangeListener(null);

        // 2. Load trạng thái đã lưu
        String status = attendanceStatusMap.get(student.getUserCode());
        if ("Present".equals(status)) {
            holder.rbPresent.setChecked(true);
        } else if ("Absent".equals(status)) {
            holder.rbAbsent.setChecked(true);
        } else {
            // "Not Yet"
            holder.rgAttendance.clearCheck();
        }

        // 3. Gán listener trở lại
        holder.rgAttendance.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPresent) {
                attendanceStatusMap.put(student.getUserCode(), "Present");
            } else if (checkedId == R.id.rbAbsent) {
                attendanceStatusMap.put(student.getUserCode(), "Absent");
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList == null ? 0 : studentList.size();
    }

    // Hàm để Activity lấy dữ liệu đã điểm danh
    public Map<String, String> getAttendanceData() {
        return this.attendanceStatusMap;
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentCode, tvStudentName;
        RadioGroup rgAttendance;
        RadioButton rbPresent, rbAbsent;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            rgAttendance = itemView.findViewById(R.id.rgAttendance);
            rbPresent = itemView.findViewById(R.id.rbPresent);
            rbAbsent = itemView.findViewById(R.id.rbAbsent);
        }
    }
}