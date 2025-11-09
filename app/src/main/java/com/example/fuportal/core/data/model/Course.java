package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

// Khai báo bảng, khóa ngoại, và chỉ mục (để đảm bảo CourseCode là duy nhất)
@Entity(tableName = "Courses",
        foreignKeys = {
                @ForeignKey(entity = Major.class,
                        parentColumns = "majorID",
                        childColumns = "majorID"),
                @ForeignKey(entity = Course.class, // Tự tham chiếu đến chính nó
                        parentColumns = "courseID",
                        childColumns = "prerequisiteCourseID")
        },
        indices = {
                @Index(value = {"courseCode"}, unique = true),
                @Index(value = {"majorID"}),
                @Index(value = {"prerequisiteCourseID"})
        })
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int courseID;

    @NonNull
    private String courseCode;

    @NonNull
    private String courseName;

    private int credits;

    // Khóa ngoại
    private int majorID;

    // Dùng Integer để cho phép null
    private Integer prerequisiteCourseID;

    // --- Constructor ---
    // Room cần constructor này
    public Course(@NonNull String courseCode, @NonNull String courseName, int credits, int majorID, Integer prerequisiteCourseID) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.majorID = majorID;
        this.prerequisiteCourseID = prerequisiteCourseID;
    }

    // --- Getters and Setters ---
    // (Dùng Alt+Insert để tự động tạo)

    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    @NonNull
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(@NonNull String courseCode) { this.courseCode = courseCode; }

    @NonNull
    public String getCourseName() { return courseName; }
    public void setCourseName(@NonNull String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getMajorID() { return majorID; }
    public void setMajorID(int majorID) { this.majorID = majorID; }

    public Integer getPrerequisiteCourseID() { return prerequisiteCourseID; }
    public void setPrerequisiteCourseID(Integer prerequisiteCourseID) { this.prerequisiteCourseID = prerequisiteCourseID; }
}