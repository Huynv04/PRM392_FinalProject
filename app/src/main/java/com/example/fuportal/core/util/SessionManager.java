package com.example.fuportal.core.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Tên file lưu trữ
    private static final String PREF_NAME = "FUPortalSession";

    // Khóa (Key)
    private static final String KEY_USER_CODE = "userCode";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ROLE = "userRole";

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Lưu phiên đăng nhập
     */
    public void saveUserSession(String userCode, String fullName, int roleId) {
        editor.putString(KEY_USER_CODE, userCode);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putInt(KEY_USER_ROLE, roleId);
        editor.apply(); // Lưu lại
    }

    /**
     * Lấy UserCode (StudentID / LecturerID...)
     */
    public String getLoggedInUserCode() {
        return prefs.getString(KEY_USER_CODE, null);
    }

    /**
     * Lấy Tên
     */
    public String getLoggedInUserName() {
        return prefs.getString(KEY_USER_NAME, "Guest");
    }

    /**
     * (Tùy chọn) Lấy Role ID
     */
    public int getLoggedInUserRole() {
        return prefs.getInt(KEY_USER_ROLE, -1);
    }

    /**
     * Xóa phiên (khi Logout)
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}