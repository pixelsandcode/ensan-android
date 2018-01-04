package ir.app.ensan.util;

import android.content.Context;
import android.content.SharedPreferences;
import ir.app.ensan.EnsanApp;

/**
 * Created by k.monem on 6/2/2016.
 */
public class SharedPreferencesUtil {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    static {
        sharedPreferences = EnsanApp.getAppContext().getSharedPreferences(
            EnsanApp.sharedPreferenceKey, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void saveString(String key, String value) {
        editor.putString(key,value);
        editor.commit();
    }

    public static void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public static void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String loadString(String key, String defaultValue) {
        return sharedPreferences.getString(key,defaultValue);
    }

    public static int loadInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static long loadLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void removeString(String key) {
        editor.remove(key);
        editor.commit();
    }
}
