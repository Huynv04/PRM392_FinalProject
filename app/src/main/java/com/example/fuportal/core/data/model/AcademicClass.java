package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Classes",
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseID",
                        childColumns = "courseID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Semester.class,
                        parentColumns = "semesterID",
                        childColumns = "semesterID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, // Liên kết với Giảng viên
                        parentColumns = "userCode",
                        childColumns = "lecturerID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Campus.class,
                        parentColumns = "campusID",
                        childColumns = "campusID",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("courseID"),
                @Index("semesterID"),
                @Index("lecturerID"),
                @Index("campusID")
        })
public class AcademicClass {

    @PrimaryKey(autoGenerate = true)
    private int classID;

    private int courseID;
    private int semesterID;
    @NonNull
    private String lecturerID; // Mã giảng viên (từ UserCode)
    private int campusID;
    private int maxSize;

    // --- Constructor ---
    public AcademicClass(int courseID, int semesterID, @NonNull String lecturerID, int campusID, int maxSize) {
        this.courseID = courseID;
        this.semesterID = semesterID;
        this.lecturerID = lecturerID;
        this.campusID = campusID;
        this.maxSize = maxSize;
    }

    // --- Getters and Setters (Dùng Alt+Insert) ---
    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public int getSemesterID() { return semesterID; }
    public void setSemesterID(int semesterID) { this.semesterID = semesterID; }

    @NonNull
    public String getLecturerID() { return lecturerID; }
    public void setLecturerID(@NonNull String lecturerID) { this.lecturerID = lecturerID; }

    public int getCampusID() { return campusID; }
    public void setCampusID(int campusID) { this.campusID = campusID; }

    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
}