package com.example.fuportal.core.data.model;

// Dùng để hiển thị chi tiết đơn từ trong RecyclerView
public class ApplicationDetail {
    public int applicationID;
    public String studentID;
    public long submissionDate;
    public String content;
    public String status;
    public String handlerID;
    public String responseContent;

    // JOIN fields
    public String typeName; // Từ ApplicationTypes
    public String handlerName; // Từ Users (nếu handlerID không null)
}