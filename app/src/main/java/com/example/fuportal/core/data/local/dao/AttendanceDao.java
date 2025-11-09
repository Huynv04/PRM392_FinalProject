package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.fuportal.core.data.model.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert
    void insertAttendance(Attendance attendance);

    // 1. Dùng để LƯU (Insert hoặc Update nếu đã tồn tại)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAttendance(Attendance attendance);

    // 2. Dùng để LẤY (danh sách điểm danh của 1 lớp vào 1 ngày)
    @Query("SELECT * FROM Attendance WHERE classID = :classId AND sessionDate = :sessionDate")
    List<Attendance> getAttendanceForClassByDate(int classId, long sessionDate);
}