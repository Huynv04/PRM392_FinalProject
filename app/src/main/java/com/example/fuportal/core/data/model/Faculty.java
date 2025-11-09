package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Faculties",
        indices = {@Index(value = "facultyName", unique = true)})
public class Faculty {
    @PrimaryKey(autoGenerate = true)
    private int facultyID;

    @NonNull
    private String facultyName;

    // --- Constructor ---
    public Faculty(@NonNull String facultyName) {
        this.facultyName = facultyName;
    }

    // --- Getters and Setters (Alt+Insert) ---
    public int getFacultyID() { return facultyID; }
    public void setFacultyID(int facultyID) { this.facultyID = facultyID; }
    @NonNull
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(@NonNull String facultyName) { this.facultyName = facultyName; }
}