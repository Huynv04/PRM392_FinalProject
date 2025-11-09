package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fuportal.core.data.model.ApplicationType;

import java.util.List;

@Dao
public interface ApplicationTypeDao {
    @Insert
    long insertAppType(ApplicationType appType);

    @Update
    void updateAppType(ApplicationType appType);

    // --- QUERIES CHO CRUD ---

    // Lấy tất cả các loại đơn đang hoạt động (Active)
    @Query("SELECT * FROM ApplicationTypes WHERE isActive = 1 ORDER BY typeName ASC")
    List<ApplicationType> getAllActiveAppTypes();

    // Lấy tất cả các loại đơn (bao gồm cả Deactive)
    @Query("SELECT * FROM ApplicationTypes ORDER BY typeName ASC")
    List<ApplicationType> getAllAppTypes();

    @Query("SELECT * FROM ApplicationTypes WHERE appTypeID = :typeId LIMIT 1")
    ApplicationType getAppTypeById(int typeId);

    // --- XÓA MỀM (SOFT DELETE) ---
    @Query("UPDATE ApplicationTypes SET isActive = 0 WHERE appTypeID = :typeId")
    void softDeleteAppType(int typeId);

    // --- KÍCH HOẠT LẠI ---
    @Query("UPDATE ApplicationTypes SET isActive = 1 WHERE appTypeID = :typeId")
    void activateAppType(int typeId);
}