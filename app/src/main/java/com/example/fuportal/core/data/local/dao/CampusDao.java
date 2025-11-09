package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.fuportal.core.data.model.Campus; // Import model của bạn


import com.example.fuportal.core.data.model.Campus;

import java.util.List;

@Dao
public interface CampusDao {

    // --- CREATE ---
    @Insert
    void insertCampus(Campus campus);

    // --- READ ---
    @Query("SELECT * FROM Campuses ORDER BY campusName ASC")
    List<Campus> getAllCampuses();

    @Query("SELECT * FROM Campuses WHERE campusID = :campusId LIMIT 1")
    Campus getCampusById(int campusId);

    // --- UPDATE ---
    @Update
    void updateCampus(Campus campus);

    // --- DELETE ---
    @Delete
    void deleteCampus(Campus campus);
}