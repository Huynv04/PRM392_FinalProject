package com.example.fuportal.core.data.model; // (Thay bằng package của bạn)

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index; // <-- Import Index
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

// Thêm "indices" để đảm bảo "username" và "gmail" là UNIQUE
@Entity(tableName = "Users", foreignKeys = {
        @ForeignKey(entity =    Role.class,
                parentColumns = "roleID",
                childColumns = "roleID"),
        @ForeignKey(entity = Campus.class,
                parentColumns = "campusID",
                childColumns = "campusID")
},
        indices = {
                @Index(value = {"username"}, unique = true),
                @Index(value = {"gmail"}, unique = true) // <-- Thêm index cho Gmail
        })
public class User {

    @PrimaryKey
    @NonNull
    private String userCode;

    @NonNull
    private String fullName;

    @NonNull
    private String username;

    @NonNull
    private String gmail;
    @NonNull
    private String hashedPassword;

    private long dateOfBirth;

    private String phoneNumber;

    private String address;

    private int roleID;
    private Integer campusID;
    private boolean isActive = true;


    public User() {} // Constructor rỗng

    public User(@NonNull String userCode, @NonNull String fullName, @NonNull String username, @NonNull String gmail, @NonNull String hashedPassword, long dateOfBirth, String phoneNumber, String address, int roleID, Integer campusID
    ,@NonNull boolean isActive) {
        this.userCode = userCode;
        this.fullName = fullName;
        this.username = username;
        this.gmail = gmail;
        this.hashedPassword = hashedPassword;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.roleID = roleID;
        this.campusID = campusID;
        this.isActive = isActive; // Khởi tạo giá trị
    }

    // (Các getters/setters khác)


    @NonNull
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(@NonNull String userCode) {
        this.userCode = userCode;
    }

    @NonNull
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getGmail() {
        return gmail;
    }

    public void setGmail(@NonNull String gmail) {
        this.gmail = gmail;
    }

    @NonNull
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(@NonNull String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public Integer getCampusID() {
        return campusID;
    }

    public void setCampusID(Integer campusID) {
        this.campusID = campusID;
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}