package com.example.fuportal.core.data.model;

// Dùng để lấy các quy tắc lịch học và ngày học kỳ
public class ClassScheduleRule {
    public int dayOfWeek; // 2=Thứ Hai, 3=Thứ Ba, ..., 7=Thứ Bảy
    public String roomNumber;
    public String startTime; // VD: 07:30:00
    public String endTime;   // VD: 09:00:00
    public long semesterStartDate;
    public long semesterEndDate;
    public int classID; // Class ID
}