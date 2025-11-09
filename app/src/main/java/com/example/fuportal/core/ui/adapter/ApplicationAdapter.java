package com.example.fuportal.core.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.ApplicationDetail;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<ApplicationDetail> applicationList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private OnApplicationClickListener listener; // <-- 1. THÊM TRƯỜNG LISTENER
    public ApplicationAdapter(List<ApplicationDetail> applicationList, OnApplicationClickListener listener) {
        this.applicationList = applicationList;
        this.listener = listener; // <-- 2. GÁN LISTENER
    }
    public interface OnApplicationClickListener {
        void onApplicationClick(ApplicationDetail app);
    }
    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_list_item, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        ApplicationDetail app = applicationList.get(position);
        android.content.Context context = holder.itemView.getContext(); // Lấy context
        holder.tvType.setText(app.typeName);
        holder.tvDate.setText("Sent: " + dateFormat.format(new Date(app.submissionDate)));
        holder.tvStatus.setText("Status: " + app.status);
        holder.tvContent.setText(app.content.length() > 50 ? app.content.substring(0, 50) + "..." : app.content);

        // Hiển thị phản hồi nếu có
        if (app.responseContent != null && !app.responseContent.isEmpty()) {
            holder.tvResponse.setVisibility(View.VISIBLE);
            holder.tvResponse.setText("Response: " + app.responseContent);
        } else {
            holder.tvResponse.setVisibility(View.GONE);
        }
// --- SỬA LỖI TẠI ĐÂY ---
        int statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);

        if ("Approved".equals(app.status)) {
            // Màu xanh cho Approved
            statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
        } else if ("Rejected".equals(app.status)) {
            // Màu đỏ cho Rejected
            statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
        } else {
            // Mặc định là màu xám (Pending/Other)
            statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);
        }

        holder.tvStatus.setTextColor(statusColor);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApplicationClick(app);
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationList == null ? 0 : applicationList.size();
    }

    public void setApplicationList(List<ApplicationDetail> newList) {
        this.applicationList = newList;
        notifyDataSetChanged();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvStatus, tvContent, tvResponse;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvAppType);
            tvDate = itemView.findViewById(R.id.tvSubmissionDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvContent = itemView.findViewById(R.id.tvContentPreview);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            // Cần tạo layout application_list_item.xml
        }
    }
}