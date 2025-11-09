package com.example.fuportal.core.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ExamScheduleDetail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamScheduleAdapter extends RecyclerView.Adapter<ExamScheduleAdapter.ExamViewHolder> {

    private List<ExamScheduleDetail> examList;
    private Context context;
    private OnItemActionListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnItemActionListener {
        void onEditClick(ExamScheduleDetail exam);
        void onDeleteClick(ExamScheduleDetail exam);
    }

    public ExamScheduleAdapter(Context context, OnItemActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setExamList(List<ExamScheduleDetail> examList) {
        this.examList = examList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exam_schedule_item, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        ExamScheduleDetail exam = examList.get(position);

        // Dạng: SWP391 - Fall 2025
        holder.tvCourseName.setText(exam.getCourseCode() + " - " + exam.getSemesterName());

        // Dạng: Date: 20/12/2025 | Time: 08:00 - 09:30
        String dateString = dateFormat.format(new Date(exam.getExamDate()));
        String timeString = exam.getStartTime().substring(0, 5) + " - " + exam.getEndTime().substring(0, 5);
        holder.tvExamDateTime.setText("Date: " + dateString + " | " + timeString);

        // Phòng thi & Invigilator
        String invigilator = exam.getInvigilatorName();
        if (invigilator == null || invigilator.isEmpty()) {
            invigilator = "Unassigned";
        }
        String roomInvigilatorInfo = "Room: " + exam.getRoomNumber() +
                " | Invigilator: " + invigilator;
        holder.tvExamRoom.setText(roomInvigilatorInfo);


        // Xử lý sự kiện Edit/Delete
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(exam));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(exam));
    }

    @Override
    public int getItemCount() {
        return examList != null ? examList.size() : 0;
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvExamDateTime, tvExamRoom; // <-- ĐÃ THÊM tvRoomNumber
        Button btnEdit, btnDelete;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvExamCourse);
            tvExamDateTime = itemView.findViewById(R.id.tvExamDateTime);

            // === BỔ SUNG DÒNG BỊ THIẾU ===
            tvExamRoom = itemView.findViewById(R.id.tvExamRoom); // <-- Dòng gây lỗi
            // ============================

             btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}