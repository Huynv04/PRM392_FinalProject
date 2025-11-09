package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fuportal.core.data.model.Major;

import java.util.List;

@Dao
public interface MajorDao {

    // --- CREATE (Tạo) ---
    @Insert
    void insertMajor(Major major);

    // --- READ (Đọc) ---
    @Query("SELECT * FROM Majors ORDER BY majorName ASC")
    List<Major> getAllMajors();

    @Query("SELECT * FROM Majors WHERE majorID = :majorId LIMIT 1")
    Major getMajorById(int majorId);

    // (Tùy chọn: Lấy các chuyên ngành theo Khoa)
    @Query("SELECT * FROM Majors WHERE facultyID = :facultyId ORDER BY majorName ASC")
    List<Major> getMajorsByFaculty(int facultyId);

    // --- UPDATE (Cập nhật) ---
    @Update
    void updateMajor(Major major);

    // --- DELETE (Xóa) ---
    @Delete
    void deleteMajor(Major major);
}