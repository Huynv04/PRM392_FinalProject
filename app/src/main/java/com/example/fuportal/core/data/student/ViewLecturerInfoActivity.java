package com.example.fuportal.core.data.student;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.local.AppDatabase;
import com.example.fuportal.core.data.model.User;
import com.example.fuportal.core.ui.adapter.StudentListAdapter; // tái sử dụng adapter cũ
import java.util.concurrent.Executors;
import java.util.List;

public class ViewLecturerInfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lecturer_info);

        recyclerView = findViewById(R.id.rvLecturers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = AppDatabase.getDatabase(getApplicationContext());

        loadLecturers();
    }

    private void loadLecturers() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // RoleID = 2 là Lecturer
            List<User> lecturers = db.userDao().getUsersByRoleId(2);
            runOnUiThread(() -> {
                StudentListAdapter adapter = new StudentListAdapter(lecturers, student -> {
                    // Hiện thông tin chi tiết nếu cần
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }
}
