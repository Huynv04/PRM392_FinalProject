package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.fuportal.core.data.model.Grade;
import java.util.List;

@Dao
public interface GradeDao {

    // Insert hoặc Update (thay thế)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertGrade(Grade grade);

    // Lấy tất cả điểm của 1 sinh viên cho 1 lớp (sẽ cần JOIN)
    // (Chúng ta sẽ tạo 1 POJO cho việc này ở Màn hình 5)
}