package com.example.fuportal.core.data.model;

// POJO này dùng để hiển thị chi tiết lịch thi (JOIN 3 bảng)
public class ExamScheduleDetail {
    // Thông tin ExamSchedule
    public int examID;
    public long examDate;
    public String startTime;
    public String endTime;
    public String roomNumber;

    // Thông tin Lớp học
    public int classID;
    public String courseCode;
    public String courseName;
    public String semesterName;

    // Thông tin Invigilator
    public String invigilatorName; // Tên cán bộ coi thi

    public ExamScheduleDetail(int examID, long examDate, String startTime, String endTime, String roomNumber, String courseCode, String semesterName, String invigilatorName) {
        this.examID = examID;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.courseCode = courseCode;
        this.semesterName = semesterName;
        this.invigilatorName = invigilatorName;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public long getExamDate() {
        return examDate;
    }

    public void setExamDate(long examDate) {
        this.examDate = examDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getInvigilatorName() {
        return invigilatorName;
    }

    public void setInvigilatorName(String invigilatorName) {
        this.invigilatorName = invigilatorName;
    }
}