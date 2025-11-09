package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "TimeSlots")
public class TimeSlot {

    @PrimaryKey(autoGenerate = true)
    private int slotID;

    @NonNull
    private String startTime; // Lưu dạng "HH:mm" (ví dụ: "07:30")

    @NonNull
    private String endTime; // (ví dụ: "09:00")

    // --- Constructor ---
    public TimeSlot(@NonNull String startTime, @NonNull String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // --- Getters and Setters ---
    public int getSlotID() { return slotID; }
    public void setSlotID(int slotID) { this.slotID = slotID; }

    @NonNull
    public String getStartTime() { return startTime; }
    public void setStartTime(@NonNull String startTime) { this.startTime = startTime; }

    @NonNull
    public String getEndTime() { return endTime; }
    public void setEndTime(@NonNull String endTime) { this.endTime = endTime; }

    // (Quan trọng) Hàm này để hiển thị trong Spinner
    @NonNull
    @Override
    public String toString() {
        return "Slot " + slotID + " (" + startTime + " - " + endTime + ")";
    }
}