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

    public void setDefaultPageStyle(SharedPreferences.Editor editor, String pageNumStyle, int id) {
        editor.putString(Constants.PREF_PAGE_STYLE, pageNumStyle);
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, id);
        editor.apply();
    }

    public void clearDefaultPageStyle(SharedPreferences.Editor editor) {
        setDefaultPageStyle(editor, null, -1);
    }

}
