package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

// Tên bảng: Applications
@Entity(tableName = "Applications",
        foreignKeys = {
                // Khóa ngoại StudentID (UserCode)
                @ForeignKey(entity = User.class, parentColumns = "userCode", childColumns = "studentID", onDelete = ForeignKey.CASCADE),
                // Khóa ngoại AppTypeID (Loại đơn)
                @ForeignKey(entity = ApplicationType.class, parentColumns = "appTypeID", childColumns = "appTypeID", onDelete = ForeignKey.CASCADE),
                // Khóa ngoại HandlerID (Người xử lý - có thể NULL)
                @ForeignKey(entity = User.class, parentColumns = "userCode", childColumns = "handlerID", onDelete = ForeignKey.SET_NULL)
        },
        indices = { @Index("studentID"), @Index("appTypeID"), @Index("handlerID") }
)
public class Application {

    @PrimaryKey(autoGenerate = true)
    private int applicationID;

    @NonNull
    private String studentID;
    private int appTypeID;

    @NonNull
    private long submissionDate; // Lưu dưới dạng long (milliseconds)

    private String content; // Nội dung/Lý do làm đơn

    @NonNull
    private String status = "Pending"; // 'Pending', 'Approved', 'Rejected'

    private String handlerID; // Có thể null (chưa được xử lý)
    private String responseContent; // Phản hồi của người xử lý

    // --- Constructor cho việc GỬI đơn mới ---
    public Application(@NonNull String studentID, int appTypeID, String content, @NonNull long submissionDate) {
        this.studentID = studentID;
        this.appTypeID = appTypeID;
        this.content = content;
        this.submissionDate = submissionDate;
        this.status = "Pending"; // Mặc định là Pending
    }

    // --- Constructor rỗng và Getters/Setters đầy đủ ---
    public Application() {}

    public int getApplicationID() { return applicationID; }
    public void setApplicationID(int applicationID) { this.applicationID = applicationID; }
    // ... (Thêm các getters/setters cho các trường còn lại) ...
    @NonNull
    public String getStudentID() { return studentID; }
    public void setStudentID(@NonNull String studentID) { this.studentID = studentID; }

    public int getAppTypeID() { return appTypeID; }
    public void setAppTypeID(int appTypeID) { this.appTypeID = appTypeID; }

    @NonNull
    public long getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(@NonNull long submissionDate) { this.submissionDate = submissionDate; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public String getHandlerID() { return handlerID; }
    public void setHandlerID(String handlerID) { this.handlerID = handlerID; }

    public String getResponseContent() { return responseContent; }
    public void setResponseContent(String responseContent) { this.responseContent = responseContent; }
}