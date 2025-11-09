package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.Faculty;
import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {

    private List<Faculty> facultyList;
    private OnFacultyActionsListener listener;

    // Interface để gửi sự kiện click về Activity
    public interface OnFacultyActionsListener {
        void onEditClick(Faculty faculty);
        void onDeleteClick(Faculty faculty);
    }

    public FacultyAdapter(List<Faculty> facultyList, OnFacultyActionsListener listener) {
        this.facultyList = facultyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faculty_list_item, parent, false);
        return new FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        Faculty faculty = facultyList.get(position);
        holder.tvFacultyName.setText(faculty.getFacultyName());

        // Gán sự kiện click cho các nút
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(faculty));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(faculty));
    }

    @Override
    public int getItemCount() {
        return facultyList == null ? 0 : facultyList.size();
    }

    // Hàm để cập nhật danh sách
    public void setFaculties(List<Faculty> newFaculties) {
        this.facultyList = newFaculties;
        notifyDataSetChanged();
    }

    // ViewHolder
    class FacultyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacultyName;
        Button btnEdit, btnDelete;

        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacultyName = itemView.findViewById(R.id.tvFacultyName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}