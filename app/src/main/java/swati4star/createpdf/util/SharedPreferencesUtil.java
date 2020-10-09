package swati4star.createpdf.util;

import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    private SharedPreferencesUtil() { }

    private static class SingletonHolder {
        static final SharedPreferencesUtil INSTANCE = new SharedPreferencesUtil();
    }

    public static SharedPreferencesUtil getInstance() {
        return SharedPreferencesUtil.SingletonHolder.INSTANCE;
    }

    /**
     * Set the default Page numbering style
     * @param editor the {@link SharedPreferences.Editor} to use for editing
     * @param pageNumStyle the page numbering style as defined in {@link Constants}
     * @param id the id of the style
     */
    public void setDefaultPageNumStyle(SharedPreferences.Editor editor, String pageNumStyle, int id) {
        editor.putString(Constants.PREF_PAGE_STYLE, pageNumStyle);
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, id);
        editor.apply();
    }

    /**
     * Clear the default Page numbering style
     * @param editor the {@link SharedPreferences.Editor} to use for editing
     */
    public void clearDefaultPageNumStyle(SharedPreferences.Editor editor) {
        setDefaultPageNumStyle(editor, null, -1);
    }

}
