package com.example.fuportal.core.data.model;

// POJO mới cho từng BUỔI HỌC CỤ THỂ theo ngày
public class SessionAttendanceDetail {
    public String sessionDateStr;   // VD: "T2 (04/09/2025)"
    public String timeSlotStr;      // VD: "07:30 - 09:00"
    public String roomNumber;
    public String attendanceStatus; // Present, Absent, Not Yet
    public long sessionDateMillis;  // Dùng để tra cứu Attendance
}