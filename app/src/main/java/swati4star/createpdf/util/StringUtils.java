package swati4star.createpdf.util;

import android.app.Activity;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

/**
 * Created by anandparmar on 18/06/18.
 */

public class StringUtils {

    private StringUtils() {
    }

    private static class SingletonHolder {
        static final StringUtils INSTANCE = new StringUtils();
    }

    public static StringUtils getInstance() {
        return StringUtils.SingletonHolder.INSTANCE;
    }

    public boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public boolean isNotEmpty(CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }

    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(Activity context, String resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public Snackbar showIndefiniteSnackbar(Activity context, String resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_INDEFINITE);
    }

    public Snackbar getSnackbarwithAction(Activity context, int resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }

    public void hideKeyboard(Activity activity) {
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

    public String getDefaultStorageLocation() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }
}
