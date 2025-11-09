package com.example.fuportal.core.ui.adapter; // (Package của bạn)

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.GradeDetail; // Import POJO

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeEntryAdapter extends RecyclerView.Adapter<GradeEntryAdapter.GradeViewHolder> {

    private List<GradeDetail> gradeDetails;
    // Map để lưu điểm số đã nhập: <ComponentID, Score>
    private Map<Integer, Float> scoreMap;

    public GradeEntryAdapter(List<GradeDetail> gradeDetails) {
        this.gradeDetails = gradeDetails;
        this.scoreMap = new HashMap<>();

        // Khởi tạo Map với các điểm số đã có
        for (GradeDetail detail : gradeDetails) {
            if (detail.grade != null) {
                scoreMap.put(detail.component.getComponentID(), detail.grade.getScore());
            }
        }
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grade_component_item, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        GradeDetail detail = gradeDetails.get(position);
        holder.bind(detail);
    }

    @Override
    public int getItemCount() {
        return gradeDetails == null ? 0 : gradeDetails.size();
    }

    // Hàm để Activity lấy Map điểm số
    public Map<Integer, Float> getScores() {
        return this.scoreMap;
    }

    class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvComponentName, tvComponentWeight;
        EditText etScore;
        // TextWatcher để theo dõi thay đổi trong EditText
        MyTextWatcher textWatcher;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComponentName = itemView.findViewById(R.id.tvComponentName);
            tvComponentWeight = itemView.findViewById(R.id.tvComponentWeight);
            etScore = itemView.findViewById(R.id.etScore);

            // Khởi tạo TextWatcher
            textWatcher = new MyTextWatcher();
            etScore.addTextChangedListener(textWatcher);
        }

        public void bind(GradeDetail detail) {
            // Cập nhật TextWatcher với ComponentID mới
            textWatcher.updateComponentId(detail.component.getComponentID());

            // Hiển thị thông tin
            tvComponentName.setText(detail.component.getComponentName());
            // (Chuyển 0.1 -> "10%")
            String weightPercent = (int)(detail.component.getWeight() * 100) + "%";
            tvComponentWeight.setText("Weight: " + weightPercent);

            // Hiển thị điểm (nếu có trong Map)
            Float score = scoreMap.get(detail.component.getComponentID());
            if (score != null) {
                etScore.setText(String.valueOf(score));
            } else {
                etScore.setText(""); // Để trống
            }
        }
    }

    // Lớp TextWatcher nội bộ để cập nhật Map
    private class MyTextWatcher implements TextWatcher {
        private int componentId;

        public void updateComponentId(int componentId) {
            this.componentId = componentId;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                // Khi người dùng gõ, cập nhật điểm vào Map
                float score = Float.parseFloat(s.toString());
                scoreMap.put(componentId, score);
            } catch (NumberFormatException e) {
                // Nếu người dùng xóa hết chữ, xóa điểm khỏi Map
                scoreMap.remove(componentId);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    }
}