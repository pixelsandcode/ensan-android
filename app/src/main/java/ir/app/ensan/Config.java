package ir.app.ensan;

import android.util.Log;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Created by k.monem on 6/2/2016.
 */
public class Config {

  public static final int LOG_LEVEL = BuildConfig.STG ? Log.VERBOSE : Log.ERROR;

  public static boolean CRASH_REPORTER_ENABLE = true; // TODO: MUST BE 'TRUE' FOR RELEASE
  public static boolean STATISTIC_ANALYSER_ENABLE = true; // TODO: MUST BE 'TRUE' FOR RELEASE
  public static boolean DEVELOP_MODE = BuildConfig.STG; // TODO: MUST BE 'FALSE' FOR RELEASE

  public static Level HTTP_LOG_LEVEL = BuildConfig.STG ? Level.BODY : Level.NONE;
}
