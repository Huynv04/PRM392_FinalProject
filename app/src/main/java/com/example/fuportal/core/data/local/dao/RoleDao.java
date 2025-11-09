package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.fuportal.core.data.model.Role;

import java.util.List;

@Dao
public interface RoleDao {

    // OnConflictStrategy.IGNORE: Nếu đã có RoleID đó, bỏ qua việc chèn
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRole(Role role);

    // Bạn có thể chèn nhiều role cùng lúc
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllRoles(Role... roles);

    @Query("SELECT * FROM Roles ORDER BY roleID ASC")
    List<Role> getAllRoles();
}