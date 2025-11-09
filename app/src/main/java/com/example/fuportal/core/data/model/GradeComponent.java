package com.example.fuportal.core.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "GradeComponents",
        foreignKeys = @ForeignKey(entity = AcademicClass.class,
                parentColumns = "classID",
                childColumns = "classID",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("classID")}
)
public class GradeComponent {

    @PrimaryKey(autoGenerate = true)
    private int componentID;

    private int classID; // Khóa ngoại đến AcademicClass

    @NonNull
    private String componentName; // Ví dụ: "Iteration 1", "PE", "FE"

    private float weight; // Ví dụ: 0.1 (10%), 0.3 (30%)

    // --- Constructor ---
    public GradeComponent(int classID, @NonNull String componentName, float weight) {
        this.classID = classID;
        this.componentName = componentName;
        this.weight = weight;
    }

    // --- Getters and Setters (Dùng Alt+Insert) ---
    public int getComponentID() { return componentID; }
    public void setComponentID(int componentID) { this.componentID = componentID; }

    public int getClassID() { return classID; }
    public void setClassID(int classID) { this.classID = classID; }

    @NonNull
    public String getComponentName() { return componentName; }
    public void setComponentName(@NonNull String componentName) { this.componentName = componentName; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
}