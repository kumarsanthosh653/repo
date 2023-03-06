package com.ozonetel.occ.util;

import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeConverter {

    protected static transient final Log log = LogFactory.getLog(TimeConverter.class);

    public static String secondsToTime(Long seconds) {
        String defaultTime = "00:00:00";
        try {
            if (seconds != null) {
                Long positiveSeconds = Math.abs(seconds);
                long hours = TimeUnit.SECONDS.toHours(positiveSeconds);
                long minute = TimeUnit.SECONDS.toMinutes(positiveSeconds) - (TimeUnit.SECONDS.toHours(positiveSeconds) * 60);
                long second = TimeUnit.SECONDS.toSeconds(positiveSeconds) - (TimeUnit.SECONDS.toMinutes(positiveSeconds) * 60);
                //defaultTime = (seconds < 0 ? "-" : "") + (hours < 9 ? "0" : "") + hours + ":" + (minute < 9 ? "0" : "") + minute + ":" + (second < 9 ? "0" : "") + second;
                defaultTime = format("%1$1s%2$02d:%3$02d:%4$02d", (seconds < 0 ? "-" : ""), hours, minute, second).trim();
            } else {
                log.error("unable to seconds : " + seconds + " ");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return defaultTime;
    }

    public static Long timeToSeconds(String time) {
        Long defaultSeconds = null;
        try {
            if (time != null && !time.trim().isEmpty() && time.matches("^(\\-?)([0-9])+:([0-5][0-9]):([0-5][0-9])$")) {
                String[] timeSlices = time.split(":");
                if (timeSlices != null && timeSlices.length == 3) {
                    defaultSeconds = ((Math.abs(Long.valueOf(timeSlices[0])) * 60 * 60) + (Long.valueOf(timeSlices[1]) * 60) + (Long.valueOf(timeSlices[2]))) * (time.startsWith("-") ? -1 : 1);
                } else {
                    log.error(time + " is invalid time format");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return defaultSeconds;
    }

    public static String format(String format, Object... args) {
        return String.format(format, args);
    }
}
