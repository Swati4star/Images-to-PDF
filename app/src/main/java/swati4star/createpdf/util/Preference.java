package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public static void setBooleanPref(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPref(Context context, String key, boolean defValue) {
        return getPreference(context).getBoolean(key, defValue);
    }

    public static boolean getBooleanPref(Context context, String key) {
        return getPreference(context).getBoolean(key, false);
    }

    public static void setStringPref(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getStringPref(Context context, String key, String defValue) {
        return getPreference(context).getString(key, defValue);
    }

    public static String getStringPref(Context context, String key) {
        return getPreference(context).getString(key, "");
    }

    public static void setIntPref(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static int getIntPref(Context context, String key, int defValue) {
        return getPreference(context).getInt(key, defValue);
    }
    public static int getIntPref(Context context, String key) {
        return getPreference(context).getInt(key, -1);
    }

    public static void deletePref(Context context, String key) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean prefsExist(Context context, String key) {
        return getPreference(context).contains(key);
    }
}
