package ir.app.ensan.util;

import android.util.Log;
import ir.app.ensan.Config;

/**
 * Created by k.monem on 10/25/2016.
 */
public class LogUtil {

  public static void logW(String tag, String msg) {
    if (Config.LOG_LEVEL > Log.WARN) {
      return;
    }
    Log.w(tag, msg);
  }

  public static void logD(String tag, String msg) {
    if (Config.LOG_LEVEL > Log.DEBUG) {
      return;
    }
    Log.d(tag, msg);
  }

  public static void logI(String tag, String msg) {
    if (Config.LOG_LEVEL > Log.INFO) {
      return;
    }
    Log.i(tag, msg);
  }

  public static void logV(String tag, String msg) {
    if (Config.LOG_LEVEL > Log.VERBOSE) {
      return;
    }
    Log.v(tag, msg);
  }

  public static void logE(String tag, String msg) {
    if (Config.LOG_LEVEL > Log.ERROR) {
      return;
    }
    Log.e(tag, msg);
  }
}
