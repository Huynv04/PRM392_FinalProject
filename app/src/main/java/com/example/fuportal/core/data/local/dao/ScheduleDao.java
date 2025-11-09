package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;

import com.example.fuportal.core.data.model.Attendance;
import com.example.fuportal.core.data.model.AttendanceDetail;
import com.example.fuportal.core.data.model.ClassScheduleRule;
import com.example.fuportal.core.data.model.Schedule;
import com.example.fuportal.core.data.model.ScheduleDetail;



import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insertSchedule(Schedule schedule);

    @Delete
    void deleteSchedule(Schedule schedule);

    @Query("SELECT * FROM Schedules WHERE classID = :classId ORDER BY dayOfWeek ASC, slotID ASC")
    List<Schedule> getSchedulesForClass(int classId);

    @Transaction
    @Query("SELECT " +
            "co.courseName, u.fullName AS lecturerName, s.roomNumber, t.startTime, t.endTime, " +
            "att.status AS attendanceStatus " + // <-- 1. LẤY THÊM TRẠNG THÁI (STATUS)
            "FROM Schedules s " +
            "JOIN TimeSlots t ON s.slotID = t.slotID " +
            "JOIN Classes ac ON s.classID = ac.classID " +
            "JOIN Courses co ON ac.courseID = co.courseID " +
            "JOIN Users u ON ac.lecturerID = u.userCode " +
            "JOIN Enrollments e ON ac.classID = e.classID " +
            "JOIN Semesters sem ON ac.semesterID = sem.semesterID " +
            "JOIN Campuses ca ON ac.campusID = ca.campusID " +

            // --- 2. LEFT JOIN VỚI BẢNG ATTENDANCE ---
            // (LEFT JOIN sẽ trả về NULL nếu không tìm thấy)
            "LEFT JOIN Attendance att ON att.classID = ac.classID " +
            "  AND att.studentID = e.studentID " +
            "  AND att.sessionDate = :selectedDate " + // Chỉ lấy điểm danh của ngày này

            "WHERE e.studentID = :studentId " +
            "  AND s.dayOfWeek = :dayOfWeek " +
            "  AND :selectedDate >= sem.startDate " +
            "  AND :selectedDate <= sem.endDate " +
            "ORDER BY t.startTime ASC")
    List<ScheduleDetail> getStudentScheduleForDay(String studentId, int dayOfWeek, long selectedDate);

    @Transaction
    @Query("SELECT " +
            "co.courseName, u.fullName AS lecturerName, s.roomNumber, t.startTime, t.endTime, " +
            "NULL AS attendanceStatus " + // (Giảng viên không cần xem status của chính mình)
            "FROM Schedules s " +
            "JOIN TimeSlots t ON s.slotID = t.slotID " +
            "JOIN Classes ac ON s.classID = ac.classID " +
            "JOIN Courses co ON ac.courseID = co.courseID " +
            "JOIN Users u ON ac.lecturerID = u.userCode " + // (Bảng Users)
            "JOIN Semesters sem ON ac.semesterID = sem.semesterID " +
            "JOIN Campuses ca ON ac.campusID = ca.campusID " + // (Bảng Campuses)
            "WHERE ac.lecturerID = :lecturerId " + // <-- 1. LỌC THEO LECTURER ID
            "  AND s.dayOfWeek = :dayOfWeek " +
            "  AND :selectedDate >= sem.startDate " +
            "  AND :selectedDate <= sem.endDate " +
            "ORDER BY t.startTime ASC")
    List<ScheduleDetail> getLecturerScheduleForDay(String lecturerId, int dayOfWeek, long selectedDate);

    @Query("SELECT COUNT(*) FROM Schedules WHERE classID = :classId AND dayOfWeek = :dayOfWeek")
    int checkScheduleExists(int classId, int dayOfWeek);

    @Transaction
    @Query("SELECT " +
            "s.dayOfWeek, s.roomNumber, t.startTime, t.endTime, " +
            "sem.startDate AS semesterStartDate, sem.endDate AS semesterEndDate, " +
            "ac.classID " +
            "FROM Schedules s " +
            "JOIN TimeSlots t ON s.slotID = t.slotID " +
            "JOIN Classes ac ON s.classID = ac.classID " + // Tên bảng Classes là đúng
            "JOIN Semesters sem ON ac.semesterID = sem.semesterID " +
            "WHERE s.classID = :classId")
    List<ClassScheduleRule> getClassScheduleRules(int classId); // <-- TRẢ VỀ QUY TẮC

    // --- LẤY TẤT CẢ TRẠNG THÁI ĐIỂM DANH (để tra cứu nhanh) ---
    @Query("SELECT * FROM Attendance WHERE classID = :classId AND studentID = :studentId")
    List<Attendance> getAllAttendanceRecords(int classId, String studentId);
}