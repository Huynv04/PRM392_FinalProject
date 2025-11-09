package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction; // Import

import com.example.fuportal.core.data.model.AcademicClass;
import com.example.fuportal.core.data.model.ClassDetail; // Import POJO
import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    void insertClass(AcademicClass newClass);

    @Update
    void updateClass(AcademicClass classToUpdate);

    @Delete
    void deleteClass(AcademicClass classToDelete);

    @Query("SELECT * FROM Classes WHERE classID = :classId LIMIT 1")
    AcademicClass getClassById(int classId);

    // --- CÂU LỆNH JOIN ĐỂ LẤY DANH SÁCH CHI TIẾT ---
    @Transaction
    @Query("SELECT " +
            "c.classID, c.maxSize, c.courseID, " + // <-- 1. THÊM c.courseID VÀO ĐÂY
            "co.courseName, s.semesterName, u.fullName AS lecturerName, ca.campusName " +
            "FROM Classes c " +
            "INNER JOIN Courses co ON c.courseID = co.courseID " +
            "INNER JOIN Semesters s ON c.semesterID = s.semesterID " +
            "INNER JOIN Users u ON c.lecturerID = u.userCode " +
            "INNER JOIN Campuses ca ON c.campusID = ca.campusID " +
            "ORDER BY s.startDate DESC, co.courseName ASC")
    List<ClassDetail> getAllClassDetails();

    @Transaction
    @Query("SELECT " +
            "c.classID, c.maxSize, c.courseID, " +
            "co.courseName, s.semesterName, u.fullName AS lecturerName, ca.campusName " +
            "FROM Classes c " +
            "INNER JOIN Courses co ON c.courseID = co.courseID " +
            "INNER JOIN Semesters s ON c.semesterID = s.semesterID " +
            "INNER JOIN Users u ON c.lecturerID = u.userCode " +
            "INNER JOIN Campuses ca ON c.campusID = ca.campusID " +
            "WHERE c.lecturerID = :lecturerId " + // <-- LỌC THEO LECTURER ID
            "ORDER BY s.startDate DESC, co.courseName ASC")
    List<ClassDetail> getClassesForLecturer(String lecturerId);

    @Transaction
    @Query("SELECT " +
            "c.classID, c.maxSize, c.courseID, " +
            "co.courseName, s.semesterName, u.fullName AS lecturerName, ca.campusName " +
            "FROM Enrollments e " + // <-- BẮT ĐẦU TỪ ENROLLMENTS
            "INNER JOIN Classes c ON e.classID = c.classID " +
            "INNER JOIN Courses co ON c.courseID = co.courseID " +
            "INNER JOIN Semesters s ON c.semesterID = s.semesterID " +
            "INNER JOIN Users u ON c.lecturerID = u.userCode " +
            "INNER JOIN Campuses ca ON c.campusID = ca.campusID " +
            "WHERE e.studentID = :studentId " + // <-- LỌC THEO STUDENT ID
            "ORDER BY s.startDate DESC, co.courseName ASC")
    List<ClassDetail> getEnrolledClassesForStudent(String studentId);

    @Query("SELECT * FROM Classes ORDER BY classID DESC")
    List<AcademicClass> getAllClasses();
}
