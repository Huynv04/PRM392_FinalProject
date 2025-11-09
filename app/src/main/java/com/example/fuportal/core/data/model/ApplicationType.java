package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "ApplicationTypes",
        indices = {@Index(value = {"typeName"}, unique = true)}
)
public class ApplicationType {

    @PrimaryKey(autoGenerate = true)
    private int appTypeID;

    @NonNull
    private String typeName;

    // === TRƯỜNG MỚI (SOFT DELETE) ===
    @NonNull
    private boolean isActive = true;

    // --- Constructor ---
    public ApplicationType(@NonNull String typeName, @NonNull boolean isActive) {
        this.typeName = typeName;
        this.isActive = isActive;
    }

    // --- Constructor rỗng (Cần cho các hàm Delete/Update) ---
    public ApplicationType() {}

    // --- Getters and Setters (Alt+Insert) ---
    public int getAppTypeID() { return appTypeID; }
    public void setAppTypeID(int appTypeID) { this.appTypeID = appTypeID; }

    @NonNull
    public String getTypeName() { return typeName; }
    public void setTypeName(@NonNull String typeName) { this.typeName = typeName; }

    @NonNull
    public boolean isActive() { return isActive; }
    public void setActive(@NonNull boolean isActive) { this.isActive = isActive; }
}