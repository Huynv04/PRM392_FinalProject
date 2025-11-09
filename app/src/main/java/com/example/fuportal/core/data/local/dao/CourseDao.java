package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fuportal.core.data.model.Course;

import java.util.List;

@Dao
public interface CourseDao {

    // --- CREATE (Tạo) ---
    @Insert
    void insertCourse(Course course);

    // --- READ (Đọc) ---
    @Query("SELECT * FROM Courses ORDER BY courseCode ASC")
    List<Course> getAllCourses();

    @Query("SELECT * FROM Courses WHERE courseID = :courseId LIMIT 1")
    Course getCourseById(int courseId);

    // --- UPDATE (Cập nhật) ---
    @Update
    void updateCourse(Course course);

    // --- DELETE (Xóa) ---
    @Delete
    void deleteCourse(Course course);
}