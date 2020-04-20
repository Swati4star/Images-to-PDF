package swati4star.createpdf.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import swati4star.createpdf.util.Constants;

import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_COLOR;

/**
 * The {@link TextToPdfDefaultManager} is responsible for managing the default enhancement values
 * for the Text-to-PDF enhancements.
 */
public class TextToPdfDefaultManager {

    private final SharedPreferences mSharedPreferences;

    public TextToPdfDefaultManager(@NonNull final Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getFontColor() {
        return mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT,
                Constants.DEFAULT_FONT_COLOR);
    }

    public void updateFontColor(final int fontColor) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_FONT_COLOR_TEXT, fontColor);
        editor.apply();
    }

    public int getPageColor() {
        return mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_TTP,
                DEFAULT_PAGE_COLOR);
    }

    public void updatePageColor(final int pageColor) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_PAGE_COLOR_TTP, pageColor);
        editor.apply();
    }

    public String getFontFamily() {
        return mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
    }

    public void updateFontFamily(@NonNull final String fontFamily) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily);
        editor.apply();
    }

    public int getFontSize() {
        return mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
    }

    public void updateFontSize(final int fontSize) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, fontSize);
        editor.apply();
    }

    public String getPageSize() {
        return mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE);
    }

    public void updatePageSize(@NonNull final String pageSize) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_PAGE_SIZE_TEXT, pageSize);
        editor.apply();
    }
}
