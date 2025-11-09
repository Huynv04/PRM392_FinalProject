package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fuportal.core.data.model.Faculty;

import java.util.List;

@Dao
public interface FacultyDao {

    @Query("SELECT * FROM Faculties ORDER BY facultyName ASC")
    List<Faculty> getAllFaculties();

    @Query("SELECT * FROM Faculties WHERE facultyID = :facultyId LIMIT 1")
    Faculty getFacultyById(int facultyId);

    @Insert
    void insertFaculty(Faculty faculty);

    @Update
    void updateFaculty(Faculty faculty);

    @Delete
    void deleteFaculty(Faculty faculty);
}