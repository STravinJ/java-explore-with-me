package ru.practicum.service.utils;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATE_TIME_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_SPACE = DateTimeFormatter.ofPattern(DATE_TIME_STRING);
    public static final String PAGE_SIZE_STRING = "10";
    public static final String APP_NAME = "ewm-main";
    public static final int USER_TIME_HOUR_BEFORE_START = 2;
    public static final int ADMIN_TIME_HOUR_BEFORE_START = 1;
}