package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.Major;
import java.util.List;

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.MajorViewHolder> {

    private List<Major> majorList;
    private OnMajorActionsListener listener;

    public interface OnMajorActionsListener {
        void onEditClick(Major major);
        void onDeleteClick(Major major);
    }

    public MajorAdapter(List<Major> majorList, OnMajorActionsListener listener) {
        this.majorList = majorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MajorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.major_list_item, parent, false);
        return new MajorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MajorViewHolder holder, int position) {
        Major major = majorList.get(position);
        holder.tvMajorName.setText(major.getMajorName());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(major));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(major));
    }

    @Override
    public int getItemCount() {
        return majorList == null ? 0 : majorList.size();
    }

    public void setMajors(List<Major> newMajors) {
        this.majorList = newMajors;
        notifyDataSetChanged();
    }

    class MajorViewHolder extends RecyclerView.ViewHolder {
        TextView tvMajorName;
        Button btnEdit, btnDelete;

        public MajorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMajorName = itemView.findViewById(R.id.tvMajorName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}