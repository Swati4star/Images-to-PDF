package swati4star.createpdf.util;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPreferencesUtil {

    private SharedPreferencesUtil() {
    }

    @NonNull
    public static SharedPreferencesUtil getInstance() {
        return SharedPreferencesUtil.SingletonHolder.INSTANCE;
    }

    /**
     * Set the default Page numbering style
     *
     * @param editor       the {@link SharedPreferences.Editor} to use for editing
     * @param pageNumStyle the page numbering style as defined in {@link Constants}
     * @param id           the id of the style
     */
    public void setDefaultPageNumStyle(@NonNull SharedPreferences.Editor editor, @NonNull String pageNumStyle, int id) {
        editor.putString(Constants.PREF_PAGE_STYLE, pageNumStyle);
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, id);
        editor.apply();
    }

    /**
     * Clear the default Page numbering style
     *
     * @param editor the {@link SharedPreferences.Editor} to use for editing
     */
    public void clearDefaultPageNumStyle(@NonNull SharedPreferences.Editor editor) {
        setDefaultPageNumStyle(editor, null, -1);
    }

    private static class SingletonHolder {
        static final SharedPreferencesUtil INSTANCE = new SharedPreferencesUtil();
    }

}
