package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Schedules",
        foreignKeys = {
                @ForeignKey(entity = AcademicClass.class,
                        parentColumns = "classID",
                        childColumns = "classID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = TimeSlot.class,
                        parentColumns = "slotID",
                        childColumns = "slotID",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("classID"),
                @Index("slotID")
        })
public class Schedule {

    @PrimaryKey(autoGenerate = true)
    private int scheduleID;

    private int classID; // Khóa ngoại đến AcademicClass
    private int dayOfWeek; // (2=Thứ 2, 3=Thứ 3, ..., 7=Thứ 7)
    private int slotID; // Khóa ngoại đến TimeSlot
    @NonNull
    private String roomNumber;

    // --- Constructor ---
    public Schedule(int classID, int dayOfWeek, int slotID, @NonNull String roomNumber) {
        this.classID = classID;
        this.dayOfWeek = dayOfWeek;
        this.slotID = slotID;
        this.roomNumber = roomNumber;
    }

    // --- Getters and Setters ---
    public int getScheduleID() { return scheduleID; }
    public void setScheduleID(int scheduleID) { this.scheduleID = scheduleID; }

    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getSlotID() { return slotID; }
    public void setSlotID(int slotID) { this.slotID = slotID; }

    @NonNull
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(@NonNull String roomNumber) { this.roomNumber = roomNumber; }
}