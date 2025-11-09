package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "ExamSchedules",
        foreignKeys = {
                @ForeignKey(entity = AcademicClass.class,
                        parentColumns = "classID",
                        childColumns = "classID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, // Invigilator
                        parentColumns = "userCode",
                        childColumns = "invigilatorID",
                        onDelete = ForeignKey.SET_NULL) // Xóa Invigilator thì không xóa lịch thi
        },
        indices = {
                @Index("classID"),
                @Index("invigilatorID")
        })
public class ExamSchedule {

    @PrimaryKey(autoGenerate = true)
    private int examID;

    private int classID;
    private long examDate; // Lưu dạng long (milliseconds)
    @NonNull
    private String startTime;
    @NonNull
    private String endTime;
    @NonNull
    private String roomNumber;
    private String invigilatorID; // UserCode của Invigilator (có thể NULL)

    // --- Constructor ---
    public ExamSchedule(int classID, long examDate, @NonNull String startTime, @NonNull String endTime, @NonNull String roomNumber, String invigilatorID) {
        this.classID = classID;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.invigilatorID = invigilatorID;
    }
    public ExamSchedule() {
    }


    // --- Getters and Setters (Alt+Insert) ---
    public int getExamID() { return examID; }
    public void setExamID(int examID) { this.examID = examID; }

    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    public long getExamDate() { return examDate; }
    public void setExamDate(long examDate) { this.examDate = examDate; }

    @NonNull
    public String getStartTime() { return startTime; }
    public void setStartTime(@NonNull String startTime) { this.startTime = startTime; }

    @NonNull
    public String getEndTime() { return endTime; }
    public void setEndTime(@NonNull String endTime) { this.endTime = endTime; }

    @NonNull
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(@NonNull String roomNumber) { this.roomNumber = roomNumber; }

    public String getInvigilatorID() { return invigilatorID; }
    public void setInvigilatorID(String invigilatorID) { this.invigilatorID = invigilatorID; }
}