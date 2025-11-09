package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Attendance",
        foreignKeys = {
                @ForeignKey(entity = User.class, // Student
                        parentColumns = "userCode",
                        childColumns = "studentID",
                        onDelete = ForeignKey.CASCADE), // Thêm onDelete
                @ForeignKey(entity = AcademicClass.class,
                        parentColumns = "classID",
                        childColumns = "classID",
                        onDelete = ForeignKey.CASCADE) // Thêm onDelete
        },
        // Khóa UNIQUE tổng hợp
        indices = {@Index(value = {"studentID", "classID", "sessionDate"}, unique = true)}
)
public class Attendance {

    @PrimaryKey(autoGenerate = true)
    private int attendanceID;

    @NonNull
    private String studentID;

    private int classID;

    private long sessionDate; // Lưu dạng 'long' (milliseconds của ngày hôm đó)

    @NonNull
    private String status; // "Present", "Absent", "Not yet"

    // --- Constructor ---
    public Attendance(@NonNull String studentID, int classID, long sessionDate, @NonNull String status) {
        this.studentID = studentID;
        this.classID = classID;
        this.sessionDate = sessionDate;
        this.status = status;
    }

    // --- Getters and Setters (Dùng Alt+Insert) ---
    // (Bao gồm tất cả các trường)
    public int getAttendanceID() { return attendanceID; }
    public void setAttendanceID(int attendanceID) { this.attendanceID = attendanceID; }

    @NonNull
    public String getStudentID() { return studentID; }
    public void setStudentID(@NonNull String studentID) { this.studentID = studentID; }

    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    public long getSessionDate() { return sessionDate; }
    public void setSessionDate(long sessionDate) { this.sessionDate = sessionDate; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
}