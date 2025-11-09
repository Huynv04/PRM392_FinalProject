package com.example.fuportal.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.fuportal.core.data.model.User; // Import model của bạn
import com.example.fuportal.core.data.model.UserDetail;

import java.util.List;

@Dao
public interface UserDao {

    // Thêm một user mới
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    // Cập nhật thông tin user
    @Update
    void updateUser(User user);


    @Query("SELECT * FROM Users WHERE (username = :loginInput OR gmail = :loginInput) LIMIT 1")
    User findUserByLoginInput(String loginInput);

    // Lấy user bằng userCode
    @Query("SELECT * FROM Users WHERE userCode = :userCode LIMIT 1")
    User getUserByCode(String userCode);

    // Lấy tất cả user (dùng cho Admin)
    @Query("SELECT * FROM Users")
    List<User> getAllUsers();
    @Query("SELECT * FROM Users WHERE gmail = :gmail LIMIT 1")
    User findUserByGmail(String gmail);

    // HÀM MỚI 2: Để cập nhật mật khẩu
    @Query("UPDATE Users SET hashedPassword = :newPassword WHERE gmail = :email")
    void updatePasswordByGmail(String email, String newPassword);

    @Query("SELECT * FROM Users WHERE roleID = 2 ORDER BY fullName ASC")
    List<User> getAllLecturers();
    @Transaction
    @Query("SELECT u.* FROM Users u " +
            "INNER JOIN Enrollments e ON u.userCode = e.studentID " +
            "WHERE e.classID = :classId " +
            "ORDER BY u.fullName ASC")
    List<User> getStudentsByClassId(int classId);

    @Query("SELECT * FROM Users WHERE roleID IN (2) ORDER BY fullName ASC")
    List<User> getAllStaffAndLecturers();

    // --- HÀM XÓA MỀM (DEACTIVATE) ---
    @Query("UPDATE Users SET isActive = 0 WHERE userCode = :userCode")
    void deactivateUser(String userCode);

    // --- HÀM KÍCH HOẠT LẠI (ACTIVATE) ---
    @Query("UPDATE Users SET isActive = 1 WHERE userCode = :userCode")
    void activateUser(String userCode);

    // --- HÀM LẤY DANH SÁCH USER VỚI FILTER & SEARCH (UC07) ---
    @Transaction
    @Query("SELECT " +
            "u.userCode, u.fullName, u.username, u.gmail, u.roleID, u.campusID, u.isActive, " +
            "r.roleName, c.campusName " +
            "FROM Users u " +
            "INNER JOIN Roles r ON u.roleID = r.roleID " +
            "LEFT JOIN Campuses c ON u.campusID = c.campusID " +
            "WHERE (:statusFilter = 0 OR u.isActive = (:statusFilter = 1)) " + // Lọc theo Status (0=All, 1=Active, 2=Deactive)
            "AND (" + // Bắt đầu Khối Tìm kiếm
            "LOWER(u.userCode) LIKE '%' || :searchQuery || '%' " +
            "OR LOWER(u.fullName) LIKE '%' || :searchQuery || '%' " +
            "OR LOWER(u.gmail) LIKE '%' || :searchQuery || '%' " +
            "OR LOWER(r.roleName) LIKE '%' || :searchQuery || '%' " +
            ") " +
            "ORDER BY u.userCode ASC")
    List<UserDetail> getFilteredUserDetails(String searchQuery, int statusFilter);
}