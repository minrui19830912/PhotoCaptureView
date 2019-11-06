package com.android.photocaptureviewer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageTimeUtils {
    public static String timeStamp2Date(long seconds, String format) {

        if (format == null || format.isEmpty())
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }

    public static long date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            long a = sdf.parse(date_str).getTime() / 1000;
            return a;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
