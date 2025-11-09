package com.example.fuportal.core.data.model;

import androidx.room.Embedded; // <-- Import

// POJO này dùng để gộp GradeComponent và Grade (nếu có)
public class GradeDetail {

    // Lấy toàn bộ thông tin của GradeComponent
    @Embedded
    public GradeComponent component;

    // Lấy điểm (nếu có)
    @Embedded(prefix = "grade_")
    public Grade grade;
}