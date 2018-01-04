package ir.app.ensan.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import ir.app.ensan.EnsanApp;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DeviceUtil {

  public static String getOSVersionRelease() {
    try {
      String raw = String.valueOf(Build.VERSION.RELEASE);
      if (raw.length() > 5) raw = raw.substring(0, 5);
      raw = raw.replaceAll("[:-]", "");
      return raw;
    } catch (Exception ex) {
      return "unknown";
    }
  }

  /**
   * Check for detect device is Rooted or not?
   *
   * @return true if rooted, else false
   */
  public boolean isDeviceRooted() {
    return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
  }

  private boolean checkRootMethod1() {
    String buildTags = Build.TAGS;
    return buildTags != null && buildTags.contains("test-keys");
  }

  private boolean checkRootMethod2() {
    String[] paths = {
        "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
        "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
        "/system/bin/failsafe/su", "/data/local/su"
    };
    for (String path : paths) {
      if (new File(path).exists()) return true;
    }
    return false;
  }

  private boolean checkRootMethod3() {
    Process process = null;
    try {
      process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
      BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      return in.readLine() != null;
    } catch (Throwable t) {
      return false;
    } finally {
      if (process != null) process.destroy();
    }
  }

  /**
   * Get Version Name  + . + Version Code
   *
   * @return if can: for example 3.3.0.196 , else 99999
   */
  public String getVersionNameAndCode() {
    try {
      return getVersionName() + "." + getVersionCode();
    } catch (PackageManager.NameNotFoundException e) {
      return "99999";
    }
  }

  private String getVersionName() throws PackageManager.NameNotFoundException {
    return EnsanApp.getAppContext()
        .getPackageManager()
        .getPackageInfo(EnsanApp.getAppContext().getPackageName(), 0).versionName;
  }

  public String getAppVersionName() {
    try {
      return getVersionName();
    } catch (PackageManager.NameNotFoundException ignore) {
      return "0";
    }
  }

  public int getVersionCode() throws PackageManager.NameNotFoundException {
    return EnsanApp.getAppContext()
        .getPackageManager()
        .getPackageInfo(EnsanApp.getAppContext().getPackageName(), 0).versionCode;
  }

  private int getAppVersionCode() {
    try {
      return getVersionCode();
    } catch (PackageManager.NameNotFoundException ignore) {
      return 0;
    }
  }

  /**
   * Get UUID, ANDROID_ID & etc...
   */
  public String getUUID() {
    return ((TelephonyManager) EnsanApp.getAppContext()
        .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
  }

  public String getModel() {
    return Build.MODEL;
  }

  public String getAndroidID() {
    return Settings.Secure.getString(EnsanApp.getAppContext().getContentResolver(),
        Settings.Secure.ANDROID_ID);
  }

  public String getOSVersion() {
    return String.valueOf(Build.VERSION.SDK_INT);
  }

  public String getSerialNumber() {
    return Build.SERIAL;
  }

  public String getAppName() {
    return EnsanApp.getAppContext().getPackageName();
  }

  public String getDeviceManufacturer() {
    return Build.MANUFACTURER;
  }

  public String getDeviceBrand() {
    return Build.BRAND;
  }

  public String getDeviceModel() {
    return Build.MODEL;
  }
}
