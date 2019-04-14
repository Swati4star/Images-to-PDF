package swati4star.createpdf.util;

import android.app.Activity;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

import static swati4star.createpdf.util.Constants.pdfDirectory;

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

    public static Snackbar showIndefiniteSnackbar(Activity context, String resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_INDEFINITE);
    }

    public static Snackbar getSnackbarwithAction(Activity context, int resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getDefaultStorageLocation() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                pdfDirectory;
    }

    /**
     * This function is used to trim the root path name from
     * the getPath() method so that it can be replaced by
     * the External Storage Directory.
     * @param path
     * @return
     */
    public static StringBuilder trimExternal(String path) {
        StringBuilder trimmedPath = new StringBuilder();
        int tempPath = path.indexOf('/');
        trimmedPath.append(path.substring(tempPath + 1));
        return trimmedPath;
    }
}
