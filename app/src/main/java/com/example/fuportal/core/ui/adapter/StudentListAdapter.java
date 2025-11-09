package com.example.fuportal.core.ui.adapter; // (Package của bạn)

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.User; // Import User (Student)
import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

    private List<User> studentList;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onStudentClick(User student);
    }

    public StudentListAdapter(List<User> studentList, OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User student = studentList.get(position);
        holder.tvStudentCode.setText(student.getUserCode()); // Mã số SV
        holder.tvStudentName.setText(student.getFullName()); // Họ tên

        holder.itemView.setOnClickListener(v -> listener.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList == null ? 0 : studentList.size();
    }

    public void setStudents(List<User> newStudents) {
        this.studentList = newStudents;
        notifyDataSetChanged();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentCode, tvStudentName;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
        }
    }
}