package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import com.example.fuportal.core.data.model.Enrollment;

@Dao
public interface EnrollmentDao {

    // (Quan trọng) Dùng ABORT để nó ném ra Exception nếu bị trùng
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertEnrollment(Enrollment enrollment);

    // (Chúng ta sẽ cần các hàm khác sau này cho UC29)
}