package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.fuportal.core.data.model.Semester;
import java.util.List;

@Dao
public interface SemesterDao {
    @Insert
    void insertSemester(Semester semester);

    @Query("SELECT * FROM Semesters ORDER BY startDate DESC")
    List<Semester> getAllSemesters();

    @Query("SELECT * FROM Semesters WHERE semesterID = :semesterId LIMIT 1")
    Semester getSemesterById(int semesterId);

    @Update
    void updateSemester(Semester semester);

    @Delete
    void deleteSemester(Semester semester);
}