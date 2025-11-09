package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ClassDetail; // Dùng POJO
import java.util.List;

public class LecturerClassAdapter extends RecyclerView.Adapter<LecturerClassAdapter.ClassViewHolder> {

    private List<ClassDetail> classDetailList;
    private OnClassClickListener listener;

    // Interface chỉ xử lý 1 cú click
    public interface OnClassClickListener {
        void onItemClick(ClassDetail classDetail);
    }

    public LecturerClassAdapter(List<ClassDetail> classDetailList, OnClassClickListener listener) {
        this.classDetailList = classDetailList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lecturer_class_item, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassDetail classDetail = classDetailList.get(position);
        holder.tvCourseName.setText(classDetail.courseName);
        holder.tvSemesterName.setText(classDetail.semesterName);
        holder.tvCampusName.setText(classDetail.campusName);

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
        TextView tvCourseName, tvSemesterName, tvCampusName;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvCampusName = itemView.findViewById(R.id.tvCampusName);
        }
    }
}