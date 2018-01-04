package ir.app.ensan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by k.monem on 6/2/2016.
 */
public class TimeUtil {

  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final String TIME_FORMAT_PATTERN = "HH:mm";
  public static TimeZone iranTimeZone;
  private static SimpleDateFormat dateFormat;
  private static SimpleDateFormat timeFormat;

  static {

    dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    timeFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN);
  }

  public static String getFormattedTime(Date date) {
    return timeFormat.format(date);
  }

  public static long getCurrentDate() {
    return System.currentTimeMillis();
  }

  public static long addDay(long time, int day) {
    return time + ((86400000) * day);
  }

  public static long addDayFromNow(int day) {
    return getCurrentDate() + ((86400000) * day);
  }
}
