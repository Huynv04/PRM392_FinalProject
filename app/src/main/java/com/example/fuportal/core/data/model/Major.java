package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Majors",
        foreignKeys = {
                @ForeignKey(entity = Faculty.class,
                        parentColumns = "facultyID",
                        childColumns = "facultyID",
                        onDelete = ForeignKey.CASCADE) // Thêm onDelete
        },
        indices = {
                @Index(value = {"majorName"}, unique = true),
                @Index(value = {"facultyID"})
        })
public class Major {

    @PrimaryKey(autoGenerate = true)
    private int majorID;

    @NonNull
    private String majorName;

    // Khóa ngoại
    private int facultyID;

    // --- Constructor ---
    // Room sẽ dùng constructor này cho @Insert và @Query
    public Major(@NonNull String majorName, int facultyID) {
        this.majorName = majorName;
        this.facultyID = facultyID;
    }

    // --- Getters and Setters (BẮT BUỘC PHẢI CÓ) ---
    // (Bạn có thể dùng Alt+Insert để tự động tạo)

    public int getMajorID() {
        return majorID;
    }

    public void setMajorID(int majorID) {
        // Room dùng hàm này cho @Query
        this.majorID = majorID;
    }

    @NonNull
    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(@NonNull String majorName) {
        this.majorName = majorName;
    }

    public int getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(int facultyID) {
        this.facultyID = facultyID;
    }
}