package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Enrollments",
        foreignKeys = {
                @ForeignKey(entity = User.class, // Liên kết với Student
                        parentColumns = "userCode",
                        childColumns = "studentID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = AcademicClass.class, // Liên kết với Class
                        parentColumns = "classID",
                        childColumns = "classID",
                        onDelete = ForeignKey.CASCADE)
        },
        // Ràng buộc UNIQUE (quan trọng)
        indices = {@Index(value = {"studentID", "classID"}, unique = true)}
)
public class Enrollment {

    @PrimaryKey(autoGenerate = true)
    private int enrollmentID;

    @NonNull
    private String studentID; // UserCode của Student

    private int classID;

    private long registrationDate; // Lưu dạng 'long' (milliseconds)

    private String status; // 'Enrolled', 'Passed', 'Failed'

    // --- Constructor ---
    public Enrollment(@NonNull String studentID, int classID, long registrationDate, @NonNull String status) {
        this.studentID = studentID;
        this.classID = classID;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // --- Getters and Setters (Dùng Alt+Insert) ---
    public int getEnrollmentID() { return enrollmentID; }
    public void setEnrollmentID(int enrollmentID) { this.enrollmentID = enrollmentID; }

    @NonNull
    public String getStudentID() { return studentID; }
    public void setStudentID(@NonNull String studentID) { this.studentID = studentID; }

    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    public long getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(long registrationDate) { this.registrationDate = registrationDate; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
}