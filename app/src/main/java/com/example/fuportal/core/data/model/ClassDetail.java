package com.example.fuportal.core.data.model;

// Đây là một POJO (Plain Old Java Object), KHÔNG PHẢI @Entity
// Nó dùng để nhận kết quả từ câu lệnh JOIN
public class ClassDetail {
    // Lấy từ bảng Classes
    public int classID;
    public int maxSize;

    // Lấy từ các bảng JOIN
    public String courseName;
    public String semesterName;
    public String lecturerName;
    public String campusName;
    public int courseID;

    // Bạn có thể thêm các trường khác nếu muốn
}