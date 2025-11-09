package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.Campus;
import java.util.List;

public class CampusAdapter extends RecyclerView.Adapter<CampusAdapter.CampusViewHolder> {

    private List<Campus> campusList;
    private OnCampusActionsListener listener;

    public interface OnCampusActionsListener {
        void onEditClick(Campus campus);
        void onDeleteClick(Campus campus);
    }

    public CampusAdapter(List<Campus> campusList, OnCampusActionsListener listener) {
        this.campusList = campusList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CampusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campus_list_item, parent, false);
        return new CampusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampusViewHolder holder, int position) {
        Campus campus = campusList.get(position);
        holder.tvCampusName.setText(campus.getCampusName());
        holder.tvCampusAddress.setText(campus.getAddress()); // Thêm địa chỉ

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(campus));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(campus));
    }

    @Override
    public int getItemCount() {
        return campusList == null ? 0 : campusList.size();
    }

    public void setCampuses(List<Campus> newCampuses) {
        this.campusList = newCampuses;
        notifyDataSetChanged();
    }

    class CampusViewHolder extends RecyclerView.ViewHolder {
        TextView tvCampusName, tvCampusAddress;
        Button btnEdit, btnDelete;

        public CampusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCampusName = itemView.findViewById(R.id.tvCampusName);
            tvCampusAddress = itemView.findViewById(R.id.tvCampusAddress);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}