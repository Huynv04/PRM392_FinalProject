package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ClassDetail; // <-- DÙNG POJO
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassDetail> classDetailList;
    private OnClassActionsListener listener;

    public interface OnClassActionsListener {
        void onEditClick(ClassDetail classDetail);
        void onDeleteClick(ClassDetail classDetail);
        void onItemClick(ClassDetail classDetail); // <-- THÊM DÒNG NÀY
    }

    public ClassAdapter(List<ClassDetail> classDetailList, OnClassActionsListener listener) {
        this.classDetailList = classDetailList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_list_item, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassDetail classDetail = classDetailList.get(position);

        holder.tvCourseName.setText(classDetail.courseName);
        holder.tvSemesterName.setText(classDetail.semesterName);
        holder.tvLecturerName.setText("Lecturer: " + classDetail.lecturerName);

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(classDetail));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(classDetail));
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
        Button btnEdit, btnDelete;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvLecturerName = itemView.findViewById(R.id.tvLecturerName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}