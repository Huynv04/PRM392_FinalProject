package com.example.fuportal.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static long dateToLong(String dateString) {
        try {
            // Định dạng này phải khớp với chuỗi bạn truyền vào (ví dụ: "2004-01-01")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                return date.getTime(); // Trả về số milliseconds (kiểu long)
            }
        } catch (ParseException e) {
            // Nếu chuỗi ngày bị lỗi, trả về 0
            return 0L;
        }
        return 0L;
    }
}
