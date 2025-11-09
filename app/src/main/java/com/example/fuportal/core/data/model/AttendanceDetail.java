package com.example.fuportal.core.data.model;

// POJO này dùng cho việc hiển thị chi tiết lịch học TỪNG BUỔI
public class AttendanceDetail {
    public int scheduleID;
    public int dayOfWeek;
    public String roomNumber;
    public String startTime;
    public String endTime;
    public String attendanceStatus; // Present, Absent, hoặc NULL (Not Yet)

    // (Thêm thông tin ngày để dễ debug/sắp xếp nếu cần)
}