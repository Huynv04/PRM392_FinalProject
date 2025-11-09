package com.example.fuportal.core.ui.adapter; //

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ClassDetail; // Dùng POJO
import java.util.List;

public class CourseRegistrationAdapter extends RecyclerView.Adapter<CourseRegistrationAdapter.ClassViewHolder> {

    private List<ClassDetail> classDetailList;
    private OnRegisterClickListener listener;

    public interface OnRegisterClickListener {
        void onRegisterClick(ClassDetail classDetail);
        void onItemClick(ClassDetail classDetail);
    }

    public CourseRegistrationAdapter(List<ClassDetail> classDetailList, OnRegisterClickListener listener) {
        this.classDetailList = classDetailList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_registration_item, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassDetail classDetail = classDetailList.get(position);

        holder.tvCourseName.setText(classDetail.courseName);
        holder.tvSemesterName.setText(classDetail.semesterName);
        holder.tvLecturerName.setText("Lecturer: " + classDetail.lecturerName);

        // Gán sự kiện cho nút Register
        holder.btnRegister.setOnClickListener(v -> listener.onRegisterClick(classDetail));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(classDetail));
    }

    @Override
    public int getItemCount() {
        return classDetailList == null ? 0 : classDetailList.size();
    }

    public void setClasses(List<ClassDetail> newClassDetails) {
        this.classDetailList = newClassDetails;
        notifyDataSetChanged();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvSemesterName, tvLecturerName;
        Button btnRegister;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvLecturerName = itemView.findViewById(R.id.tvLecturerName);
            btnRegister = itemView.findViewById(R.id.btnRegister);
        }
    }
}