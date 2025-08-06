package edu.yb.strtgst.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String setDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd");
        return currentDate.format(dateTimeFormatter);
    }
}
