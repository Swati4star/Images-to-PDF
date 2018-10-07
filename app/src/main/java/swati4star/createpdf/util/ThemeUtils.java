package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.THEME_BLACK;
import static swati4star.createpdf.util.Constants.THEME_DARK;
import static swati4star.createpdf.util.Constants.THEME_WHITE;

public class ThemeUtils {

    /**
     * Set selected theme to current context
     * @param context - current context
     */
    public static void setThemeApp(Context context) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = mSharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                Constants.DEFAULT_THEME);
        switch (themeName) {
            case THEME_WHITE:
                context.setTheme(R.style.AppThemeWhite);
                break;
            case THEME_BLACK:
                context.setTheme(R.style.AppThemeBlack);
                break;
            case THEME_DARK:
                context.setTheme(R.style.ActivityThemeDark);
                break;
        }
    }

    /**
     * get position of selected theme
     * @param context - current context
     * @return - position
     */
    public static int getSelectedThemePosition(Context context) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = mSharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                Constants.DEFAULT_THEME);
        switch (themeName) {
            case THEME_BLACK:
                return  0;
            case THEME_DARK:
                return  1;
            case THEME_WHITE:
                return  2;
        }
        return 0;
    }

    /**
     * Save given theme to shared prefs
     * @param context - current context
     * @param themeName - name of theme to save
     */
    public static void saveTheme(Context context, String themeName) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_THEME_TEXT, themeName);
        editor.apply();
    }
}
