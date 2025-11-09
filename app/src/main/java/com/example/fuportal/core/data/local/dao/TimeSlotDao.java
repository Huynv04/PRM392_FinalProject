package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.fuportal.core.data.model.TimeSlot;
import java.util.List;



@Dao
public interface TimeSlotDao {
    @Insert
    void insertTimeSlot(TimeSlot timeSlot);

    @Query("SELECT * FROM TimeSlots")
    List<TimeSlot> getAllTimeSlots();
}