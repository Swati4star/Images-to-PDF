package swati4star.createpdf.util;

/**
 * Created by anandparmar on 18/06/18.
 */

public class StringUtils {

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public static boolean isNotEmpty(CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }
}
