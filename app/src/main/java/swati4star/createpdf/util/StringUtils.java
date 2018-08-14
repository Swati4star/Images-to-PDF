package swati4star.createpdf.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import java.util.Objects;

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

    public static void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbar(Activity context, String resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }
}
