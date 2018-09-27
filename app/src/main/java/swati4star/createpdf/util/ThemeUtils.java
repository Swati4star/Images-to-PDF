package swati4star.createpdf.util;

import android.content.Context;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.THEME_BLACK;
import static swati4star.createpdf.util.Constants.THEME_DARK;
import static swati4star.createpdf.util.Constants.THEME_WHITE;

public class ThemeUtils {

    public static void setThemeApp(String theme, Context context) {
        switch (theme) {
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
}
