package com.example.fuportal.core.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.Course;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseActionsListener listener;

    public interface OnCourseActionsListener {
        void onEditClick(Course course);
        void onDeleteClick(Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseActionsListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_list_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvCourseCode.setText(course.getCourseCode());
        holder.tvCourseName.setText(course.getCourseName());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(course));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(course));
    }

    @Override
    public int getItemCount() {
        return courseList == null ? 0 : courseList.size();
    }

    public void setCourses(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvCourseName;
        Button btnEdit, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}