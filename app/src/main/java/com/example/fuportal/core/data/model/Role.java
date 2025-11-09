package com.example.fuportal.core.data.model; // (Thay bằng package của bạn)

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

// @Entity(tableName = "Roles") // Bạn có thể đặt tên bảng nếu muốn
@Entity(tableName = "Roles", indices = {
        @Index(value = {"roleName"}, unique = true) // Đảm bảo roleName là duy nhất
})

public class Role
{

    @PrimaryKey(autoGenerate = true) // Tự động tăng (giống IDENTITY)
    private int roleID;

    @NonNull
    private String roleName;

    // --- Constructor ---
    // Room cần constructor này để gán giá trị
    public Role(@NonNull String roleName) {
        this.roleName = roleName;
    }

    // --- Getters and Setters ---
    public int getRoleID() {
        return roleID;
    }

    // Room sẽ dùng setter này khi autoGenerate=true
    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    @NonNull
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(@NonNull String roleName) {
        this.roleName = roleName;
    }
}