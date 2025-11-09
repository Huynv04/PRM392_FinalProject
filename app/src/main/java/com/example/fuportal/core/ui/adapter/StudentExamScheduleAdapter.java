package com.example.fuportal.core.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ExamScheduleDetail; // Dùng POJO chung

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentExamScheduleAdapter extends RecyclerView.Adapter<StudentExamScheduleAdapter.ExamViewHolder> {

    private List<ExamScheduleDetail> examList;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public StudentExamScheduleAdapter(Context context, List<ExamScheduleDetail> examList) {
        this.context = context;
        this.examList = examList;
    }

    public void setExamList(List<ExamScheduleDetail> examList) {
        this.examList = examList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tải layout mới (student_exam_schedule_item)
        View view = LayoutInflater.from(context).inflate(R.layout.student_exam_schedule_item, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        ExamScheduleDetail exam = examList.get(position);

        // 1. Course & Semester
        holder.tvExamCourse.setText(exam.getCourseCode() + " - " + exam.getSemesterName());

        // 2. Date & Time
        String dateString = dateFormat.format(new Date(exam.getExamDate()));
        String timeString = exam.getStartTime().substring(0, 5) + " - " + exam.getEndTime().substring(0, 5);
        holder.tvExamDateTime.setText("Date: " + dateString + " | Time: " + timeString);

        // 3. Room & Invigilator
        String invigilator = exam.getInvigilatorName();
        if (invigilator == null || invigilator.isEmpty()) {
            invigilator = "Unassigned";
        }

        // Gán vào tvRoomNumber và tvInvigilator
        holder.tvRoomNumber.setText("Room: " + exam.getRoomNumber());
        holder.tvInvigilator.setText("Invigilator: " + invigilator);
    }

    @Override
    public int getItemCount() {
        return examList != null ? examList.size() : 0;
    }

    // --- ViewHolder cho Student (KHÔNG có Button) ---
    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        // Ánh xạ 4 TextView có trong layout student_exam_schedule_item.xml
        TextView tvExamCourse, tvExamDateTime, tvRoomNumber, tvInvigilator;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamCourse = itemView.findViewById(R.id.tvExamCourse);
            tvExamDateTime = itemView.findViewById(R.id.tvExamDateTime);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvInvigilator = itemView.findViewById(R.id.tvInvigilator);
            // KHÔNG CẦN ÁNH XẠ btnEdit/btnDelete
        }
    }
}