package swati4star.createpdf.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import swati4star.createpdf.util.Constants;

/**
 * The {@link TextToPdfPreferences} is responsible for managing the default enhancement values
 * for the Text-to-PDF enhancements.
 */
public class TextToPdfPreferences {

    private final SharedPreferences mSharedPreferences;

    /**
     * Creates a new {@link TextToPdfPreferences}.
     *
     * @param context The {@link Context} used for the {@link SharedPreferences}.
     */
    public TextToPdfPreferences(@NonNull final Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return the default font color.
     */
    public int getFontColor() {
        return mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT,
                Constants.DEFAULT_FONT_COLOR);
    }

    /**
     * Set the default font color.
     *
     * @param fontColor The font color.
     */
    public void setFontColor(final int fontColor) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_FONT_COLOR_TEXT, fontColor);
        editor.apply();
    }

    /**
     * @return the default page color.
     */
    public int getPageColor() {
        return mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_TTP,
                Constants.DEFAULT_PAGE_COLOR);
    }

    /**
     * Set the default page color.
     *
     * @param pageColor The page color.
     */
    public void setPageColor(final int pageColor) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_PAGE_COLOR_TTP, pageColor);
        editor.apply();
    }

    /**
     * @return the default font family.
     */
    public String getFontFamily() {
        return mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
    }

    /**
     * Set the default font family.
     *
     * @param fontFamily The font family.
     */
    public void setFontFamily(@NonNull final String fontFamily) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily);
        editor.apply();
    }

    /**
     * @return the default font size.
     */
    public int getFontSize() {
        return mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
    }

    /**
     * Sets the default font size.
     *
     * @param fontSize The font size.
     */
    public void setFontSize(final int fontSize) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, fontSize);
        editor.apply();
    }

    /**
     * @return the default page size.
     */
    public String getPageSize() {
        return mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE);
    }

    /**
     * Sets the default page size.
     *
     * @param pageSize The page size.
     */
    public void setPageSize(@NonNull final String pageSize) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_PAGE_SIZE_TEXT, pageSize);
        editor.apply();
    }
}
