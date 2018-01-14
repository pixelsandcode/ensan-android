package ir.app.ensan;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import ir.app.ensan.component.view.FontType;

/**
 * Created by Khashayar on 30/12/2017.
 */

public class EnsanApp extends Application {

  public static final String sharedPreferenceKey = "ensan";

  private static Context appContext;
  private static boolean appInForeground = false;

  private static Typeface iranSansTypeface;
  private static Typeface iranSansBoldTypeface;
  private static Typeface iranSansMediumTypeface;

  @Override public void onCreate() {
    super.onCreate();
    appContext = getApplicationContext();
    initFonts();
  }

  private void initFonts() {
    iranSansTypeface = Typeface.createFromAsset(appContext.getAssets(), "fonts/iran_sans.ttf");
    iranSansBoldTypeface =
        Typeface.createFromAsset(appContext.getAssets(), "fonts/iran_sans_bold.ttf");
    iranSansMediumTypeface =
        Typeface.createFromAsset(appContext.getAssets(), "fonts/iran_sans_medium.ttf");
  }

  public static Typeface getTypeFace(FontType fontType) {
    switch (fontType) {
      case NORMAL:
        return iranSansTypeface;
      case BOLD:
        return iranSansBoldTypeface;
      case MEDIUM:
      default:
        return iranSansMediumTypeface;
    }
  }

  public static Context getAppContext() {
    return appContext;
  }

  public static boolean isAppInForeground() {
    return appInForeground;
  }

  public static void setAppInForeground(boolean appInForeground) {
    EnsanApp.appInForeground = appInForeground;
  }
}
