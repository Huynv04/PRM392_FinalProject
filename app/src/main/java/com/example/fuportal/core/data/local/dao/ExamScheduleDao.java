package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.fuportal.core.data.model.ExamSchedule;
import com.example.fuportal.core.data.model.ExamScheduleDetail;

import java.util.List;

@Dao
public interface ExamScheduleDao {

    @Insert
    long insertExamSchedule(ExamSchedule examSchedule);

    @Update
    void updateExamSchedule(ExamSchedule examSchedule);

    @Delete
    void deleteExamSchedule(ExamSchedule examSchedule);

    @Query("SELECT * FROM ExamSchedules WHERE examID = :examId LIMIT 1")
    ExamSchedule getExamScheduleById(int examId);

    // --- HÀM JOIN LẤY DANH SÁCH LỊCH THI ---
    @Transaction
    @Query("SELECT " +
            "es.examID, es.examDate, es.startTime, es.endTime, es.roomNumber, es.classID, " +
            "co.courseName, co.courseCode, sem.semesterName, " +
            "u.fullName AS invigilatorName " +
            "FROM ExamSchedules es " +
            "JOIN Classes ac ON es.classID = ac.classID " +
            "JOIN Courses co ON ac.courseID = co.courseID " +
            "JOIN Semesters sem ON ac.semesterID = sem.semesterID " +
            "LEFT JOIN Users u ON es.invigilatorID = u.userCode " + // LEFT JOIN vì Invigilator có thể NULL
            "ORDER BY es.examDate DESC, es.startTime ASC")
    List<ExamScheduleDetail> getAllExamSchedules();

    @Transaction
    @Query("SELECT " +
            "E.examID, E.examDate, E.startTime, E.endTime, E.roomNumber, Cl.classID, " + // Lấy ClassID và ExamID gốc
            "Co.courseName, Co.courseCode, S.semesterName, " +
            "U.fullName AS invigilatorName " +
            "FROM ExamSchedules E " +
            "INNER JOIN Classes Cl ON E.classID = Cl.classID " + // Tên bảng Classes
            "INNER JOIN Courses Co ON Cl.courseID = Co.courseID " +
            "INNER JOIN Semesters S ON Cl.semesterID = S.semesterID " +
            "LEFT JOIN Users U ON E.invigilatorID = U.userCode " +
            "ORDER BY E.examDate DESC, E.startTime ASC")
    List<ExamScheduleDetail> getAllExamScheduleDetails();

    @Transaction
    @Query("SELECT " +
            "es.examID, es.examDate, es.startTime, es.endTime, es.roomNumber, Cl.classID, " +
            "Co.courseCode, S.semesterName, " +
            "U.fullName AS invigilatorName " +
            "FROM Enrollments E " + // Bắt đầu từ Enrollment (để lọc theo StudentID)
            "INNER JOIN ExamSchedules ES ON E.classID = ES.classID " + // JOIN với ExamSchedules
            "INNER JOIN Classes Cl ON E.classID = Cl.classID " +
            "INNER JOIN Courses Co ON Cl.courseID = Co.courseID " +
            "INNER JOIN Semesters S ON Cl.semesterID = S.semesterID " +
            "LEFT JOIN Users U ON ES.invigilatorID = U.userCode " + // Lấy tên Invigilator
            "WHERE E.studentID = :studentId " + // Lọc theo Student ID
            "ORDER BY ES.examDate ASC, ES.startTime ASC")
    List<ExamScheduleDetail> getStudentExamSchedules(String studentId);



}