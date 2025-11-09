package com.example.fuportal.core.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ApplicationType;
import java.util.List;

public class ApplicationTypeAdapter extends RecyclerView.Adapter<ApplicationTypeAdapter.AppTypeViewHolder> {

    private List<ApplicationType> appTypeList;
    private OnAppTypeActionsListener listener;

    public interface OnAppTypeActionsListener {
        void onEditClick(ApplicationType type);
        void onDeleteClick(ApplicationType type); // Soft Delete
    }

    public ApplicationTypeAdapter(List<ApplicationType> appTypeList, OnAppTypeActionsListener listener) {
        this.appTypeList = appTypeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_type_list_item, parent, false);
        return new AppTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppTypeViewHolder holder, int position) {
        ApplicationType type = appTypeList.get(position);

        holder.tvTypeName.setText(type.getTypeName());

        // Logic Delete/Activate
        if (type.isActive()) {
            holder.btnDelete.setText("Remove");
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setText("Restore");
            holder.btnDelete.setVisibility(View.VISIBLE); // Hoặc ẩn nút Edit
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(type));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(type));
    }

    @Override
    public int getItemCount() {
        return appTypeList == null ? 0 : appTypeList.size();
    }

    public void setAppTypeList(List<ApplicationType> newAppTypes) {
        this.appTypeList = newAppTypes;
        notifyDataSetChanged();
    }

    public static class AppTypeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeName;
        Button btnEdit, btnDelete;

        public AppTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeName = itemView.findViewById(R.id.tvTypeName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}