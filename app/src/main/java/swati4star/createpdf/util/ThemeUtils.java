package swati4star.createpdf.util;

import android.content.Context;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.THEME_BLACK;
import static swati4star.createpdf.util.Constants.THEME_DARK;
import static swati4star.createpdf.util.Constants.THEME_WHITE;

public class ThemeUtils {

    public static void setTheme(String theme, Context context) {
        switch (theme) {
            case THEME_WHITE:
                context.setTheme(R.style.AppTheme_NoActionBar);
                break;
            case THEME_BLACK:
                context.setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar);
                break;
            case THEME_DARK:
                context.setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
                break;
        }
    }
}
