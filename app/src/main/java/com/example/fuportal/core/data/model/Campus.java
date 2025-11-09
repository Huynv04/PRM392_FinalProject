package com.example.fuportal.core.data.model; // (Thay bằng package của bạn)

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Campuses")
public class Campus {

    @PrimaryKey(autoGenerate = true) // Tự động tăng
    private int campusID;

    @NonNull
    private String campusName;

    private String address; // Cho phép address là null

    // --- Constructor ---
    public Campus(@NonNull String campusName, String address) {
        this.campusName = campusName;
        this.address = address;
    }

    // --- Getters and Setters ---
    public int getCampusID() {
        return campusID;
    }

    public void setCampusID(int campusID) {
        this.campusID = campusID;
    }

    @NonNull
    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(@NonNull String campusName) {
        this.campusName = campusName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}