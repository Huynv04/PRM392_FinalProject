package com.example.fuportal.core.ui.adapter; // (Package của bạn)

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.GradeDetail; // Import POJO

import java.util.List;

public class GradeDetailAdapter extends RecyclerView.Adapter<GradeDetailAdapter.GradeViewHolder> {

    private List<GradeDetail> gradeDetails;

    public GradeDetailAdapter(List<GradeDetail> gradeDetails) {
        this.gradeDetails = gradeDetails;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grade_detail_item, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeDetail detail = gradeDetails.get(position);

        // Gán tên
        holder.tvComponentName.setText(detail.component.getComponentName());

        // Gán trọng số
        String weightPercent = (int)(detail.component.getWeight() * 100) + "%";
        holder.tvComponentWeight.setText(weightPercent);

        // Gán điểm (nếu có)
        if (detail.grade != null) {
            holder.tvScore.setText(String.valueOf(detail.grade.getScore()));
        } else {
            holder.tvScore.setText("-"); // Hiển thị "-" nếu chưa có điểm
        }
    }

    @Override
    public int getItemCount() {
        return gradeDetails == null ? 0 : gradeDetails.size();
    }

    public void setGradeDetails(List<GradeDetail> newGradeDetails) {
        this.gradeDetails = newGradeDetails;
        notifyDataSetChanged();
    }

    class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvComponentName, tvComponentWeight, tvScore;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComponentName = itemView.findViewById(R.id.tvComponentName);
            tvComponentWeight = itemView.findViewById(R.id.tvComponentWeight);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}