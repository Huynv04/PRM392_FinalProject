package com.example.fuportal.core.data.model;

// POJO này dùng để hiển thị danh sách User chi tiết
public class UserDetail {
    // Thuộc tính từ Users
    public String userCode;
    public String fullName;
    public String username;
    public String gmail;
    public int roleID; // Để dùng cho filtering/logic
    public Integer campusID;

    // Thuộc tính JOIN
    public String roleName;
    public String campusName; // Có thể null
    public boolean isActive;
}