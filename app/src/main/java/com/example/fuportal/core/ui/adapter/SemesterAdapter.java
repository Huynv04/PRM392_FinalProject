package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.Semester;

import java.text.SimpleDateFormat; // <-- Import
import java.util.Date; // <-- Import
import java.util.List;
import java.util.Locale; // <-- Import

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

    private List<Semester> semesterList;
    private OnSemesterActionsListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnSemesterActionsListener {
        void onEditClick(Semester semester);
        void onDeleteClick(Semester semester);
    }

    public SemesterAdapter(List<Semester> semesterList, OnSemesterActionsListener listener) {
        this.semesterList = semesterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.semester_list_item, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        Semester semester = semesterList.get(position);
        holder.tvSemesterName.setText(semester.getSemesterName());

        // Format ngày (long) sang String
        String startDate = dateFormat.format(new Date(semester.getStartDate()));
        String endDate = dateFormat.format(new Date(semester.getEndDate()));
        holder.tvSemesterDates.setText("Start: " + startDate + " - End: " + endDate);

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(semester));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(semester));
    }

    @Override
    public int getItemCount() {
        return semesterList == null ? 0 : semesterList.size();
    }

    public void setSemesters(List<Semester> newSemesters) {
        this.semesterList = newSemesters;
        notifyDataSetChanged();
    }

    class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSemesterName, tvSemesterDates;
        Button btnEdit, btnDelete;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvSemesterDates = itemView.findViewById(R.id.tvSemesterDates);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}