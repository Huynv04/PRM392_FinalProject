package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Semesters",
        indices = {@Index(value = "semesterName", unique = true)})
public class Semester {

    @PrimaryKey(autoGenerate = true)
    private int semesterID;

    @NonNull
    private String semesterName;

    // Room lưu trữ DATE dưới dạng 'long' (milliseconds)
    private long startDate;
    private long endDate;
    private long registrationStartDate;
    private long registrationEndDate;

    // --- Constructor ---
    public Semester(@NonNull String semesterName, long startDate, long endDate, long registrationStartDate, long registrationEndDate) {
        this.semesterName = semesterName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationStartDate = registrationStartDate;
        this.registrationEndDate = registrationEndDate;
    }

    // --- Getters and Setters (Dùng Alt+Insert) ---
    public int getSemesterID() { return semesterID; }
    public void setSemesterID(int semesterID) { this.semesterID = semesterID; }

    @NonNull
    public String getSemesterName() { return semesterName; }
    public void setSemesterName(@NonNull String semesterName) { this.semesterName = semesterName; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }

    public long getRegistrationStartDate() { return registrationStartDate; }
    public void setRegistrationStartDate(long registrationStartDate) { this.registrationStartDate = registrationStartDate; }

    public long getRegistrationEndDate() { return registrationEndDate; }
    public void setRegistrationEndDate(long registrationEndDate) { this.registrationEndDate = registrationEndDate; }
}