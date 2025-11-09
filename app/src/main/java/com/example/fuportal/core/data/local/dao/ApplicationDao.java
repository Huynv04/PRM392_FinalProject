package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;

import com.example.fuportal.core.data.model.Application;
import com.example.fuportal.core.data.model.ApplicationDetail;

import java.util.List;

@Dao
public interface ApplicationDao {
    @Insert
    long insertApplication(Application application);

    @Update
    void updateApplication(Application application);

    // --- QUERIES CHO STUDENT (VIEW APPLICATIONS SENT) ---

    @Transaction
    @Query("SELECT " +
            "a.applicationID, a.studentID, a.submissionDate, a.content, a.status, a.handlerID, a.responseContent, " +
            "at.typeName, u.fullName AS handlerName " +
            "FROM Applications a " +
            "JOIN ApplicationTypes at ON a.appTypeID = at.appTypeID " +
            "LEFT JOIN Users u ON a.handlerID = u.userCode " + // LEFT JOIN vì HandlerID có thể null
            "WHERE a.studentID = :studentId " +
            "ORDER BY a.submissionDate DESC")
    List<ApplicationDetail> getStudentApplications(String studentId);

    @Transaction
    @Query("SELECT " +
            "a.applicationID, a.studentID, a.submissionDate, a.content, a.status, a.handlerID, a.responseContent, " +
            "at.typeName, u.fullName AS handlerName " +
            "FROM Applications a " +
            "JOIN ApplicationTypes at ON a.appTypeID = at.appTypeID " +
            "LEFT JOIN Users u ON a.handlerID = u.userCode " +
            "ORDER BY a.submissionDate DESC")
    List<ApplicationDetail> getAllApplicationsDetails();

    // --- HÀM MỚI 2: LẤY APPLICATION GỐC BẰNG ID ---
    @Query("SELECT * FROM Applications WHERE applicationID = :appId LIMIT 1")
    Application getApplicationById(int appId);

    // --- HÀM MỚI 3: CẬP NHẬT PHẢN HỒI ---
    @Query("UPDATE Applications SET status = :newStatus, responseContent = :responseContent, handlerID = :handlerId " +
            "WHERE applicationID = :appId")
    void updateApplicationStatus(int appId, String newStatus, String responseContent, String handlerId);
}