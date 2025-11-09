package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Grades",
        foreignKeys = {
                @ForeignKey(entity = User.class, // Student
                        parentColumns = "userCode",
                        childColumns = "studentID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = GradeComponent.class,
                        parentColumns = "componentID",
                        childColumns = "componentID",
                        onDelete = ForeignKey.CASCADE)
        },
        // Ràng buộc UNIQUE: 1 sinh viên chỉ có 1 điểm cho 1 đầu điểm
        indices = {@Index(value = {"studentID", "componentID"}, unique = true)}
)
public class Grade {

    @PrimaryKey(autoGenerate = true)
    private int gradeID;

    @NonNull
    private String studentID; // UserCode của Student

    private int componentID; // Khóa ngoại đến GradeComponent

    private float score; // Điểm số

    // --- Constructor ---
    public Grade(@NonNull String studentID, int componentID, float score) {
        this.studentID = studentID;
        this.componentID = componentID;
        this.score = score;
    }

    // --- Getters and Setters (Dùng Alt+Insert) ---
    public int getGradeID() { return gradeID; }
    public void setGradeID(int gradeID) { this.gradeID = gradeID; }

    @NonNull
    public String getStudentID() { return studentID; }
    public void setStudentID(@NonNull String studentID) { this.studentID = studentID; }

    public int getComponentID() { return componentID; }
    public void setComponentID(int componentID) { this.componentID = componentID; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
}