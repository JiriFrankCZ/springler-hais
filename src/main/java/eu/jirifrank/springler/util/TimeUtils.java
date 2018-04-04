package eu.jirifrank.springler.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtils {

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Prague");

    public static Date fromDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZONE_ID).toInstant());
    }
}
