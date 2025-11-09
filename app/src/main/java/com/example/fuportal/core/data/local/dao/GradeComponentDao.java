package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.fuportal.core.data.model.GradeComponent;
import com.example.fuportal.core.data.model.GradeDetail;

import java.util.List;

@Dao
public interface GradeComponentDao {
    @Insert
    long insertGradeComponent(GradeComponent component); // Trả về long (ID)

    // Lấy tất cả đầu điểm của MỘT LỚP
    @Query("SELECT * FROM GradeComponents WHERE classID = :classId ORDER BY componentName ASC")
    List<GradeComponent> getComponentsForClass(int classId);

    @Transaction
    @Query("SELECT c.*, " + // <-- 1. Lấy tất cả cột từ GradeComponent (bảng c)

            // 2. Đổi tên (Alias) các cột của Grades (bảng g) để khớp với prefix "grade_"
            "g.gradeID AS grade_gradeID, " +
            "g.studentID AS grade_studentID, " +
            "g.componentID AS grade_componentID, " +
            "g.score AS grade_score " +

            "FROM GradeComponents c " + // (Tên bảng c)
            "LEFT JOIN Grades g ON c.componentID = g.componentID AND g.studentID = :studentId " +
            "WHERE c.classID = :classId " +
            "ORDER BY c.componentName ASC")
    List<GradeDetail> getGradesForStudentInClass(int classId, String studentId);
}